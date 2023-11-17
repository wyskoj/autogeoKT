package org.wysko.autogeokt.rinex

import org.wysko.autogeokt.geospatial.Cartesian3D
import org.wysko.autogeokt.rinex.RinexObservationData.Header.HeaderData.ObservationInfo.ObservationType
import kotlin.reflect.KClass

private const val MIN_SIGNAL_STRENGTH = 1
private const val MAX_SIGNAL_STRENGTH = 9

/**
 * Corresponds to the "Observation Data File" type, as specified in the RINEX specification.
 */
object RinexObservationData {

    /**
     * The header section of an observation file contains a list of header data records. Each record
     * contains a single piece of information about the file.
     *
     * @property headerData The header data records.
     */
    data class Header(
        val headerData: List<HeaderData>,
    ) {

        /**
         * Inline function that takes a KClass as input, checks if an instance of it
         * exists in the [headerData] list, and returns it.
         *
         * @param T The type of the class to get.
         * @param klass The class to check for an instance of among the [headerData].
         * @return An instance of the passed class from [headerData], or null if no such instance exists.
         */
        @Suppress("DataClassContainsFunctions")
        inline operator fun <reified T : HeaderData> get(klass: KClass<T>): T? =
            headerData.firstOrNull { klass.isInstance(it) } as? T

        /**
         * A type of header data record.
         */
        sealed class HeaderData {

            /**
             * Information relating to the RINEX version and satellite system.
             *
             * @property version The RINEX format version.
             * @property satelliteSystem The satellite system.
             */
            data class RinexInfo(
                val version: String,
                val satelliteSystem: SatelliteSystem,
            ) : HeaderData()

            /**
             * A comment line.
             *
             * @property comment The text content of the comment.
             */
            data class Comment(
                val comment: String,
            ) : HeaderData()

            /**
             * Information about the program that created the file.
             *
             * @property program The name of the program that created the file.
             * @property agency The name of the agency that created the file.
             * @property date The date the file was created.
             */
            data class ProgramInfo(
                val program: String,
                val agency: String,
                val date: String,
            ) : HeaderData()

            /**
             * Name information about the antenna marker.
             *
             * @property name The name of the antenna marker.
             */
            data class MarkerName(
                val name: String,
            ) : HeaderData()

            /**
             * Number information about the antenna marker.
             *
             * @property number The number of the antenna marker.
             */
            data class MarkerNumber(
                val number: String,
            ) : HeaderData()

            /**
             * Information about the observer and related agency.
             *
             * @property observer The name of the observer.
             * @property agency The name of the agency.
             */
            data class ObserverInfo(
                val observer: String,
                val agency: String,
            ) : HeaderData()

            /**
             * Information about the receiver.
             *
             * @property number The receiver number.
             * @property type The receiver type.
             * @property version The receiver version.
             */
            data class ReceiverInfo(
                val number: String,
                val type: String,
                val version: String,
            ) : HeaderData()

            /**
             * Information about the antenna.
             *
             * @property number The antenna number.
             * @property type The antenna type.
             */
            data class AntennaInfo(
                val number: String,
                val type: String,
            ) : HeaderData()

            /**
             * Approximate position of the antenna marker, in WGS84 geocentric cartesian coordinates. All units are in
             * meters.
             *
             * @property x The x coordinate.
             * @property y The y coordinate.
             * @property z The z coordinate.
             */
            data class ApproximatePosition(
                val x: Double,
                val y: Double,
                val z: Double,
            ) : HeaderData() {
                /**
                 * Converts the approximate position to a [Cartesian3D].
                 *
                 * @return The [Cartesian3D] representation of the approximate position.
                 */
                fun toCartesian3D(): Cartesian3D = Cartesian3D(x, y, z)
            }

            /**
             * The height of the antenna's bottom surface above the marker, and any eccentricities east and north. All
             * units are in meters.
             *
             * @property deltaH The height of the antenna's bottom surface above the marker.
             * @property deltaE The eccentricity east.
             * @property deltaN The eccentricity north.
             */
            data class AntennaOffset(
                val deltaH: Double,
                val deltaE: Double,
                val deltaN: Double,
            ) : HeaderData()

            /**
             * For GPS only, the wavelength factors for the L1 and L2 carrier frequencies.
             *
             * This record is optional for GPS and obsolete for other satellite systems. Wavelength factors default
             * to full cycle ambiguities.
             *
             * @property l1 The wavelength factor for the L1 carrier frequency.
             * @property l2 The wavelength factor for the L2 carrier frequency.
             * @property prns The PRNs of the satellites for which the wavelength factors apply.
             */
            data class GPSWavelengthFactors(
                val l1: WavelengthFactor,
                val l2: WavelengthFactor,
                val prns: List<Satellite>,
            ) : HeaderData() {
                /**
                 * A wavelength factor for a GPS carrier frequency.
                 */
                sealed class WavelengthFactor {
                    /** Full cycle ambiguities. */
                    data object FullCycleAmbiguities : WavelengthFactor()

                    /** Half cycle ambiguities. */
                    data object HalfCycleAmbiguities : WavelengthFactor()

                    /** Single frequency instrument. */
                    data object SingleFrequencyInstrument : WavelengthFactor()

                    companion object {
                        /**
                         * Converts a RINEX wavelength factor integer to a [WavelengthFactor].
                         *
                         * @param value The RINEX wavelength factor integer.
                         * @return The corresponding [WavelengthFactor].
                         */
                        fun fromInt(value: Int): WavelengthFactor {
                            return when (value) {
                                0 -> FullCycleAmbiguities
                                1 -> HalfCycleAmbiguities
                                2 -> SingleFrequencyInstrument
                                else -> throw IllegalArgumentException("Unknown wavelength factor $value")
                            }
                        }
                    }
                }
            }

            /**
             * Information about the observation types.
             *
             * @property types The observation types.
             */
            data class ObservationInfo(
                val types: List<ObservationType>,
            ) : HeaderData() {
                /**
                 * An observation type, like L1 or L2.
                 *
                 * @property observationCode The observation code.
                 * @property frequencyCode The frequency code.
                 */
                data class ObservationType(
                    val observationCode: ObservationCode,
                    val frequencyCode: Int,
                ) {
                    @Suppress("MagicNumber")
                    private val validFrequencyCodes = listOf(1, 2, 5, 6, 7, 8)

                    init {
                        check(
                            frequencyCode in validFrequencyCodes,
                        ) { "Frequency code $frequencyCode must be one of ${validFrequencyCodes.joinToString()}" }
                    }

                    /**
                     * An observation code.
                     *
                     * @property code The code.
                     */
                    @Suppress("unused")
                    sealed class ObservationCode(val code: String) {
                        /** Pseudorange: C/A, L2C for GPS; C/A for Glonass; All for Galileo. The units are meters. */
                        data object Pseudorange : ObservationCode("C")

                        /** Pseudorange: P code for GPS and Glonass. The units are meters. */
                        data object PCode : ObservationCode("P")

                        /** Carrier phase. The units are full cycles. */
                        data object CarrierPhase : ObservationCode("L")

                        /** Doppler frequency. The units are hertz. */
                        data object DopplerFrequency : ObservationCode("D")

                        /**
                         * Raw signal strengths or SNR values as given by the receiver for the respective phase
                         * observations. The units are receiver-dependent.
                         */
                        data object RawSignalStrength : ObservationCode("S")

                        companion object {
                            /**
                             * Converts a RINEX observation code to an [ObservationCode].
                             *
                             * @param code The RINEX observation code.
                             * @return The corresponding [ObservationCode].
                             */
                            fun fromString(code: String): ObservationCode =
                                ObservationCode::class.sealedSubclasses.map { it.objectInstance!! }
                                    .first { it.code == code }
                        }
                    }
                }
            }

            /**
             * Information about the interval between observations.
             *
             * @property interval The interval between observations.
             */
            data class ObservationalInterval(
                val interval: Double,
            ) : HeaderData()

            /**
             * Information about the time of the first observation.
             *
             * @property epoch The epoch.
             * @property system The time system.
             */
            data class TimeOfFirstObservation(
                val epoch: Epoch,
                val system: TimeSystem,
            ) : HeaderData()

            /**
             * Information about the time of the last observation.
             *
             * @property epoch The epoch.
             * @property system The time system.
             */
            data class TimeOfLastObservation(
                val epoch: Epoch,
                val system: TimeSystem,
            ) : HeaderData()

            /**
             * Epoch, code, and phase are corrected by applying the realtime-derived receiver clock offset.
             *
             * @property offset `true` if the offset is applied, `false` otherwise.
             */
            data class ReceiverClockOffset(
                val offset: Boolean,
            ) : HeaderData()

            /**
             * Information about the leap seconds.
             *
             * @property seconds The number of leap seconds since 6 January 1980.
             */
            data class LeapSeconds(
                val seconds: Int,
            ) : HeaderData()

            /**
             * Information about the number of satellites.
             *
             * @property count The number of satellites.
             */
            data class SatelliteCount(
                val count: Int,
            ) : HeaderData()

            /**
             * Information about the satellites.
             *
             * @property satelliteCode The satellite code.
             * @property satelliteNumber The satellite number.
             * @property observationFrequencies Each value in this list corresponds to an observation type in the
             * [ObservationInfo] header record. The value is the number of observations for that type of observation.
             */
            data class SatelliteInformation(
                val satelliteCode: String,
                val satelliteNumber: String,
                val observationFrequencies: List<Int>,
            ) : HeaderData()

            /**
             * RINEX header lines that aren't supported by this parser.
             *
             * @property data The data.
             * @property label The label.
             */
            data class Unknown(
                val data: String,
                val label: String,
            ) : HeaderData()
        }
    }

