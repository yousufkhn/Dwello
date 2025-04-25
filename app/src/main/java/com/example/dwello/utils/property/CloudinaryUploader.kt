package com.example.dwello.utils.property

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CloudinaryUploader(private val context: Context) {
    private val cloudName = "dhhiykg31"
    private val uploadPreset = "dwelloproperties"

    private val client = OkHttpClient()

    /**
     * Uploads an image to Cloudinary and returns the secure URL
     */
    suspend fun uploadImage(uri: Uri): String = withContext(Dispatchers.IO) {
        val file = convertUriToFile(uri)

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file.name, file.asRequestBody("image/jpeg".toMediaTypeOrNull()))
            .addFormDataPart("upload_preset", uploadPreset)
            .build()

        val request = Request.Builder()
            .url("https://api.cloudinary.com/v1_1/$cloudName/image/upload")
            .post(requestBody)
            .build()

        suspendCoroutine { continuation ->
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        continuation.resumeWithException(IOException("Upload failed with code ${response.code}"))
                        return
                    }

                    val responseBody = response.body?.string()
                    if (responseBody == null) {
                        continuation.resumeWithException(IOException("Empty response body"))
                        return
                    }

                    try {
                        val jsonResponse = JSONObject(responseBody)
                        val secureUrl = jsonResponse.getString("secure_url")
                        continuation.resume(secureUrl)
                    } catch (e: Exception) {
                        continuation.resumeWithException(IOException("Failed to parse response", e))
                    }
                }
            })
        }
    }

    /**
     * Converts a content Uri to a temporary File for upload
     */
    private fun convertUriToFile(uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IOException("Could not open input stream for URI")

        val fileName = "upload_${System.currentTimeMillis()}.jpg"
        val tempFile = File(context.cacheDir, fileName)

        FileOutputStream(tempFile).use { outputStream ->
            inputStream.use { input ->
                input.copyTo(outputStream)
            }
        }

        return tempFile
    }
}
