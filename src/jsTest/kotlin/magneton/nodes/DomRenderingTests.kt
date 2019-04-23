package magneton.nodes

import magneton.observable.observable
import magneton.render
import magneton.renderToDom
import magneton.style.StyleSheet
import magneton.style.px
import org.w3c.dom.get
import kotlin.browser.document
import kotlin.test.*

class DomRenderingTests {
    @Test
    fun dom_nodes_should_be_created_when_rendering() {
        val cmp = TestComponent()
        render(cmp)
        val domNode = cmp.domNode

        assertNotNull(domNode)
        assertEquals("DIV", domNode.nodeName)
        assertEquals(1, domNode.childNodes.length)
        assertEquals("DIV", domNode.childNodes[0]!!.nodeName)
    }

    @Test
    fun render_to_dom() {
        val cmp = TestComponent()
        val doc = document.createDocumentFragment()
        renderToDom(doc, cmp)

        assertEquals(2, doc.querySelectorAll("div").length)
    }

    @Test
    fun render_to_dom_with_stylesheet() {
        val styles = object : StyleSheet() {
            val outer by cssClass()
            val inner by cssClass()

            init {
                outer {
                    width = 200.px
                }
                inner {
                    height = 50.px
                }
            }
        }
        val cmp = object : Component() {
            override fun render() = div(styles.outer) {
                span(styles.inner)
            }
        }
        val doc = document.createDocumentFragment()
        renderToDom(doc, cmp)

        val style = doc.querySelector("style")
        assertNotNull(style)
        val styleContent = style.textContent
        assertNotNull(styleContent)
        assertTrue(styleContent.contains(styles.outer.selector.css))
        assertTrue(styleContent.contains(styles.inner.selector.css))
        assertEquals(1, doc.querySelectorAll(styles.outer.selector.css).length)
        assertEquals(1, doc.querySelectorAll(styles.inner.selector.css).length)
    }

    @Test
    fun components_can_render_nothing() {
        val cmp = object : Component() {
            override fun render(): Node? = null
        }
        render(cmp)
        val domNode = cmp.domNode

        assertNull(domNode)
    }

    @Test
    fun components_can_render_nothing_then_something_then_nothing() {
        var renderNode by observable(false)

        class Inner : Component() {
            override fun render(): Node? = if (renderNode) {
                span()
            } else {
                null
            }
        }

        val cmp = object : Component() {
            override fun render() = div {
                component(::Inner)
            }
        }
        val doc = document.createDocumentFragment()
        renderToDom(doc, cmp)

        assertNull(doc.firstChild!!.firstChild)

        renderNode = true
        assertNotNull(doc.firstChild!!.firstChild)
        assertEquals("SPAN", doc.firstChild!!.firstChild!!.nodeName)

        renderNode = false
        assertNull(doc.firstChild!!.firstChild)
    }

    private class TestComponent : Component() {
        override fun render() = div {
            div { }
        }
    }
}
