package com.patxi.intellijsplashscreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(color = Color(0xFF514B4B)) {
                IntelliJSplashScreen(
                    sizing = Sizing.RowsAndColumns(rows = 15, columns = 8),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

sealed class IntelliJCell {
    object Circle : IntelliJCell()
    sealed class Quadrant(val startAngle: Float, val topLeftOffset: (Size) -> Offset) :
        IntelliJCell() {
        object TopLeft : Quadrant(180f, { Offset.Zero })
        object TopRight : Quadrant(270f, { -Offset(it.width, 0f) })
        object BottomLeft : Quadrant(90f, { -Offset(0f, it.height) })
        object BottomRight : Quadrant(0f, { -Offset(it.width, it.height) })
    }
}

val cellTypes = listOf(
    IntelliJCell.Circle,
    IntelliJCell.Quadrant.TopLeft,
    IntelliJCell.Quadrant.TopRight,
    IntelliJCell.Quadrant.BottomLeft,
    IntelliJCell.Quadrant.BottomRight
)

val cellColors = listOf(
    Color(0xFFFF7000),
    Color(0xFF007EFF),
    Color(0xFFFF0058)
)

fun DrawScope.drawIntelliJCell(cellSize: Size, offset: Offset) {
    val cell = cellTypes.random()
    val color = cellColors.random()
    when (cell) {
        is IntelliJCell.Circle -> drawOval(
            color = color,
            size = cellSize,
            topLeft = offset
        )
        is IntelliJCell.Quadrant -> drawArc(
            color = color,
            startAngle = cell.startAngle,
            sweepAngle = 90f,
            useCenter = true,
            topLeft = offset + cell.topLeftOffset(cellSize),
            size = Size(cellSize.width * 2, cellSize.height * 2)
        )
    }
}

@Composable
fun IntelliJSplashScreen(sizing: Sizing, modifier: Modifier = Modifier) {
    GridCanvas(
        sizing = sizing,
        onDrawCell = { _, cellSize, offset ->
            drawIntelliJCell(cellSize, offset)
        },
        modifier = modifier,
        contentAlignment = Alignment.Center,
    )
}

@Composable
@Preview
fun RowsAndColumnsPreview() = IntelliJSplashScreen(
    sizing = Sizing.RowsAndColumns(rows = 13, columns = 6),
    modifier = Modifier.fillMaxSize()
)

@Composable
@Preview
fun CellSizePreview() =
    IntelliJSplashScreen(
        sizing = Sizing.CellSize(180f),
        modifier = Modifier.fillMaxSize()
    )

@Composable
@Preview
fun RowsPreview() =
    IntelliJSplashScreen(sizing = Sizing.Rows(13), modifier = Modifier.fillMaxSize())

@Composable
@Preview
fun ColumnsPreview() =
    IntelliJSplashScreen(sizing = Sizing.Columns(6), modifier = Modifier.fillMaxSize())


@Composable
@Preview
fun CirclesPreview() {
    GridCanvas(
        sizing = Sizing.RowsAndColumns(26, 6, sizeRatio = 2f),
        onDrawCell = { (row, column), cellSize, offset ->
            drawOval(
                color = Color.Magenta,
                topLeft = offset,
                size = Size(cellSize.width * (1 + column) / 6, cellSize.height * (1 + row) / 26)
            )
        },
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart,
    )
}
