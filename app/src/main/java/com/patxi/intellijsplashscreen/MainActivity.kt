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
                    sizing = Sizing.RowsAndColumns(
                        rows = 15,
                        columns = 8,
                        filling = Filling.CellsAspectRatio.One
                    ),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

sealed class IntelliJCell {
    object Circle : IntelliJCell()
    sealed class Quadrant(val startAngle: Float, val topLeftOffset: Offset) :
        IntelliJCell() {
        object TopLeft : Quadrant(180f, Offset.Zero)
        object TopRight : Quadrant(270f, -Offset(1f, 0f))
        object BottomLeft : Quadrant(90f, -Offset(0f, 1f))
        object BottomRight : Quadrant(0f, -Offset(1f, 1f))
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

fun DrawScope.drawIntelliJCell(cellSize: Size) {
    val cell = cellTypes.random()
    val color = cellColors.random()
    when (cell) {
        is IntelliJCell.Circle -> drawOval(
            color = color,
            size = cellSize
        )
        is IntelliJCell.Quadrant -> drawArc(
            color = color,
            startAngle = cell.startAngle,
            sweepAngle = 90f,
            useCenter = true,
            topLeft = Offset(
                cell.topLeftOffset.x * cellSize.width,
                cell.topLeftOffset.y * cellSize.height
            ),
            size = cellSize.times(2f)
        )
    }
}

@Composable
fun IntelliJSplashScreen(sizing: Sizing, modifier: Modifier) {
    GridCanvas(
        sizing = sizing,
        onDrawCell = { _, _, cellSize ->
            drawIntelliJCell(cellSize)
        },
        modifier = modifier,
        contentAlignment = Alignment.Center,
    )
}

@Composable
@Preview(widthDp = 1280, heightDp = 800)
fun RowsAndColumnsPreview() =
    IntelliJSplashScreen(
        sizing = Sizing.RowsAndColumns(rows = 8, columns = 13, filling = Filling.All),
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
    IntelliJSplashScreen(
        sizing = Sizing.Rows(rows = 13, cellsAspectRatio = 1f),
        modifier = Modifier.fillMaxSize()
    )

@Composable
@Preview
fun ColumnsPreview() =
    IntelliJSplashScreen(
        sizing = Sizing.Columns(columns = 6, cellsAspectRatio = 1f),
        modifier = Modifier.fillMaxSize()
    )

@Composable
@Preview(widthDp = 1080, heightDp = 1920)
fun CirclesPreview() {
    GridCanvas(
        sizing = Sizing.RowsAndColumns(
            rows = 26,
            columns = 6,
            filling = Filling.All
        ),
        onDrawCell = { row, column, cellSize ->
            drawOval(
                color = Color.Magenta,
                size = Size(cellSize.width * (1 + column) / 6, cellSize.height * (1 + row) / 26)
            )
        },
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart,
        spacing = Spacing(50f),
    )
}
