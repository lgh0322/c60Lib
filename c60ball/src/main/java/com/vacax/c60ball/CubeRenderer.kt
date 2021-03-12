package com.vaca.c60.renderer

import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix

import com.vacax.c60ball.R

import com.vacax.c60ball.ResReadUtils.readResource

import com.vacax.c60ball.ShaderUtils.compileFragmentShader
import com.vacax.c60ball.ShaderUtils.compileVertexShader
import com.vacax.c60ball.ShaderUtils.linkProgram
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.random.Random


class CubeRenderer : GLSurfaceView.Renderer {
    private val vertexBuffer: FloatBuffer
    private val colorBuffer: FloatBuffer
    private val indicesBuffer: ShortBuffer

    private var mProgram = 0
    private val _initMatrix = FloatArray(16)
    private val rotationMatrix = FloatArray(16)
    private val rotationMatriy = FloatArray(16)
    private val rotationMatriz = FloatArray(16)

    // vPMatrix is an abbreviation for "Model View Projection Matrix"
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    var angleX = 0f
    var angleY = 0f
    var angleZ = 0f

    /**
     * 点的坐标
     */

    private var vertexPointsConverted = FloatArray(1044)
    private var len = 0
    lateinit var vv: FloatArray
    private var vertexPoints = floatArrayOf(
        0f, 1.26295146067f, 1.192569588f,
        0f, 1.63147573033f, 0.596284793999f,
        1.1342010778f, 1.26295146067f, 0.368524269667f,
        0.567100538899f, 1.63147573033f, 0.184262134833f,
        0.70097481616f, 1.26295146067f, -0.964809063667f,
        0.35048740808f, 1.63147573033f, -0.482404531833f,
        -0.70097481616f, 1.26295146067f, -0.964809063667f,
        -0.35048740808f, 1.63147573033f, -0.482404531833f,
        -1.1342010778f, 1.26295146067f, 0.368524269667f,
        -0.567100538899f, 1.63147573033f, 0.184262134833f,
        1.1342010778f, 0.894427191f, 0.964809063666f,
        0.567100538899f, 0.894427191f, 1.37683172283f,
        -1.1342010778f, 0.894427191f, 0.964809063666f,
        -0.567100538899f, 0.894427191f, 1.37683172283f,
        -0.70097481616f, -0.298142397001f, 1.56109385767f,
        -0.35048740808f, 0.298142397001f, 1.67497411983f,
        0.70097481616f, -0.298142397001f, 1.56109385767f,
        0.35048740808f, 0.298142397001f, 1.67497411983f,
        1.26807535506f, 0.894427191f, -0.780546928834f,
        1.48468848588f, 0.894427191f, -0.113880262166f,
        1.26807535506f, -0.298142397001f, 1.1490711985f,
        1.48468848588f, 0.298142397001f, 0.8509288015f,
        1.7013016167f, -0.298142397001f, -0.184262134834f,
        1.7013016167f, 0.298142397001f, 0.184262134834f,
        -0.350487408081f, 0.894427191f, -1.4472135955f,
        0.350487408081f, 0.894427191f, -1.4472135955f,
        0.35048740808f, -0.298142397001f, -1.67497411983f,
        0.70097481616f, 0.298142397001f, -1.56109385767f,
        1.48468848588f, -0.298142397001f, -0.8509288015f,
        1.26807535506f, 0.298142397001f, -1.1490711985f,
        -1.48468848588f, 0.894427191f, -0.113880262166f,
        -1.26807535506f, 0.894427191f, -0.780546928834f,
        -0.35048740808f, -0.298142397001f, -1.67497411983f,
        -0.70097481616f, 0.298142397001f, -1.56109385767f,
        -1.48468848588f, -0.298142397001f, -0.8509288015f,
        -1.26807535506f, 0.298142397001f, -1.1490711985f,
        -1.7013016167f, -0.298142397001f, -0.184262134834f,
        -1.7013016167f, 0.298142397001f, 0.184262134834f,
        -1.26807535506f, -0.298142397001f, 1.1490711985f,
        -1.48468848588f, 0.298142397001f, 0.8509288015f,
        0f, -1.26295146067f, -1.192569588f,
        0f, -1.63147573033f, -0.596284793999f,
        -1.1342010778f, -1.26295146067f, -0.368524269667f,
        -0.567100538899f, -1.63147573033f, -0.184262134833f,
        -0.70097481616f, -1.26295146067f, 0.964809063667f,
        -0.35048740808f, -1.63147573033f, 0.482404531833f,
        0.70097481616f, -1.26295146067f, 0.964809063667f,
        0.35048740808f, -1.63147573033f, 0.482404531833f,
        1.1342010778f, -1.26295146067f, -0.368524269667f,
        0.567100538899f, -1.63147573033f, -0.184262134833f,
        -1.1342010778f, -0.894427191f, -0.964809063666f,
        -0.567100538899f, -0.894427191f, -1.37683172283f,
        1.1342010778f, -0.894427191f, -0.964809063666f,
        0.567100538899f, -0.894427191f, -1.37683172283f,
        -1.26807535506f, -0.894427191f, 0.780546928834f,
        -1.48468848588f, -0.894427191f, 0.113880262166f,
        0.350487408081f, -0.894427191f, 1.4472135955f,
        -0.350487408081f, -0.894427191f, 1.4472135955f,
        1.48468848588f, -0.894427191f, 0.113880262166f,
        1.26807535506f, -0.894427191f, 0.780546928834f

    )