    /**
     * The body section of an observation file contains a list of observation data records. Each record
     * contains the observations for a single epoch.
     *
     * @property epoch The epoch.
     * @property epochFlag The epoch flag.
     * @property satelliteCount The number of satellites in the current epoch.
     * @property prns The PRNs of the satellites in the current epoch.
     * @property receiverClockOffset The receiver clock offset. This is optional; if it is not present, the value is
     * `0.0`.
     * @property observations The observations for the current epoch.
     */
    data class ObservationDataRecord(
        val epoch: Epoch,
        val epochFlag: EpochFlag,
        val satelliteCount: Int,
        val prns: List<Satellite>,
        val receiverClockOffset: Double,
        val observations: List<SatelliteObservation>,
    ) {
        /**
         * A single observation for a single satellite.
         *
         * @property satellite The satellite.
         * @property observationType The type of observation, e.g., L1 or L2.
         * @property observation The observation value.
         * @property lossOfLockIndication The loss of lock indication.
         * @property signalStrength The signal strength.
         */
        data class SatelliteObservation(
            val satellite: Satellite,
            val observationType: ObservationType,
            val observation: Double,
            val lossOfLockIndication: LossOfLockIndication,
            val signalStrength: SignalStrength,
        )

        /**
         * Represents different flags associated with an epoch.
         *
         * @property flag The integer value associated with the epoch flag.
         */
        @Suppress("unused", "MagicNumber")
        sealed class EpochFlag(val flag: Int) {
            /** The data object is OK. */
            data object OK : EpochFlag(0)

            /** There was a power failure between the previous and current epoch. */
            data object PowerFailure : EpochFlag(1)

            /** The antenna is starting to move. */
            data object StartMovingAntenna : EpochFlag(2)

            /** The receiver is starting to record new site occupation. */
            data object NewSiteOccupation : EpochFlag(3)

            /** Header info. */
            data object HeaderInfo : EpochFlag(4)

            /** External event. */
            data object ExternalEvent : EpochFlag(5)

            companion object {
                /**
                 * Converts a RINEX epoch flag integer to an [EpochFlag].
                 *
                 * @param flag The RINEX epoch flag integer.
                 * @return The corresponding [EpochFlag].
                 */
                fun fromInt(flag: Int): EpochFlag =
                    EpochFlag::class.sealedSubclasses.map { it.objectInstance!! }.first { it.flag == flag }
            }
        }

        /**
         * Indicates any loss of lock during this observation.
         *
         * @property lossOfLock `true` if there was a loss of lock, `false` otherwise.
         * @property oppositeWavelengthFactor `true` if the observation is under the opposite wavelength factor,
         * `false` otherwise.
         * @property observationUnderAntispoofing `true` if the observation is under anti-spoofing (may suffer from
         * increased noise), `false` otherwise.
         */
        data class LossOfLockIndication(
            val lossOfLock: Boolean,
            val oppositeWavelengthFactor: Boolean,
            val observationUnderAntispoofing: Boolean,
        ) {

            companion object {
                /**
                 * Converts a RINEX loss of lock indication string to a [LossOfLockIndication].
                 *
                 * @param value The RINEX loss of lock indication string.
                 * @return The corresponding [LossOfLockIndication].
                 */
                fun fromString(value: String): LossOfLockIndication {
                    if (value.isBlank()) {
                        return LossOfLockIndication(
                            lossOfLock = false,
                            oppositeWavelengthFactor = false,
                            observationUnderAntispoofing = false,
                        )
                    }
                    val int = value.toInt()
                    return LossOfLockIndication(
                        lossOfLock = int and 0b1 == 0b1,
                        oppositeWavelengthFactor = int and 0b10 == 0b10,
                        observationUnderAntispoofing = int and 0b100 == 0b100,
                    )
                }
            }
        }

        /**
         * The signal strength of the observation.
         */
        sealed class SignalStrength {
            /**
             * The signal strength is given as a ratio of the maximum possible signal strength.
             *
             * @property value The signal strength, from 1 to 9.
             */
            data class IntervalProjection(val value: Int) : SignalStrength() {
                init {
                    check(value in MIN_SIGNAL_STRENGTH..MAX_SIGNAL_STRENGTH) {
                        "Signal strength $value must be between 1 and 9"
                    }
                }
            }

            /**
             * The signal strength is either not known, or we don't care about it.
             */
            data object NotKnownDontCare : SignalStrength()
        }
    }

    /**
     * The data section of an observation file contains a list of observation data records.
     *
     * @property dataRecords The observation data records.
     */
    data class Body(val dataRecords: List<ObservationDataRecord>)
}
