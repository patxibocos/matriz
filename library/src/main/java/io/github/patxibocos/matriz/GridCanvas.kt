package io.github.patxibocos.matriz

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.IntSize

@Composable
fun GridCanvas(
    sizing: Sizing,
    modifier: Modifier,
    onDrawCell: DrawScope.(row: Int, column: Int, cellSize: Size) -> Unit,
    contentAlignment: Alignment = Alignment.Center,
    spacing: Spacing = Spacing.Zero,
    contentDescription: String? = null,
) {
    fun Size.toIntSize(): IntSize = IntSize(width.toInt(), height.toInt())

    val onDraw: DrawScope.() -> Unit = {
        val canvasData = sizing.calculateSizing(canvasSize = size, spacing = spacing)
        val alignOffset = contentAlignment.align(
            Size(
                canvasData.columns * canvasData.cellSize.width + (canvasData.columns - 1) * spacing.horizontal,
                canvasData.rows * canvasData.cellSize.height + (canvasData.rows - 1) * spacing.vertical,
            ).toIntSize(),
            size.toIntSize(),
            layoutDirection,
        )
        translate(alignOffset.x.toFloat(), alignOffset.y.toFloat()) {
            for (row in 0 until canvasData.rows) {
                for (column in 0 until canvasData.columns) {
                    translate(
                        left = column * (canvasData.cellSize.width + spacing.horizontal),
                        top = row * (canvasData.cellSize.height + spacing.vertical),
                    ) {
                        onDrawCell(row, column, canvasData.cellSize)
                    }
                }
            }
        }
    }
    if (contentDescription == null) {
        Canvas(modifier = modifier, onDraw = onDraw)
    } else {
        Canvas(modifier = modifier, onDraw = onDraw, contentDescription = contentDescription)
    }
}
