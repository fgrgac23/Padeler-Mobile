package com.example.rampu2506_padeler.repositories

import com.example.rampu2506_padeler.api.ApiClient
import com.example.rampu2506_padeler.database.UsersDAO
import com.example.rampu2506_padeler.entities.User
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject

class UsersRepository(
    private val dao: UsersDAO,
    private val api: ApiClient
) {
    fun observeAllLocal(): Flow<List<User>> = dao.observeAll()

    suspend fun getByIdLocal(userId: Int): User? = dao.getById(userId)
    suspend fun upsertLocal(user: User) = dao.upsert(user)
    suspend fun upsertAllLocal(users: List<User>) = dao.upsertAll(users)

    /**
     * Server -> Room cache for a single user.
     * Returns cached User, or null if server did not return expected payload.
     */
    private suspend fun fetchAndCacheUser(userId: Int): User? {
        val res = api.getJson(
            "api/users/get_user.php",
            mapOf("user_id" to userId.toString())
        )
        if (!res.optBoolean("success", false)) return null

        // Defensive: some errors return {success:false,...} or missing "user"
        val userObj = res.optJSONObject("user") ?: return null
        val user = JsonMappers.userFromJson(userObj)
        dao.upsert(user)
        return user
    }

    /**
     * Force refresh of the logged-in user from server.
     * Use this in UI screens that depend on fresh fields like Latitude/Longitude.
     */
    suspend fun refreshUser(userId: Int): User? = fetchAndCacheUser(userId)

    /** POST /api/users/login.php -> {success:true,user_id:int} */
    suspend fun login(username: String, password: String): User? {
        val body = JSONObject()
            .put("username", username)
            .put("password", password)

        val res = api.postJson("api/users/login.php", body)
        if (!res.optBoolean("success", false)) return null
        return fetchAndCacheUser(res.getInt("user_id"))
    }

    /** POST /api/users/register.php -> {success:true,user_id:int} */
    suspend fun register(user: User, plainPassword: String): User? {
        val res = api.postJson(
            "api/users/register.php",
            JsonMappers.userToRegisterJson(user, plainPassword)
        )
        if (!res.optBoolean("success", false)) return null
        return fetchAndCacheUser(res.getInt("user_id"))
    }

    /** POST /api/users/update_profile.php -> {success:true} */
    suspend fun updateProfile(user: User): User? {
        val res = api.postJson(
            "api/users/update_profile.php",
            JsonMappers.userToUpdateJson(user)
        )
        if (!res.optBoolean("success", false)) return null
        return fetchAndCacheUser(user.userId)
    }

    /** GET /api/users/nearby.php?lat=..&lng=..&radius=..&gender=..&level=..&position=..&frequency=.. */
    suspend fun fetchNearby(
        currentUserId: Int,
        lat: Double,
        lng: Double,
        radius: Int,
        gender: String? = null,
        level: String? = null,
        position: String? = null,
        frequency: String? = null
    ): List<User> {

        val params = mutableMapOf(
            "current_user_id" to currentUserId.toString(),
            "lat" to lat.toString(),
            "lng" to lng.toString(),
            "radius" to radius.toString()
        )

        if (!gender.isNullOrBlank()) params["gender"] = gender.trim()
        if (!level.isNullOrBlank()) params["level"] = level.trim()
        if (!position.isNullOrBlank()) params["position"] = position.trim()
        if (!frequency.isNullOrBlank()) params["frequency"] = frequency.trim()

        val res = api.getJson("api/users/nearby.php", params)

        if (!res.optBoolean("success", false)) return emptyList()

        val users = JsonMappers.usersFromJsonArray(res.getJSONArray("users"))
        dao.upsertAll(users)
        return users
    }


    suspend fun updateLocation(userId: Int, lat: Double, lng: Double): Boolean {
        val body = JSONObject()
            .put("user_id", userId)
            .put("lat", lat)
            .put("lng", lng)

        val res = api.postJson("api/users/update_location.php", body)
        return res.optBoolean("success", false)
    }


    suspend fun fetchUserImage(userId: Int): Pair<ByteArray?, String?> {
        val res = api.getJson(
            "api/users/get_user_image.php",
            mapOf("user_id" to userId.toString())
        )
        if (!res.optBoolean("success", false)) return null to null

        val base64 = res.optString("image_base64", null)
        val mime = res.optString("mime_type", null)

        if (base64.isNullOrBlank() || mime.isNullOrBlank()) return null to null

        return try {
            val bytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
            bytes to mime
        } catch (_: Exception) {
            null to null
        }
    }

    suspend fun deleteUser(userId: Int): Boolean{
        val res = api.getJson(
            "api/users/delete_user.php",
            mapOf("user_id" to userId.toString())
        )
        if (!res.optBoolean("success", false))
            return false
        dao.deleteUser(userId)
        return true
    }
}