package composite

import androidx.compose.runtime.Composable

class CompositeComponent(
    private val children: MutableList<UiComponent> = mutableListOf()
) : UiComponent {

    fun add(child: UiComponent) {
        children.add(child)
    }

    fun remove(child: UiComponent) {
        children.remove(child)
    }

    @Composable
    override fun Render() {
        children.forEach { it.Render() }
    }
}