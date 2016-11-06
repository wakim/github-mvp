package br.com.github.sample.tests

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.Intents.intending
import android.support.test.espresso.intent.matcher.IntentMatchers.isInternal
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import br.com.github.sample.R
import br.com.github.sample.application.TestApplication
import br.com.github.sample.common.util.toObservable
import br.com.github.sample.data.UserDataSource
import br.com.github.sample.data.model.Repository
import br.com.github.sample.data.model.User
import br.com.github.sample.data.model.UserSearch
import br.com.github.sample.data.remote.model.UserRepositoriesResponse
import br.com.github.sample.ui.RecyclerViewAdapter
import br.com.github.sample.ui.userdetail.UserDetailActivity
import br.com.github.sample.util.*
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import java.util.*
import javax.inject.Inject

class UserDetailActivityTest {

    companion object {
        val imageUrl = "http://www.nitwaa.in/media//1/profile_pictures/raghavender-mittapalli/raghavender-mittapalli-present.png"

        val USERS_SEARCH: List<UserSearch> = Collections.unmodifiableList(listOf(
                UserSearch("sample1", 10L, imageUrl, "https://www.github.com/sample1"),
                UserSearch("sample2", 11L, imageUrl, "https://www.github.com/sample2"),
                UserSearch("sample3", 12L, imageUrl, "https://www.github.com/sample3")
        ))

        val USERS: List<User> = Collections.unmodifiableList(listOf(
                User("Sample 1", "sample1", imageUrl, "Company 1", "https://www.google.com", "Rio de Janeiro",
                        "1@sample.com", false, "User Sample 1", 10, 10, 10, 10, Date(), Date()),
                User("Sample 2", "sample2", imageUrl, "Company 2", "https://www.google.com", "Rio de Janeiro",
                        "2@sample.com", false, "User Sample 2", 10, 10, 10, 10, Date(), Date()),
                User("Sample 3", "sample3", imageUrl, "Company 3", "https://www.google.com", "Rio de Janeiro",
                        "3@sample.com", false, "User Sample 3", 10, 10, 10, 10, Date(), Date())
        ))

        val REPOSITORIES: List<Repository> = Collections.unmodifiableList(listOf(
                Repository("Repository 1", "sample/repository 1", "Sample Repository 1",
                        "https://www.github.com/sample/repository1", 100, 100, 100, 0, "Kotlin"
                ),
                Repository("Repository 2", "sample/repository2", "Sample Repository 2",
                        "https://www.github.com/sample/repository2", 100, 100, 100, 0, "Kotlin"
                )
        ))
    }

    @Rule @JvmField
    val activityRule: ActivityTestRule<UserDetailActivity> = ActivityTestRule(
            UserDetailActivity::class.java,
            true, // initialTouchMode
            false)   // launchActivity. False so we can customize the intent per test method

    @Rule @JvmField
    val disableAnimationsRule = DisableAnimationsRule()

    @Inject
    lateinit var userDataSource: UserDataSource

    @Before
    fun setUp() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val app = instrumentation.targetContext.applicationContext as TestApplication

        (app.testAppComponent).inject(this)

        // Bad Smell. Must reset because UserRepository is @Singleton
        reset(userDataSource)

        Intents.init()

