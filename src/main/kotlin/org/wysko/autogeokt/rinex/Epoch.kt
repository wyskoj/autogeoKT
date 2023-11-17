package org.wysko.autogeokt.rinex

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val NANOSECONDS_IN_SECOND = 1_000_000_000

/**
 * An entity representing a specific moment in the form of date and time components.
 * It consists of year, month, day, hour, minute, and second.
 *
 * @property year The year of the date.
 * @property month The month of the year. January is represented by 1, and December is represented by 12.
 * @property day The day of the month.
 * @property hour The hour of the day, using a 24-hour format.
 * @property minute The minute of the hour.
 * @property second The second of the minute, including fractional seconds.
 */
data class Epoch(
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
    val second: Double,
) : Comparable<Epoch> {

    /**
     * Converts the Moment into a LocalDateTime object.
     *
     * @return A LocalDateTime object representing the same moment.
     */
    fun toDateTime(): LocalDateTime =
        LocalDateTime.of(year, month, day, hour, minute, second.toInt(), (second % 1 * NANOSECONDS_IN_SECOND).toInt())

    override fun compareTo(other: Epoch): Int {
        val thisDateTime = this.toDateTime()
        val otherDateTime = other.toDateTime()

        return when {
            thisDateTime.isBefore(otherDateTime) -> -1
            thisDateTime.isAfter(otherDateTime) -> 1
            else -> 0
        }
    }

    /**
     * Converts the epoch into a formatted string.
     *
     * @return A string representing the epoch in the "yyyy-MM-dd HH:mm:ss.SSS" format.
     */
    override fun toString(): String = this.toDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
}