    /**
     * 定义索引
     */
    private var indices = ShortArray(20 * 4 * 3 + 12 * 3 * 3)

    var a:Float=0f
    var r: Float = 0f
    var g: Float = 0f
    var b: Float = 0f

    //立方体的顶点颜色
    private var colors = FloatArray(1392)

    private val six = intArrayOf(
        0,
        1,
        9,
        8,
        12,
        13,
        1,
        0,
        11,
        10,
        2,
        3,
        3,
        2,
        19,
        18,
        4,
        5,
        5,
        4,
        25,
        24,
        6,
        7,
        7,
        6,
        31,
        30,
        8,
        9,
        10,
        11,
        17,
        16,
        20,
        21,
        13,
        12,
        39,
        38,
        14,
        15,
        15,
        14,
        57,
        56,
        16,
        17,
        18,
        19,
        23,
        22,
        28,
        29,
        21,
        20,
        59,
        58,
        22,
        23,
        24,
        25,
        27,
        26,
        32,
        33,
        26,
        27,
        29,
        28,
        52,
        53,
        30,
        31,
        35,
        34,
        36,
        37,
        33,
        32,
        51,
        50,
        34,
        35,
        37,
        36,
        55,
        54,
        38,
        39,
        40,
        41,
        43,
        42,
        50,
        51,
        41,
        40,
        53,
        52,
        48,
        49,
        42,
        43,
        45,
        44,
        54,
        55,
        44,
        45,
        47,
        46,
        56,
        57,
        46,
        47,
        49,
        48,
        58,
        59
    )

    val five = intArrayOf(
        11,
        0,
        13,
        15,
        17,
        1,
        3,
        5,
        7,
        9,
        2,
        10,
        21,
        23,
        19,
        4,
        18,
        29,
        27,
        25,
        6,
        24,
        33,
        35,
        31,
        12,
        8,
        30,
        37,
        39,
        14,
        38,
        54,
        44,
        57,
        20,
        16,
        56,
        46,
        59,
        28,
        22,
        58,
        48,
        52,
        32,
        26,
        53,
        40,
        51,
        36,
        34,
        50,
        42,
        55,
        43,
        41,
        49,
        47,
        45
    )

