package com.example.otthonappolas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun SignatureDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {

    var paths by remember { mutableStateOf(mutableListOf<Path>()) }
    var currentPath by remember { mutableStateOf<Path?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth().height(400.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Beteg aláírása", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))


                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray.copy(alpha = 0.3f))
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    currentPath = Path().apply { moveTo(offset.x, offset.y) }
                                },
                                onDrag = { change, _ ->
                                    currentPath?.lineTo(change.position.x, change.position.y)
                                    paths = paths.toMutableList()
                                },
                                onDragEnd = {
                                    currentPath?.let { paths.add(it) }
                                    currentPath = null
                                }
                            )
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        paths.forEach { path ->
                            drawPath(path = path, color = Color.Black, style = Stroke(width = 5f))
                        }
                        currentPath?.let { path ->
                            drawPath(path = path, color = Color.Black, style = Stroke(width = 5f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = { paths = mutableListOf() }) {
                        Text("Törlés")
                    }
                    Row {
                        TextButton(onClick = onDismiss) {
                            Text("Mégse")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = onConfirm) {
                            Text("Mentés")
                        }
                    }
                }
            }
        }
    }
}