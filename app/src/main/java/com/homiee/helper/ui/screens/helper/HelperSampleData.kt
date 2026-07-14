package com.homiee.helper.ui.screens.helper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.ElderlyWoman
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SoupKitchen
import androidx.compose.ui.graphics.vector.ImageVector

enum class RequestStatus { NEW, ACCEPTED, REJECTED }
enum class BookingStatus { UPCOMING, ACTIVE, COMPLETED }

data class ServiceType(val label: String, val icon: ImageVector) {
    companion object {
        val Cleaning = ServiceType("House Cleaning", Icons.Filled.Brush)
        val Cooking = ServiceType("Cooking", Icons.Filled.SoupKitchen)
        val Babysitting = ServiceType("Babysitting", Icons.Filled.ChildCare)
        val Eldercare = ServiceType("Eldercare", Icons.Filled.ElderlyWoman)
    }
}

data class JobRequest(
    val id: String,
    val residentName: String,
    val residentInitials: String,
    val service: ServiceType,
    val location: String,
    val fullAddress: String,
    val date: String,
    val time: String,
    val duration: String,
    val salary: String,
    val timeAgo: String,
    val status: RequestStatus,
    val email: String = "",
    val phone: String = "",
    val instructions: String = ""
)

data class JobBooking(
    val id: String,
    val residentName: String,
    val residentInitials: String,
    val service: ServiceType,
    val location: String,
    val fullAddress: String,
    val date: String,
    val time: String,
    val duration: String,
    val salary: String,
    val status: BookingStatus,
    val progressFraction: Float = 0f,
    val elapsedLabel: String = "",
    val email: String = "",
    val phone: String = "",
    val instructions: String = ""
)

data class ChatPreview(
    val id: String,
    val name: String,
    val initials: String,
    val service: String,
    val date: String,
    val lastMessage: String,
    val timeLabel: String,
    val unreadCount: Int = 0,
    val online: Boolean = false
)

data class ChatMessage(
    val text: String,
    val time: String,
    val isMe: Boolean,
    val dateSeparator: String? = null
)

/** Central place for shared demo data so every screen stays consistent. */
object HelperSampleData {

    val newRequests = listOf(
        JobRequest(
            id = "req1",
            residentName = "Neha Sharma",
            residentInitials = "NS",
            service = ServiceType.Cleaning,
            location = "Indirapuram, Ghaziabad",
            fullAddress = "A-1204, ATS Advantage, Indirapuram, Ghaziabad, Uttar Pradesh - 201014",
            date = "12 May 2025",
            time = "10:00 AM",
            duration = "2 Hours",
            salary = "₹ 500",
            timeAgo = "2 mins ago",
            status = RequestStatus.NEW,
            email = "neha.sharma@example.com",
            phone = "+91 98765 43210",
            instructions = "Please focus on kitchen cleaning and bathrooms. Bring your own cleaning materials. Thank you!"
        ),
        JobRequest(
            id = "req2",
            residentName = "Ritu Verma",
            residentInitials = "RV",
            service = ServiceType.Cooking,
            location = "Vaishali, Ghaziabad",
            fullAddress = "B-45, Vaishali Sector 4, Ghaziabad, Uttar Pradesh - 201012",
            date = "13 May 2025",
            time = "02:00 PM",
            duration = "3 Hours",
            salary = "₹ 600",
            timeAgo = "15 mins ago",
            status = RequestStatus.NEW,
            email = "ritu.verma@example.com",
            phone = "+91 91234 56780"
        ),
        JobRequest(
            id = "req3",
            residentName = "Anjali Singh",
            residentInitials = "AS",
            service = ServiceType.Babysitting,
            location = "Crossings Republik, Ghaziabad",
            fullAddress = "Tower 7, Crossings Republik, Ghaziabad, Uttar Pradesh - 201016",
            date = "14 May 2025",
            time = "05:00 PM",
            duration = "4 Hours",
            salary = "₹ 550",
            timeAgo = "28 mins ago",
            status = RequestStatus.NEW,
            email = "anjali.singh@example.com",
            phone = "+91 99887 76655"
        ),
        JobRequest(
            id = "req4",
            residentName = "Sunita Agarwal",
            residentInitials = "SA",
            service = ServiceType.Eldercare,
            location = "Indirapuram, Ghaziabad",
            fullAddress = "C-22, Indirapuram, Ghaziabad, Uttar Pradesh - 201014",
            date = "15 May 2025",
            time = "09:00 AM",
            duration = "2 Hours",
            salary = "₹ 600",
            timeAgo = "1 hr ago",
            status = RequestStatus.NEW
        )
    )

