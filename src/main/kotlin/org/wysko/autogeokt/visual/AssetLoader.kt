package org.wysko.autogeokt.visual

import com.jme3.app.Application
import com.jme3.material.Material
import com.jme3.math.Vector3f
import com.jme3.scene.Spatial

private const val LIGHTING_MAT: String = "Common/MatDefs/Light/Lighting.j3md"
private const val DIFFUSE_MAP: String = "DiffuseMap"
private const val FRESNEL_PARAMS: String = "FresnelParams"
private const val ENV_MAP_AS_SPHERE_MAP: String = "EnvMapAsSphereMap"
private const val ENV_MAP: String = "EnvMap"

private fun String.assetPrefix(): String = if (this.startsWith("assets/")) this else "assets/$this"

private const val TEXTURE_REFLECTIVITY = 0.18f

/**
 * Provides utility functions for loading assets from files.
 */
context(Application)
class AssetLoader {

    /**
     * Loads a [model] and applies a diffuse [texture].
     */
    fun loadDiffuseModel(model: String, texture: String): Spatial = assetManager.loadModel(model.assetPrefix()).apply {
        setMaterial(diffuseMaterial(texture))
    }

    /**
     * Loads a [model] and applies a reflective [texture].
     */
    fun loadReflectiveModel(model: String, texture: String): Spatial =
        assetManager.loadModel(model.assetPrefix()).apply {
            setMaterial(reflectiveMaterial(texture))
        }

    private fun diffuseMaterial(texture: String): Material =
        Material(assetManager, LIGHTING_MAT).apply {
            setTexture(DIFFUSE_MAP, assetManager.loadTexture(texture.assetPrefix()))
        }

    private fun reflectiveMaterial(texture: String): Material =
        Material(assetManager, LIGHTING_MAT).apply {
            setVector3(FRESNEL_PARAMS, Vector3f(TEXTURE_REFLECTIVITY, TEXTURE_REFLECTIVITY, TEXTURE_REFLECTIVITY))
            setBoolean(ENV_MAP_AS_SPHERE_MAP, true)
            setTexture(ENV_MAP, assetManager.loadTexture(texture.assetPrefix()))
            setTexture("DiffuseMap", assetManager.loadTexture("Assets/Black.bmp"))
        }
}
