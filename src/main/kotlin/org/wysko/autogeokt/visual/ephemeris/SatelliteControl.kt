package org.wysko.autogeokt.visual.ephemeris

import com.jme3.font.BitmapText
import com.jme3.material.Material
import com.jme3.math.ColorRGBA
import com.jme3.math.Vector3f
import com.jme3.renderer.RenderManager
import com.jme3.renderer.ViewPort
import com.jme3.scene.Geometry
import com.jme3.scene.Node
import com.jme3.scene.Spatial
import com.jme3.scene.control.AbstractControl
import com.jme3.scene.shape.Sphere
import org.wysko.autogeokt.rinex.Satellite
import org.wysko.autogeokt.sp3.EphemerisFile
import org.wysko.autogeokt.visual.ephemeris.EphemerisVisualizer.Companion.scaleDown

private const val LABEL_OFFSET = 3f

/**
 * Controls the movement of satellites in the [EphemerisVisualizer].
 */
context(EphemerisVisualizer)
class SatelliteControl(
    private val satellite: Satellite,
    private val timedPositions: List<EphemerisFile.Body.TimedPosition>,
) : AbstractControl() {
    private lateinit var label: BitmapText

    override fun setSpatial(spatial: Spatial?) {
        super.setSpatial(spatial)
        spatial as Node

        spatial.attachChild(
            Geometry("Satellite", Sphere(32, 32, 0.5f)).also {
                it.material = Material(app.assetManager, "Common/MatDefs/Misc/Unshaded.j3md").apply {
                    setColor("Color", ColorRGBA(0.95f, 0.05f, 0.05f, 1f))
                }
            },
        )

        label = BitmapText(font).also {
            it.move(0f, LABEL_OFFSET, 0f)
            it.size = 2f
            it.text = satellite.satelliteNumber.toString()
            spatial.attachChild(it)
        }
    }

    override fun controlUpdate(tpf: Float) {
        val obsIndex = time.toInt()
        val timePhase = time - obsIndex

        val timedPosition = timedPositions[obsIndex % 96].position.scaleDown()
        val timedPositionAhead = timedPositions[(obsIndex + 1) % 96].position.scaleDown()

        spatial.localTranslation = timedPosition.interpolateLocal(timedPositionAhead, timePhase.toFloat())

        label.lookAt(app.camera.location, Vector3f.UNIT_Y)
    }

    override fun controlRender(rm: RenderManager?, vp: ViewPort?) = Unit
}
