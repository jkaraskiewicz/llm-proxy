package utils

fun Collection<HeaderEntity.Header>.values(): Map<String, String> =
  associate { it.name to it.value }