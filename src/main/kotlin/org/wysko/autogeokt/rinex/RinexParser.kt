package org.wysko.autogeokt.rinex

import org.wysko.autogeokt.rinex.RinexObservationData.Header.HeaderData
import org.wysko.autogeokt.rinex.RinexObservationData.Header.HeaderData.GPSWavelengthFactors
import org.wysko.autogeokt.rinex.RinexObservationData.ObservationDataRecord
import java.io.BufferedReader
import kotlin.reflect.KClass

private const val UNEXPECTED_END_OF_FILE = "Unexpected end of file"

/**
 * A parser for RINEX files.
 */
@Suppress("MagicNumber")
object RinexParser {

    /**
     * A parser for RINEX observation files.
     */
    @Suppress("TooManyFunctions")
    object ObservationFile {

        /**
         * Parses the header of a RINEX observation file.
         */
        internal fun parseHeader(reader: BufferedReader): List<HeaderData> {
            val list = mutableListOf<HeaderData>()
            while (true) {
                val line = reader.readLine() ?: break
                val data = when (val headerLabel = line.substring(60..<80).trim()) {
                    "RINEX VERSION / TYPE" -> parseRinexVersionAndType(line)
                    "PGM / RUN BY / DATE" -> parsePgmRunByDate(line)
                    "COMMENT" -> parseComment(line)
                    "MARKER NAME" -> parseMarkerName(line)
                    "MARKER NUMBER" -> parseMarkerNumber(line)
                    "OBSERVER / AGENCY" -> parseObserverAgency(line)
                    "REC # / TYPE / VERS" -> parseReceiver(line)
                    "ANT # / TYPE" -> parseAntenna(line)
                    "APPROX POSITION XYZ" -> parseApproxPosition(line)
                    "ANTENNA: DELTA H/E/N" -> parseAntennaDelta(line)
                    "WAVELENGTH FACT L1/2" -> parseWavelengthFactor(line)
                    "# / TYPES OF OBSERV" -> parseObservationTypes(line, reader)
                    "INTERVAL" -> parseInterval(line)
                    "TIME OF FIRST OBS" -> parseTimeOfFirstObs(line)
                    "TIME OF LAST OBS" -> parseTimeOfLastObs(line)
                    "RCV CLOCK OFFS APPL" -> parseReceiverClockOffset(line)
                    "LEAP SECONDS" -> parseLeapSeconds(line)
                    "# OF SATELLITES" -> parseNumberOfSatellites(line)
                    "PRN / # OF OBS" -> parsePseduorandomObservationCount(line)
                    "END OF HEADER" -> null
                    else -> HeaderData.Unknown(line.substring(0..<60).trim(), headerLabel)
                }
                data?.let {
                    list.add(it)
                } ?: break
            }

            // Non-optionals
            check(
                list.containsEach(
                    HeaderData.RinexInfo::class,
                    HeaderData.ProgramInfo::class,
                    HeaderData.MarkerName::class,
                    HeaderData.ObserverInfo::class,
                    HeaderData.ReceiverInfo::class,
                    HeaderData.AntennaInfo::class,
                    HeaderData.ApproximatePosition::class,
                    HeaderData.AntennaOffset::class,
                    HeaderData.ObservationInfo::class,
                    HeaderData.TimeOfFirstObservation::class,
                ),
            ) {
                "RINEX observation file header is missing required fields"
            }

            return list
        }

        private fun parseRinexVersionAndType(line: String): HeaderData.RinexInfo {
            val version = line.substring(0..<20).trim()
            val type = line.substring(20..<40).trim()

            require(Rinex.SUPPORTED_VERSIONS.contains(version)) {
                "RINEX version $version is not supported"
            }

            require(type.first() == 'O') {
                "RINEX type $type is not an observation file"
            }

            val satelliteSystem = line.substring(40..<60).trim().let {
                SatelliteSystem.fromChar(it.first())
            }

            return HeaderData.RinexInfo(version, satelliteSystem)
        }

        private fun parseComment(line: String): HeaderData.Comment = HeaderData.Comment(line.substring(0..<60).trim())

        private fun parsePgmRunByDate(line: String): HeaderData.ProgramInfo {
            val program = line.substring(0..<20).trim()
            val agency = line.substring(20..<40).trim()
            val date = line.substring(40..<60).trim()

            return HeaderData.ProgramInfo(program, agency, date)
        }

        private fun parseMarkerName(line: String): HeaderData.MarkerName =
            HeaderData.MarkerName(line.substring(0..<60).trim())

