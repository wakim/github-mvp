package br.com.github.sample.tests

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.Intents.intending
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.support.test.espresso.intent.matcher.IntentMatchers.isInternal
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import br.com.github.sample.R
import br.com.github.sample.application.TestApplication
import br.com.github.sample.common.util.concat
import br.com.github.sample.common.util.toObservable
import br.com.github.sample.data.RepositoryDataSource
import br.com.github.sample.data.UserDataSource
import br.com.github.sample.data.model.User
import br.com.github.sample.data.model.UserSearch
import br.com.github.sample.data.remote.model.RepositorySearchResponse
import br.com.github.sample.data.remote.model.SearchNextPage
import br.com.github.sample.data.remote.model.UserRepositoriesResponse
import br.com.github.sample.data.remote.model.UserSearchResponse
import br.com.github.sample.ui.RecyclerViewAdapter
import br.com.github.sample.ui.search.SearchActivity
import br.com.github.sample.ui.search.usersearch.UserSearchFragment
import br.com.github.sample.ui.userdetail.UserDetailActivity
import br.com.github.sample.util.*
import io.reactivex.Observable
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import java.util.*
import javax.inject.Inject

class UserSearchFragmentTest {

    companion object {
        val EMPTY_USERS = emptyList<UserSearch>()

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
    }

    @Rule @JvmField
    val screenshotRule: ScreenshotTestRule = ScreenshotTestRule()

    @Rule @JvmField
    val activityRule: ActivityTestRule<SearchActivity> = ActivityTestRule(
            SearchActivity::class.java,
            true, // initialTouchMode
            false)   // launchActivity. False so we can customize the intent per test method

    @Rule @JvmField
    val disableAnimationsRule = DisableAnimationsRule()

    @Inject
    lateinit var userDataSource: UserDataSource

    @Inject
    lateinit var repositoryDataSource: RepositoryDataSource

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

