package org.wysko.autogeokt.visual.ephemeris

import com.jme3.math.Quaternion
import com.jme3.renderer.RenderManager
import com.jme3.renderer.ViewPort
import com.jme3.scene.control.AbstractControl

context(EphemerisVisualizer)
class EarthRotationControl : AbstractControl() {

    override fun controlUpdate(tpf: Float) {
        spatial.localRotation = Quaternion().fromAngles(
            0f, // 0
            (time / 96.0 * 2.0 * Math.PI).toFloat(), // 2pi
            0f, // 0
        )
    }

    override fun controlRender(rm: RenderManager?, vp: ViewPort?) = Unit

}