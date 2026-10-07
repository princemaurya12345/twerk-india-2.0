package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.IncomingCallScreen
import com.example.ui.screens.LobbyScreen
import com.example.ui.screens.MatchingScreen
import com.example.ui.screens.VideoChatScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ChatScreenState
import com.example.ui.viewmodel.VideoChatViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: VideoChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF090D16)
                ) {
                    Box(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
                        TwerkIndiaApp(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun TwerkIndiaApp(viewModel: VideoChatViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState.screenState) {
        ChatScreenState.LOBBY -> {
            LobbyScreen(
                viewModel = viewModel,
                uiState = uiState,
                modifier = Modifier.fillMaxSize()
            )
        }
        ChatScreenState.MATCHING -> {
            MatchingScreen(
                uiState = uiState,
                onCancel = { viewModel.disconnectToLobby() },
                modifier = Modifier.fillMaxSize()
            )
        }
        ChatScreenState.CALL_RINGING -> {
            IncomingCallScreen(
                uiState = uiState,
                onAccept = { viewModel.acceptIncomingCall() },
                onDecline = { viewModel.declineIncomingCall() },
                modifier = Modifier.fillMaxSize()
            )
        }
        ChatScreenState.CONNECTED -> {
            VideoChatScreen(
                viewModel = viewModel,
                uiState = uiState,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