    val acceptedRequests = listOf(
        newRequests[0].copy(status = RequestStatus.ACCEPTED),
        newRequests[1].copy(status = RequestStatus.ACCEPTED)
    )

    val rejectedRequests = listOf(
        newRequests[2].copy(status = RequestStatus.REJECTED),
        newRequests[3].copy(status = RequestStatus.REJECTED),
        newRequests[0].copy(id = "req5", status = RequestStatus.REJECTED)
    )

    val allRequests = newRequests + acceptedRequests + rejectedRequests

    fun requestById(id: String): JobRequest? = allRequests.find { it.id == id }

    val upcomingBookings = listOf(
        JobBooking(
            id = "job1",
            residentName = "Neha Sharma",
            residentInitials = "NS",
            service = ServiceType.Cleaning,
            location = "Indirapuram, Ghaziabad",
            fullAddress = "A-1204, ATS Advantage, Indirapuram, Ghaziabad, Uttar Pradesh - 201014",
            date = "12 May 2025",
            time = "10:00 AM",
            duration = "2 Hours",
            salary = "₹ 500",
            status = BookingStatus.UPCOMING,
            email = "neha.sharma@example.com",
            phone = "+91 98765 43210",
            instructions = "Please focus on kitchen cleaning and bathrooms. Bring your own cleaning materials. Thank you!"
        ),
        JobBooking(
            id = "job2",
            residentName = "Ritu Verma",
            residentInitials = "RV",
            service = ServiceType.Cooking,
            location = "Vaishali, Ghaziabad",
            fullAddress = "B-45, Vaishali Sector 4, Ghaziabad, Uttar Pradesh - 201012",
            date = "13 May 2025",
            time = "02:00 PM",
            duration = "3 Hours",
            salary = "₹ 600",
            status = BookingStatus.UPCOMING
        ),
        JobBooking(
            id = "job3",
            residentName = "Anjali Singh",
            residentInitials = "AS",
            service = ServiceType.Babysitting,
            location = "Crossings Republik, Ghaziabad",
            fullAddress = "Tower 7, Crossings Republik, Ghaziabad, Uttar Pradesh - 201016",
            date = "14 May 2025",
            time = "05:00 PM",
            duration = "4 Hours",
            salary = "₹ 550",
            status = BookingStatus.UPCOMING
        ),
        JobBooking(
            id = "job4",
            residentName = "Sunita Agarwal",
            residentInitials = "SA",
            service = ServiceType.Eldercare,
            location = "Indirapuram, Ghaziabad",
            fullAddress = "C-22, Indirapuram, Ghaziabad, Uttar Pradesh - 201014",
            date = "15 May 2025",
            time = "09:00 AM",
            duration = "2 Hours",
            salary = "₹ 500",
            status = BookingStatus.UPCOMING
        )
    )

    val activeBooking = JobBooking(
        id = "job_active",
        residentName = "Neha Sharma",
        residentInitials = "NS",
        service = ServiceType.Cleaning,
        location = "Indirapuram, Ghaziabad",
        fullAddress = "A-1204, ATS Advantage, Indirapuram, Ghaziabad, Uttar Pradesh - 201014",
        date = "12 May 2025",
        time = "10:00 AM - 12:00 PM",
        duration = "2 Hours",
        salary = "₹ 500",
        status = BookingStatus.ACTIVE,
        progressFraction = 0.55f,
        elapsedLabel = "01:10:00 of 02:00:00",
        phone = "+91 98765 43210"
    )

