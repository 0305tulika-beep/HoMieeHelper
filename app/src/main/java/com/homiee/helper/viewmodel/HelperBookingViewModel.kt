package com.homiee.helper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homiee.helper.data.model.BookingDto
import com.homiee.helper.data.model.endMillis
import com.homiee.helper.data.model.startMillis
import com.homiee.helper.data.repository.BookingRepository
import com.homiee.helper.data.repository.BookingResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val STATUS_PENDING = "pending"
private const val STATUS_CONFIRMED = "confirmed"
private const val STATUS_COMPLETED = "completed"

/** How often the clock is re-checked so jobs move between tabs on their own. */
private const val CLOCK_TICK_MS = 30_000L

private fun BookingDto.isConfirmed() = status.equals(STATUS_CONFIRMED, true)

private fun List<BookingDto>.pendingOnly() =
    filter { it.status.equals(STATUS_PENDING, true) }.sortedByDescending { it.createdAt }

/** Accepted and the start time hasn't arrived yet (soonest first). */
private fun List<BookingDto>.upcomingAt(now: Long) =
    filter { it.isConfirmed() && (it.startMillis() == 0L || now < it.startMillis()) }
        .sortedBy { it.bookingDate + it.startTime }          // ISO strings sort correctly

/** Accepted and happening right now: start <= now < end. */
private fun List<BookingDto>.activeAt(now: Long) =
    filter { it.isConfirmed() && it.startMillis() in 1..now && now < it.endMillis() }
        .sortedBy { it.bookingDate + it.startTime }

/** Marked completed by the server, OR accepted and the end time has passed. Most recent first. */
private fun List<BookingDto>.completedAt(now: Long) =
    filter {
        it.status.equals(STATUS_COMPLETED, true) ||
                (it.isConfirmed() && it.endMillis() in 1..now)
    }.sortedByDescending { it.bookingDate + it.startTime }

/**
 * One shared source of truth for every helper screen (Home, Job Requests,
 * My Jobs, Request/Job details). It fetches ?status=all once and derives the
 * lists locally, so accepting a request moves it from "requests" to
 * "upcoming" instantly, on every screen, with no extra network call.
 *
 * Accepted jobs also move on their own as time passes:
 * Upcoming -> Active (start time reached) -> Completed (end time reached).
 *
 * Create it ONCE in HomieeNavHost and pass it down.
 *
 * [initialBookings] is only used by @Preview to show sample data.
 */
class HelperBookingViewModel(
    private val repo: BookingRepository = BookingRepository(),
    initialBookings: List<BookingDto> = emptyList()
) : ViewModel() {

    // ── All bookings assigned to this helper ─────────────────────────────────
    private val _bookings = MutableStateFlow(initialBookings)
    val bookings: StateFlow<List<BookingDto>> = _bookings.asStateFlow()

    // The current time, refreshed on a timer; the lists below recompute whenever it changes.
    private val initialNow = System.currentTimeMillis()
    private val _now = MutableStateFlow(initialNow)

    /** New requests waiting for the helper's answer (newest first). */
    val pendingRequests: StateFlow<List<BookingDto>> = _bookings
        .map { it.pendingOnly() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, initialBookings.pendingOnly())

    /** Accepted jobs that haven't started yet. */
    val upcoming: StateFlow<List<BookingDto>> = combine(_bookings, _now) { list, now -> list.upcomingAt(now) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, initialBookings.upcomingAt(initialNow))

    /** Accepted jobs that are in progress right now. */
    val active: StateFlow<List<BookingDto>> = combine(_bookings, _now) { list, now -> list.activeAt(now) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, initialBookings.activeAt(initialNow))

    /** Finished jobs. */
    val completed: StateFlow<List<BookingDto>> = combine(_bookings, _now) { list, now -> list.completedAt(now) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, initialBookings.completedAt(initialNow))

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loadError = MutableStateFlow<String?>(null)
    val loadError: StateFlow<String?> = _loadError.asStateFlow()

    private var loadJob: Job? = null

    // ── Accept / reject ──────────────────────────────────────────────────────
    /** Ids currently being accepted/rejected, so each card can show its own spinner. */
    private val _respondingIds = MutableStateFlow<Set<Int>>(emptySet())
    val respondingIds: StateFlow<Set<Int>> = _respondingIds.asStateFlow()

    /** One-shot messages (collected once in HomieeNavHost and shown as toasts). */
    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 4)
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    init {
        // Keeps the clock moving so jobs shift tabs without the screen being reopened.
        viewModelScope.launch {
            while (true) {
                delay(CLOCK_TICK_MS)
                _now.value = System.currentTimeMillis()
            }
        }
    }

    // NOTE: no fetch in init - screens call load() when they appear.
    fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _isLoading.value = true
            _loadError.value = null

            // isLoading is reset only inside the branches (not in a finally), so a
            // cancelled load can't switch off the spinner of the newer one.
            when (val result = repo.incomingBookings("all")) {
                is BookingResult.Success -> {
                    _bookings.value = result.data.data.orEmpty()
                    _now.value = System.currentTimeMillis()
                    _isLoading.value = false
                }
                is BookingResult.Error -> {
                    _loadError.value = result.message
                    _isLoading.value = false
                }
            }
        }
    }

    fun accept(id: Int, onResult: (success: Boolean) -> Unit = {}) = respond(id, "accept", onResult)

    fun reject(id: Int, onResult: (success: Boolean) -> Unit = {}) = respond(id, "reject", onResult)

    private fun respond(id: Int, action: String, onResult: (Boolean) -> Unit) {
        if (id in _respondingIds.value) return
        viewModelScope.launch {
            _respondingIds.value = _respondingIds.value + id

            when (val result = repo.respond(id, action)) {
                is BookingResult.Success -> {
                    val updated = result.data.data
                    if (updated != null) {
                        // Status flips to confirmed/rejected, so the derived lists update by themselves.
                        _bookings.value = _bookings.value.map { if (it.id == id) updated else it }
                    } else {
                        load()
                    }
                    _messages.tryEmit(
                        result.data.message
                            ?: if (action == "accept") "Booking accepted" else "Booking rejected"
                    )
                    onResult(true)
                }
                is BookingResult.Error -> {
                    _messages.tryEmit(result.message)
                    onResult(false)
                }
            }
            _respondingIds.value = _respondingIds.value - id
        }
    }

    /** Call on logout so the next account never sees the previous helper's bookings. */
    fun clear() {
        loadJob?.cancel()
        _bookings.value = emptyList()
        _loadError.value = null
        _isLoading.value = false
        _respondingIds.value = emptySet()
    }
}