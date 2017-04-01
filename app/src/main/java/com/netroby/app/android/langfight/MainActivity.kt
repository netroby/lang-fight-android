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
import okhttp3.Callback
import okhttp3.Call
import okhttp3.Response
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.webView
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException

class MainActivity : AppCompatActivity() {
    var context: Context? = null
    val GITHUB_API_HOST: String = "https://api.github.com/"
    val TOKEN_STRING: String = "?access_token=318f3c9b15d41ad3ac88514854506c281001d991"
    val LOG_TAG: String = "langfight.main"
    var ll: LinearLayout? = null
    var htmlOut: String = "<strong>Language develop commit stats:</strong>"

    override fun onCreate(savedInstanceState: Bundle?) {
        context = applicationContext

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        Thread().run {
            DLHttpClient.preparePool(GITHUB_API_HOST)
            getSum("Rust", "rust-lang/rust")
            getSum("Python", "python/cpython")
            getSum("PHP", "php/php-src")
            getSum("Golang", "golang/go")
            getSum("Ruby", "ruby/ruby")
            getSum("Swift", "apple/swift")
            getSum("Scala", "scala/scala")
            getSum("Clojure", "clojure/clojure")
            getSum("Kotlin", "JetBrains/kotlin")
        }
    }

    fun paintHtml() {
        val wv = findViewById(R.id.mainWebView) as WebView
        wv.removeAllViews()
        wv.loadData(htmlOut, "text/html;charset=UTF-8", "UTF-8")
    }

    fun appendHtmlOut(str: String) {
        var sb = StringBuilder()
        sb.append(htmlOut).append(str)
        htmlOut = sb.toString()
    }

    fun getSum(repo_name: String?, repo_url: String?) {
        Log.d(LOG_TAG, "Try to load page: " + repo_url)

        var real_repo_url = GITHUB_API_HOST + "repos/" + repo_url + "/stats/commit_activity" + TOKEN_STRING;
        try {
                DLHttpClient.doGet(real_repo_url, object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, resp: Response) {


                        Log.d(LOG_TAG, "Response code: " + resp.code())
                        val respBodyString = resp.body().string()
                        Log.d(LOG_TAG, "Response body: " + respBodyString)

                            val json = JSONTokener(respBodyString).nextValue()
                            if (json is JSONObject) {
                                Log.d(LOG_TAG, "Expected JSONArray, but got JSONObject")
                            } else if (json is JSONArray) {

                                val response = JSONArray(respBodyString)
                                val length = response.length()
                                if (resp.code() != 200) {
                                    Log.d(LOG_TAG, "Can not load data")
                                    return
                                }

                                val data = response.getJSONObject(length - 1)
                                val count = data.getInt("total")

                                appendHtmlOut("<p>" + repo_name + " commits in this week: " + count.toString() + "</p>")
                                    Log.d(LOG_TAG, htmlOut)
                                runOnUiThread {
                                    paintHtml()
                                }
                            }
                        }

                })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