    val completedBookings = listOf(
        JobBooking(
            id = "job5",
            residentName = "Neha Sharma",
            residentInitials = "NS",
            service = ServiceType.Cleaning,
            location = "Indirapuram, Ghaziabad",
            fullAddress = "A-1204, ATS Advantage, Indirapuram, Ghaziabad, Uttar Pradesh - 201014",
            date = "10 May 2025",
            time = "10:00 AM - 12:00 PM",
            duration = "2 Hours",
            salary = "₹ 500",
            status = BookingStatus.COMPLETED,
            email = "neha.sharma@example.com",
            phone = "+91 98765 43210",
            instructions = "Please focus on kitchen cleaning and bathrooms. Bring your own cleaning materials. Thank you!"
        ),
        JobBooking(
            id = "job6",
            residentName = "Ritu Verma",
            residentInitials = "RV",
            service = ServiceType.Cooking,
            location = "Vaishali, Ghaziabad",
            fullAddress = "B-45, Vaishali Sector 4, Ghaziabad, Uttar Pradesh - 201012",
            date = "09 May 2025",
            time = "02:00 PM",
            duration = "3 Hours",
            salary = "₹ 600",
            status = BookingStatus.COMPLETED
        ),
        JobBooking(
            id = "job7",
            residentName = "Anjali Singh",
            residentInitials = "AS",
            service = ServiceType.Babysitting,
            location = "Crossings Republik, Ghaziabad",
            fullAddress = "Tower 7, Crossings Republik, Ghaziabad, Uttar Pradesh - 201016",
            date = "08 May 2025",
            time = "05:00 PM",
            duration = "4 Hours",
            salary = "₹ 550",
            status = BookingStatus.COMPLETED
        ),
        JobBooking(
            id = "job8",
            residentName = "Sunita Agarwal",
            residentInitials = "SA",
            service = ServiceType.Eldercare,
            location = "Indirapuram, Ghaziabad",
            fullAddress = "C-22, Indirapuram, Ghaziabad, Uttar Pradesh - 201014",
            date = "07 May 2025",
            time = "09:00 AM",
            duration = "2 Hours",
            salary = "₹ 500",
            status = BookingStatus.COMPLETED
        )
    )

    val allBookings = upcomingBookings + activeBooking + completedBookings

    fun bookingById(id: String): JobBooking? = allBookings.find { it.id == id }

    val chatPreviews = listOf(
        ChatPreview("chat1", "Neha Sharma", "NS", "House Cleaning", "12 May 2025", "Hi, please be on time tomorrow.", "10:30 AM", unreadCount = 2, online = true),
        ChatPreview("chat2", "Ritu Verma", "RV", "Cooking", "13 May 2025", "Thanks for your help!", "Yesterday", unreadCount = 1),
        ChatPreview("chat3", "Anjali Singh", "AS", "Babysitting", "14 May 2025", "I will share the address with you.", "2 Days ago"),
        ChatPreview("chat4", "Sunita Agarwal", "SA", "Eldercare", "15 May 2025", "Good job, thank you!", "3 Days ago"),
        ChatPreview("chat5", "Pooja Khurana", "PK", "House Cleaning", "08 May 2025", "Can you come for 3 hours instead?", "1 Week ago"),
        ChatPreview("chat6", "Arvind Mehta", "AM", "House Cleaning", "02 May 2025", "Payment has been done. Thanks!", "1 Week ago")
    )

    fun chatById(id: String): ChatPreview? = chatPreviews.find { it.id == id }

    fun messagesFor(chatId: String): List<ChatMessage> = listOf(
        ChatMessage("Hi! I have booked a house cleaning service on 12 May.", "10:20 AM", isMe = false, dateSeparator = "10 May 2025"),
        ChatMessage("Hello Neha! Thank you for the booking. I have noted the details.", "10:22 AM", isMe = true),
        ChatMessage("Please make sure to bring your own cleaning materials.", "10:23 AM", isMe = false),
        ChatMessage("Sure, I will. See you on 12 May at 10 AM.", "10:24 AM", isMe = true),
        ChatMessage("Hi, please be on time tomorrow.", "10:30 AM", isMe = false, dateSeparator = "Today"),
        ChatMessage("Yes, I will be there on time.", "10:31 AM", isMe = true)
    )
}