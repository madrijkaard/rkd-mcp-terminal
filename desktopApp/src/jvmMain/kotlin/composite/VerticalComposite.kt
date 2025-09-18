package composite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

class VerticalComposite(
    private val children: MutableList<UiComponent> = mutableListOf()
) : UiComponent {

    fun add(child: UiComponent) {
        children.add(child)
    }

    fun remove(child: UiComponent) {
        children.remove(child)
    }

    fun addWithWeight(child: UiComponent, weightRatio: Float = 1f) {
        val weightedChild = WeightedComponent(child, weightRatio)
        children.add(weightedChild)
    }

    @Composable
    override fun Render() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            children.forEach { child ->
                when (child) {
                    is WeightedComponent -> {
                        Column(
                            modifier = Modifier.weight(child.weightRatio)
                        ) {
                            child.component.Render()
                        }
                    }
                    is UiComponent -> {
                        child.Render()
                    }
                }
            }
        }
    }

    fun isEmpty(): Boolean = children.isEmpty()

    fun childCount(): Int = children.size

    fun getChild(index: Int): UiComponent? = children.getOrNull(index) as? UiComponent

    fun clear() {
        children.clear()
    }

    private class WeightedComponent(
        val component: UiComponent,
        val weightRatio: Float
    ) : UiComponent {
        @Composable
        override fun Render() {
            component.Render()
        }
    }
}