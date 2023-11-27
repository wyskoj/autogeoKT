package org.wysko.autogeokt.visual.ephemeris

import com.jme3.app.Application
import com.jme3.app.state.AbstractAppState
import com.jme3.app.state.AppStateManager
import com.jme3.font.BitmapFont
import com.jme3.font.BitmapText
import com.jme3.material.Material
import com.jme3.math.ColorRGBA
import com.jme3.math.Quaternion
import com.jme3.math.Vector3f
import com.jme3.scene.Geometry
import com.jme3.scene.Node
import com.jme3.scene.debug.Grid
import org.wysko.autogeokt.geospatial.Cartesian3D
import org.wysko.autogeokt.sp3.EphemerisFile
import org.wysko.autogeokt.visual.AutoGeoJme
import org.wysko.autogeokt.visual.LightingUtils.setupStandardLighting
import org.wysko.autogeokt.visual.plusAssign

private const val CAMERA_MOVE_SPEED = 30f
private const val CAMERA_ZOOM_SPEED = 10f

private const val GRID_DIMENSION = 100
private const val M_IN_KM = 0.001f

/**
 * JME app state that visualizes an [EphemerisFile].
 */
@Suppress("LateinitUsage")
class EphemerisVisualizer(
    private val ephemerisFile: EphemerisFile,
) : AbstractAppState() {

    private val epochs = ephemerisFile.body.timedPositions.map { it.epoch }.distinct()

    internal lateinit var app: AutoGeoJme
    private val rootNode = Node("EphemerisVisualizer")

    internal lateinit var font: BitmapFont
    private lateinit var satelliteNodes: List<Node>
    private lateinit var epochText: BitmapText

    internal var time = 0.0

    override fun initialize(stateManager: AppStateManager?, app: Application?) {
        super.initialize(stateManager, app)
        this.app = (app!! as AutoGeoJme).also {
            it.rootNode.attachChild(rootNode)
        }
        setCameraSpeeds()

        font = app.assetManager.loadFont("Interface/Fonts/Console.fnt")

        satelliteNodes = ephemerisFile.header.satellites.map { satellite ->
            Node("Satellite ${satellite.satelliteNumber}").apply {
                addControl(
                    SatelliteControl(
                        satellite,
                        ephemerisFile.body.timedPositions.filter { it.satellite == satellite },
                    ),
                )
                rootNode += this
            }
        }

        addGrid()
        positionCamera()
        addHudText()
        addEarth()
        setupStandardLighting(rootNode)
        rootNode.addControl(EarthRotationControl())
    }

    private fun setCameraSpeeds() {
        with(app.flyByCamera) {
            moveSpeed = CAMERA_MOVE_SPEED
            isDragToRotate = true
            zoomSpeed = CAMERA_ZOOM_SPEED
        }
    }

    override fun update(tpf: Float) {
        epochText.text = epochs[time.toInt()].toString()
        time += tpf
    }

    private fun addEarth() {
        rootNode += app.assetLoader.loadDiffuseModel(
            "assets/ephemeris/earth/earth_grs80.obj",
            "assets/ephemeris/earth/earth_8k.jpg",
        )
    }

    private fun addHudText() {
        this.epochText = BitmapText(font).apply {
            move(300f, 30f, 0f)
        }.also {
            app.guiNode += it
        }
    }

    private fun positionCamera() {
        app.camera.setLocation(Vector3f(-0.11902428f, 50.462025f, 116.464966f))
        app.camera.setRotation(Quaternion(3.6473344E-9f, 0.98247325f, -0.18640342f, 2.1995094E-8f))
    }

    private fun addGrid() {
        val dimension = GRID_DIMENSION
        val grid = Geometry(
            "Grid",
            Grid(dimension, dimension, 1f),
        ).also {
            it.material = Material(app.assetManager, "Common/MatDefs/Misc/Unshaded.j3md").apply {
                setColor("Color", ColorRGBA(0.05f, 0.05f, 0.05f, 1f))
            }
        }
        app.rootNode.attachChild(grid)
        grid.move(-dimension / 2f, 0f, -dimension / 2f)
    }

    companion object {
        /**
         * Scales down a [Cartesian3D] by a factor of 0.001. This is to reduce measurements made in kilometers to meters
         * for easier handling.
         */
        fun Cartesian3D.scaleDown(): Vector3f = Vector3f(x.toFloat(), y.toFloat(), z.toFloat()).mult(M_IN_KM)
    }
}
