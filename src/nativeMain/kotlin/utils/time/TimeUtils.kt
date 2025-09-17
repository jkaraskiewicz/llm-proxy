package utils.time

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
object TimeUtils {
  fun currentTimeInMillis(): Long = Clock.System.now().toEpochMilliseconds()
}
