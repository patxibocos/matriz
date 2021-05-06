# Matriz

[![Maven Central](https://img.shields.io/maven-central/v/io.github.patxibocos/matriz?label=latest%20release)](https://search.maven.org/artifact/io.github.patxibocos/matriz)

Matriz is an Android library providing a [Jetpack Compose](https://developer.android.com/jetpack/compose) composable to use a Canvas as a grid. It allows making a **rows/columns** division of the canvas based on different criterias.

## Setup ⚙️

Simply add the dependency:

```kotlin
dependencies {
    ...
    implementation("io.github.patxibocos:matriz:$version")
}
```

## Usage 📙

The API is very simple, there is a GridCanvas composable function that wraps a Canvas. This function is parameterized with arguments to calculate the amount of cells of the grid and where will be placed:

```kotlin
GridCanvas(
    sizing = ...,
    onDrawCell = { row: Int, column: Int, cellSize: Size ->
        ...        
    }
    modifier = ...,
    contentAlignment = ...,
    spacing = ...,
)
```

## Sizings 📏

There are four different ways to set how the grid of cells will be sized.

### Rows

This way the number of rows is fixed, and the amount of resulting columns will depend on the passed aspect ratio that each cell will have:

```kotlin
Sizing.Rows(
    rows = 3,
    cellsAspectRatio = Aspect.CellsRatio(2f),
)
```

### Columns

Similar to Rows sizing but in the opposite way:

```kotlin
Sizing.Columns(
    columns = 3,
    cellsAspectRatio = Aspect.CellsRatio(2f),
)
```

### RowsAndColumns

Here both the number of rows and columns is provided. The third argument allows setting either an aspect ratio for sizing the cells, or filling the entire canvas width and height:

```kotlin
Sizing.RowsAndColumns(
    rows = 3,
    columns = 4,
    aspect = Aspect.Fill,
)
```

### CellSize

This one defines the size (both width and height) of the cells. The number of rows and columns will depend on how many cells fit on the canvas:

```kotlin
Sizing.CellSize(
    size = Size(
        width = 50f,
        height = 30f,
    )
)
```

## Content alignment 📐

It is also possible to set how the cells grid will be placed relative to the canvas. This works the same way as **contentAligment** for [Box](https://developer.android.com/reference/kotlin/androidx/compose/foundation/layout/package-summary#Box(androidx.compose.ui.Modifier,androidx.compose.ui.Alignment,kotlin.Boolean,kotlin.Function1)).

In this example, the grid of cells will be centered relative to the canvas:

```kotlin
GridCanvas(
    ...,
    contentAlignment = Alignment.Center
)
```

## Spacing 🌌

Setting space between cells is also supported via the spacing argument. It can be set both for horizontal and vertical spacing:

```kotlin
GridCanvas(
    ...,
    spacing = Spacing(
        horizontal = 10f,
        vertical = 20f,
    )
)
```