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
    abstract fun calculateCanvasData(canvasSize: Size): CanvasData

    class Rows(private val rows: Int, private val sizeRatio: Float = 1f) : Sizing() {
        init {
            require(rows > 0)
            require(sizeRatio > 0)
        }

        override fun calculateCanvasData(canvasSize: Size): CanvasData {
            val cellWidth = min(canvasSize.width, canvasSize.height / rows * sizeRatio)
            val columns = (canvasSize.width / cellWidth).toInt()
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

        override fun calculateCanvasData(canvasSize: Size): CanvasData {
            val cellHeight = min(canvasSize.height, canvasSize.width / columns / sizeRatio)
            val rows = (canvasSize.height / cellHeight).toInt()
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

        override fun calculateCanvasData(canvasSize: Size): CanvasData {
            val canvasRatio = canvasSize.width / canvasSize.height
            val cellWidth: Float
            val cellHeight: Float
            if (sizeRatio * (columns.toFloat() / rows) < canvasRatio) {
                cellHeight = canvasSize.height / rows
                cellWidth = cellHeight * sizeRatio
            } else {
                cellWidth = canvasSize.width / columns
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

        override fun calculateCanvasData(canvasSize: Size): CanvasData =
            CanvasData(
                rows = (canvasSize.height / size.height).toInt(),
                columns = (canvasSize.width / size.width).toInt(),
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
) {
    fun Size.toIntSize(): IntSize = IntSize(width.toInt(), height.toInt())

    Canvas(modifier = modifier) {
        val canvasData = sizing.calculateCanvasData(size)
        val alignOffset = contentAlignment.align(
            Size(
                canvasData.columns * canvasData.cellSize.width,
                canvasData.rows * canvasData.cellSize.height
            ).toIntSize(), size.toIntSize(), layoutDirection
        )
        translate(alignOffset.x.toFloat(), alignOffset.y.toFloat()) {
            for (row in 0 until canvasData.rows) {
                for (column in 0 until canvasData.columns) {
                    translate(
                        left = column * canvasData.cellSize.width,
                        top = row * canvasData.cellSize.height
                    ) {
                        onDrawCell(row, column, canvasData.cellSize)
                    }
                }
            }
        }
    }
}