        `when`(repositoryDataSource.search(anyString(), any()))
                .thenReturn(RepositorySearchResponse(emptyList(), null).toObservable())
    }

    @After
    fun tearDown() {
        try {
            Intents.release()
        } catch (ignored: Throwable) { ignored.printStackTrace() }
    }

    @Test
    fun shouldAppearHintWhenStart() {
        activityRule.launchActivity(Intent())

        onView(withId(R.id.et_search))
                .check(matches(withHint(R.string.search_user_or_repository)))
    }

    @Test
    fun shouldListUsers() {
        val query = "Teste"

        `when`(userDataSource.search(query, null))
                .thenReturn(UserSearchResponse(USERS_SEARCH, SearchNextPage(-1)).toObservable())

        activityRule.launchActivity(Intent())

        doSearch(query)

        USERS_SEARCH.asSequence()
                .forEachIndexed { i, userSearch ->
                    onView(withRecyclerViewTag(UserSearchFragment.RECYCLER_VIEW_TAG)
                            .atPositionOnView(i, R.id.tv_person_name))
                            .check(matches(withText(userSearch.login)))
                }
    }

    @Test
    fun shouldOpenDetailWhenClickOnUser() {
        val query = "Teste"

        `when`(userDataSource.search(query, null))
                .thenReturn(UserSearchResponse(USERS_SEARCH, null).toObservable())

        `when`(userDataSource.getUser(USERS_SEARCH[0].login))
                .thenReturn((USERS[0] to UserRepositoriesResponse(emptyList(), null)).toObservable())

        activityRule.launchActivity(Intent())

        doSearch(query)

        onView(withRecyclerViewTag(UserSearchFragment.RECYCLER_VIEW_TAG)
                .atPosition(0))
                .perform(click())

        intended(hasComponent(UserDetailActivity::class.java.name))
    }

    @Test
    fun shouldShowEmptyViewWhenNoResults() {
        val query = "Teste"

        `when`(userDataSource.search(query, null))
                .thenReturn(UserSearchResponse(EMPTY_USERS, null).toObservable())

        activityRule.launchActivity(Intent())

        doSearch(query)

        onView(withTag(UserSearchFragment.RECYCLER_VIEW_TAG))
                .check(recyclerViewAdapterCount(0))

        onView(withTag(UserSearchFragment.EMPTY_VIEW_TAG))
                .check(matches(allOf(isDisplayed(), withText(R.string.no_users_found))))

        verify(userDataSource).search(query, null)
        verifyNoMoreInteractions(userDataSource)
    }

    @Test
    fun shouldLoadAgainWhenSwipeToRefresh() {
        val query = "Teste"

        `when`(userDataSource.search(query, null))
                .thenReturn(UserSearchResponse(USERS_SEARCH, null).toObservable())

        activityRule.launchActivity(Intent())

        doSearch(query)

        onView(withTag(UserSearchFragment.RECYCLER_VIEW_TAG)).check(recyclerViewAdapterCount(USERS_SEARCH.size))
        onView(withTag(UserSearchFragment.SWIPE_REFRESH_TAG)).perform(swipeDown())

        verify(userDataSource, times(2)).search(query, null)
        verifyNoMoreInteractions(userDataSource)
    }

    @Test
    fun shouldNotUpdateRecyclerViewWhenSwipeRefreshError() {
        val query = "Teste"

        `when`(userDataSource.search(query, null))
                .thenReturn(UserSearchResponse(USERS_SEARCH, null).toObservable(),
                        Observable.error<UserSearchResponse>(NullPointerException()))

        activityRule.launchActivity(Intent())

        doSearch(query)

        onView(withTag(UserSearchFragment.RECYCLER_VIEW_TAG))
                .check(recyclerViewAdapterCount(USERS_SEARCH.size))

        onView(withTag(UserSearchFragment.SWIPE_REFRESH_TAG))
                .perform(swipeDown())

        onView(withId(R.id.snackbar_text))
                .check(matches(allOf(isDisplayed(), withText(R.string.error_loading_users))))

        verify(userDataSource, times(2)).search(query, null)
        verifyNoMoreInteractions(userDataSource)
    }

    @Test
    fun shouldLoadNextPageWhenReachListEnd() {
        val query = "Teste"
        val list = ArrayList(USERS_SEARCH)
        val newList = list.concat(list).concat(list).concat(list)
        val nextPage = SearchNextPage(2)

        `when`(userDataSource.search(query, null))
                .thenReturn(UserSearchResponse(newList, nextPage).toObservable())

        `when`(userDataSource.search(query, nextPage))
                .thenReturn(UserSearchResponse(newList, null).toObservable())

        activityRule.launchActivity(Intent())

        doSearch(query)

        onView(withTag(UserSearchFragment.RECYCLER_VIEW_TAG))
                .perform(scrollToPosition<RecyclerViewAdapter.RecyclerViewHolder<*>>(newList.size - 1))

        onView(withTag(UserSearchFragment.RECYCLER_VIEW_TAG))
                .check(recyclerViewAdapterCount(newList.size + list.size))

        verify(userDataSource).search(query, null)
        verify(userDataSource).search(query, nextPage)

        verifyNoMoreInteractions(userDataSource)
    }

    @Test
    fun shouldSaveAndRestoreInstanceState() {
        val query = "Teste"

        `when`(userDataSource.search(query, null))
                .thenReturn(UserSearchResponse(USERS_SEARCH, null).toObservable())

        activityRule.launchActivity(Intent())

        doSearch(query)

        onView(withTag(UserSearchFragment.RECYCLER_VIEW_TAG))
                .check(recyclerViewAdapterCount(USERS_SEARCH.size))

        activityRule.activity.rotateScreen()

        onView(withTag(UserSearchFragment.RECYCLER_VIEW_TAG))
                .check(recyclerViewAdapterCount(USERS_SEARCH.size))

        onView(withId(R.id.et_search))
                .check(ViewAssertions.matches(withText(query)))

        verify(userDataSource).search(query, null)
        verifyNoMoreInteractions(userDataSource)
    }

    fun doSearch(text: String) {
        onView(withId(R.id.et_search)).apply {
            perform(clearText(), replaceText(text))
            perform(pressImeActionButton())
        }
    }
}
