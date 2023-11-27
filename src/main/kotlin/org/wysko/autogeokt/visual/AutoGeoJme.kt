package org.wysko.autogeokt.visual

import com.jme3.app.SimpleApplication
import com.jme3.app.state.AppState
import com.jme3.post.FilterPostProcessor
import com.jme3.system.AppSettings
import org.wysko.autogeokt.sp3.EphemerisParser
import org.wysko.autogeokt.visual.ephemeris.EphemerisVisualizer
import java.io.File

/**
 * A simple JME3 application that visualizes the ephemeris file.
 */
@Suppress("LateinitUsage")
class AutoGeoJme(private val startingAppState: AppState) : SimpleApplication() {

    internal lateinit var assetLoader: AssetLoader

    override fun simpleInitApp() {
        assetLoader = AssetLoader()

        renderer.defaultAnisotropicFilter = 4
        viewPort.addProcessor(FilterPostProcessor(assetManager).apply { numSamples = 4 })

        stateManager.attach(startingAppState)
    }

    companion object {
        /**
         * Launches the JME instance, starting with the given [appState].
         *
         * @param appState The starting [AppState].
         */
        fun launch(appState: AppState) {
            with(AutoGeoJme(appState)) {
                isShowSettings = false
                setSettings(
                    AppSettings(true).apply {
                        width = 1600
                        height = 900
                    },
                )
                setDisplayStatView(false)
                setDisplayFps(false)
                isPauseOnLostFocus = false
                start()
            }
        }
    }
}

fun main() {
    AutoGeoJme.launch(EphemerisVisualizer(
        EphemerisParser.parseFile(File("src/test/resources/IGS0OPSRAP_20232640000_01D_15M_ORB.SP3").bufferedReader())
    ))
}
