package com.example.rampu2506_padeler.repositories

import com.example.rampu2506_padeler.entities.Notification
import com.example.rampu2506_padeler.entities.User
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale
import android.util.Base64

object JsonMappers {
    private val df = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun userFromJson(o: JSONObject): User {
        val blockedInt = when (val b = o.opt("Blocked")) {
            is Number -> b.toInt()
            is String -> b.toIntOrNull() ?: 0
            is Boolean -> if (b) 1 else 0
            else -> 0
        }

        return User(
            userId = o.getInt("UserId"),
            username = o.getString("Username"),
            email = o.optString("Email", ""),
            passwordHash = o.optString("Password_hash", ""),
            phone = o.optString("Phone", null),
            name = o.optString("Name", null),
            surname = o.optString("Surname", null),
            gender = o.optString("Gender", null),
            dateOfBirth = o.optString("DateOfBirth", null)?.let { df.parse(it) },
            frequencyOfPlay = o.optString("FrequencyOfPlaying", null),
            levelOfPlay = o.optString("Level", null),
            position = o.optString("Position", null),
            rating = if (o.has("Rating") && !o.isNull("Rating")) o.getDouble("Rating") else null,
            numberOfRatings = if (o.has("NumberOfRatings") && !o.isNull("NumberOfRatings")) o.getInt("NumberOfRatings") else null,
            numOfSwipes = if (o.has("SwipeNum") && !o.isNull("SwipeNum")) o.getInt("SwipeNum") else null,
            latitude = if (o.has("Latitude") && !o.isNull("Latitude")) o.getDouble("Latitude") else null,
            longitude = if (o.has("Longitude") && !o.isNull("Longitude")) o.getDouble("Longitude") else null,
            blocked = blockedInt == 1,
            image = null,
            mimeType = null,
            distance = if (o.has("distance_km") && !o.isNull("distance_km")) o.getDouble("distance_km") else null
        )
    }

    fun usersFromJsonArray(arr: JSONArray): List<User> =
        (0 until arr.length()).map { i -> userFromJson(arr.getJSONObject(i)) }

    fun userToRegisterJson(user: User, plainPassword: String): JSONObject {
        val o = JSONObject()
            .put("username", user.username)
            .put("email", user.email)
            .put("password", plainPassword)
            .put("phone", user.phone)
            .put("name", user.name)
            .put("surname", user.surname)
            .put("gender", user.gender)
            .put("date_of_birth", user.dateOfBirth?.let { df.format(it) })
            .put("frequency", user.frequencyOfPlay)
            .put("level", user.levelOfPlay)
            .put("position", user.position)
            .put("lat", user.latitude)
            .put("lng", user.longitude)

        val bytes = user.image
        val mime = user.mimeType
        if (bytes != null && bytes.isNotEmpty() && !mime.isNullOrBlank()) {
            o.put("mime_type", mime)
            o.put("image_base64", Base64.encodeToString(bytes, Base64.NO_WRAP))
        }

        return o
    }


    fun userToUpdateJson(user: User): JSONObject {
        val o = JSONObject()
            .put("user_id", user.userId)
            .put("email", user.email)
            .put("username", user.username)
            .put("phone", user.phone)
            .put("name", user.name)
            .put("surname", user.surname)
            .put("gender", user.gender)
            .put("date_of_birth", user.dateOfBirth?.let { df.format(it) })
            .put("frequency", user.frequencyOfPlay)
            .put("level", user.levelOfPlay)
            .put("position", user.position)
            .put("lat", user.latitude)
            .put("lng", user.longitude)
        val bytes = user.image
        val mime = user.mimeType
        if (bytes != null && bytes.isNotEmpty() && !mime.isNullOrBlank()) {
            o.put("mime_type", mime)
            o.put("image_base64", Base64.encodeToString(bytes, Base64.NO_WRAP))
        }

        return o
    }


    fun notificationFromJson(o: JSONObject, userId: Int): Notification {
        val isReadInt = when (val r = o.opt("IsRead")) {
            is Number -> r.toInt()
            is String -> r.toIntOrNull() ?: 0
            is Boolean -> if (r) 1 else 0
            else -> 0
        }

        return Notification(
            notificationId = o.getInt("NotificationId"),
            userId = userId,
            type = o.getString("Type"),
            title = o.getString("Title"),
            content = o.getString("Content"),
            createdAt = df.parse(o.getString("CreatedAt"))!!,
            isRead = isReadInt == 1
        )
    }

    fun notificationsFromJsonArray(arr: JSONArray, userId: Int): List<Notification> =
        (0 until arr.length()).map { i -> notificationFromJson(arr.getJSONObject(i), userId) }
}
