# Homiee Helper — Auth Flow (Jetpack Compose)

A Kotlin + Jetpack Compose module implementing the Splash, Sign Up, Login,
Verify OTP, Forgot Password and Reset Password screens from the reference
design.

## How to open
1. Open Android Studio (Koala/Ladybug or newer recommended).
2. **File → Open** → select the `HomieeHelper` folder.
3. Let Gradle sync (requires internet access to pull dependencies the first
   time). This project was authored/edited in an offline sandbox, so it has
   **not** been compiled here — do a Gradle sync + build in Android Studio to
   confirm it builds clean in your environment.
4. Run the `app` module on an emulator or device (minSdk 24).

## What's implemented

### Screens (`ui/screens/...`)
- **Splash** — gradient background, layered animated teal waves, fade + scale
  entrance animation for the logo/text, page-indicator dots. System bars are
  fully **hidden** here.
- **Sign Up** ("Create Account") — first/last name, email, password, confirm
  password, terms checkbox, Google sign-up.
- **Login** ("Welcome Back!") — email, password, forgot-password link, Google
  login.
- **Verify OTP** — 6-digit auto-advancing input, resend countdown timer.
- **Forgot Password** — email input, "secure link" info card.
- **Reset Password** — new/confirm password, success info card.

### Shared components (`ui/components/...`)
- **`SystemBars.kt`**
  - `TransparentSystemBars()` — makes the status bar *and* navigation bar
    fully transparent. Used by every auth screen.
  - `HiddenSystemBars()` — fully hides both system bars. Used only by Splash.
- **`AuthScaffold.kt`** — the joint shared scaffold used by **Sign Up,
  Login, OTP, Forgot Password and Reset Password**. It:
  1. Calls `TransparentSystemBars()` so both bars are transparent.
  2. Applies `Modifier.windowInsetsPadding(WindowInsets.systemBars)` so the
     actual UI content (title, fields, buttons) always sits **between** the
     two bars and never gets obscured by them, even though the bars
     themselves are see-through.
  3. Provides the shared back button, horizontal padding and optional
     scrolling used by every one of those five screens.
- **`CommonComponents.kt`** — text fields, password fields (with show/hide
  toggle), primary button, Google button, divider, info cards.
- **`GoogleLogo.kt`** — dependency-free vector rendering of the Google mark.

### Navigation (`ui/navigation/...`)
- **`QuickTransitions.kt`** — replaces Compose Navigation's default ~300ms
  slide with a **fast 110ms fade**, applied once at the `NavHost` level so
  every screen-to-screen transition in the app is quick rather than the
  default "smooth" slide.
- **`NavGraph.kt`** — wires Splash → Login/SignUp → OTP → back to Login,
  and Login → Forgot Password → Reset Password → Login.

## Notes / next steps
- Auth actions (`onLoginClick`, `onSignUpClick`, Google buttons, actual OTP
  verification, etc.) are wired as callbacks with `// TODO` markers — plug in
  your backend/Firebase/etc. there.
- Colors live in `ui/theme/Color.kt` if you want to fine-tune the teal shade
  or wave colors to match your final brand palette exactly.
- The Google "G" mark is drawn with Canvas so there's no external asset
  dependency — swap in a real vector/drawable asset any time.
