package org.wysko.autogeokt.operation

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.wysko.autogeokt.geospatial.DegreesMinutesSeconds
import org.wysko.autogeokt.geospatial.Ellipsoid
import org.wysko.autogeokt.gui.form.FormDetails
import org.wysko.autogeokt.gui.form.Formable
import org.wysko.autogeokt.gui.form.components.FormField
import org.wysko.autogeokt.gui.form.components.InputData
import org.wysko.autogeokt.serialization.OptionalSerializer
import java.util.*
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.reflect.KProperty

/**
 * Computes the radii of curvature of the ellipsoid at a given latitude and azimuth.
 */
@Serializable
class Radii(
    override val data: RadiiData,
) : Operation<RadiiData, RadiiResult>() {

    override val result: RadiiResult by lazy {
        val radiusMeridian = radiusInMeridian()
        val radiusPrimeVertical = radiusPrimeVertical(data.ellipsoid, data.latitude)
        val radiusAzimuth = radiusAzimuth(radiusMeridian, radiusPrimeVertical)
        RadiiResult(radiusPrimeVertical, radiusMeridian, radiusAzimuth)
    }

    @Suppress("MagicNumber")
    private fun radiusInMeridian(): Double =
        data.ellipsoid.a * (1 - data.ellipsoid.eccentricitySquared) / (
            1 - data.ellipsoid.eccentricitySquared * sin(
                data.latitude.toRadians(),
            ).pow(2)
            ).pow(1.5)

    private fun radiusAzimuth(radiusMeridian: Double, radiusPrimeVertical: Double): Optional<Double> =
        data.azimuth.map { azimuth ->
            1 / (
                cos(azimuth.toRadians()).pow(2) / radiusMeridian + sin(azimuth.toRadians()).pow(2) /
                    radiusPrimeVertical
                )
        }

    companion object : Formable {
        override val form = FormDetails(
            formContents = listOf(
                FormField.EllipsoidInput(
                    InputData(
                        name = "ellipsoid",
                        title = "Ellipsoid",
                        description = "Select the ellipsoid to use for the calculation.",
                    ),
                ),
                FormField.DegreesMinutesSecondsInput(
                    InputData(
                        name = "latitude",
                        title = "Latitude",
                        description = "Enter the latitude at which to calculate the radii of curvature.",
                    ),
                ),
                FormField.DegreesMinutesSecondsInput(
                    InputData(
                        name = "azimuth",
                        title = "Azimuth",
                        description = "Enter the azimuth at which to calculate the radii of curvature at an azimuth.",
                        isOptional = true,
                    ),
                ),
            ),
            toData = { data ->
                RadiiData(
                    ellipsoid = data["ellipsoid"] as Ellipsoid,
                    latitude = data["latitude"] as DegreesMinutesSeconds,
                    azimuth = Optional.ofNullable(data["azimuth"] as? DegreesMinutesSeconds?),
                )
            },
            canBeTemporary = true,
            operationDetails = OPERATION_DETAILS.getValue(Radii::class),
        )

        /**
         * Computes the radius in the prime vertical.
         *
         * @param ellipsoid The [Ellipsoid].
         * @param latitude [DegreesMinutesSeconds] of the latitude
         */
        fun radiusPrimeVertical(ellipsoid: Ellipsoid, latitude: DegreesMinutesSeconds): Double =
            ellipsoid.a / sqrt(1 - ellipsoid.eccentricitySquared * sin(latitude.toRadians()).pow(2))
    }
}

/**
 * @property ellipsoid Ellipsoid to use for the calculation.
 * @property latitude Latitude at which to calculate the radii of curvature.
 * @property azimuth Azimuth at which to calculate the radii of curvature at an azimuth, if any.
 */
@Serializable
data class RadiiData
@OptIn(ExperimentalSerializationApi::class)
constructor(
    @PropertyTitle("Ellipsoid")
    val ellipsoid: Ellipsoid,
    @PropertyTitle("Latitude")
    val latitude: DegreesMinutesSeconds,
    @PropertyTitle("Azimuth")
    @Serializable(with = OptionalSerializer::class)
    val azimuth: Optional<DegreesMinutesSeconds>,
) : OperationData() {

    @Transient
    override val propertyOrder: List<KProperty<*>> = listOf(
        RadiiData::ellipsoid,
        RadiiData::latitude,
        RadiiData::azimuth,
    )
}

/**
 * @property radiusPrimeVertical Radius of curvature in the prime vertical.
 * @property radiusMeridian Radius of curvature in the meridian.
 * @property radiusAzimuth Radius of curvature at a given azimuth, if any.
 */
@Serializable
data class RadiiResult
@OptIn(ExperimentalSerializationApi::class)
constructor(
    @PropertyTitle("Radius of Curvature in the Prime Vertical")
    val radiusPrimeVertical: Double,
    @PropertyTitle("Radius of Curvature in the Meridian")
    val radiusMeridian: Double,
    @PropertyTitle("Radius of Curvature at an Azimuth")
    @Serializable(with = OptionalSerializer::class)
    val radiusAzimuth: Optional<Double>,
) : OperationResult() {
    @Transient
    override val propertyOrder: List<KProperty<*>> = listOf(
        RadiiResult::radiusPrimeVertical,
        RadiiResult::radiusMeridian,
        RadiiResult::radiusAzimuth,
    )
}
