package com.netroby.app.android.langfight

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.netroby.app.android.langfight.common.DLHttpClient
import org.json.JSONObject
import okhttp3.Callback
import okhttp3.Call
import okhttp3.Response
import java.io.IOException

class MainActivity : AppCompatActivity() {
    val GITHUB_API_HOST: String = "https://api.github.com/"
    val LOG_TAG: String = "langfight.main"
    var context : Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DLHttpClient.preparePool(GITHUB_API_HOST)
        setContentView(R.layout.activity_main)
        context = this.context

        val ll = findViewById(R.id.mainLinearLayout) as LinearLayout
        ll.removeAllViews()
    }

    @JvmOverloads fun loadList(repo_name: String?, repo_url: String?) {
        Log.d(LOG_TAG, "Try to load page: " + repo_url)

        try {
            DLHttpClient.doGet(repo_url!!, object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, resp: Response) {
                    Log.d(LOG_TAG, "Response code: " + resp.code())
                    val handler = Handler(Looper.getMainLooper())
                    handler.post {
                        try {
                            val respBodyString = resp.body().string()
                            Log.d(LOG_TAG, "Response body: " + respBodyString)
                            val response = JSONObject(respBodyString)
                            if (resp.code() != 200) {
                                val additionMsg = response.getString("msg")
                                Handler(Looper.getMainLooper()).post { Toast.makeText(context, "Can not load data, please re login then try again" + additionMsg, Toast.LENGTH_SHORT).show() }
                            }

                            val data = response.getJSONArray("data")
                            val len = data.length()
                            val ll = findViewById(R.id.mainLinearLayout) as LinearLayout
                            for (i in 0..len - 1) {
                                val line = data.getJSONObject(i)
                                Log.d(LOG_TAG, line.toString())
                                val wv = WebView(context)
                                val content = "[" + line.getString("PublishTime") + "]<br />" + line.getString("Content")
                                Log.d(LOG_TAG, content)
                                wv.loadData(content, "text/html;charset=UTF-8", "UTF-8")
                                ll.addView(wv)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
