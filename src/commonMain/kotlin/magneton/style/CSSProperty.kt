package magneton.style

import kotlin.reflect.KProperty

class CSSProperty<T>(
        val name: String,
        val vendorPrefixes: List<String>,
        var value: T?
) {
    @Suppress("UNCHECKED_CAST")
    operator fun getValue(thisRef: CSSStyleDeclaration, property: KProperty<*>): T? =
            thisRef.properties[name]?.value as T?

    operator fun setValue(thisRef: CSSStyleDeclaration, property: KProperty<*>, value: T?) {
        if (this.value != value) {
            this.value = value

            if (value == null) {
                thisRef.properties.remove(name)
            } else {
                thisRef.properties[name] = this
            }

            thisRef.updateCss()
        }
    }

    fun toCss(sb: StringBuilder) {
        sb.append(name).append(": ")
        valueToCss(sb, value)
        sb.append(';')

        for (prefix in vendorPrefixes) {
            sb.append("\n    ")
            sb.append('-').append(prefix).append('-').append(name).append(": ")
            valueToCss(sb, value)
            sb.append(';')
        }
    }

    private fun valueToCss(sb: StringBuilder, value: Any?) {
        when (value) {
            is StylePropertyType -> sb.append(value.css)
            is Number -> sb.append(value)
            is List<*> -> {
                var first = true

                for (v in value) {
                    if (first) {
                        first = false
                    } else {
                        sb.append(", ")
                    }

                    valueToCss(sb, v)
                }
            }
            else -> sb.append('"').append(value).append('"')
        }
    }
}
