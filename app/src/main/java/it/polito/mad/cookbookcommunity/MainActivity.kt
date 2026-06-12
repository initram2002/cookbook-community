package it.polito.mad.cookbookcommunity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import it.polito.mad.cookbookcommunity.data.favorite.FavoriteCollectionRepository
import androidx.navigation.toRoute
import it.polito.mad.cookbookcommunity.data.auth.AuthRepository
import it.polito.mad.cookbookcommunity.data.favorite.FavoriteRepository
import it.polito.mad.cookbookcommunity.data.notification.NotificationRepository
import it.polito.mad.cookbookcommunity.data.recipe.RecipeRepository
import it.polito.mad.cookbookcommunity.data.review.ReviewRepository
import it.polito.mad.cookbookcommunity.data.repository.TipRepository
import it.polito.mad.cookbookcommunity.data.repository.TriedRecipeRepository
import it.polito.mad.cookbookcommunity.data.user.UserRepository
import it.polito.mad.cookbookcommunity.domain.usecase.DeleteRecipeProposalUseCase
import it.polito.mad.cookbookcommunity.navigation.AdaptRecipeRoute
import it.polito.mad.cookbookcommunity.navigation.AddReviewRoute
import it.polito.mad.cookbookcommunity.navigation.AddTipRoute
import it.polito.mad.cookbookcommunity.navigation.AppNavigation
import it.polito.mad.cookbookcommunity.navigation.AuthEntryPhotoRoleRoute
import it.polito.mad.cookbookcommunity.navigation.AuthEntryPreferencesRoute
import it.polito.mad.cookbookcommunity.navigation.AuthEntryRoute
import it.polito.mad.cookbookcommunity.navigation.AuthGraphRoute
import it.polito.mad.cookbookcommunity.navigation.CollectionDetailNavRoute
import it.polito.mad.cookbookcommunity.navigation.CreateRecipeRoute
import it.polito.mad.cookbookcommunity.navigation.HomeRoute
import it.polito.mad.cookbookcommunity.navigation.NotificationsRoute
import it.polito.mad.cookbookcommunity.navigation.OtherProfileRoute
import it.polito.mad.cookbookcommunity.navigation.OwnProfileRoute
import it.polito.mad.cookbookcommunity.navigation.RecipeDetailRoute
import it.polito.mad.cookbookcommunity.navigation.RecipeListOwnedRoute
import it.polito.mad.cookbookcommunity.navigation.RecipeReviewsNavRoute
import it.polito.mad.cookbookcommunity.navigation.RecipesListRoute
import it.polito.mad.cookbookcommunity.navigation.SaveToCollectionDialogRoute
import it.polito.mad.cookbookcommunity.navigation.SavedCollectionsRoute
import it.polito.mad.cookbookcommunity.navigation.SignUpAccountRoute
import it.polito.mad.cookbookcommunity.navigation.TriedRecipesRoute
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
import it.polito.mad.cookbookcommunity.ui.theme.CookBookCommunityTheme
import it.polito.mad.cookbookcommunity.viewmodel.NewRecipeStep
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val authRepository: AuthRepository by lazy {
        (application as CookBookCommunityApp).appContainer.authRepository
    }

    private val recipeRepository: RecipeRepository by lazy {
        (application as CookBookCommunityApp).appContainer.recipeRepository
    }

    private val userProfileRepository: UserRepository by lazy {
        (application as CookBookCommunityApp).appContainer.userProfileRepository
    }

    private val favoriteCollectionRepository: FavoriteCollectionRepository by lazy {
        (application as CookBookCommunityApp).appContainer.favoriteCollectionRepository
    }

    private val favoriteRepository: FavoriteRepository by lazy {
        (application as CookBookCommunityApp).appContainer.favoriteRepository
    }

    private val reviewRepository: ReviewRepository by lazy {
        (application as CookBookCommunityApp).appContainer.reviewRepository
    }

    private val notificationRepository: NotificationRepository by lazy {
        (application as CookBookCommunityApp).appContainer.notificationRepository
    }

    private val triedRecipeRepository: TriedRecipeRepository by lazy {
        (application as CookBookCommunityApp).appContainer.triedRecipeRepository
    }

    private val tipRepository: TipRepository by lazy {
        (application as CookBookCommunityApp).appContainer.tipRepository
    }

    private val deleteRecipeProposalUseCase: DeleteRecipeProposalUseCase by lazy {
        (application as CookBookCommunityApp).appContainer.deleteRecipeProposalUseCase
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CookBookCommunityTheme {
                MainHost(
                    recipeRepository = recipeRepository,
                    userProfileRepository = userProfileRepository,
                    favoriteCollectionRepository = favoriteCollectionRepository,
                    favoriteRepository = favoriteRepository,
                    reviewRepository = reviewRepository,
                    notificationRepository = notificationRepository,
                    triedRecipeRepository = triedRecipeRepository,
                    tipRepository = tipRepository,
                    deleteRecipeProposalUseCase = deleteRecipeProposalUseCase,
                    modifier = Modifier.fillMaxSize(),
                    authRepository = authRepository
                )
            }
        }
    }
}

