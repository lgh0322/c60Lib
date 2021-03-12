package com.vacax.c60ball

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Half
import android.view.MotionEvent
import android.view.View
import com.vaca.c60.renderer.CubeRenderer
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class C60View : GLSurfaceView {
    private val NS2S = 1.0f / 1000000000.0f
    private val deltaRotationVector = FloatArray(4) { 0f }
    private var timestamp: Float = 0f
    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            // This timestep's delta rotation to be multiplied by the current rotation
            // after computing it from the gyro sample data.
            if (timestamp != 0f && event != null) {
                val dT = (event.timestamp - timestamp) * NS2S
                // Axis of the rotation sample, not normalized yet.
                var axisX: Float = event.values[0]
                var axisY: Float = event.values[1]
                var axisZ: Float = event.values[2]
                if (!lock){
                    renderer.angleX = (-axisY)
                    renderer.angleY = (-axisX)
                    renderer.angleZ = (axisZ)
                }

                // Calculate the angular speed of the sample
                val omegaMagnitude: Float = sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ)

                // Normalize the rotation vector if it's big enough to get the axis
                // (that is, EPSILON should represent your maximum allowable margin of error)
                if (omegaMagnitude > Half.EPSILON) {
                    axisX /= omegaMagnitude
                    axisY /= omegaMagnitude
                    axisZ /= omegaMagnitude
                }

                // Integrate around this axis with the angular speed by the timestep
                // in order to get a delta rotation from this sample over the timestep
                // We will convert this axis-angle representation of the delta rotation
                // into a quaternion before turning it into the rotation matrix.
                val thetaOverTwo: Float = omegaMagnitude * dT / 2.0f
                val sinThetaOverTwo: Float = sin(thetaOverTwo)
                val cosThetaOverTwo: Float = cos(thetaOverTwo)
                deltaRotationVector[0] = sinThetaOverTwo * axisX
                deltaRotationVector[1] = sinThetaOverTwo * axisY
                deltaRotationVector[2] = sinThetaOverTwo * axisZ
                deltaRotationVector[3] = cosThetaOverTwo
            }
            timestamp = event?.timestamp?.toFloat() ?: 0f
            val deltaRotationMatrix = FloatArray(9) { 0f }
            SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
            // User code should concatenate the delta rotation we computed with the current rotation
            // in order to get the updated rotation.
            // rotationCurrent = rotationCurrent * deltaRotationMatrix;
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }

    }
    private var lock=false
    private var previousX: Float = 0f
    private var previousY: Float = 0f

    val touchBall = View.OnTouchListener { _, event ->
        val x = event!!.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN->{
                lock=true
            }
            MotionEvent.ACTION_UP->{
                lock=false
                performClick()
            }
            MotionEvent.ACTION_MOVE -> {
                var dx = x - previousX
                var dy = y - previousY
                renderer.angleX = (dx / 5)
                renderer.angleY = (dy / 5)
            }

        }
        previousX = x
        previousY = y
        true
    }

    private val renderer = CubeRenderer()

    init {
        setEGLContextClientVersion(3)
        setRenderer(renderer)
        setOnTouchListener(touchBall)
        renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }

    companion object {
        lateinit var myresources: Resources
    }

    constructor(context: Context) : super(context) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        sensorManager.registerListener(sensorListener, sensor, 100)
        myresources = context.resources
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        val ta = context.obtainStyledAttributes(attributeSet, R.styleable.C60View)
        val color = ta.getColor(R.styleable.C60View_c60BackgroundColor, Color.BLACK)

        val a = color.toUInt().and(0xff.toUInt().shl(24)).shr(24).toFloat() / 255f
        val r = color.toUInt().and(0xff.toUInt().shl(16)).shr(16).toFloat() / 255f
        val g = color.toUInt().and(0xff.toUInt().shl(8)).shr(8).toFloat() / 255f
        val b = color.toUInt().and(0xff.toUInt().shl(0)).shr(0).toFloat() / 255f
        renderer.setBackground(a,r, g, b)



        sensorManager.registerListener(sensorListener, sensor, 100)
        myresources = context.resources
    }


}