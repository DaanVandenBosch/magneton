package magneton.nodes

import magneton.observable.observable
import kotlin.test.Test
import kotlin.test.assertEquals

class ElementTests {
    @Test
    fun attributes_should_be_removed_when_necessary() {
        var green by observable(true)
        val cmp = object : Component() {
            override fun render() = div {
                if (green) hidden = true
            }
        }
        render(cmp)

        val div = (cmp.children[0] as Element)
        assertEquals(1, div.attributes.size)

        green = false

        assertEquals(0, div.attributes.size)
    }
}