        private fun parseMarkerNumber(line: String): HeaderData.MarkerNumber =
            HeaderData.MarkerNumber(line.substring(0..<20).trim())

        private fun parseObserverAgency(line: String): HeaderData.ObserverInfo {
            val observer = line.substring(0..<20).trim()
            val agency = line.substring(20..<60).trim()

            return HeaderData.ObserverInfo(observer, agency)
        }

        private fun parseReceiver(line: String): HeaderData.ReceiverInfo {
            val number = line.substring(0..<20).trim()
            val type = line.substring(20..<40).trim()
            val version = line.substring(40..<60).trim()

            return HeaderData.ReceiverInfo(number, type, version)
        }

        private fun parseAntenna(line: String): HeaderData.AntennaInfo {
            val number = line.substring(0..<20).trim()
            val type = line.substring(20..<40).trim()

            return HeaderData.AntennaInfo(number, type)
        }

        private fun parseApproxPosition(line: String): HeaderData.ApproximatePosition {
            val x = line.substring(0..<14).trim().toDouble()
            val y = line.substring(14..<28).trim().toDouble()
            val z = line.substring(28..<42).trim().toDouble()

            return HeaderData.ApproximatePosition(x, y, z)
        }

        private fun parseAntennaDelta(line: String): HeaderData.AntennaOffset {
            val h = line.substring(0..<14).trim().toDouble()
            val e = line.substring(14..<28).trim().toDouble()
            val n = line.substring(28..<42).trim().toDouble()

            return HeaderData.AntennaOffset(h, e, n)
        }

        private fun parseWavelengthFactor(line: String): GPSWavelengthFactors {
            val l1 =
                GPSWavelengthFactors.WavelengthFactor.fromInt(
                    line.substring(0..<6).trim().toInt().also {
                        require(it != 0) { "L1 wavelength factor cannot be 0 (single frequency)" }
                    },
                )
            val l2 = GPSWavelengthFactors.WavelengthFactor.fromInt(line.substring(6..<12).trim().toInt())
            val prnCount = line.substring(12..<18).trim().toInt()
            val prns = mutableListOf<Satellite>()
            repeat(prnCount) {
                val code = line[21 + it * 6]
                val number = line.substring(22 + it * 6..23 + it * 6).toInt()
                prns += Satellite(SatelliteSystem.fromChar(code), number)
            }

            return GPSWavelengthFactors(l1, l2, prns)
        }

        private fun parseObservationTypes(line: String, reader: BufferedReader): HeaderData.ObservationInfo {
            val numTypes = line.substring(0..<6).trim().toInt()
            val types = line.substring(6..<60).chunked(6).map { it.trim() }.toMutableList()
            val continuationLines = (numTypes - 1) / 9
            repeat(continuationLines) {
                val nextLine = reader.readLine() ?: error(UNEXPECTED_END_OF_FILE)
                types += nextLine.substring(6..<60).chunked(6).map { it.trim() }
            }

            return HeaderData.ObservationInfo(
                types.map {
                    HeaderData.ObservationInfo.ObservationType(
                        HeaderData.ObservationInfo.ObservationType.ObservationCode.fromString(it[0].toString()),
                        it[1].toString().toInt(),
                    )
                },
            )
        }

        private fun parseInterval(line: String): HeaderData.ObservationalInterval =
            HeaderData.ObservationalInterval(line.substring(0..<10).trim().toDouble())

        private fun parseTime(line: String): Pair<Epoch, String> {
            val year = line.substring(0..<6).trim().toInt()
            val month = line.substring(6..<12).trim().toInt()
            val day = line.substring(12..<18).trim().toInt()
            val hour = line.substring(18..<24).trim().toInt()
            val minute = line.substring(24..<30).trim().toInt()
            val second = line.substring(30..<43).trim().toDouble()
            val system = line.substring(48..<51).trim()

            return Pair(
                Epoch(
                    year = year,
                    month = month,
                    day = day,
                    hour = hour,
                    minute = minute,
                    second = second,
                ),
                system,
            )
        }

        private fun parseTimeOfFirstObs(line: String): HeaderData.TimeOfFirstObservation {
            val (epoch, system) = parseTime(line)
            return HeaderData.TimeOfFirstObservation(epoch, TimeSystem.fromString(system))
        }

        private fun parseTimeOfLastObs(line: String): HeaderData.TimeOfLastObservation {
            val (epoch, system) = parseTime(line)
            return HeaderData.TimeOfLastObservation(epoch, TimeSystem.fromString(system))
        }

