package com.netroby.app.android.langfight.common

/**
 * Created by huzhifeng on 2017/4/1.
 */

import okhttp3.*
import java.io.IOException

object DLHttpClient {

    private val JSON = MediaType.parse("application/json;charset=utf-8")
    val client: OkHttpClient? = OkHttpClient.Builder()
            .connectionPool(ConnectionPool())
            .build()

    @Throws(IOException::class)
    fun preparePool(host: String?)  {
        client!!.newCall(Request.Builder().url(host).head().build()).enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {

            }

            override fun onResponse(call: Call?, response: Response?) {

            }
        })
    }

    /**
     * 获取GET
     */
    @Throws(IOException::class)
    fun doGet(url: String, callback: Callback) {
        val request = Request.Builder()
                .url(url)
                .get()
                .build()
        client!!.newCall(request).enqueue(callback)
    }

    /**
     * 获取Post
     */
    @Throws(IOException::class)
    fun doPost(url: String, json: String, callback: Callback) {
        val body = RequestBody.create(JSON, json)
        val request = Request.Builder()
                .url(url)
                .post(body)
                .build()
        client!!.newCall(request).enqueue(callback)
    }

    @Throws(IOException::class)
    fun fileUpload(url: String, fileUri: String, imageByteArray: ByteArray, callback: Callback) {
        val MEDIA_TYPE_JPG = MediaType.parse("image/jpg")

        val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("uploadfile", fileUri, RequestBody.create(MEDIA_TYPE_JPG, imageByteArray))
                .build()
        val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()
        client!!.newCall(request).enqueue(callback)
    }
}