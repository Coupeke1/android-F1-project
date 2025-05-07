package com.example.groeiproject.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PurpleButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(30),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF512DA8)),
        modifier = Modifier
            .height(48.dp)
            .widthIn(min = 100.dp)
    ) {
        Text(text = text, color = Color.White)
    }
}
