package com.kevng2.treear

import android.util.Log
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL

class PolyFetcher {
    fun getUrlBytes(urlSpec: String): ByteArray {
        val url = URL(urlSpec)
        val connection = url.openConnection() as HttpURLConnection

        val out = ByteArrayOutputStream()
        val inputStreamIn = connection.inputStream

        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            Log.e(
                "PolyFetcher",
                "getUrlBytes (line 16): ${connection.responseMessage}: with $urlSpec"
            )
        }

        var bytesRead: Int
        val buffer = ByteArray(1024)
        bytesRead = inputStreamIn.read(buffer)

        while (bytesRead > 0) {
            out.write(buffer, 0, bytesRead)
            bytesRead = inputStreamIn.read(buffer)
        }
        out.close()
        connection.disconnect()
        return out.toByteArray()
    }

    fun getUrlString(urlSpec: String): String {
        return String(getUrlBytes(urlSpec))
    }
}