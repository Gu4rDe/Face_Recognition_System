package com.example.kotlinapp.service

import com.github.sarxos.webcam.Webcam
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

object WebcamService {

    private var webcam: Webcam? = null
    private var isOpen = false

    fun open(): Boolean {
        if (isOpen) return true
        return try {
            val cam = Webcam.getDefault() ?: return false
            webcam = cam
            cam.open()
            isOpen = true
            true
        } catch (_: Exception) {
            webcam = null
            isOpen = false
            false
        }
    }

    fun capture(): ByteArray? {
        val cam = webcam ?: return null
        if (!isOpen) return null
        return try {
            val image = cam.image ?: return null
            bufferedImageToJpegBytes(image)
        } catch (_: Exception) {
            null
        }
    }

    fun close() {
        try {
            webcam?.close()
        } catch (_: Exception) {
        }
        webcam = null
        isOpen = false
    }

    fun isAvailable(): Boolean {
        return try {
            Webcam.getDefault() != null
        } catch (_: Exception) {
            false
        }
    }

    private fun bufferedImageToJpegBytes(image: BufferedImage): ByteArray {
        val baos = ByteArrayOutputStream()
        ImageIO.write(image, "jpg", baos)
        return baos.toByteArray()
    }
}