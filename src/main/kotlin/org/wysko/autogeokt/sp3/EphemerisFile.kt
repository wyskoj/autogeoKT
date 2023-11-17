package org.wysko.autogeokt.sp3

import org.wysko.autogeokt.geospatial.Cartesian3D
import org.wysko.autogeokt.rinex.Epoch
import org.wysko.autogeokt.rinex.Satellite
import kotlin.reflect.KClass

data class EphemerisFile(
    val header: Header,
    val body: Body,
) {
    data class Header(
        val satellites: List<Satellite>,
        val data: Collection<HeaderData>,
    ) {
        @Suppress("DataClassContainsFunctions")
        inline operator fun <reified T : HeaderData> get(klass: KClass<T>): T? =
            data.firstOrNull { klass.isInstance(it) } as? T

        sealed class HeaderData {
            data class EphemerisStart(val epoch: Epoch) : HeaderData()
        }
    }

    data class Body(
        val timedPositions: List<TimedPosition>,
    ) {
        data class TimedPosition(
            val satellite: Satellite,
            val epoch: Epoch,
            val position: Cartesian3D,
            val clock: Double,
            val positionSdev: Triple<Int, Int, Int>,
            val clockSdev: Double,
        )
    }
}
