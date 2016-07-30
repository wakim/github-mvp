package br.com.github.sample.tests

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.support.annotation.IdRes
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.Intents.intending
import android.support.test.espresso.intent.matcher.IntentMatchers.*
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import br.com.github.sample.R
import br.com.github.sample.activity.DetailActivity
import br.com.github.sample.activity.MainActivity
import br.com.github.sample.adapter.RecyclerViewAdapter
import br.com.github.sample.api.model.SearchNextPage
import br.com.github.sample.api.model.SearchResponse
import br.com.github.sample.api.model.UserRepositoriesResponse
import br.com.github.sample.application.TestApplication
import br.com.github.sample.controller.ApiControllerSpec
import br.com.github.sample.model.Repository
import br.com.github.sample.model.User
import br.com.github.sample.model.UserSearch
import br.com.github.sample.util.*
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import rx.Single
import java.util.*
import javax.inject.Inject

class MainActivityTest {

    companion object {
        val EMPTY_REPOSITORIES = emptyList<Repository>()

        val imageUrl = "http://www.nitwaa.in/media//1/profile_pictures/raghavender-mittapalli/raghavender-mittapalli-present.png"

        val USERS_SEARCH: List<UserSearch> = Collections.unmodifiableList(listOf(
                UserSearch("sample1", 10L, imageUrl, "https://www.github.com/sample1"),
                UserSearch("sample2", 11L, imageUrl, "https://www.github.com/sample2"),
                UserSearch("sample3", 12L, imageUrl, "https://www.github.com/sample3")
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
    val activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(
            MainActivity::class.java,
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

        System.out.println("Intents.init")
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
    fun shouldAppearHintWhenStart() {
        activityRule.launchActivity(Intent())
        onView(withId(R.id.et_search))
                .check(matches(withHint(R.string.search_user_or_repository)))
    }

    @Test
    fun shouldListUsers() {
        val query = "Teste"
        `when`(apiController.search(query, null))
                .thenReturn(SearchResponse(SearchNextPage(-1, -1), USERS_SEARCH).toSingle())

        activityRule.launchActivity(Intent())

        onView(withId(R.id.et_search)).apply {
            perform(clearText(), replaceText(query))
            perform(pressImeActionButton())
        }

        USERS_SEARCH.asSequence()
                .forEachIndexed { i, userSearch ->
                    onView(withRecyclerView(R.id.recycler_view).atPositionOnView(i, R.id.tv_person_name))
                        .check(matches(withText(userSearch.login)))
                }
    }

    @Test
    fun shouldListRepositoriesAfterUsers() {
        val query = "Teste"
        `when`(apiController.search(query, null))
                .thenReturn(SearchResponse(SearchNextPage(-1, -1), USERS_SEARCH + REPOSITORIES).toSingle())

        activityRule.launchActivity(Intent())

        onView(withId(R.id.et_search)).apply {
            perform(clearText(), replaceText(query))
            perform(pressImeActionButton())
        }

        USERS_SEARCH.asSequence()
                .forEachIndexed { i, userSearch ->
                    onView(withId(R.id.recycler_view))
                            .perform(scrollToPosition<RecyclerViewAdapter.RecyclerViewHolder<*>>(i))

                    onView(withRecyclerView(R.id.recycler_view).atPositionOnView(i, R.id.tv_person_name))
                            .check(matches(withText(USERS_SEARCH[i].login)))
                }

        val repositoryStart = USERS_SEARCH.size

        REPOSITORIES.asSequence()
                .forEachIndexed { i, repository ->
                    val repoI = repositoryStart + i

                    onView(withId(R.id.recycler_view))
                            .perform(scrollToPosition<RecyclerViewAdapter.RecyclerViewHolder<*>>(repoI))

                    onView(withRecyclerView(R.id.recycler_view).atPositionOnView(repoI, R.id.tv_repository_name))
                            .check(matches(withText(repository.fullName)))
                }
    }

    @Test
    fun shouldOpenDetailWhenClickOnUser() {
        val query = "Teste"

        `when`(apiController.search(query, null))
                .thenReturn(SearchResponse(SearchNextPage(-1, -1), USERS_SEARCH + REPOSITORIES).toSingle())

        `when`(apiController.getUser(USERS_SEARCH[0].login))
                .thenReturn((USERS[0] to UserRepositoriesResponse(emptyList(), false)).toSingle())

        activityRule.launchActivity(Intent())

        onView(withId(R.id.et_search)).apply {
            perform(clearText(), replaceText(query))
            perform(pressImeActionButton())
        }

        onView(withRecyclerView(R.id.recycler_view).atPosition(0))
                .perform(click())

        intended(hasComponent(DetailActivity::class.java.name))
    }

    @Test
    fun shouldOpenBrowserWhenClickOnRepository() {
        val query = "Teste"

        `when`(apiController.search(query, null))
                .thenReturn(SearchResponse(SearchNextPage(-1, -1), REPOSITORIES).toSingle())

        activityRule.launchActivity(Intent())

        onView(withId(R.id.et_search)).apply {
            perform(clearText(), replaceText(query))
            perform(pressImeActionButton())
        }

        onView(withRecyclerView(R.id.recycler_view).atPosition(0))
                .perform(click())

        intended(hasAction(Intent.ACTION_VIEW))
    }

    @Test
    fun shouldShowEmptyViewWhenNoResults() {
        val query = "Teste"

        `when`(apiController.search(query, null))
                .thenReturn(SearchResponse(SearchNextPage(-1, -1), EMPTY_REPOSITORIES).toSingle())

        activityRule.launchActivity(Intent())

        onView(withId(R.id.et_search)).apply {
            perform(clearText(), replaceText(query))
            perform(pressImeActionButton())
        }

        onView(withId(R.id.recycler_view)).check(recyclerViewAdapterCount(0))
    }

    @Test
    fun shouldLoadAgainWhenSwipeToRefresh() {
        val query = "Teste"

        `when`(apiController.search(query, null))
                .thenReturn(SearchResponse(SearchNextPage(-1, -1), USERS_SEARCH).toSingle())

        activityRule.launchActivity(Intent())

        onView(withId(R.id.et_search)).apply {
            perform(clearText(), replaceText(query))
            perform(pressImeActionButton())
        }

        onView(withId(R.id.recycler_view)).check(recyclerViewAdapterCount(USERS_SEARCH.size))
        onView(withId(R.id.swipe_refresh_layout)).perform(swipeDown())

        verify(apiController, times(2)).search(query, null)
        verifyNoMoreInteractions(apiController)
    }

    @Test
    fun shouldNotUpdateRecyclerViewWhenSwipeRefreshError() {
        val query = "Teste"

        `when`(apiController.search(query, null))
                .thenReturn(SearchResponse(SearchNextPage(-1, -1), USERS_SEARCH).toSingle())

        activityRule.launchActivity(Intent())

        onView(withId(R.id.et_search)).apply {
            perform(clearText(), replaceText(query))
            perform(pressImeActionButton())
        }

        onView(withId(R.id.recycler_view))
                .check(recyclerViewAdapterCount(USERS_SEARCH.size))

        `when`(apiController.search(query, null))
                .thenReturn(Single.error(NullPointerException()))

        onView(withId(R.id.swipe_refresh_layout))
                .perform(swipeDown())

        onView(withId(R.id.snackbar_text))
                .check(matches(allOf(isDisplayed(), withText(R.string.unknown_error))))

        verify(apiController, times(2)).search(query, null)
        verifyNoMoreInteractions(apiController)
    }

    @Test
    fun shouldLoadNextPageWhenReachListEnd() {
        val query = "Teste"
        val list = ArrayList(USERS_SEARCH)
        val newList = list.concat(list).concat(list).concat(list)
        val nextPage = SearchNextPage(1, 1)

        `when`(apiController.search(query, null))
                .thenReturn(SearchResponse(nextPage, newList).toSingle())

        `when`(apiController.search(query, nextPage))
                .thenReturn(SearchResponse(SearchNextPage(-1, -1), list).toSingle())

        activityRule.launchActivity(Intent())

        onView(withId(R.id.et_search)).apply {
            perform(clearText(), replaceText(query))
            perform(pressImeActionButton())
        }

        onView(withId(R.id.recycler_view))
                .perform(scrollToPosition<RecyclerViewAdapter.RecyclerViewHolder<*>>(newList.size - 1))

        onView(withId(R.id.recycler_view))
                .check(recyclerViewAdapterCount(newList.size + list.size))

        verify(apiController).search(query, null)
        verify(apiController).search(query, nextPage)

        verifyNoMoreInteractions(apiController)
    }

    @Test
    fun shouldSaveAndRestoreInstanceState() {
        val query = "Teste"

        `when`(apiController.search(query, null))
                .thenReturn(SearchResponse(SearchNextPage(-1, -1), USERS_SEARCH).toSingle())

        activityRule.launchActivity(Intent())

        onView(withId(R.id.et_search)).apply {
            perform(clearText(), replaceText(query))
            perform(pressImeActionButton())
        }

        onView(withId(R.id.recycler_view))
                .check(recyclerViewAdapterCount(USERS_SEARCH.size))

        activityRule.activity.rotateScreen()

        onView(withId(R.id.recycler_view))
                .check(recyclerViewAdapterCount(USERS_SEARCH.size))

        onView(withId(R.id.et_search))
                .check(ViewAssertions.matches(withText(query)))

        verify(apiController).search(query, null)
        verifyNoMoreInteractions(apiController)
    }
}

fun withRecyclerView(@IdRes id: Int) = RecyclerViewMatcher(id)
