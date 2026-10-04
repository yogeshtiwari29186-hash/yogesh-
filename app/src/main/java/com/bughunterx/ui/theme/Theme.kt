package com.bughunterx.ui.theme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
@Composable fun BugHunterXTheme(content:@Composable()->Unit){ MaterialTheme(colorScheme=darkColorScheme(),content=content) }
