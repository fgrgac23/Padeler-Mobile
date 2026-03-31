package com.example.rampu2506_padeler.api

import org.json.JSONObject
interface ApiClient {
    suspend fun getJson(path: String, query: Map<String, String> = emptyMap()): JSONObject
    suspend fun postJson(path: String, body: JSONObject): JSONObject
}