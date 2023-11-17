package org.wysko.autogeokt.rinex

import org.wysko.autogeokt.geospatial.Ellipsoid
import org.wysko.autogeokt.geospatial.toGeographic
import org.wysko.autogeokt.rinex.Rinex.SUPPORTED_VERSIONS
import org.wysko.autogeokt.rinex.RinexObservationData.Header.HeaderData.*
import org.wysko.autogeokt.trimLines
import java.io.File
import java.time.ZoneOffset.UTC

/**
 * A RINEX file.
 */
sealed class RinexFile {

    /**
     * A RINEX observation file.
     *
     * @property filename The name of the file.
     * @property fileSize The size of the file in bytes.
     * @property header The header of the file.
     * @property body The body of the file.
     */
    data class ObservationFile(
        val filename: String,
        val fileSize: Long,
        val header: RinexObservationData.Header,
        val body: RinexObservationData.Body,
    ) : RinexFile() {

        /**
         * The number of epochs that are possible to be missing from the file.
         *
         * This is calculated by taking the difference between the number of epochs that should be in the file
         * (calculated by taking the difference in epochs, and the sampling interval), and the number of epochs that are
         * actually in the file.
         *
         * @return The number of possible missing epochs.
         */
        val possibleMissingEpochs by lazy {
            val end = header[TimeOfLastObservation::class]!!.epoch.toDateTime()
            val start =
                header[TimeOfFirstObservation::class]!!.epoch.toDateTime()
            val interval = header[ObservationalInterval::class]!!.interval
            val expectedEpochs = ((end.toEpochSecond(UTC) - start.toEpochSecond(UTC)) / interval).toInt() + 1

            expectedEpochs - body.dataRecords.size
        }

        /**
         * The approximate geographic coordinates of the antenna.
         *
         * @return The approximate geographic coordinates of the antenna.
         */
        val approximateGeographicCoordinates by lazy {
            with(header[ApproximatePosition::class]!!) {
                toCartesian3D().toGeographic(Ellipsoid.WGS84)
            }
        }

        companion object {
            /**
             * Parses the given [file] as a RINEX observation file. This function expects the RINEX version to be
             * one of the [SUPPORTED_VERSIONS].
             *
             * @param file The file to parse.
             * @return The parsed RINEX file.
             * @throws UnknownRinexVersionException If the RINEX version is not supported.
             */
            fun parseFile(file: File): ObservationFile {
                val reader = file.bufferedReader()
                reader.use {
                    val header = RinexObservationData.Header(RinexParser.ObservationFile.parseHeader(reader))
                    val body = RinexObservationData.Body(RinexParser.ObservationFile.parseBody(reader, header))
                    return ObservationFile(
                        filename = file.name,
                        fileSize = file.length(),
                        header = header,
                        body = body,
                    )
                }
            }
        }
    }
}

/**
 * Returns the metadata for a given file, roughly in the style of teqc's `+meta` option.
 *
 * @return The metadata in the form of a formatted string.
 */
@Suppress("MagicNumber")
fun RinexFile.ObservationFile.metadata(): String {
    return """
        filename:                $filename
        file format:             RINEX
        file size (bytes):       $fileSize
        start date & time:       ${header[TimeOfFirstObservation::class]!!.epoch}
        final date & time:       ${header[TimeOfLastObservation::class]!!.epoch}
        sample interval:         ${"%.4f".format(header[ObservationalInterval::class]!!.interval)}
        possible missing epochs: $possibleMissingEpochs
        4-char station code:     ${filename.take(4)}
        station name:            ${header[MarkerName::class]!!.name}
        station ID number:       ${header[MarkerNumber::class]!!.number}
        antenna ID number:       ${header[AntennaInfo::class]!!.number}
        antenna type:            ${header[AntennaInfo::class]!!.type}
        antenna latitude (deg):  ${"%16.9f".format(approximateGeographicCoordinates.latitude.toDegrees())}
        antenna longitude (deg): ${"%16.9f".format(approximateGeographicCoordinates.longitude.toDegrees())}
        antenna elevation (m):   ${"%16.4f".format(approximateGeographicCoordinates.height)}
        antenna height (m):      ${"%.4f".format(header[AntennaOffset::class]!!.deltaH)}
        receiver ID number:      ${header[ReceiverInfo::class]!!.number}
        receiver type:           ${header[ReceiverInfo::class]!!.type}
        receiver firmware:       ${header[ReceiverInfo::class]!!.version}
        RINEX version:           ${header[RinexInfo::class]!!.version}
        RINEX translator:        ${header[ProgramInfo::class]!!.program}
        trans date & time:       ${header[ProgramInfo::class]!!.date}
    """.trimIndent().trimLines()
}
