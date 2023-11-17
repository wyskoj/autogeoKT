package org.wysko.autogeokt.visual

import com.jme3.app.SimpleApplication
import com.jme3.system.AppSettings
import org.wysko.autogeokt.sp3.EphemerisParser
import java.io.File

private const val EPHEMERIS_FILE = "src/test/resources/IGS0OPSRAP_20232640000_01D_15M_ORB.SP3"

fun main() {
    with(JmeVisualizer()) {
        isShowSettings = false
        setSettings(
            AppSettings(true).apply {
                width = 1600
                height = 900
            },
        )
        isPauseOnLostFocus = false
        start()
    }
}

/**
 * A simple JME3 application that visualizes the ephemeris file.
 */
class JmeVisualizer : SimpleApplication() {
    override fun simpleInitApp() {
        EphemerisVisualizer(EphemerisParser.parseFile(File(EPHEMERIS_FILE).bufferedReader())).also {
            stateManager.attach(it)
        }
    }
}
