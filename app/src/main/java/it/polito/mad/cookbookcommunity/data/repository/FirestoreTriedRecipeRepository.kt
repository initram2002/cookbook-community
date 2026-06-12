package it.polito.mad.cookbookcommunity.data.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObjects
import it.polito.mad.cookbookcommunity.data.firestore.FirestoreCollections
import it.polito.mad.cookbookcommunity.data.firestore.FirestoreCollections.TriedRecipe as TriedRecipeFirestore
import it.polito.mad.cookbookcommunity.model.review.TriedRecipeLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreTriedRecipeRepository : TriedRecipeRepository {
    private val db = Firebase.firestore

    private val collection
        get() = db.collection(FirestoreCollections.TRIED_RECIPES)

    override fun getTriedRecipesByUser(userId: String): Flow<List<TriedRecipeLog>> =
        collection
            .whereEqualTo(TriedRecipeFirestore.USER_ID, userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects<TriedRecipeLog>()
                    .sortedByDescending { it.cookedAt }
            }

    override suspend fun addTriedRecipe(log: TriedRecipeLog) {
        validateTriedRecipeLog(log)

        collection
            .document(log.id)
            .set(log.toFirestoreMap())
            .await()
    }

    override suspend fun deleteTriedRecipe(logId: String) {
        collection
            .document(logId)
            .delete()
            .await()
    }

    override suspend fun deleteTriedRecipesByRecipe(recipeId: String) {
        val logs = collection
            .whereEqualTo(TriedRecipeFirestore.RECIPE_ID, recipeId)
            .get()
            .await()

        for (doc in logs.documents) {
            doc.reference.delete().await()
        }
    }

    private fun TriedRecipeLog.toFirestoreMap(): Map<String, Any?> = mapOf(
        TriedRecipeFirestore.ID to id,
        TriedRecipeFirestore.RECIPE_ID to recipeId,
        TriedRecipeFirestore.USER_ID to userId,
        TriedRecipeFirestore.REVIEW_ID to reviewId,
        TriedRecipeFirestore.COOKED_AT to cookedAt,
        TriedRecipeFirestore.NOTES to notes
    )

    private fun validateTriedRecipeLog(log: TriedRecipeLog) {
        require(log.recipeId.isNotBlank()) { "A tried recipe log must be linked to a recipe." }
        require(log.userId.isNotBlank()) { "A tried recipe log must be linked to a user." }
    }
}
