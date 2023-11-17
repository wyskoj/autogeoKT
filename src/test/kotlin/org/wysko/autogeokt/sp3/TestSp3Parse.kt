package org.wysko.autogeokt.sp3

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.InputStream

class TestSp3Parse {
    private lateinit var sp3: InputStream

    @BeforeEach
    fun setUp() {
        sp3 = TestSp3Parse::class.java.getResourceAsStream("/IGS0OPSRAP_20232640000_01D_15M_ORB.SP3")!!
    }

    @Test
    fun `Test parse ephemeris`() {
        EphemerisParser.parseFile(
            sp3.bufferedReader(),
        )
    }
}
