package br.com.github.sample.tests

import android.app.Activity
import android.app.Instrumentation
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.Intents.intending
import android.support.test.espresso.intent.matcher.IntentMatchers.isInternal
import android.support.test.rule.ActivityTestRule
import br.com.github.sample.application.TestApplication
import br.com.github.sample.data.UserDataSource
import br.com.github.sample.data.model.Repository
import br.com.github.sample.data.model.User
import br.com.github.sample.data.model.UserSearch
import br.com.github.sample.ui.userdetail.UserDetailActivity
import br.com.github.sample.util.DisableAnimationsRule
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.mockito.Mockito.reset
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

    /*
    @Test
    fun shouldShowUserInfo() {
        val username = USERS_SEARCH.first().login
        val user = USERS.first()

        `when`(apiController.getUser(username))
                .thenReturn((user to UserRepositoriesResponse(emptyList(), false)).toSingle())

        activityRule.launchActivity(Intent().putExtra(DetailActivity.USERNAME_EXTRA, username))

        verifyUser(user)

        verify(apiController).getUser(username)
        verifyNoMoreInteractions(apiController)
    }

    @Test
    fun shouldShowSnackWhenErrorLoadingUser() {
        val username = USERS_SEARCH.first().login

        `when`(apiController.getUser(username))
                .thenReturn(NullPointerException().toSingle())

        activityRule.launchActivity(Intent().putExtra(DetailActivity.USERNAME_EXTRA, username))

        onView(withId(R.id.snackbar_text))
                .check(matches(Matchers.allOf(ViewMatchers.isDisplayed(), withText(R.string.unknown_error))))

        verify(apiController).getUser(username)

        verifyNoMoreInteractions(apiController)
    }

    @Test
    fun shouldLoadAfterErrorWhenClickSnackBar() {
        val username = USERS_SEARCH.first().login
        val user = USERS.first()

        `when`(apiController.getUser(username))
                .thenReturn(NullPointerException().toSingle())

        activityRule.launchActivity(Intent().putExtra(DetailActivity.USERNAME_EXTRA, username))

        `when`(apiController.getUser(username))
                .thenReturn((user to UserRepositoriesResponse(emptyList(), false)).toSingle())

        onView(withId(R.id.snackbar_action))
                .perform(click())

        verify(apiController, times(2)).getUser(username)

        verifyNoMoreInteractions(apiController)
    }

    @Test
    fun shouldShowRepositories() {
        val username = USERS_SEARCH.first().login
        val user = USERS.first()

        `when`(apiController.getUser(username))
                .thenReturn((user to UserRepositoriesResponse(REPOSITORIES, false)).toSingle())

        activityRule.launchActivity(Intent().putExtra(DetailActivity.USERNAME_EXTRA, username))

        verifyUser(user)

        onView(withId(R.id.recycler_view))
                .check(recyclerViewAdapterCount(REPOSITORIES.size + 1))

        verify(apiController).getUser(username)

        verifyNoMoreInteractions(apiController)
    }

    @Test
    fun shouldShowRepositoriesInfo() {
        val username = USERS_SEARCH.first().login
        val user = USERS.first()

        `when`(apiController.getUser(username))
                .thenReturn((user to UserRepositoriesResponse(REPOSITORIES, false)).toSingle())

        activityRule.launchActivity(Intent().putExtra(DetailActivity.USERNAME_EXTRA, username))

        REPOSITORIES.asSequence()
                .forEachIndexed { i, repository ->
                    onView(withId(R.id.recycler_view))
                            .perform(scrollToPosition<RecyclerViewAdapter.RecyclerViewHolder<*>>(i + 1))

                    onView(withRecyclerView(R.id.recycler_view).atPositionOnView(i + 1, R.id.tv_repository_name))
                            .check(matches(withText(repository.fullName)))
                }

        verify(apiController).getUser(username)

        verifyNoMoreInteractions(apiController)
    }

    @Test
    fun shouldSaveAndRestoreInstanceState() {
        val username = USERS_SEARCH.first().login
        val user = USERS.first()

        `when`(apiController.getUser(username))
                .thenReturn((user to UserRepositoriesResponse(REPOSITORIES, false)).toSingle())

        activityRule.launchActivity(Intent().putExtra(DetailActivity.USERNAME_EXTRA, username))

        activityRule.activity.rotateScreen()

        onView(withId(R.id.recycler_view))
                .check(recyclerViewAdapterCount(REPOSITORIES.size + 1))

        verify(apiController).getUser(username)

        verifyNoMoreInteractions(apiController)
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
    */
}