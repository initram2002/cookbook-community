package it.polito.mad.cookbookcommunity.model.review

import java.util.UUID

data class TriedRecipeLog(
    val id: String = UUID.randomUUID().toString(),
    val recipeId: String = "",
    val userId: String = "",
    val reviewId: String? = null,
    val cookedAt: Long = System.currentTimeMillis(),
    val notes: String? = null
) {
    fun belongsTo(userId: String): Boolean = this.userId == userId

    fun hasLinkedReview(): Boolean = !reviewId.isNullOrBlank()
}