        // By default Espresso Intents does not stub any Intents. Stubbing needs to be setup before
        // every test run. In this case all external Intents will be blocked.
        intending(not(isInternal()))
                .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))
    }

    @After
    fun tearDown() {
        try {
            Intents.release()
        } catch (ignored: Throwable) { ignored.printStackTrace() }
    }

    @Test
    fun shouldShowUserInfo() {
        val username = USERS_SEARCH.first().login
        val user = USERS.first()

        `when`(userDataSource.getUser(username))
                .thenReturn((user to UserRepositoriesResponse(emptyList(), null)).toObservable())

        activityRule.launchActivity(Intent().putExtra(UserDetailActivity.USERNAME_EXTRA, username))

        verifyUser(user)

        verify(userDataSource).getUser(username)
        verifyNoMoreInteractions(userDataSource)
    }

    @Test
    fun shouldShowSnackWhenErrorLoadingUser() {
        val username = USERS_SEARCH.first().login

        `when`(userDataSource.getUser(username))
                .thenReturn(NullPointerException().toObservable())

        activityRule.launchActivity(Intent().putExtra(UserDetailActivity.USERNAME_EXTRA, username))

        onView(withId(R.id.snackbar_text))
                .check(matches(allOf(isDisplayed(), withText(R.string.error_loading_user))))

        verify(userDataSource).getUser(username)

        verifyNoMoreInteractions(userDataSource)
    }

    @Test
    fun shouldLoadAfterErrorWhenClickSnackBar() {
        val username = USERS_SEARCH.first().login
        val user = USERS.first()

        `when`(userDataSource.getUser(username))
                .thenReturn(NullPointerException().toObservable())

        activityRule.launchActivity(Intent().putExtra(UserDetailActivity.USERNAME_EXTRA, username))

        `when`(userDataSource.getUser(username))
                .thenReturn((user to UserRepositoriesResponse(emptyList(), null)).toObservable())

        onView(withId(R.id.snackbar_action))
                .perform(click())

        verify(userDataSource, times(2)).getUser(username)

        verifyNoMoreInteractions(userDataSource)
    }

    @Test
    fun shouldShowRepositories() {
        val username = USERS_SEARCH.first().login
        val user = USERS.first()

        `when`(userDataSource.getUser(username))
                .thenReturn((user to UserRepositoriesResponse(REPOSITORIES, null)).toObservable())

        activityRule.launchActivity(Intent().putExtra(UserDetailActivity.USERNAME_EXTRA, username))

        verifyUser(user)

        onView(withId(R.id.recycler_view))
                .check(recyclerViewAdapterCount(REPOSITORIES.size + 1))

        verify(userDataSource).getUser(username)

        verifyNoMoreInteractions(userDataSource)
    }

    @Test
    fun shouldPresentEmptyRepositories() {
        val username = USERS_SEARCH.first().login
        val user = USERS.first()

        `when`(userDataSource.getUser(username))
                .thenReturn((user to UserRepositoriesResponse(emptyList(), null)).toObservable())

        activityRule.launchActivity(Intent().putExtra(UserDetailActivity.USERNAME_EXTRA, username))

        verifyUser(user)

        onView(withId(R.id.recycler_view))
                .check(recyclerViewAdapterCount(1))

        onView(withRecyclerViewId(R.id.recycler_view).atPositionOnView(0, R.id.tv_repositories_header))
                .check(matches(withText(R.string.no_repositories_found)))

        verify(userDataSource).getUser(username)

        verifyNoMoreInteractions(userDataSource)
    }

    @Test
    fun shouldShowRepositoriesInfo() {
        val username = USERS_SEARCH.first().login
        val user = USERS.first()

        `when`(userDataSource.getUser(username))
                .thenReturn((user to UserRepositoriesResponse(REPOSITORIES, null)).toObservable())

        activityRule.launchActivity(Intent().putExtra(UserDetailActivity.USERNAME_EXTRA, username))

        REPOSITORIES.asSequence()
                .forEachIndexed { i, repository ->
                    onView(withId(R.id.recycler_view))
                            .perform(scrollToPosition<RecyclerViewAdapter.RecyclerViewHolder<*>>(i + 1))

                    onView(withRecyclerViewId(R.id.recycler_view).atPositionOnView(i + 1, R.id.tv_repository_name))
                            .check(matches(withText(repository.fullName)))
                }

        verify(userDataSource).getUser(username)

        verifyNoMoreInteractions(userDataSource)
    }

    @Test
    fun shouldSaveAndRestoreInstanceState() {
        val username = USERS_SEARCH.first().login
        val user = USERS.first()

        `when`(userDataSource.getUser(username))
                .thenReturn((user to UserRepositoriesResponse(REPOSITORIES, null)).toObservable())

        activityRule.launchActivity(Intent().putExtra(UserDetailActivity.USERNAME_EXTRA, username))

        activityRule.activity.rotateScreen()

        onView(withId(R.id.recycler_view))
                .check(recyclerViewAdapterCount(REPOSITORIES.size + 1))

        verify(userDataSource).getUser(username)

        verifyNoMoreInteractions(userDataSource)
    }

    fun verifyUser(user: User) {
        val res = activityRule.activity.resources

        onView(withId(R.id.collapsing_toolbar))
                .check(collapsingToolbarTitle(user.name ?: user.login))

        onView(withId(R.id.tv_followers))
                .check(matches(withText(res.getString(R.string.followers, user.followers))))

        onView(withId(R.id.tv_following))
                .check(matches(withText(res.getString(R.string.following, user.following))))

        onView(withId(R.id.tv_public_repos))
                .check(matches(withText(res.getString(R.string.public_repos, user.publicRepos))))

        onView(withId(R.id.tv_public_gists))
                .check(matches(withText(res.getString(R.string.public_gists, user.publicGists))))

        onView(withId(R.id.tv_bio))
                .check(matches(withText(user.bio)))

        onView(withId(R.id.tv_blog))
                .check(matches(withText(user.blog)))

        onView(withId(R.id.tv_email))
                .check(matches(withText(user.email)))

        onView(withId(R.id.tv_location))
                .check(matches(withText(user.location)))

        onView(withId(R.id.tv_hireable))
                .check(matches(withText(if (user.hireable) "✓" else "×")))
    }
}