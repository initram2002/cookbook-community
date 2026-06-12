package it.polito.mad.cookbookcommunity

import android.app.Application
import it.polito.mad.cookbookcommunity.data.AppContainer
import it.polito.mad.cookbookcommunity.data.seed.SeedUserProfiles
import it.polito.mad.cookbookcommunity.session.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class CookBookCommunityApp : Application() {

    val appContainer: AppContainer by lazy {
        AppContainer()
    }

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        appContainer
        ensureSystemCollectionsForCurrentUser()
        seedFirestoreIfNeeded()
    }

    private fun ensureSystemCollectionsForCurrentUser() {
        val userIds = SeedUserProfiles.items.map { it.internalId } +
                listOf(SessionManager.CURRENT_LOGGED_IN_USER_ID)

        appScope.launch {
            userIds.distinct().forEach { userId ->
                appContainer.favoriteCollectionRepository.ensureSystemCollections(userId)
            }
        }
    }

    private fun seedFirestoreIfNeeded() {
        val userId = SessionManager.CURRENT_LOGGED_IN_USER_ID
        appScope.launch {
            try {
                appContainer.seedRepository.seedIfEmpty(
                    loggedUserId = userId,
                    loggedUserDisplayName = "Current User"
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
