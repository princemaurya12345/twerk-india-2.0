package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.camera.CameraPreview
import com.example.ui.camera.VideoFilter
import com.example.ui.components.ChatBottomSheet
import com.example.ui.components.PartnerVideoView
import com.example.ui.components.ReactionsOverlay
import com.example.ui.components.SubtitlesCard
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.DesiPink
import com.example.ui.theme.EmeraldLive
import com.example.ui.theme.IndianPeacock
import com.example.ui.theme.MidnightBlack
import com.example.ui.theme.SaffronPrimary
import com.example.ui.viewmodel.VideoChatUiState
import com.example.ui.viewmodel.VideoChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoChatScreen(
    viewModel: VideoChatViewModel,
    uiState: VideoChatUiState,
    modifier: Modifier = Modifier
) {
    var showChatSheet by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showFilterPicker by remember { mutableStateOf(false) }
    var showDisconnectConfirm by remember { mutableStateOf(false) }

    // Intercept back button to prompt disconnect
    BackHandler {
        showDisconnectConfirm = true
    }

    val currentPartner = uiState.partner

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlack)
    ) {
        if (currentPartner != null) {
            // Main Partner Video Canvas
            PartnerVideoView(
                partner = currentPartner,
                isBlurred = uiState.isPartnerBlurred,
                isTypingOrSpeaking = uiState.isTypingResponse,
                onToggleBlur = { viewModel.togglePartnerBlur() },
                onReport = { showReportDialog = true },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Floating Reaction Emojis
        ReactionsOverlay(
            reactions = uiState.reactions,
            modifier = Modifier.fillMaxSize()
        )

        // Top Status Chip: Call Type & Quality
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black.copy(alpha = 0.55f))
                .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(EmeraldLive)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "${uiState.callType.icon} ${uiState.callType.label} • ${uiState.qualityMode.label}",
                color = Color(0xFFE2E8F0),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Local Inset Camera (PIP)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 64.dp, end = 14.dp)
                .size(width = 110.dp, height = 150.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, SaffronPrimary.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                .testTag("local_camera_pip")
        ) {
            CameraPreview(
                isVideoEnabled = uiState.isLocalCameraEnabled,
                isFrontCamera = uiState.isFrontCamera,
                filter = uiState.activeFilter,
                modifier = Modifier.fillMaxSize()
            )

            // Local Camera Quick Mini Overlays
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Switch front/back
                IconButton(
                    onClick = { viewModel.switchCameraLens() },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Cameraswitch,
                        contentDescription = "Switch Camera Lens",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }

                // Mic Mute toggle
                IconButton(
                    onClick = { viewModel.toggleMute() },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (uiState.isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Toggle Mic",
                        tint = if (uiState.isMicMuted) Color(0xFFFF5252) else EmeraldLive,
                        modifier = Modifier.size(14.dp)
                    )
                }

                // Camera Shutter toggle
                IconButton(
                    onClick = { viewModel.toggleLocalCamera() },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (uiState.isLocalCameraEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff,
                        contentDescription = "Toggle Video",
                        tint = if (uiState.isLocalCameraEnabled) Color.White else Color(0xFFFF5252),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        // Live Subtitles & Floating Controls Container (Bottom pinned)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            // Real-Time Subtitles Card
            SubtitlesCard(
                subtitle = uiState.activeSubtitle,
                showDual = uiState.showDualSubtitles,
                onSpeak = { viewModel.speakCurrentSubtitle() }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Emoji Reaction Bar (Floating above Omegle bar)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val emojiList = listOf("❤️", "🔥", "😂", "👏", "🇮🇳", "💃", "☕")
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        emojiList.forEach { emoji ->
                            Text(
                                text = emoji,
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .clickable { viewModel.triggerReaction(emoji) }
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Iconic Omegle Bottom Control Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // STOP (Red Disconnect Button)
                Button(
                    onClick = { showDisconnectConfirm = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("stop_chat_button")
                ) {
                    Text(
                        text = "STOP",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                }

                // Icebreaker Button 💡
                IconButton(
                    onClick = { viewModel.sendIcebreaker() },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B))
                        .testTag("icebreaker_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Send Icebreaker",
                        tint = AccentYellow,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Video Filter Selector 🎨
                IconButton(
                    onClick = { showFilterPicker = !showFilterPicker },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B))
                        .testTag("filter_picker_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ColorLens,
                        contentDescription = "Camera Filters",
                        tint = DesiPink,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Live Chat Drawer 💬
                IconButton(
                    onClick = { showChatSheet = true },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B))
                        .testTag("open_chat_drawer_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = "Open Chat",
                        tint = IndianPeacock,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Text To Speech Auto-Speak Toggle 🔊
                IconButton(
                    onClick = { viewModel.toggleAutoSpeak() },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(if (uiState.autoSpeakTranslations) SaffronPrimary else Color(0xFF1E293B))
                        .testTag("auto_speak_toggle")
                ) {
                    Icon(
                        imageVector = if (uiState.autoSpeakTranslations) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "Auto Speak",
                        tint = if (uiState.autoSpeakTranslations) Color.Black else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // NEXT MATCH ⏭ (Iconic Omegle Skip Button)
                Button(
                    onClick = { viewModel.nextMatch() },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("next_match_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "NEXT",
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Camera Filter Chooser Popup
        if (showFilterPicker) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 86.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF1E293B))
                    .padding(12.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    VideoFilter.values().forEach { filter ->
                        val isSelected = uiState.activeFilter == filter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) SaffronPrimary else Color(0xFF0F172A))
                                .clickable {
                                    viewModel.setVideoFilter(filter)
                                    showFilterPicker = false
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = filter.label,
                                color = if (isSelected) Color.Black else Color.White,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // Live Chat Modal Bottom Sheet
        if (showChatSheet) {
            ChatBottomSheet(
                messages = uiState.messages,
                isPartnerTyping = uiState.isTypingResponse,
                onSendMessage = { viewModel.sendUserMessage(it) },
                onClose = { showChatSheet = false },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        // Disconnect Confirmation Dialog
        if (showDisconnectConfirm) {
            AlertDialog(
                onDismissRequest = { showDisconnectConfirm = false },
                title = { Text("Disconnect from chat?", color = Color.White) },
                text = {
                    Text(
                        "Are you sure you want to end this conversation and return to the lobby?",
                        color = Color(0xFFCBD5E1)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDisconnectConfirm = false
                            viewModel.disconnectToLobby()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                    ) {
                        Text("Disconnect", color = Color.White)
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showDisconnectConfirm = false }) {
                        Text("Stay", color = Color.White)
                    }
                },
                containerColor = Color(0xFF1E293B)
            )
        }

        // Report & Disconnect Dialog
        if (showReportDialog) {
            AlertDialog(
                onDismissRequest = { showReportDialog = false },
                title = { Text("Report Stranger", color = Color.White) },
                text = {
                    Text(
                        "Twerk India maintains a strict zero-tolerance policy against inappropriate behavior. Reporting will instantly disconnect and block this user.",
                        color = Color(0xFFCBD5E1)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showReportDialog = false
                            viewModel.nextMatch()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                    ) {
                        Text("Report & Skip", color = Color.White)
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showReportDialog = false }) {
                        Text("Cancel", color = Color.White)
                    }
                },
                containerColor = Color(0xFF1E293B)
            )
        }
    }
}
