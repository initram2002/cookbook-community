package it.polito.mad.cookbookcommunity.navigation

import androidx.navigation.NavController

class AppNavigation(private val navCtrl_r: NavController) {
    fun navigateToHome() {
        navCtrl_r.navigate(HomeRoute) {
            popUpTo<HomeRoute> {
                inclusive = false
                saveState = false
            }
            launchSingleTop = true
            restoreState = false
        }
    }

    fun navigateToOwnProfile() {
        navCtrl_r.navigate(OwnProfileRoute) {
            popUpTo<HomeRoute> {
                saveState = false
                inclusive = false
            }
            launchSingleTop = true
            restoreState = false
        }
    }

    fun navigateToOwnProfileFromContent() {
        navCtrl_r.navigate(OwnProfileRoute)
    }

    fun navigateToOtherProfile(userId: String) {
        navCtrl_r.navigate(OtherProfileRoute(userId)) {
            launchSingleTop = true
        }
    }

    fun navigateToExplore() {
        navCtrl_r.navigate(RecipesListRoute) {
            popUpTo<HomeRoute> {
                saveState = false
                inclusive = false
            }
            launchSingleTop = true
            restoreState = false
        }
    }

    fun navigateToRecipeDetail(recipeId: String, fromList: Boolean) {
        navCtrl_r.navigate(RecipeDetailRoute(recipeId, fromList)) {
            launchSingleTop = true
        }
    }

    fun navigateToPublishedRecipeFromCreate(recipeId: String) {
        navCtrl_r.navigate(RecipeDetailRoute(recipeId, false)) {
            popUpTo<CreateRecipeRoute> {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    fun navigateToPublishedRecipeFromAdapt(recipeId: String) {
        navCtrl_r.navigate(RecipeDetailRoute(recipeId, false)) {
            popUpTo<AdaptRecipeRoute> {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    fun navigateToRecipeListOwned(ownerId: String, isOwner: Boolean, ownerName: String) {
        navCtrl_r.navigate(RecipeListOwnedRoute(ownerId, isOwner, ownerName)) {
            launchSingleTop = true
        }
    }

    fun navigateToCreateRecipe() {
        navCtrl_r.navigate(CreateRecipeRoute) {
            popUpTo<HomeRoute> {
                saveState = false
                inclusive = false
            }
            launchSingleTop = true
            restoreState = false
        }
    }

    fun navigateToAdaptRecipe(sourceRecipeId: String) {
        navCtrl_r.navigate(AdaptRecipeRoute(sourceRecipeId)) {
            launchSingleTop = true
        }
    }

    fun navigateToNotifications() {
        navCtrl_r.navigate(NotificationsRoute) {
            launchSingleTop = true
        }
    }

    fun navigateToTriedRecipes(userId: String) {
        navCtrl_r.navigate(TriedRecipesRoute(userId)) {
            launchSingleTop = true
        }
    }

    fun navigateToSavedCollections() {
        navCtrl_r.navigate(SavedCollectionsRoute) {
            popUpTo<HomeRoute> {
                inclusive = false
                saveState = false
            }
            launchSingleTop = true
            restoreState = false
        }
    }

    fun navigateToAuthGraph() {
        navCtrl_r.navigate(AuthGraphRoute) {
            launchSingleTop = true
        }
    }

    fun navigateToSignUp() {
        navCtrl_r.navigate(SignUpAccountRoute) {
            launchSingleTop = true
        }
    }

    fun navigateToAuthEntry() {
        navCtrl_r.navigate(AuthEntryRoute) {
            launchSingleTop = true
        }
    }

    fun navigateToAuthPhotoRole() {
        navCtrl_r.navigate(AuthEntryPhotoRoleRoute) {
            launchSingleTop = true
        }
    }

    fun navigateToAuthPreferences() {
        navCtrl_r.navigate(AuthEntryPreferencesRoute) {
            launchSingleTop = true
        }
    }

    fun navigateBack() {
        navCtrl_r.popBackStack()
    }

    fun navigateToCollectionDetail(collectionId: String, collectionName: String) {
        navCtrl_r.navigate(
            CollectionDetailNavRoute(
                collectionId = collectionId,
                collectionName = collectionName
            )
        ) {
            launchSingleTop = true
        }
    }

    fun navigateToSaveToCollection(recipeId: String) {
        navCtrl_r.navigate(SaveToCollectionDialogRoute(recipeId)) {
            launchSingleTop = true
        }
    }

    fun navigateToAddReview(recipeId: String, reviewId: String? = null) {
        navCtrl_r.navigate(AddReviewRoute(recipeId, reviewId)) {
            launchSingleTop = true
        }
    }

    fun navigateToAddTip(recipeId: String) {
        navCtrl_r.navigate(AddTipRoute(recipeId)) {
            launchSingleTop = true
        }
    }

    fun navigateToRecipeReviews(recipeId: String) {
        navCtrl_r.navigate(RecipeReviewsNavRoute(recipeId)) {
            launchSingleTop = true
        }
    }
}
