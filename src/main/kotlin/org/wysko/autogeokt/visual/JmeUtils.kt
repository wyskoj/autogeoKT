package org.wysko.autogeokt.visual

import com.jme3.scene.Node
import com.jme3.scene.Spatial

/**
 * Convenience operator to assign a child.
 */
operator fun Node.plusAssign(spatial: Spatial) {
    attachChild(spatial)
}