    fun setBackground(a:Float,r: Float, g: Float, b: Float) {
        this.a=a
        this.r = r
        this.g = g
        this.b = b
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {

        //设置背景颜色
        GLES30.glClearColor(r, g, b, 1.0f)
        //编译
        val vertexShaderId = compileVertexShader(readResource(R.raw.vertex_c60_shader))
        val fragmentShaderId = compileFragmentShader(readResource(R.raw.fragment_c60_shader))
        //链接程序片段
        mProgram = linkProgram(vertexShaderId, fragmentShaderId)
        //使用程序片段
        GLES30.glUseProgram(mProgram)
        GLES30.glVertexAttribPointer(
            0,
            VERTEX_POSITION_SIZE,
            GLES30.GL_FLOAT,
            false,
            0,
            vertexBuffer
        )
        //启用位置顶点属性
        GLES30.glEnableVertexAttribArray(0)
        GLES30.glVertexAttribPointer(1, VERTEX_COLOR_SIZE, GLES30.GL_FLOAT, true, 0, colorBuffer)
        //启用颜色顶点属性
        GLES30.glEnableVertexAttribArray(1)


        GLES30.glClearDepthf(1.0f); // 设置深度缓存
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);    // 启用深度测试
        GLES30.glDepthFunc(GLES30.GL_LEQUAL);     // 深度测试类型

        Matrix.setIdentityM(_initMatrix, 0)
    }


    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height.toFloat()
        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, -ratio/2, ratio/2, -0.50f, 0.5f, 1f, 100f)
    }

    override fun onDrawFrame(gl: GL10) {
        val scratch = FloatArray(16)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -6f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

        // Calculate the projection and view transformation
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)


        Matrix.setRotateM(rotationMatrix, 0, angleX, 0f, 1f, 0f)
        Matrix.setRotateM(rotationMatriy, 0, angleY, -1f, 0f, 0f)
        Matrix.setRotateM(rotationMatriz, 0, angleZ, 0f, 0f, 1f)
        Matrix.multiplyMM(rotationMatrix, 0, rotationMatrix, 0, rotationMatriy, 0)
        Matrix.multiplyMM(rotationMatrix, 0, rotationMatrix, 0, rotationMatriz, 0)
        Matrix.multiplyMM(_initMatrix, 0, rotationMatrix, 0, _initMatrix, 0)

        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, _initMatrix, 0)


        val vPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix")
        GLES30.glUniformMatrix4fv(vPMatrixHandle, 1, false, scratch, 0)

        GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        GLES30.glDrawArrays(
            GLES30.GL_TRIANGLES,
            0,
            len
        )
    }

    companion object {
        private const val VERTEX_POSITION_SIZE = 3
        private const val VERTEX_COLOR_SIZE = 4
    }

    init {


        for (k in 0 until 60) {
            colors[k * 4] = Random.nextFloat()
            colors[k * 4 + 1] = Random.nextFloat()
            colors[k * 4 + 2] = Random.nextFloat()
            colors[k * 4 + 3] = 1f
        }

        var x = 0
        var a: Float
        var b: Float
        var c: Float
        for (k in 0 until 20) {
            a = Random.nextFloat()
            b = Random.nextFloat()
            c = Random.nextFloat()
            for (j in 0 until 4) {
                vertexPointsConverted[3 * x] = vertexPoints[3 * six[6 * k]]
                vertexPointsConverted[3 * x + 1] = vertexPoints[3 * six[6 * k] + 1]
                vertexPointsConverted[3 * x + 2] = vertexPoints[3 * six[6 * k] + 2]
                colors[4 * x] = a
                colors[4 * x + 1] = b
                colors[4 * x + 2] = c
                colors[4 * x + 3] = 1f
                x++
                vertexPointsConverted[3 * x] = vertexPoints[3 * six[6 * k + 1 + j]]
                vertexPointsConverted[3 * x + 1] = vertexPoints[3 * six[6 * k + 1 + j] + 1]
                vertexPointsConverted[3 * x + 2] = vertexPoints[3 * six[6 * k + 1 + j] + 2]
                colors[4 * x] = a
                colors[4 * x + 1] = b
                colors[4 * x + 2] = c
                colors[4 * x + 3] = 1f
                x++
                vertexPointsConverted[3 * x] = vertexPoints[3 * six[6 * k + 2 + j]]
                vertexPointsConverted[3 * x + 1] = vertexPoints[3 * six[6 * k + 2 + j] + 1]
                vertexPointsConverted[3 * x + 2] = vertexPoints[3 * six[6 * k + 2 + j] + 2]
                colors[4 * x] = a
                colors[4 * x + 1] = b
                colors[4 * x + 2] = c
                colors[4 * x + 3] = 1f
                x++
            }
        }

        for (k in 0 until 12) {
            a = Random.nextFloat()
            b = Random.nextFloat()
            c = Random.nextFloat()
            for (j in 0 until 3) {
                vertexPointsConverted[3 * x] = vertexPoints[3 * five[5 * k]]
                vertexPointsConverted[3 * x + 1] = vertexPoints[3 * five[5 * k] + 1]
                vertexPointsConverted[3 * x + 2] = vertexPoints[3 * five[5 * k] + 2]
                colors[4 * x] = a
                colors[4 * x + 1] = b
                colors[4 * x + 2] = c
                colors[4 * x + 3] = 1f
                x++
                vertexPointsConverted[3 * x] = vertexPoints[3 * five[5 * k + 1 + j]]
                vertexPointsConverted[3 * x + 1] = vertexPoints[3 * five[5 * k + 1 + j] + 1]
                vertexPointsConverted[3 * x + 2] = vertexPoints[3 * five[5 * k + 1 + j] + 2]
                colors[4 * x] = a
                colors[4 * x + 1] = b
                colors[4 * x + 2] = c
                colors[4 * x + 3] = 1f
                x++
                vertexPointsConverted[3 * x] = vertexPoints[3 * five[5 * k + 2 + j]]
                vertexPointsConverted[3 * x + 1] = vertexPoints[3 * five[5 * k + 2 + j] + 1]
                vertexPointsConverted[3 * x + 2] = vertexPoints[3 * five[5 * k + 2 + j] + 2]
                colors[4 * x] = a
                colors[4 * x + 1] = b
                colors[4 * x + 2] = c
                colors[4 * x + 3] = 1f
                x++
            }
        }

        len = x

        vertexBuffer = ByteBuffer.allocateDirect(vertexPointsConverted.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer().apply {
                put(vertexPointsConverted)
                position(0)
            }

        //分配内存空间,每个浮点型占4字节空间
        colorBuffer = ByteBuffer.allocateDirect(colors.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer().apply {
                put(colors)
                position(0)
            }


        indicesBuffer = ByteBuffer.allocateDirect(indices.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer().apply {
                put(indices)
                position(0)
            }
    }
}