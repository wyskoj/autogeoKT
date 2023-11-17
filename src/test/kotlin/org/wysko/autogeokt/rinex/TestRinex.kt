package org.wysko.autogeokt.rinex

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.io.File

class TestRinex {
    @Test
    fun `Test parse observation file`() {
        assertDoesNotThrow {
            RinexFile.ObservationFile.parseFile(File("src/test/resources/19062640.23o"))
        }
    }

    @Test
    fun `Test observation file meta summary 1`() {
        val observationFile = RinexFile.ObservationFile.parseFile(File("src/test/resources/19062640.23o"))
        val expected = """
            filename:                19062640.23o
            file format:             RINEX
            file size (bytes):       1575051
            start date & time:       2023-09-21 18:43:30.000
            final date & time:       2023-09-21 20:52:45.000
            sample interval:         15.0000
            possible missing epochs: 0
            4-char station code:     1906
            station name:            1 coleman
            station ID number:
            antenna ID number:
            antenna type:            TRMR12I         NONE
            antenna latitude (deg):      47.119644652
            antenna longitude (deg):    -88.549911772
            antenna elevation (m):           179.8096
            antenna height (m):      2.0500
            receiver ID number:      6309F01906
            receiver type:           TRIMBLE R12i
            receiver firmware:       6.21
            RINEX version:           2.11
            RINEX translator:        cnvtToRINEX 3.14.0
            trans date & time:       20230925 141910 UTC
        """.trimIndent()

        assertEquals(expected, observationFile.metadata())
    }

    @Test
    fun `Test observation file meta summary 2`() {
        val observationFile = RinexFile.ObservationFile.parseFile(File("src/test/resources/09802640.23o"))
        val expected = """
            filename:                09802640.23o
            file format:             RINEX
            file size (bytes):       1685557
            start date & time:       2023-09-21 18:37:30.000
            final date & time:       2023-09-21 20:48:00.000
            sample interval:         15.0000
            possible missing epochs: 0
            4-char station code:     0980
            station name:            1
            station ID number:
            antenna ID number:
            antenna type:            TRMR12I         NONE
            antenna latitude (deg):      47.119490854
            antenna longitude (deg):    -88.550227866
            antenna elevation (m):           180.2857
            antenna height (m):      0.6596
            receiver ID number:      6311F00980
            receiver type:           TRIMBLE R12i
            receiver firmware:       6.21
            RINEX version:           2.11
            RINEX translator:        cnvtToRINEX 3.14.0
            trans date & time:       20230925 142328 UTC
        """.trimIndent()

        assertEquals(expected, observationFile.metadata())
    }

    @Test
    fun `Test observation file meta summary 3`() {
        val observationFile = RinexFile.ObservationFile.parseFile(File("src/test/resources/18962640.23o"))
        val expected = """
            filename:                18962640.23o
            file format:             RINEX
            file size (bytes):       1716848
            start date & time:       2023-09-21 18:36:30.000
            final date & time:       2023-09-21 20:47:00.000
            sample interval:         15.0000
            possible missing epochs: 0
            4-char station code:     1896
            station name:            seiler pk
            station ID number:
            antenna ID number:
            antenna type:            TRMR12I         NONE
            antenna latitude (deg):      47.117375642
            antenna longitude (deg):    -88.543558742
            antenna elevation (m):           174.9131
            antenna height (m):      2.0500
            receiver ID number:      6309F01896
            receiver type:           TRIMBLE R12i
            receiver firmware:       6.21
            RINEX version:           2.11
            RINEX translator:        cnvtToRINEX 3.14.0
            trans date & time:       20230925 142301 UTC
        """.trimIndent()

        assertEquals(expected, observationFile.metadata())
    }

    @Test
    fun `Test fail parse wrong RINEX type`() {
        val exception = assertThrows<IllegalArgumentException> {
            RinexFile.ObservationFile.parseFile(File("src/test/resources/19062640.23n"))
        }
        assertEquals("RINEX type NAVIGATION DATA is not an observation file", exception.message)
    }
}
