package io.github.patxibocos.matriz

import androidx.compose.ui.geometry.Size
import kotlin.math.min

class SizingResult(val rows: Int, val columns: Int, val cellSize: Size)

sealed interface Sizing {
    fun calculateSizing(canvasSize: Size, spacing: Spacing = Spacing.Zero): SizingResult

    class Rows(private val rows: Int, private val cellsAspectRatio: Aspect.CellsRatio) : Sizing {
        init {
            require(rows > 0)
        }

        override fun calculateSizing(canvasSize: Size, spacing: Spacing): SizingResult {
            val cellWidth =
                min(
                    canvasSize.width,
                    (canvasSize.height - (rows - 1) * spacing.vertical) / rows * cellsAspectRatio.ratio,
                )
            val columns =
                ((canvasSize.width + spacing.horizontal) / (cellWidth + spacing.horizontal)).toInt()
            return SizingResult(
                rows = rows,
                columns = columns,
                cellSize = Size(cellWidth, cellWidth / cellsAspectRatio.ratio),
            )
        }
    }

    class Columns(
        private val columns: Int,
        private val cellsAspectRatio: Aspect.CellsRatio,
    ) : Sizing {
        init {
            require(columns > 0)
        }

        override fun calculateSizing(canvasSize: Size, spacing: Spacing): SizingResult {
            val cellHeight =
                min(
                    canvasSize.height,
                    (canvasSize.width - (columns - 1) * spacing.horizontal) / columns / cellsAspectRatio.ratio,
                )
            val rows =
                ((canvasSize.height + spacing.vertical) / (cellHeight + spacing.vertical)).toInt()
            return SizingResult(
                rows = rows,
                columns = columns,
                cellSize = Size(cellHeight * cellsAspectRatio.ratio, cellHeight),
            )
        }
    }

    class RowsAndColumns(
        private val rows: Int,
        private val columns: Int,
        private val aspect: Aspect,
    ) : Sizing {
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
                cellSize = Size(cellWidth, cellHeight),
            )
        }
    }

    class CellSize(private val size: Size) : Sizing {
        init {
            require(size.width > 0f)
            require(size.height > 0f)
        }

        constructor(size: Float) : this(Size(size, size))

        override fun calculateSizing(canvasSize: Size, spacing: Spacing): SizingResult =
            SizingResult(
                rows = ((canvasSize.height + spacing.vertical) / (size.height + spacing.vertical)).toInt(),
                columns = ((canvasSize.width + spacing.horizontal) / (size.width + spacing.horizontal)).toInt(),
                cellSize = size,
            )
    }
}