private enum class PostLoginDestination {
    PROFILE,
    SAVED,
    PUBLISH,
    NOTIFICATIONS
}

@Composable
private fun MainHost(
    authRepository: AuthRepository,
    recipeRepository: RecipeRepository,
    userProfileRepository: UserRepository,
    favoriteCollectionRepository: FavoriteCollectionRepository,
    favoriteRepository: FavoriteRepository,
    reviewRepository: ReviewRepository,
    notificationRepository: NotificationRepository,
    triedRecipeRepository: TriedRecipeRepository,
    tipRepository: TipRepository,
    deleteRecipeProposalUseCase: DeleteRecipeProposalUseCase,
    modifier: Modifier = Modifier,
) {
    val navCtrl_r = rememberNavController()
    val appNavigation = remember(navCtrl_r) {
        AppNavigation(navCtrl_r)
    }

    val currentBackStack by navCtrl_r.currentBackStackEntryAsState()
    val currentDest = currentBackStack?.destination

    val currentAuthUser by authRepository.currentUser.collectAsStateWithLifecycle()
    val isUserAuthenticated = currentAuthUser != null
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    fun showFeedback(message: String) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    val bottomBarRoutes = listOf(
        HomeRoute,
        RecipesListRoute,
        CreateRecipeRoute,
        SavedCollectionsRoute,
        OwnProfileRoute
    )

    val isBottomBarScreen = bottomBarRoutes.any { route ->
        currentDest?.hasRoute(route::class) == true
    }

    var newRecipeCurrentStep by remember { mutableStateOf(NewRecipeStep.BASIC_INFO) }
    var newRecipeHasDraftData by remember { mutableStateOf(false) }
    var isProfileEditing by remember { mutableStateOf(false) }
    var postLoginDestination by remember {
        mutableStateOf<PostLoginDestination?>(null)
    }

    val isCreateRecipeRoute = currentDest?.hasRoute<CreateRecipeRoute>() == true
    val isAdaptRecipeRoute = currentDest?.hasRoute<AdaptRecipeRoute>() == true
    val isNewRecipeWizardRoute = isCreateRecipeRoute || isAdaptRecipeRoute

    val isBasicInfoStep =
        isNewRecipeWizardRoute &&
                newRecipeCurrentStep == NewRecipeStep.BASIC_INFO
    val isOwnProfileRoute = currentDest?.hasRoute<OwnProfileRoute>() == true

    val isSignInRoute =
        currentDest?.hasRoute<SignUpAccountRoute>() == true

    val isAuthEntryRoute =
        currentDest?.hasRoute<AuthEntryRoute>() == true ||
                currentDest?.hasRoute<AuthEntryPhotoRoleRoute>() == true ||
                currentDest?.hasRoute<AuthEntryPreferencesRoute>() == true

    val isAuthRoute = isSignInRoute || isAuthEntryRoute

    var isNavigating: (() -> Unit)? by remember { mutableStateOf(null) }
    var resetNewRecipeForm: (() -> Unit) by remember { mutableStateOf({}) }

    if (isNavigating != null) {
        AlertDialog(
            onDismissRequest = { isNavigating = null },
            title = { Text("Discard recipe draft?") },
            text = { Text("Leaving now will discard the data you've entered for this recipe.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val nav = isNavigating
                        isNavigating = null
                        resetNewRecipeForm()
                        nav?.invoke()
                    }
                ) {
                    Text("Discard")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { isNavigating = null }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    fun checkNavigateFromNewRecipe(navigate: () -> Unit) {
        if (isNewRecipeWizardRoute && newRecipeHasDraftData) {
            isNavigating = navigate
        } else {
            navigate()
        }
    }

    fun navigateToLoginFor(destination: PostLoginDestination) {
        postLoginDestination = destination
        appNavigation.navigateToAuthGraph()
    }

    fun navigateAfterSuccessfulLogin() {
        val destination = postLoginDestination
        postLoginDestination = null

        when (destination) {
            PostLoginDestination.SAVED -> {
                navCtrl_r.navigate(SavedCollectionsRoute) {
                    popUpTo<HomeRoute> {
                        inclusive = false
                        saveState = false
                    }
                    launchSingleTop = true
                    restoreState = false
                }
            }

            PostLoginDestination.PUBLISH -> {
                navCtrl_r.navigate(CreateRecipeRoute) {
                    popUpTo<HomeRoute> {
                        inclusive = false
                        saveState = false
                    }
                    launchSingleTop = true
                    restoreState = false
                }
            }

            PostLoginDestination.NOTIFICATIONS -> {
                navCtrl_r.navigate(NotificationsRoute) {
                    popUpTo<HomeRoute> {
                        inclusive = false
                        saveState = false
                    }
                    launchSingleTop = true
                    restoreState = false
                }
            }

            PostLoginDestination.PROFILE,
            null -> {
                navCtrl_r.navigate(OwnProfileRoute) {
                    popUpTo<HomeRoute> {
                        inclusive = false
                        saveState = false
                    }
                    launchSingleTop = true
                    restoreState = false
                }
            }
        }
    }

    fun navigateIfLoggedIn(
        destinationAfterLogin: PostLoginDestination,
        navigateWhenLoggedIn: () -> Unit
    ) {
        if (isUserAuthenticated) {
            navigateWhenLoggedIn()
        } else {
            showFeedback("Sign in to continue.")
            navigateToLoginFor(destinationAfterLogin)
        }
    }

    val isProtectedRoute =
        currentDest?.hasRoute<OwnProfileRoute>() == true ||
                currentDest?.hasRoute<CreateRecipeRoute>() == true ||
                currentDest?.hasRoute<AdaptRecipeRoute>() == true ||
                currentDest?.hasRoute<NotificationsRoute>() == true ||
                currentDest?.hasRoute<SavedCollectionsRoute>() == true ||
                currentDest?.hasRoute<CollectionDetailNavRoute>() == true ||
                currentDest?.hasRoute<TriedRecipesRoute>() == true ||
                currentDest?.hasRoute<AddReviewRoute>() == true ||
                currentDest?.hasRoute<AddTipRoute>() == true ||
                currentDest?.hasRoute<SaveToCollectionDialogRoute>() == true

    LaunchedEffect(isUserAuthenticated, currentDest) {
        if (!isUserAuthenticated && isProtectedRoute) {
            showFeedback("Sign in to continue.")
            navigateToLoginFor(
                when {
                    currentDest?.hasRoute<CreateRecipeRoute>() == true ||
                            currentDest?.hasRoute<AdaptRecipeRoute>() == true -> {
                        PostLoginDestination.PUBLISH
                    }

                    currentDest?.hasRoute<SavedCollectionsRoute>() == true ||
                            currentDest?.hasRoute<CollectionDetailNavRoute>() == true ||
                            currentDest?.hasRoute<SaveToCollectionDialogRoute>() == true -> {
                        PostLoginDestination.SAVED
                    }

                    currentDest?.hasRoute<NotificationsRoute>() == true -> {
                        PostLoginDestination.NOTIFICATIONS
                    }

                    else -> {
                        PostLoginDestination.PROFILE
                    }
                }
            )
        }
    }

    val showBottomBar =
        (isBottomBarScreen || isSignInRoute) &&
                (!isCreateRecipeRoute || isBasicInfoStep) &&
                !(isOwnProfileRoute && isProfileEditing)

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentDest?.hasRoute<HomeRoute>() == true,
                        onClick = {
                            checkNavigateFromNewRecipe {
                                postLoginDestination = null
                                appNavigation.navigateToHome()
                            }
                        },
                        icon = {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home") }
                    )

                    NavigationBarItem(
                        selected = currentDest?.hasRoute<RecipesListRoute>() == true,
                        onClick = {
                            checkNavigateFromNewRecipe {
                                postLoginDestination = null
                                appNavigation.navigateToExplore()
                            }
                        },
                        icon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Explore"
                            )
                        },
                        label = { Text("Explore") }
                    )

                    NavigationBarItem(
                        selected =
                            currentDest?.hasRoute<CreateRecipeRoute>() == true ||
                                    (isSignInRoute && postLoginDestination == PostLoginDestination.PUBLISH),
                        onClick = {
                            checkNavigateFromNewRecipe {
                                navigateIfLoggedIn(
                                    destinationAfterLogin = PostLoginDestination.PUBLISH,
                                    navigateWhenLoggedIn = {
                                        appNavigation.navigateToCreateRecipe()
                                    }
                                )
                            }
                        },
                        icon = {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Create Recipe"
                            )
                        },
                        label = { Text("Publish") }
                    )

                    NavigationBarItem(
                        selected = currentDest?.hasRoute<SavedCollectionsRoute>() == true ||
                                (isSignInRoute && postLoginDestination == PostLoginDestination.SAVED),
                        onClick = {
                            checkNavigateFromNewRecipe {
                                navigateIfLoggedIn(
                                    destinationAfterLogin = PostLoginDestination.SAVED,
                                    navigateWhenLoggedIn = {
                                        appNavigation.navigateToSavedCollections()
                                    }
                                )
                            }
                        },
                        icon = {
                            Icon(
                                Icons.Default.Bookmark,
                                contentDescription = "My Collections"
                            )
                        },
                        label = { Text("Saved") }
                    )

                    NavigationBarItem(
                        selected =
                            currentDest?.hasRoute<OwnProfileRoute>() == true ||
                                    (isSignInRoute &&
                                            (postLoginDestination == PostLoginDestination.PROFILE ||
                                                    postLoginDestination == null)) ||
                                    isAuthEntryRoute,
                        onClick = {
                            checkNavigateFromNewRecipe {
                                navigateIfLoggedIn(
                                    destinationAfterLogin = PostLoginDestination.PROFILE,
                                    navigateWhenLoggedIn = {
                                        appNavigation.navigateToOwnProfile()
                                    }
                                )
                            }
                        },
                        icon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Profile"
                            )
                        },
                        label = { Text("Profile") }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navCtrl_r,
            startDestination = HomeRoute,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
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
                        navigateIfLoggedIn(
                            destinationAfterLogin = PostLoginDestination.NOTIFICATIONS,
                            navigateWhenLoggedIn = {
                                appNavigation.navigateToNotifications()
                            }
                        )
                    },
                    onFeedback = { message ->
                        showFeedback(message)
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
                    onEditingChanged = {
                        isProfileEditing = it
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
                    onSignOutClick = {
                        coroutineScope.launch {
                            SessionManager.signOut()

                            postLoginDestination = PostLoginDestination.PROFILE

                            navCtrl_r.navigate(AuthGraphRoute) {
                                popUpTo<HomeRoute> {
                                    inclusive = false
                                    saveState = false
                                }
                                launchSingleTop = true
                                restoreState = false
                            }
                        }
                    }
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
                        navigateIfLoggedIn(
                            destinationAfterLogin = PostLoginDestination.PUBLISH,
                            navigateWhenLoggedIn = {
                                appNavigation.navigateToAdaptRecipe(sourceRecipeId)
                            }
                        )
                    },
                    onAuthorProfileClick = { userId ->
                        if (userId == SessionManager.CURRENT_LOGGED_IN_USER_ID) {
                            appNavigation.navigateToOwnProfileFromContent()
                        } else {
                            appNavigation.navigateToOtherProfile(userId)
                        }
                    },
                    onSaveToCollectionClick = { recipeId ->
                        navigateIfLoggedIn(
                            destinationAfterLogin = PostLoginDestination.SAVED,
                            navigateWhenLoggedIn = {
                                appNavigation.navigateToSaveToCollection(recipeId)
                            }
                        )
                    },
                    onAddReviewClick = { recipeId, reviewId ->
                        navigateIfLoggedIn(
                            destinationAfterLogin = PostLoginDestination.PROFILE,
                            navigateWhenLoggedIn = {
                                appNavigation.navigateToAddReview(recipeId, reviewId)
                            }
                        )
                    },
                    onAddTipClick = { recipeId ->
                        navigateIfLoggedIn(
                            destinationAfterLogin = PostLoginDestination.PROFILE,
                            navigateWhenLoggedIn = {
                                appNavigation.navigateToAddTip(recipeId)
                            }
                        )
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
                        newRecipeCurrentStep = step
                    },
                    onFormDataChanged = { hasDraftData ->
                        newRecipeHasDraftData = hasDraftData
                    },
                    onRegisterReset = { reset ->
                        resetNewRecipeForm = reset
                    },
                    onFeedback = { message ->
                        showFeedback(message)
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
                        newRecipeCurrentStep = step
                    },
                    onFormDataChanged = { hasDraftData ->
                        newRecipeHasDraftData = hasDraftData
                    },
                    onRegisterReset = { reset ->
                        resetNewRecipeForm = reset
                    },
                    onFeedback = { message ->
                        showFeedback(message)
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
                        showFeedback(message)
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
                            showFeedback("Login successful.")
                            navigateAfterSuccessfulLogin()
                        },
                        onCreateAccount = {
                            appNavigation.navigateToAuthEntry()
                        },
                        onFeedback = { message ->
                            showFeedback(message)
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
                            navCtrl_r.navigate(SignUpAccountRoute) {
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
                        showFeedback("Review published.")
                        appNavigation.navigateBack()
                    },
                    onFeedback = { message ->
                        showFeedback(message)
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
                        showFeedback(message)
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
}
