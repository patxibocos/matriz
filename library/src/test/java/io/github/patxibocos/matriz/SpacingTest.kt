package io.github.patxibocos.matriz

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.property.checkAll

class SpacingTest : StringSpec({
    "spacing is taken into account for rows/columns calculation" {
        checkAll(
            canvasAndCellSizeArb,
            spacingArb,
            spacingArb,
        ) { (canvasSize, cellSize), horizontalSpacing, verticalSpacing ->
            val cellSizeSizing = Sizing.CellSize(cellSize)

            val sizingResult = cellSizeSizing.calculateSizing(
                canvasSize,
                Spacing(horizontalSpacing, verticalSpacing),
            )

            sizingResult.rows shouldBeExactly ((canvasSize.height + verticalSpacing) / (cellSize.height + verticalSpacing)).toInt()
            sizingResult.columns shouldBeExactly ((canvasSize.width + horizontalSpacing) / (cellSize.width + horizontalSpacing)).toInt()
        }
    }
})
