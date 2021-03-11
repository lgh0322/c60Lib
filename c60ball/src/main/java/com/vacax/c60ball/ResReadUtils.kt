package com.vacax.c60ball

import android.content.res.Resources.NotFoundException

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


object ResReadUtils {
    /**
     * 读取资源
     *
     * @param resourceId
     * @return
     */
    @JvmStatic
    fun readResource(resourceId: Int): String {
        val builder = StringBuilder()
        try {
            val inputStream = C60View.myresources.openRawResource(resourceId)
            val streamReader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(streamReader)
            var textLine: String?
            while (bufferedReader.readLine().also { textLine = it } != null) {
                builder.append(textLine)
                builder.append("\n")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: NotFoundException) {
            e.printStackTrace()
        }
        return builder.toString()
    }
}