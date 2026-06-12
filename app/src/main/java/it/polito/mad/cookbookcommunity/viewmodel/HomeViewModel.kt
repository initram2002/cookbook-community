package it.polito.mad.cookbookcommunity.viewmodel

import androidx.lifecycle.ViewModel
import it.polito.mad.cookbookcommunity.data.recipe.RecipeRepository
import it.polito.mad.cookbookcommunity.model.recipe.RecipeProposal
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import androidx.lifecycle.viewModelScope

data class HomeUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val forYouRecipes: List<RecipeProposal> = emptyList(),
    val newRecipes: List<RecipeProposal> = emptyList()
)

class HomeViewModel(
    repository: RecipeRepository
): ViewModel() {

    val uiState = repository.getAllRecipeProposals()
        .catch { throwable ->
            emit(emptyList())
        }
        .map { allRecipes ->
            HomeUiState(
                isLoading = false,
                forYouRecipes = allRecipes.take(4),
                newRecipes = allRecipes
                    .sortedByDescending { it.createdAt }
                    .take(4)
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState()
        )
}