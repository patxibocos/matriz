package io.github.patxibocos.matriz

class Spacing(val horizontal: Float, val vertical: Float) {
    constructor(amount: Float) : this(amount, amount)

    companion object {
        val Zero = Spacing(0f)
    }
}
