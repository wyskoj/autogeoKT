package org.wysko.autogeokt.visual

import com.jme3.app.Application
import com.jme3.app.SimpleApplication
import com.jme3.app.state.AbstractAppState
import com.jme3.app.state.AppStateManager
import com.jme3.font.BitmapText
import com.jme3.light.AmbientLight
import com.jme3.light.DirectionalLight
import com.jme3.material.Material
import com.jme3.math.ColorRGBA
import com.jme3.math.Quaternion
import com.jme3.math.Vector3f
import com.jme3.post.FilterPostProcessor
import com.jme3.scene.Geometry
import com.jme3.scene.Node
import com.jme3.scene.debug.Grid
import com.jme3.scene.shape.Sphere
import org.wysko.autogeokt.geospatial.Cartesian3D
import org.wysko.autogeokt.sp3.EphemerisFile

private const val CAMERA_MOVE_SPEED = 30f

private const val GRID_DIMENSION = 100

@Suppress("LateinitUsage")
class EphemerisVisualizer(
    val ephemerisFile: EphemerisFile,
) : AbstractAppState() {

    private lateinit var simpleApp: SimpleApplication
    private val rootNode = Node("EphemerisVisualizer")
    private var satelliteNodes = Array(ephemerisFile.header.satellites.size) { Node() }
    private lateinit var texts: Array<BitmapText>
    private var time = 0.0
    private val posBySat = ephemerisFile.body.timedPositions.groupBy { it.satellite }
    private lateinit var epochText: BitmapText
    private lateinit var assetLoader: AssetLoader

    override fun initialize(stateManager: AppStateManager?, app: Application?) {
        super.initialize(stateManager, app)
        simpleApp = (app!! as SimpleApplication).also {
            it.rootNode.attachChild(rootNode)
            with(it.flyByCamera) {
                moveSpeed = CAMERA_MOVE_SPEED
                isDragToRotate = true
                zoomSpeed = 10f
            }
        }
        assetLoader = AssetLoader(simpleApp)
        simpleApp.renderer.defaultAnisotropicFilter = 4
        simpleApp.viewPort.addProcessor(
            FilterPostProcessor(simpleApp.assetManager).apply {
                numSamples = 4
            },
        )

        assignSatelliteGeometries(simpleApp)
        addGrid(simpleApp)
        positionCamera(simpleApp)
        addHudText(simpleApp)
        addEarth()
        setupLights()
    }

    override fun update(tpf: Float) {
        val obsIndex = time.toInt()
        val timePhase = time - obsIndex

        epochText.text = posBySat.entries.first().value[obsIndex % 96].epoch.toString()

        texts.forEach {
            it.lookAt(simpleApp.camera.location, Vector3f.UNIT_Y)
        }

        ephemerisFile.header.satellites.forEachIndexed { index, satellite ->
            val timedPosition = posBySat[satellite]!![obsIndex % 96].position.scaleDown()
            val timedPositionAhead = posBySat[satellite]!![(obsIndex + 1) % 96].position.scaleDown()
            val position = timedPosition.interpolateLocal(timedPositionAhead, timePhase.toFloat())
            satelliteNodes[index].localTranslation = position
        }

        // 0 --> 0
        // 96 --> 2pi
        rootNode.localRotation = Quaternion().fromAngles(
            0f, // 0
            (time / 96.0 * 2.0 * Math.PI).toFloat(), // 2pi
            0f, // 0
        )

        time += tpf * 10.0
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

    private fun setupLights() {
        val shadowsOnly = createDirectionalLight(rootNode, ColorRGBA.Black, Vector3f(0.1f, -1f, -0.1f))
        createDirectionalLight(rootNode, ColorRGBA(0.9f, 0.9f, 0.9f, 1f), Vector3f(0f, -1f, -1f)) // Main light
        createDirectionalLight(rootNode, ColorRGBA(0.1f, 0.1f, 0.3f, 1f), Vector3f(0f, 1f, 1f)) // Backlight
        createAmbientLight(rootNode, ColorRGBA(0.5f, 0.5f, 0.5f, 1f)) // Ambience
    }

    private fun addEarth() {
        // It's just a sphere with radius 6378.1
        rootNode.attachChild(
            assetLoader.loadDiffuseModel("assets/earth_grs80.obj", "assets/earth_8k.jpg"),
        )
    }

    private fun addHudText(app: SimpleApplication) {
        val font = app.assetManager.loadFont("Interface/Fonts/Console.fnt")
        val text = BitmapText(font).also {
            it.move(300f, 30f, 0f)
        }
        app.guiNode.attachChild(text)
        this.epochText = text
    }

    private fun positionCamera(app: SimpleApplication) {
        app.camera.setLocation(Vector3f(-0.11902428f, 50.462025f, 116.464966f))
        app.camera.setRotation(Quaternion(3.6473344E-9f, 0.98247325f, -0.18640342f, 2.1995094E-8f))
    }

    private fun addGrid(app: SimpleApplication) {
        val dimension = GRID_DIMENSION
        val grid = Geometry(
            "Grid",
            Grid(dimension, dimension, 1f),
        ).also {
            it.material = Material(app.assetManager, "Common/MatDefs/Misc/Unshaded.j3md").apply {
                setColor("Color", ColorRGBA(0.05f, 0.05f, 0.05f, 1f))
            }
        }
        simpleApp.rootNode.attachChild(grid)
        grid.move(-dimension / 2f, 0f, -dimension / 2f)
    }

    private fun assignSatelliteGeometries(app: SimpleApplication) {
        val sphere = Sphere(32, 32, 0.5f)
        val font = app.assetManager.loadFont("Interface/Fonts/Console.fnt")
        val texts = mutableListOf<BitmapText>()
        satelliteNodes.forEachIndexed { index, node ->
            node.attachChild(
                Geometry(
                    "Satellite $index",
                    sphere,
                ).also {
                    it.material = app.assetManager.loadMaterial("Common/Materials/RedColor.j3m")
                },
            )
            node.attachChild(
                BitmapText(
                    font,
                ).also {
                    it.move(0f, 3f, 0f)
                    it.size = 2f
                    it.text = ephemerisFile.header.satellites[index].satelliteNumber.toString()
                    texts += it
                },
            )
            rootNode.attachChild(node)
        }
        this.texts = texts.toTypedArray()
    }
}

fun Cartesian3D.scaleDown(): Vector3f = Vector3f(x.toFloat(), y.toFloat(), z.toFloat()).mult(0.001f)
