package org.wysko.autogeokt

/**
 * Trims leading and trailing whitespace from each line in a multi-line string.
 *
 * @return A new string with each line trimmed.
 */
fun String.trimLines() = lines().joinToString("\n") { it.trim() }

/**
 * Convenience operator function to return the [substring].
 *
 * @param range The substring range.
 */
operator fun String.get(range: IntRange) = substring(range)
