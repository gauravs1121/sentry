package com.gaurav.sentrydemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.sentry.Breadcrumb
import io.sentry.Sentry
import io.sentry.SentryLevel
import io.sentry.protocol.User

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SentryDemoScreen()
                }
            }
        }
    }
}

@Composable
fun SentryDemoScreen() {
    var lastAction by remember { mutableStateOf("Tap a button, then check your Sentry issue stream.") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Sentry Android Demo", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = lastAction, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                Sentry.captureMessage("Hello from the Sentry demo app")
                lastAction = "Sent an info-level message."
            }
        ) {
            Text("Send Message")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                Sentry.addBreadcrumb(
                    Breadcrumb().apply {
                        category = "ui.click"
                        message = "User tapped 'Send Handled Exception'"
                        level = SentryLevel.INFO
                    }
                )
                try {
                    throw RuntimeException("Handled exception from the demo app")
                } catch (e: Exception) {
                    Sentry.captureException(e)
                }
                lastAction = "Caught an exception and sent it to Sentry."
            }
        ) {
            Text("Send Handled Exception")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                Sentry.setUser(
                    User().apply {
                        id = "demo-user-42"
                        email = "demo@example.com"
                    }
                )
                lastAction = "Attached a demo user to future events."
            }
        ) {
            Text("Set User Context")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // Intentionally unhandled so Sentry's UncaughtExceptionHandler
                // integration reports it as a crash the next time the app starts.
                throw RuntimeException("Unhandled crash from the demo app")
            }
        ) {
            Text("Crash the App")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SentryDemoScreenPreview() {
    MaterialTheme {
        SentryDemoScreen()
    }
}