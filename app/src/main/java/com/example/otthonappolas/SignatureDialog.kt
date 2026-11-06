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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun SignatureDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {

    val strokes = remember { mutableStateListOf<List<Offset>>() }
    var currentStroke by remember { mutableStateOf<List<Offset>>(emptyList()) }

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
                                    currentStroke = listOf(offset)
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    currentStroke = currentStroke + change.position
                                },
                                onDragEnd = {
                                    if (currentStroke.isNotEmpty()) {
                                        strokes.add(currentStroke)
                                        currentStroke = emptyList()
                                    }
                                }
                            )
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        fun drawPointsAsPath(points: List<Offset>) {
                            if (points.isEmpty()) return
                            val path = Path().apply {
                                moveTo(points.first().x, points.first().y)
                                for (i in 1 until points.size) {
                                    lineTo(points[i].x, points[i].y)
                                }
                            }
                            drawPath(
                                path = path,
                                color = Color.Black,
                                style = Stroke(
                                    width = 6f,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }

                        strokes.forEach { stroke -> drawPointsAsPath(stroke) }
                        drawPointsAsPath(currentStroke)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = {
                        strokes.clear()
                        currentStroke = emptyList()
                    }) {
                        Text("Törlés")
                    }
                    Row {
                        TextButton(onClick = onDismiss) {
                            Text("Mégse")
                        }
                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = onConfirm,
                            enabled = strokes.isNotEmpty() || currentStroke.isNotEmpty()
                        ) {
                            Text("Mentés")
                        }
                    }
                }
            }
        }
    }
}