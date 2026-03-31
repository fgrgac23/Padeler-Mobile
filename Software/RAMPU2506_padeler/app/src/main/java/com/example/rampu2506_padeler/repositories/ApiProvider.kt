package com.example.rampu2506_padeler.repositories

import com.example.rampu2506_padeler.api.ApiClient
import com.example.rampu2506_padeler.api.ApiClientImpl
object ApiProvider {
    val api: ApiClient by lazy { ApiClientImpl() }
}
