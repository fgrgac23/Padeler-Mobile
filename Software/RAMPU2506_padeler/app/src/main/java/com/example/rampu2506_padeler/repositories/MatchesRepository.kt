package com.example.rampu2506_padeler.repositories

import com.example.rampu2506_padeler.api.ApiClient
import com.example.rampu2506_padeler.entities.MatchItem
import org.json.JSONArray
import org.json.JSONObject

data class SwipeResult(
    val status: String,
    val matched: Boolean
)

class MatchesRepository(
    private val api: ApiClient
) {
    suspend fun swipe(fromUserId: Int, toUserId: Int, response: String): SwipeResult? {
        val body = JSONObject()
            .put("from_user_id", fromUserId)
            .put("to_user_id", toUserId)
            .put("response", response)

        val res = api.postJson("api/match/swipe.php", body)
        if (!res.optBoolean("success", false)) return null
        return SwipeResult(
            status = res.optString("status", "PENDING"),
            matched = res.optBoolean("matched", false)
        )
    }
    /**
     * GET /api/match/get_my_matches.php?user_id=..
     * Response:
     * { success:true, matches:[ {matchId, otherUserId, otherName, otherSurname, otherPhone}, ... ] }
     */
    suspend fun fetchMyMatches(userId: Int): List<MatchItem> {
        val res = api.getJson(
            "api/match/get_my_matches.php",
            mapOf("user_id" to userId.toString())
        )
        if (!res.optBoolean("success", false)) return emptyList()

        val arr = res.optJSONArray("matches") ?: return emptyList()
        return matchesFromJsonArray(arr)
    }
    private fun matchesFromJsonArray(arr: JSONArray): List<MatchItem> {
        val out = ArrayList<MatchItem>(arr.length())

        for (i in 0 until arr.length()) {
            val o = arr.optJSONObject(i) ?: continue

            val matchId = o.optInt("matchId", -1)
            val otherUserId = o.optInt("otherUserId", -1)
            val otherName = o.optString("otherName", "")
            val otherSurname = o.optString("otherSurname", "")
            val otherPhone = o.optString("otherPhone", "")

            if (matchId <= 0 || otherUserId <= 0) continue
            if (otherName.isBlank() || otherSurname.isBlank() || otherPhone.isBlank()) continue

            out.add(
                MatchItem(
                    matchId = matchId,
                    otherUserId = otherUserId,
                    otherName = otherName,
                    otherSurname = otherSurname,
                    otherPhone = otherPhone
                )
            )
        }
        return out
    }
}
