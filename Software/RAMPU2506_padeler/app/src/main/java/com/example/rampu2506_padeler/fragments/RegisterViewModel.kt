package com.example.rampu2506_padeler.fragments

import androidx.lifecycle.ViewModel
import java.util.Date
import android.net.Uri

class RegisterViewModel : ViewModel() {
    var name: String = ""
    var surname: String = ""
    var dateOfBirth: Date? = null
    var gender: String = ""

    var username: String = ""
    var email: String = ""
    var password: String = ""
    var phoneNumber: String = ""

    var levelOfPlay: String = ""
    var frequencyOfPlay: String = ""
    var position: String = ""

    var geoLat: Double? = null
    var geoLong: Double? = null

    var imageUri: Uri? = null
}