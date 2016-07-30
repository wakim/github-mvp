package br.com.github.sample.tests

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.Intents.intending
import android.support.test.espresso.intent.matcher.IntentMatchers.isInternal
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import br.com.github.sample.R
import br.com.github.sample.activity.DetailActivity
import br.com.github.sample.api.model.UserRepositoriesResponse
import br.com.github.sample.application.TestApplication
import br.com.github.sample.controller.ApiControllerSpec
import br.com.github.sample.model.Repository
import br.com.github.sample.model.User
import br.com.github.sample.model.UserSearch
import br.com.github.sample.util.DisableAnimationsRule
import br.com.github.sample.util.collapsingToolbarTitle
import br.com.github.sample.util.recyclerViewAdapterCount
import br.com.github.sample.util.toSingle
import org.hamcrest.Matchers
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import java.util.*
import javax.inject.Inject

class DetailActivityTest {

    companion object {
        val imageUrl = "http://www.nitwaa.in/media//1/profile_pictures/raghavender-mittapalli/raghavender-mittapalli-present.png"

        val USERS_SEARCH: List<UserSearch> = Collections.unmodifiableList(listOf(
                UserSearch("sample1", 10L, MainActivityTest.imageUrl, "https://www.github.com/sample1"),
                UserSearch("sample2", 11L, MainActivityTest.imageUrl, "https://www.github.com/sample2"),
                UserSearch("sample3", 12L, MainActivityTest.imageUrl, "https://www.github.com/sample3")
        ))

        val USERS: List<User> = Collections.unmodifiableList(listOf(
                User("Sample 1", imageUrl, "Company 1", "https://www.google.com", "Rio de Janeiro",
                        "1@sample.com", false, "User Sample 1", 10, 10, 10, 10, Date(), Date()),
                User("Sample 2", imageUrl, "Company 2", "https://www.google.com", "Rio de Janeiro",
                        "2@sample.com", false, "User Sample 2", 10, 10, 10, 10, Date(), Date()),
                User("Sample 3", imageUrl, "Company 3", "https://www.google.com", "Rio de Janeiro",
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
    val activityRule: ActivityTestRule<DetailActivity> = ActivityTestRule(
            DetailActivity::class.java,
            true, // initialTouchMode
            false)   // launchActivity. False so we can customize the intent per test method

    @Rule @JvmField
    val disableAnimationsRule = DisableAnimationsRule()

    @Inject
    lateinit var apiController: ApiControllerSpec

    @Before
    fun setUp() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val app = instrumentation.targetContext.applicationContext as TestApplication

        (app.testAppComponent).inject(this)

        // Bad Smell. Must reset because ApiController is @Singleton
        reset(apiController)

        Intents.init()

        // By default Espresso Intents does not stub any Intents. Stubbing needs to be setup before
        // every test run. In this case all external Intents will be blocked.
        intending(not(isInternal()))
                .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))
    }

    @After
    fun tearDown() {
        Intents.release()
    }

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

    fun verifyUser(user: User) {
        val res = activityRule.activity.resources

        onView(withId(R.id.collapsing_toolbar))
                .check(collapsingToolbarTitle(user.name))

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