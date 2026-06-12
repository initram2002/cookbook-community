package it.polito.mad.cookbookcommunity.data.repository

import it.polito.mad.cookbookcommunity.model.review.TriedRecipeLog
import kotlinx.coroutines.flow.Flow

interface TriedRecipeRepository {
    fun getTriedRecipesByUser(userId: String): Flow<List<TriedRecipeLog>>

    suspend fun addTriedRecipe(log: TriedRecipeLog)

    suspend fun deleteTriedRecipe(logId: String)

    suspend fun deleteTriedRecipesByRecipe(recipeId: String)
}