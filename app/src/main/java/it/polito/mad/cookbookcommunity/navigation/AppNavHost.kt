package it.polito.mad.cookbookcommunity.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navigation
import androidx.navigation.toRoute
import it.polito.mad.cookbookcommunity.data.AppContainer
import it.polito.mad.cookbookcommunity.session.SessionManager
import it.polito.mad.cookbookcommunity.ui.authentication.AuthEntryPhotoRoleScreen
import it.polito.mad.cookbookcommunity.ui.authentication.AuthEntryPreferencesScreen
import it.polito.mad.cookbookcommunity.ui.authentication.AuthEntryScreen
import it.polito.mad.cookbookcommunity.ui.authentication.SignUpAccountScreenRoute
import it.polito.mad.cookbookcommunity.ui.home.HomeScreenRoute
import it.polito.mad.cookbookcommunity.ui.notification.NotificationsRoute as NotificationsScreenRoute
import it.polito.mad.cookbookcommunity.ui.profile.CollectionDetailRoute
import it.polito.mad.cookbookcommunity.ui.profile.ProfileRoute
import it.polito.mad.cookbookcommunity.ui.profile.SaveToCollectionRoute
import it.polito.mad.cookbookcommunity.ui.profile.SavedRoute
import it.polito.mad.cookbookcommunity.ui.profile.TriedRecipesScreenRoute
import it.polito.mad.cookbookcommunity.ui.recipeProposal.NewRecipeProposalRoute
import it.polito.mad.cookbookcommunity.ui.recipeProposal.RecipeListRoute
import it.polito.mad.cookbookcommunity.ui.recipeProposal.RecipeProposalRoute
import it.polito.mad.cookbookcommunity.ui.recipeProposal.RecipeReviewsRoute
import it.polito.mad.cookbookcommunity.ui.recipeProposal.list.RecipeProposalListRoute
import it.polito.mad.cookbookcommunity.ui.review.AddReviewScreenRoute
import it.polito.mad.cookbookcommunity.ui.review.AddTipScreenRoute
import it.polito.mad.cookbookcommunity.viewmodel.NewRecipeStep

