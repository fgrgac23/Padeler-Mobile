package com.example.rampu2506_padeler.repositories

import com.example.rampu2506_padeler.api.ApiClient
import org.json.JSONObject

class CommentsRepository(
    private val api: ApiClient
) {
    suspend fun addComment(
        commenterId: Int,
        commentedId: Int,
        grade: Double,
        comment: String?
    ): Boolean {
        val body = JSONObject()
            .put("commenter_id", commenterId)
            .put("commented_id", commentedId)
            .put("grade", grade)
            .put("comment", comment)

        val res = api.postJson("api/comments/add_comment.php", body)
        return res.optBoolean("success", false)
    }

    suspend fun getMyRatedIds(commenterId: Int): Set<Int> {
        val res = api.getJson("api/comments/my_rated.php?commenter_id=$commenterId")
        if (!res.optBoolean("success", false)) return emptySet()

        val arr = res.optJSONArray("rated_ids") ?: return emptySet()
        val set = HashSet<Int>()
        for (i in 0 until arr.length()) {
            set.add(arr.optInt(i))
        }
        return set
    }

}