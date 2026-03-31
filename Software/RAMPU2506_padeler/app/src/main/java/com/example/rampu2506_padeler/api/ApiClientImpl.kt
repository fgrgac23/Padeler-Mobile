package com.example.rampu2506_padeler.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

const val baseURL = "https://grgac.ase-lab.ovh/"

class ApiClientImpl : ApiClient {
    override suspend fun getJson(
        path: String,
        query: Map<String, String>
    ): JSONObject = withContext(Dispatchers.IO) {

        val queryString = if (query.isNotEmpty()) {
            "?" + query.entries.joinToString("&") {
                "${it.key}=${URLEncoder.encode(it.value, "UTF-8")}"
            }
        } else ""

        val url = URL(baseURL + path + queryString)
        val conn = url.openConnection() as HttpURLConnection

        conn.requestMethod = "GET"
        conn.connectTimeout = 10_000
        conn.readTimeout = 10_000

        val code = conn.responseCode
        val stream = if (code in 200..299) conn.inputStream else conn.errorStream

        val raw = stream?.bufferedReader()?.use(BufferedReader::readText).orEmpty()
        try {
            JSONObject(raw.ifBlank { "{}" })
        } catch (e: Exception) {
            JSONObject()
                .put("success", false)
                .put("error", raw.ifBlank { "Invalid JSON response (HTTP $code)" })
        }
    }

    override suspend fun postJson(
        path: String,
        body: JSONObject
    ): JSONObject = withContext(Dispatchers.IO) {

        val url = URL(baseURL + path)
        val conn = url.openConnection() as HttpURLConnection

        conn.requestMethod = "POST"
        conn.doOutput = true
        conn.setRequestProperty("Content-Type", "application/json")
        conn.connectTimeout = 10_000
        conn.readTimeout = 10_000

        conn.outputStream.use {
            it.write(body.toString().toByteArray())
        }

        val code = conn.responseCode
        val stream = if (code in 200..299) conn.inputStream else conn.errorStream

        val raw = stream?.bufferedReader()?.use(BufferedReader::readText).orEmpty()
        try {
            JSONObject(raw.ifBlank { "{}" })
        } catch (e: Exception) {
            JSONObject()
                .put("success", false)
                .put("error", raw.ifBlank { "Invalid JSON response (HTTP $code)" })
        }
    }
}