@Composable
fun AppNavHost(
    navController: NavHostController,
    appNavigation: AppNavigation,
    appContainer: AppContainer,
    modifier: Modifier = Modifier,
    navigateIfLoggedIn: (PostLoginDestination, () -> Unit) -> Unit,
    navigateAfterSuccessfulLogin: () -> Unit,
    onNewRecipeStepChanged: (NewRecipeStep) -> Unit,
    onNewRecipeFormDataChanged: (Boolean) -> Unit,
    onRegisterNewRecipeReset: (() -> Unit) -> Unit,
    onProfileEditingChanged: (Boolean) -> Unit,
    onSignOutClick: () -> Unit,
    onFeedback: (String) -> Unit,
) {
    val authRepository = appContainer.authRepository
    val recipeRepository = appContainer.recipeRepository
    val userProfileRepository = appContainer.userProfileRepository
    val favoriteCollectionRepository = appContainer.favoriteCollectionRepository
    val favoriteRepository = appContainer.favoriteRepository
    val reviewRepository = appContainer.reviewRepository
    val notificationRepository = appContainer.notificationRepository
    val triedRecipeRepository = appContainer.triedRecipeRepository
    val tipRepository = appContainer.tipRepository
    val deleteRecipeProposalUseCase = appContainer.deleteRecipeProposalUseCase

    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier
    ) {
        composable<HomeRoute> {
            HomeScreenRoute(
                repository = recipeRepository,
                notificationRepository = notificationRepository,
                onRecipeClick = { id ->
                    appNavigation.navigateToRecipeDetail(id, true)
                },
                onSeeAllClick = {
                    appNavigation.navigateToExplore()
                },
                onNotificationsClick = {
                    navigateIfLoggedIn(PostLoginDestination.NOTIFICATIONS) {
                        appNavigation.navigateToNotifications()
                    }
                },
                onFeedback = { message ->
                    onFeedback(message)
                }
            )
        }

        composable<OwnProfileRoute> {
            val currentAuthUser by authRepository.currentUser.collectAsStateWithLifecycle()
            val userId = currentAuthUser?.uid ?: return@composable

            ProfileRoute(
                profileUserId = userId,
                viewerUserId = userId,
                userProfileRepository = userProfileRepository,
                onExitProfile = {
                    appNavigation.navigateBack()
                },
                myProfile = true,
                onEditingChanged = { isEditing ->
                    onProfileEditingChanged(isEditing)
                },
                onMyRecipesClick = { ownerId ->
                    appNavigation.navigateToRecipeListOwned(
                        ownerId,
                        true,
                        "My"
                    )
                },
                onTriedRecipesClick = { ownerId ->
                    appNavigation.navigateToTriedRecipes(ownerId)
                },
                onSavedClick = {
                    appNavigation.navigateToSavedCollections()
                },
                onSignOutClick = onSignOutClick
            )
        }

        composable<OtherProfileRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<OtherProfileRoute>()

            ProfileRoute(
                profileUserId = args.userId,
                viewerUserId = SessionManager.CURRENT_LOGGED_IN_USER_ID,
                userProfileRepository = userProfileRepository,
                onExitProfile = {
                    appNavigation.navigateBack()
                },
                myProfile = false,
                onMyRecipesClick = { ownerId ->
                    appNavigation.navigateToRecipeListOwned(
                        ownerId,
                        false,
                        "Their"
                    )
                }
            )
        }

        composable<RecipesListRoute> {
            RecipeProposalListRoute(
                repository = recipeRepository,
                onBackClick = {
                    appNavigation.navigateBack()
                },
                onRecipeClick = { id ->
                    appNavigation.navigateToRecipeDetail(id, true)
                },
                showBackButton = false
            )
        }

        composable<RecipeDetailRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<RecipeDetailRoute>()

            RecipeProposalRoute(
                recipeId = args.recipeId,
                repository = recipeRepository,
                userProfileRepository = userProfileRepository,
                reviewRepository = reviewRepository,
                tipRepository = tipRepository,
                deleteRecipeProposalUseCase = deleteRecipeProposalUseCase,
                onBackClick = {
                    appNavigation.navigateBack()
                },
                onDuplicateClick = { sourceRecipeId ->
                    navigateIfLoggedIn(PostLoginDestination.PUBLISH) {
                        appNavigation.navigateToAdaptRecipe(sourceRecipeId)
                    }
                },
                onAuthorProfileClick = { userId ->
                    if (userId == SessionManager.CURRENT_LOGGED_IN_USER_ID) {
                        appNavigation.navigateToOwnProfileFromContent()
                    } else {
                        appNavigation.navigateToOtherProfile(userId)
                    }
                },
                onSaveToCollectionClick = { recipeId ->
                    navigateIfLoggedIn(PostLoginDestination.SAVED) {
                        appNavigation.navigateToSaveToCollection(recipeId)
                    }
                },
                onAddReviewClick = { recipeId, reviewId ->
                    navigateIfLoggedIn(PostLoginDestination.PROFILE) {
                        appNavigation.navigateToAddReview(recipeId, reviewId)
                    }
                },
                onAddTipClick = { recipeId ->
                    navigateIfLoggedIn(PostLoginDestination.PROFILE) {
                        appNavigation.navigateToAddTip(recipeId)
                    }
                },
                onSeeAllReviewsClick = { recipeId ->
                    appNavigation.navigateToRecipeReviews(recipeId)
                }
            )
        }

        composable<RecipeListOwnedRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<RecipeListOwnedRoute>()

            RecipeListRoute(
                ownerId = args.ownerId,
                isOwnerView = args.isOwner,
                ownerDisplayName = args.ownerName,
                repository = recipeRepository,
                deleteRecipeProposalUseCase = deleteRecipeProposalUseCase,
                onBackClick = {
                    appNavigation.navigateBack()
                },
                onRecipeClick = { id ->
                    appNavigation.navigateToRecipeDetail(id, false)
                }
            )
        }

        composable<CreateRecipeRoute> {
            NewRecipeProposalRoute(
                repository = recipeRepository,
                onBackClick = {
                    appNavigation.navigateBack()
                },
                onPublished = { id ->
                    appNavigation.navigateToPublishedRecipeFromCreate(id)
                },
                onStepChanged = { step ->
                    onNewRecipeStepChanged(step)
                },
                onFormDataChanged = { hasDraftData ->
                    onNewRecipeFormDataChanged(hasDraftData)
                },
                onRegisterReset = { reset ->
                    onRegisterNewRecipeReset(reset)
                },
                onFeedback = { message ->
                    onFeedback(message)
                }
            )
        }

        composable<AdaptRecipeRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<AdaptRecipeRoute>()

            NewRecipeProposalRoute(
                repository = recipeRepository,
                sourceRecipeId = args.sourceRecipeId,
                onBackClick = {
                    appNavigation.navigateBack()
                },
                onPublished = { id ->
                    appNavigation.navigateToPublishedRecipeFromAdapt(id)
                },
                onStepChanged = { step ->
                    onNewRecipeStepChanged(step)
                },
                onFormDataChanged = { hasDraftData ->
                    onNewRecipeFormDataChanged(hasDraftData)
                },
                onRegisterReset = { reset ->
                    onRegisterNewRecipeReset(reset)
                },
                onFeedback = { message ->
                    onFeedback(message)
                }
            )
        }

        composable<NotificationsRoute> {
            val currentAuthUser by authRepository.currentUser.collectAsStateWithLifecycle()
            val userId = currentAuthUser?.uid ?: return@composable

            NotificationsScreenRoute(
                userId = userId,
                repository = notificationRepository,
                onBackClick = {
                    appNavigation.navigateBack()
                },
                onRecipeClick = { recipeId ->
                    appNavigation.navigateToRecipeDetail(recipeId, false)
                },
                onReviewsClick = { recipeId ->
                    appNavigation.navigateToRecipeReviews(recipeId)
                },
                onFeedback = { message ->
                    onFeedback(message)
                }
            )
        }

        navigation<AuthGraphRoute>(
            startDestination = SignUpAccountRoute
        ) {
            composable<SignUpAccountRoute> {
                SignUpAccountScreenRoute(
                    authRepository = authRepository,
                    userRepository = userProfileRepository,
                    onLoginSuccess = {
                        onFeedback("Login successful.")
                        navigateAfterSuccessfulLogin()
                    },
                    onCreateAccount = {
                        appNavigation.navigateToAuthEntry()
                    },
                    onFeedback = { message ->
                        onFeedback(message)
                    }
                )
            }

            composable<AuthEntryRoute> {
                AuthEntryScreen(
                    onNext = {
                        appNavigation.navigateToAuthPhotoRole()
                    },
                    onBack = {
                        appNavigation.navigateBack()
                    }
                )
            }

            composable<AuthEntryPhotoRoleRoute> {
                AuthEntryPhotoRoleScreen(
                    onNext = {
                        appNavigation.navigateToAuthPreferences()
                    },
                    onBack = {
                        appNavigation.navigateBack()
                    }
                )
            }

            composable<AuthEntryPreferencesRoute> {
                AuthEntryPreferencesScreen(
                    onCreationSuccess = {
                        navController.navigate(SignUpAccountRoute) {
                            popUpTo<AuthGraphRoute> {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    },
                    onBack = {
                        appNavigation.navigateBack()
                    }
                )
            }
        }

        composable<AddReviewRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<AddReviewRoute>()

            AddReviewScreenRoute(
                recipeId = args.recipeId,
                initialReviewId = args.reviewId,
                recipeRepository = recipeRepository,
                reviewRepository = reviewRepository,
                triedRecipeRepository = triedRecipeRepository,
                onBackClick = {
                    appNavigation.navigateBack()
                },
                onPublished = {
                    onFeedback("Review published.")
                    appNavigation.navigateBack()
                },
                onFeedback = { message ->
                    onFeedback(message)
                }
            )
        }

        composable<AddTipRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<AddTipRoute>()

            AddTipScreenRoute(
                recipeId = args.recipeId,
                recipeRepository = recipeRepository,
                tipRepository = tipRepository,
                onBackClick = {
                    appNavigation.navigateBack()
                },
                onPublished = {
                    appNavigation.navigateBack()
                }
            )
        }

        composable<TriedRecipesRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<TriedRecipesRoute>()

            TriedRecipesScreenRoute(
                userId = args.userId,
                triedRecipeRepository = triedRecipeRepository,
                recipeRepository = recipeRepository,
                reviewRepository = reviewRepository,
                onBackClick = {
                    appNavigation.navigateBack()
                },
                onRecipeClick = { recipeId ->
                    appNavigation.navigateToRecipeDetail(recipeId, false)
                }
            )
        }

        composable<SavedCollectionsRoute> {
            SavedRoute(
                ownerId = SessionManager.CURRENT_LOGGED_IN_USER_ID,
                repository = favoriteCollectionRepository,
                onCollectionClick = { collectionId, collectionName ->
                    appNavigation.navigateToCollectionDetail(
                        collectionId = collectionId,
                        collectionName = collectionName
                    )
                }
            )
        }

        composable<CollectionDetailNavRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<CollectionDetailNavRoute>()

            CollectionDetailRoute(
                collectionId = args.collectionId,
                collectionName = args.collectionName,
                repository = favoriteCollectionRepository,
                favoriteRepository = favoriteRepository,
                recipeProposalRepository = recipeRepository,
                onBackClick = {
                    appNavigation.navigateBack()
                },
                onRecipeClick = { recipeId ->
                    appNavigation.navigateToRecipeDetail(recipeId, false)
                }
            )
        }

        dialog<SaveToCollectionDialogRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<SaveToCollectionDialogRoute>()

            SaveToCollectionRoute(
                ownerId = SessionManager.CURRENT_LOGGED_IN_USER_ID,
                recipeId = args.recipeId,
                repository = favoriteCollectionRepository,
                favoriteRepository = favoriteRepository,
                recipeRepository = recipeRepository,
                onDismiss = {
                    appNavigation.navigateBack()
                },
                onFeedback = { message ->
                    onFeedback(message)
                }
            )
        }

        composable<RecipeReviewsNavRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<RecipeReviewsNavRoute>()

            RecipeReviewsRoute(
                recipeId = args.recipeId,
                recipeRepository = recipeRepository,
                reviewRepository = reviewRepository,
                onBackClick = {
                    appNavigation.navigateBack()
                }
            )
        }
    }
}