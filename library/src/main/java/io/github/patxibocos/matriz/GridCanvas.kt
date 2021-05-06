package io.github.patxibocos.matriz

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.IntSize
import kotlin.math.min

class SizingResult(val rows: Int, val columns: Int, val cellSize: Size)

sealed class Aspect {
    object Fill : Aspect()

    class CellsRatio(val ratio: Float) : Aspect() {
        init {
            require(ratio > 0)
        }

        companion object {
            val One = CellsRatio(1f)
        }
    }
}

sealed class Sizing {
    abstract fun calculateSizing(canvasSize: Size, spacing: Spacing = Spacing.Zero): SizingResult

    class Rows(private val rows: Int, private val cellsAspectRatio: Aspect.CellsRatio) : Sizing() {
        init {
            require(rows > 0)
        }

        override fun calculateSizing(canvasSize: Size, spacing: Spacing): SizingResult {
            val cellWidth =
                min(
                    canvasSize.width,
                    (canvasSize.height - (rows - 1) * spacing.vertical) / rows * cellsAspectRatio.ratio
                )
            val columns =
                ((canvasSize.width + spacing.horizontal) / (cellWidth + spacing.horizontal)).toInt()
            return SizingResult(
                rows = rows,
                columns = columns,
                cellSize = Size(cellWidth, cellWidth / cellsAspectRatio.ratio)
            )
        }
    }

    class Columns(
        private val columns: Int,
        private val cellsAspectRatio: Aspect.CellsRatio
    ) : Sizing() {
        init {
            require(columns > 0)
        }

        override fun calculateSizing(canvasSize: Size, spacing: Spacing): SizingResult {
            val cellHeight =
                min(
                    canvasSize.height,
                    (canvasSize.width - (columns - 1) * spacing.horizontal) / columns / cellsAspectRatio.ratio
                )
            val rows =
                ((canvasSize.height + spacing.vertical) / (cellHeight + spacing.vertical)).toInt()
            return SizingResult(
                rows = rows,
                columns = columns,
                cellSize = Size(cellHeight * cellsAspectRatio.ratio, cellHeight)
            )
        }
    }

    class RowsAndColumns(
        private val rows: Int,
        private val columns: Int,
        private val aspect: Aspect
    ) : Sizing() {
        init {
            require(rows > 0)
            require(columns > 0)
        }

        override fun calculateSizing(canvasSize: Size, spacing: Spacing): SizingResult {
            val canvasAvailableSpaceRatio =
                (canvasSize.width - (columns - 1) * spacing.horizontal) / (canvasSize.height - (rows - 1) * spacing.vertical)
            val cellWidth: Float
            val cellHeight: Float
            when (aspect) {
                Aspect.Fill -> {
                    cellWidth = (canvasSize.width - (columns - 1) * spacing.horizontal) / columns
                    cellHeight = (canvasSize.height - (rows - 1) * spacing.vertical) / rows
                }
                is Aspect.CellsRatio -> {
                    if (aspect.ratio * (columns.toFloat() / rows) < canvasAvailableSpaceRatio) {
                        cellHeight = (canvasSize.height - (rows - 1) * spacing.vertical) / rows
                        cellWidth = cellHeight * aspect.ratio
                    } else {
                        cellWidth =
                            (canvasSize.width - (columns - 1) * spacing.horizontal) / columns
                        cellHeight = cellWidth / aspect.ratio
                    }
                }
            }
            return SizingResult(
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

        override fun calculateSizing(canvasSize: Size, spacing: Spacing): SizingResult =
            SizingResult(
                rows = ((canvasSize.height + spacing.vertical) / (size.height + spacing.vertical)).toInt(),
                columns = ((canvasSize.width + spacing.horizontal) / (size.width + spacing.horizontal)).toInt(),
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
        val canvasData = sizing.calculateSizing(canvasSize = size, spacing = spacing)
        val alignOffset = contentAlignment.align(
            Size(
                canvasData.columns * canvasData.cellSize.width + (canvasData.columns - 1) * spacing.horizontal,
                canvasData.rows * canvasData.cellSize.height + (canvasData.rows - 1) * spacing.vertical
            ).toIntSize(),
            size.toIntSize(), layoutDirection
        )
        translate(alignOffset.x.toFloat(), alignOffset.y.toFloat()) {
            for (row in 0 until canvasData.rows) {
                for (column in 0 until canvasData.columns) {
                    translate(
                        left = column * (canvasData.cellSize.width + spacing.horizontal),
                        top = row * (canvasData.cellSize.height + spacing.vertical)
                    ) {
                        onDrawCell(row, column, canvasData.cellSize)
                    }
                }
            }
        }
    }
}

class Spacing(val horizontal: Float, val vertical: Float) {
    constructor(amount: Float) : this(amount, amount)

    companion object {
        val Zero = Spacing(0f, 0f)
    }
}
