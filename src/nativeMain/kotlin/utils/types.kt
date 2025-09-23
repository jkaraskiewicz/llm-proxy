package utils

sealed class HeaderEntity {
  fun matches(headerName: String): Boolean {
    return when (this) {
      is HeaderPrefix -> generateCaseStrings(headerName).any { it.startsWith(prefix) }
      is Header -> name in generateCaseStrings(headerName)
    }
  }

  private fun generateCaseStrings(value: String): Set<String> {
    val lowerCase = value.lowercase()

    val capitalized = value
      .split('-')
      .joinToString("-") { part ->
        part.lowercase().replaceFirstChar {
          if (it.isLowerCase()) it.titlecase() else it.toString()
        }
      }

    val upperCase = value.uppercase()

    return setOf(lowerCase, capitalized, upperCase)
  }

  data class HeaderPrefix(val prefix: String) : HeaderEntity()

  data class Header(val name: String, val value: String = "") : HeaderEntity()
}

