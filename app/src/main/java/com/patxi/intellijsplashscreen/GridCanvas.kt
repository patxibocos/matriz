package com.patxi.intellijsplashscreen

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.IntSize
import kotlin.math.min

class CanvasData(val rows: Int, val columns: Int, val cellSize: Size)

sealed class Sizing {
    abstract fun calculateCanvasData(canvasSize: Size, spacing: Spacing): CanvasData

    class Rows(private val rows: Int, private val sizeRatio: Float = 1f) : Sizing() {
        init {
            require(rows > 0)
            require(sizeRatio > 0)
        }

        override fun calculateCanvasData(canvasSize: Size, spacing: Spacing): CanvasData {
            val cellWidth =
                min(
                    canvasSize.width,
                    (canvasSize.height - (rows - 1) * spacing.amount.height) / rows * sizeRatio
                )
            val columns =
                ((canvasSize.width + spacing.amount.width) / (cellWidth + spacing.amount.width)).toInt()
            return CanvasData(
                rows = rows,
                columns = columns,
                cellSize = Size(cellWidth, cellWidth / sizeRatio)
            )
        }
    }

    class Columns(private val columns: Int, private val sizeRatio: Float = 1f) : Sizing() {
        init {
            require(columns > 0)
            require(sizeRatio > 0)
        }

        override fun calculateCanvasData(canvasSize: Size, spacing: Spacing): CanvasData {
            val cellHeight =
                min(
                    canvasSize.height,
                    (canvasSize.width - (columns - 1) * spacing.amount.width) / columns / sizeRatio
                )
            val rows =
                ((canvasSize.height + spacing.amount.height) / (cellHeight + spacing.amount.height)).toInt()
            return CanvasData(
                rows = rows,
                columns = columns,
                cellSize = Size(cellHeight * sizeRatio, cellHeight)
            )
        }
    }

    class RowsAndColumns(
        private val rows: Int,
        private val columns: Int,
        private val sizeRatio: Float = 1f
    ) : Sizing() {
        init {
            require(rows > 0)
            require(columns > 0)
            require(sizeRatio > 0)
        }

        override fun calculateCanvasData(canvasSize: Size, spacing: Spacing): CanvasData {
            val canvasRatio = canvasSize.width / canvasSize.height
            val cellWidth: Float
            val cellHeight: Float
            if (sizeRatio * (columns.toFloat() / rows) < canvasRatio) {
                cellHeight = (canvasSize.height - (rows - 1) * spacing.amount.height) / rows
                cellWidth = cellHeight * sizeRatio
            } else {
                cellWidth = (canvasSize.width - (columns - 1) * spacing.amount.width) / columns
                cellHeight = cellWidth / sizeRatio
            }
            return CanvasData(
                rows = rows,
                columns = columns,
                cellSize = Size(cellWidth, cellHeight)
            )
        }
    }

    class CellSize(private val size: Size) : Sizing() {
        init {
            require(size.width > 0f)
            require(size.height > 0f)
        }

        constructor(size: Float) : this(Size(size, size))

        override fun calculateCanvasData(canvasSize: Size, spacing: Spacing): CanvasData =
            CanvasData(
                rows = ((canvasSize.height + spacing.amount.height) / (size.height + spacing.amount.height)).toInt(),
                columns = ((canvasSize.width + spacing.amount.width) / (size.width + spacing.amount.width)).toInt(),
                cellSize = size
            )
    }
}

@Composable
fun GridCanvas(
    sizing: Sizing,
    onDrawCell: DrawScope.(row: Int, column: Int, size: Size) -> Unit,
    modifier: Modifier,
    contentAlignment: Alignment = Alignment.Center,
    spacing: Spacing = Spacing.Zero,
) {
    fun Size.toIntSize(): IntSize = IntSize(width.toInt(), height.toInt())

    Canvas(modifier = modifier) {
        val canvasData = sizing.calculateCanvasData(canvasSize = size, spacing = spacing)
        val alignOffset = contentAlignment.align(
            Size(
                canvasData.columns * canvasData.cellSize.width + (canvasData.columns - 1) * spacing.amount.width,
                canvasData.rows * canvasData.cellSize.height + (canvasData.rows - 1) * spacing.amount.height
            ).toIntSize(),
            size.toIntSize(), layoutDirection
        )
        translate(alignOffset.x.toFloat(), alignOffset.y.toFloat()) {
            for (row in 0 until canvasData.rows) {
                for (column in 0 until canvasData.columns) {
                    translate(
                        left = column * (canvasData.cellSize.width + spacing.amount.width),
                        top = row * (canvasData.cellSize.height + spacing.amount.height)
                    ) {
                        onDrawCell(row, column, canvasData.cellSize)
                    }
                }
            }
        }
    }
}

class Spacing private constructor(
    val amount: Size,
    val includeBorders: Boolean

) {
    companion object {
        fun spacing(horizontal: Float, vertical: Float, includeBorders: Boolean): Spacing =
            Spacing(Size(horizontal, vertical), includeBorders)

        val Zero = spacing(0f, 0f, false)
    }
}
