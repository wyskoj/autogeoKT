package org.wysko.autogeokt.sp3

import org.wysko.autogeokt.geospatial.Cartesian3D
import org.wysko.autogeokt.get
import org.wysko.autogeokt.rinex.Epoch
import org.wysko.autogeokt.rinex.Satellite
import org.wysko.autogeokt.rinex.SatelliteSystem
import org.wysko.autogeokt.sp3.EphemerisFile.Header.HeaderData
import java.io.BufferedReader

object EphemerisParser {

    fun parseFile(reader: BufferedReader): EphemerisFile {
        val header = parseHeader(reader)
        val body = parseBody(reader, header.satellites)
        return EphemerisFile(header, body)
    }

    private fun parseBody(reader: BufferedReader, satellites: List<Satellite>): EphemerisFile.Body {
        val timedPositions = mutableListOf<EphemerisFile.Body.TimedPosition>()
        while (true) {
            val epochLine = reader.readLine()
            if (epochLine.trim() == "EOF") break

            val yearStart = epochLine[3..6].trim().toInt()
            val monthStart = epochLine[8..9].trim().toInt()
            val dayOfMonthStart = epochLine[11..12].trim().toInt()
            val hourStart = epochLine[14..15].trim().toInt()
            val minuteStart = epochLine[17..18].trim().toInt()
            val secondStart = epochLine[20..30].trim().toDouble()
            val epoch = Epoch(yearStart, monthStart, dayOfMonthStart, hourStart, minuteStart, secondStart)

            satellites.forEach {
                val line = reader.readLine()
                check(Satellite.fromSpaceVehicleId(line[1..3]) == it) {
                    "Satellites are not in order or missing?"
                }
                val xyz = Cartesian3D(
                    line[4..17].trim().toDouble(),
                    line[18..31].trim().toDouble(),
                    line[32..45].trim().toDouble(),
                )
                val clockMuS = line[46..59].trim().toDouble()

                val xSdev = line[61..62].trim().ifBlank { "0" }.toInt()
                val ySdev = line[64..65].trim().ifBlank { "0" }.toInt()
                val zSdev = line[67..68].trim().ifBlank { "0" }.toInt()
                val cSdev = line[70..72].trim().ifBlank { "0" }.toInt()

                timedPositions += EphemerisFile.Body.TimedPosition(
                    it,
                    epoch,
                    xyz,
                    clockMuS,
                    Triple(xSdev, ySdev, zSdev),
                    cSdev.toDouble(),
                )
            }
        }

        return EphemerisFile.Body(timedPositions)
    }

    private fun readEpoch(line: String): Epoch {
        val yearStart = line[3..6].trim().toInt()
        val monthStart = line[8..9].trim().toInt()
        val dayOfMonthStart = line[11..12].trim().toInt()
        val hourStart = line[14..15].trim().toInt()
        val minuteStart = line[17..18].trim().toInt()
        val secondStart = line[20..30].trim().toDouble()
        return Epoch(yearStart, monthStart, dayOfMonthStart, hourStart, minuteStart, secondStart)
    }

    @Suppress("MagicNumber")
    private fun parseHeader(reader: BufferedReader): EphemerisFile.Header {
        var header = mutableListOf<HeaderData>()

        // Line 1
        val line1 = reader.readLine()

        val versionSymbol = line1[0..1].also {
            require(it == "#c") {
                "The SP3 ephemeris parser only supports version \"c\" files."
            }
        }
        val posOrVelFlag = line1[2].also {
            require(it == 'P') {
                "The SP3 ephemeris parser only supports \"position\" (V) files."
            }
        }

        val epoch = readEpoch(line1).also { header += HeaderData.EphemerisStart(it) }
        val epochCount = line1[32..38].trim().toInt()
        val dataUsed = line1[40..44].trim()
        val coordinateSystem = line1[46..50].trim()
        val orbitType = line1[52..54].trim()
        val agency = line1[56..59].trim()

        // Line 2
        val line2 = reader.readLine()
        val gpsWeek = line2[3..6].trim().toInt()
        val gpsSecondsOfWeek = line2[8..22].trim().toDouble()
        val epochInterval = line2[24..37].trim().toDouble()
        val modJulDayStart = line2[39..43].trim().toInt()
        val fractionOfDayStart = line2[45..59].trim().toDouble()

        // Line 3
        val line3 = reader.readLine()
        val satelliteIds = mutableListOf<String>()
        val satelliteCount = line3[4..5].trim().toInt()

        // Lines 3-7
        for (i in 3..7) {
            val line = if (i == 3) line3 else reader.readLine()
            satelliteIds += line[9..59].chunked(3)
        }

        val satellites = satelliteIds.mapNotNull {
            val vehicleNumber = it[1..2].trim().toInt()
            if (vehicleNumber == 0) null else Satellite(SatelliteSystem.fromChar(it[0]), vehicleNumber)
        }

        // Lines 8-12
        val allAccuracies = run {
            val list = mutableListOf<Int>()
            for (i in 8..12) {
                val line = reader.readLine()
                val accuracies = line[9..59].chunked(3)
                list += accuracies.map { it.trim().toInt() }
            }
            list.take(satelliteCount)
        }
        val satelliteAccuracies = satellites.zip(allAccuracies).toMap()

        // Line 13
        val line13 = reader.readLine()

        val fileType = line13[3..4].trim()
        val timeSystem = line13[9..11]

        // Line 14
        reader.readLine()

        // Line 15
        val line15 = reader.readLine()
        val baseForPosMillimeters = line15[3..12].trim().toDouble()
        val baseForClk = line15[14..25].trim().toDouble()

        // Lines 16-22
        for (i in 16..22) {
            reader.readLine()
        }

        return EphemerisFile.Header(
            satellites,
            header,
        )
    }
}
