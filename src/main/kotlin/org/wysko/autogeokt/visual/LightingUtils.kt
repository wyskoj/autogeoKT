package org.wysko.autogeokt.visual

import com.jme3.light.AmbientLight
import com.jme3.light.DirectionalLight
import com.jme3.math.ColorRGBA
import com.jme3.math.Vector3f
import com.jme3.scene.Node

object LightingUtils {
    fun setupStandardLighting(rootNode: Node) {
        createDirectionalLight(rootNode, ColorRGBA(0.9f, 0.9f, 0.9f, 1f), Vector3f(0f, -1f, -1f)) // Main light
        createDirectionalLight(rootNode, ColorRGBA(0.025f, 0.025f, 0.1f, 1f), Vector3f(0f, 1f, 1f)) // Backlight
        createAmbientLight(rootNode, ColorRGBA(0.05f, 0.05f, 0.05f, 1f)) // Ambience
    }

    private fun createDirectionalLight(rootNode: Node, colorRGBA: ColorRGBA, direction: Vector3f): DirectionalLight {
        return DirectionalLight().apply {
            color = colorRGBA
            this.direction = direction
            rootNode.addLight(this)
        }
    }

    private fun createAmbientLight(rootNode: Node, colorRGBA: ColorRGBA): AmbientLight {
        return AmbientLight().apply {
            color = colorRGBA
            rootNode.addLight(this)
        }
    }
}