package providers

/**
 * Extension property to get the string value of a provider name.
 * This helps bridge the gap during the transition from string-based to enum-based provider names.
 */
val ProviderSpec.nameAsString: String
  get() = name.value

/**
 * Extension function to compare provider names with strings.
 */
fun ProviderSpec.hasName(nameString: String): Boolean {
  return name.value.equals(nameString, ignoreCase = true)
}