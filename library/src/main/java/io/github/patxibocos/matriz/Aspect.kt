package io.github.patxibocos.matriz

sealed interface Aspect {
    data object Fill : Aspect

    class CellsRatio(
        val ratio: Float,
    ) : Aspect {
        init {
            require(ratio > 0)
        }

        companion object {
            val One = CellsRatio(1f)
        }
    }
}