        private fun parseReceiverClockOffset(line: String): HeaderData.ReceiverClockOffset =
            HeaderData.ReceiverClockOffset(line.substring(0..<6).trim().toInt() == 1)

        private fun parseLeapSeconds(line: String): HeaderData.LeapSeconds =
            HeaderData.LeapSeconds(line.substring(0..<6).trim().toInt())

        private fun parseNumberOfSatellites(line: String): HeaderData.SatelliteCount =
            HeaderData.SatelliteCount(line.substring(0..<6).trim().toInt())

        private fun parsePseduorandomObservationCount(line: String): HeaderData.SatelliteInformation {
            val satelliteCode = line[3].toString()
            val satelliteNumber = line.substring(4..5)
            val values = line.substring(6..<60).chunked(6).map { it.trim().toInt() }

            return HeaderData.SatelliteInformation(satelliteCode, satelliteNumber, values)
        }

        fun parseBody(
            reader: BufferedReader,
            header: RinexObservationData.Header,
        ): List<ObservationDataRecord> {
            val dataRecords = mutableListOf<ObservationDataRecord>()
            while (true) {
                var line = reader.readLine() ?: break
                val epoch = Epoch(
                    year = line.substring(1..2).trim().toInt(),
                    month = line.substring(4..5).trim().toInt(),
                    day = line.substring(7..8).trim().toInt(),
                    hour = line.substring(10..11).trim().toInt(),
                    minute = line.substring(13..14).trim().toInt(),
                    second = line.substring(15..26).trim().toDouble(),
                )
                val flag = ObservationDataRecord.EpochFlag.fromInt(line[28].toString().toInt())
                val receiverClockOffset = try {
                    line.substring(68..<80).trim().toDouble()
                } catch (e: Throwable) {
                    0.0
                }

                val satelliteCount = line.substring(30..31).trim().toInt()
                val satellites = line.substring(32).chunked(3).map {
                    Satellite(SatelliteSystem.fromChar(it.first()), it.substring(1).toInt())
                }.toMutableList()
                val continuationLines = (satelliteCount - 1) / 12

                repeat(continuationLines) {
                    line = reader.readLine() ?: error(UNEXPECTED_END_OF_FILE)
                    satellites += line.substring(32).chunked(3).map {
                        Satellite(SatelliteSystem.fromChar(it.first()), it.substring(1).toInt())
                    }
                }

                val observationTypes =
                    (header.headerData.first { it is HeaderData.ObservationInfo } as HeaderData.ObservationInfo).types.toList()
                val observationTypeCount = observationTypes.size
                val observationContinuationLines = (observationTypeCount - 1) / 5

                val observations = mutableListOf<ObservationDataRecord.SatelliteObservation>()

                repeat(satelliteCount) { satelliteIndex ->
                    var observationRecord = 0
                    repeat(1 + observationContinuationLines) {
                        line = (reader.readLine() ?: error(UNEXPECTED_END_OF_FILE)).padEnd(80, ' ')
                        var readThisLine = 0
                        while (observationRecord < observationTypeCount && readThisLine++ < 5) {
                            val offset = observationRecord % 5
                            val observation = line.substring(offset * 16..<offset * 16 + 14).trim().run {
                                if (isBlank()) 0.0 else toDouble()
                            }
                            val lli =
                                ObservationDataRecord.LossOfLockIndication.fromString(line[offset * 16 + 14].toString())
                            val signalStrength = line[offset * 16 + 15].toString().run {
                                if (isBlank()) {
                                    ObservationDataRecord.SignalStrength.NotKnownDontCare
                                } else {
                                    ObservationDataRecord.SignalStrength.IntervalProjection(
                                        toInt(),
                                    )
                                }
                            }
                            observations += ObservationDataRecord.SatelliteObservation(
                                satellites[satelliteIndex],
                                observationTypes[observationRecord],
                                observation,
                                lli,
                                signalStrength,
                            )
                            observationRecord++
                        }
                    }
                }

                dataRecords += ObservationDataRecord(
                    epoch,
                    flag,
                    satelliteCount,
                    satellites,
                    receiverClockOffset,
                    observations,
                )
            }

            return dataRecords
        }
    }
}

/**
 * Checks if all the given classes are present in the list of [HeaderData].
 *
 * @param classes The classes to check for in the list.
 * @return `true` if all the classes are found in the list, otherwise `false`.
 */
fun List<HeaderData>.containsEach(vararg classes: KClass<out HeaderData>): Boolean =
    classes.all { this.any { headerData -> it.isInstance(headerData) } }
