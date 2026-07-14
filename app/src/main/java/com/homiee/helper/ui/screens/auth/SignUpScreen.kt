package com.homiee.helper.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homiee.helper.ui.components.*
import com.homiee.helper.ui.theme.TealPale
import com.homiee.helper.ui.theme.TealPrimary
import com.homiee.helper.ui.theme.TealPrimaryDark
import com.homiee.helper.ui.theme.TextSecondary

@Composable
fun SignUpScreen(
    onBackClick: () -> Unit,
    onSignUpClick: (email: String) -> Unit,
    onGoogleSignUpClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(true) }

    AuthScaffold(onBackClick = onBackClick) {
        AuthTitle(
            title = "Create Account",
            subtitle = "Join Homiee Helper and get more job opportunities."
        )
        Spacer(modifier = Modifier.height(28.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            HomieeTextField(
                value = firstName,
                onValueChange = { firstName = it },
                placeholder = "First Name",
                leadingIcon = Icons.Filled.Person,
                modifier = Modifier.weight(1f)
            )
            HomieeTextField(
                value = lastName,
                onValueChange = { lastName = it },
                placeholder = "Last Name",
                leadingIcon = Icons.Filled.Person,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        HomieeTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "Email",
            leadingIcon = Icons.Filled.Email,
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(14.dp))
        HomieePasswordField(
            value = password,
            onValueChange = { password = it },
            placeholder = "Create Password"
        )

        Spacer(modifier = Modifier.height(14.dp))
        HomieePasswordField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = "Confirm Password"
        )

        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.Top) {
            Checkbox(
                checked = termsAccepted,
                onCheckedChange = { termsAccepted = it },
                colors = CheckboxDefaults.colors(checkedColor = TealPrimary)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = buildAnnotatedString {
                    append("I agree to the ")
                    withStyle(SpanStyle(color = TealPrimary, fontWeight = FontWeight.SemiBold)) {
                        append("Terms & Conditions")
                    }
                    append(" and ")
                    withStyle(SpanStyle(color = TealPrimary, fontWeight = FontWeight.SemiBold)) {
                        append("Privacy Policy")
                    }
                },
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        PrimaryButton(text = "Sign Up", onClick = { onSignUpClick(email) }, enabled = termsAccepted)

        // Nudge for people who already have a resident Homiee account.
        // TODO: not wired up yet - decide where this should actually route
        // (a dedicated resident-login flow?) before re-enabling the click.
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(TealPale)
                .border(1.dp, TealPrimaryDark, RoundedCornerShape(12.dp))
                .clickable { /* TODO: wire up resident login routing */ }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = buildAnnotatedString {
                    append("Already a Homiee resident? ")
                    withStyle(SpanStyle(color = TealPrimary, fontWeight = FontWeight.SemiBold)) {
                        append("Login here")
                    }
                },
                fontSize = 12.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        OrDivider()

        Spacer(modifier = Modifier.height(20.dp))
        GoogleButton(text = "Sign up with Google", onClick = onGoogleSignUpClick)

        Spacer(modifier = Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Text("Already have an account? ", fontSize = 13.sp, color = TextSecondary)
            Text(
                "Login",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TealPrimary,
                modifier = Modifier.clickable { onLoginClick() }
            )
        }
    }
}