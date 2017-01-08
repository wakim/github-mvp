package br.com.github.sample.tests

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.Intents.intending
import android.support.test.espresso.intent.matcher.IntentMatchers.hasAction
import android.support.test.espresso.intent.matcher.IntentMatchers.isInternal
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import br.com.github.sample.R
import br.com.github.sample.application.TestApplication
import br.com.github.sample.common.util.concat
import br.com.github.sample.common.util.toObservable
import br.com.github.sample.data.RepositoryDataSource
import br.com.github.sample.data.UserDataSource
import br.com.github.sample.data.model.Repository
import br.com.github.sample.data.remote.model.RepositorySearchResponse
import br.com.github.sample.data.remote.model.SearchNextPage
import br.com.github.sample.data.remote.model.UserSearchResponse
import br.com.github.sample.ui.RecyclerViewAdapter
import br.com.github.sample.ui.search.SearchActivity
import br.com.github.sample.ui.search.repositorysearch.RepositorySearchFragment
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

class RepositorySearchFragmentTest {

    companion object {
        val REPOSITORIES: List<Repository> = Collections.unmodifiableList(listOf(
                Repository("Repository 1", 1, "sample/repository 1", "Sample Repository 1",
                        "https://www.github.com/sample/repository1", 100, 100, 100, 0, "Kotlin"
                ),
                Repository("Repository 2", 2, "sample/repository2", "Sample Repository 2",
                        "https://www.github.com/sample/repository2", 100, 100, 100, 0, "Kotlin"
                )
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
    lateinit var repositoryDataSource: RepositoryDataSource

    @Inject
    lateinit var userDataSource: UserDataSource

    @Before
    fun setUp() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val app = instrumentation.targetContext.applicationContext as TestApplication

        (app.testAppComponent).inject(this)

        // Bad Smell. Must reset because UserRepository is @Singleton
        reset(repositoryDataSource)

        Intents.init()

        // By default Espresso Intents does not stub any Intents. Stubbing needs to be setup before
        // every test run. In this case all external Intents will be blocked.
        intending(not(isInternal()))
                .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))

        `when`(userDataSource.search(anyString(), any()))
                .thenReturn(UserSearchResponse(emptyList(), null).toObservable())
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
    fun shouldListRepositories() {
        val query = "Teste"

        `when`(repositoryDataSource.search(query, null))
                .thenReturn(RepositorySearchResponse(REPOSITORIES, null).toObservable())

        activityRule.launchActivity(Intent())

        showRepositoriesPage()

        doSearch(query)

        REPOSITORIES.asSequence()
                .forEachIndexed { i, repository ->
                    onView(withTag(RepositorySearchFragment.RECYCLER_VIEW_TAG))
                            .perform(scrollToPosition<RecyclerViewAdapter.RecyclerViewHolder<*>>(i))

                    onView(withRecyclerViewTag(RepositorySearchFragment.RECYCLER_VIEW_TAG)
                            .atPositionOnView(i, R.id.tv_repository_name))
                            .check(matches(withText(repository.fullName)))
                }
    }

    @Test
    fun shouldOpenBrowserWhenClickOnRepository() {
        val query = "Teste"

        `when`(repositoryDataSource.search(query, null))
                .thenReturn(RepositorySearchResponse(REPOSITORIES, null).toObservable())

        activityRule.launchActivity(Intent())

        showRepositoriesPage()

        doSearch(query)

        onView(withRecyclerViewTag(RepositorySearchFragment.RECYCLER_VIEW_TAG).atPosition(0))
                .perform(click())

        intended(hasAction(Intent.ACTION_VIEW))
    }

    @Test
    fun shouldShowEmptyViewWhenNoResults() {
        val query = "Teste"

        `when`(repositoryDataSource.search(query, null))
                .thenReturn(RepositorySearchResponse(emptyList(), null).toObservable())

        activityRule.launchActivity(Intent())

        showRepositoriesPage()

        doSearch(query)

        onView(withTag(RepositorySearchFragment.RECYCLER_VIEW_TAG))
                .check(recyclerViewAdapterCount(0))

        onView(withTag(RepositorySearchFragment.EMPTY_VIEW_TAG))
                .check(matches(allOf(isDisplayed(), withText(R.string.no_repositories_found))))

        verify(repositoryDataSource).search(query, null)
        verifyNoMoreInteractions(repositoryDataSource)
    }

    @Test
    fun shouldLoadAgainWhenSwipeToRefresh() {
        val query = "Teste"

        `when`(repositoryDataSource.search(query, null))
                .thenReturn(RepositorySearchResponse(REPOSITORIES, null).toObservable())

        activityRule.launchActivity(Intent())

        showRepositoriesPage()

        doSearch(query)

        onView(withTag(RepositorySearchFragment.RECYCLER_VIEW_TAG))
                .check(recyclerViewAdapterCount(REPOSITORIES.size))

        onView(withTag(RepositorySearchFragment.SWIPE_REFRESH_TAG))
                .perform(swipeDown())

        verify(repositoryDataSource, times(2)).search(query, null)
        verifyNoMoreInteractions(repositoryDataSource)
    }

    @Test
    fun shouldNotUpdateRecyclerViewWhenSwipeRefreshError() {
        val query = "Teste"

        `when`(repositoryDataSource.search(query, null))
                .thenReturn(RepositorySearchResponse(REPOSITORIES, null).toObservable(),
                        Observable.error<RepositorySearchResponse>(NullPointerException()))

        activityRule.launchActivity(Intent())

        showRepositoriesPage()

        doSearch(query)

        onView(withTag(RepositorySearchFragment.RECYCLER_VIEW_TAG))
                .check(recyclerViewAdapterCount(REPOSITORIES.size))

        onView(withTag(RepositorySearchFragment.SWIPE_REFRESH_TAG))
                .perform(swipeDown())

        onView(withId(R.id.snackbar_text))
                .check(matches(allOf(isDisplayed(), withText(R.string.error_loading_users))))

        verify(repositoryDataSource, times(2)).search(query, null)
        verifyNoMoreInteractions(repositoryDataSource)
    }

    @Test
    fun shouldLoadNextPageWhenReachListEnd() {
        val query = "Teste"
        val list = ArrayList(REPOSITORIES)
        val newList = ArrayList<Repository>()
                .concat(list)
                .concat(list)
                .concat(list)
                .concat(list)

        val nextPage = SearchNextPage(2)

        `when`(repositoryDataSource.search(query, null))
                .thenReturn(RepositorySearchResponse(newList, nextPage).toObservable())

        `when`(repositoryDataSource.search(query, nextPage))
                .thenReturn(RepositorySearchResponse(list, null).toObservable())

        activityRule.launchActivity(Intent())

        showRepositoriesPage()

        doSearch(query)

        onView(withTag(RepositorySearchFragment.RECYCLER_VIEW_TAG))
                .perform(scrollToPosition<RecyclerViewAdapter.RecyclerViewHolder<*>>(newList.size - 1))

        onView(withTag(RepositorySearchFragment.RECYCLER_VIEW_TAG))
                .check(recyclerViewAdapterCount(newList.size + list.size))

        verify(repositoryDataSource).search(query, null)
        verify(repositoryDataSource).search(query, nextPage)

        verifyNoMoreInteractions(repositoryDataSource)
    }

    @Test
    fun shouldSaveAndRestoreInstanceState() {
        val query = "Teste"

        `when`(repositoryDataSource.search(query, null))
                .thenReturn(RepositorySearchResponse(REPOSITORIES, null).toObservable())

        activityRule.launchActivity(Intent())

        showRepositoriesPage()

        doSearch(query)

        onView(withTag(RepositorySearchFragment.RECYCLER_VIEW_TAG))
                .check(recyclerViewAdapterCount(REPOSITORIES.size))

        activityRule.activity.rotateScreen()

        onView(withTag(RepositorySearchFragment.RECYCLER_VIEW_TAG))
                .check(recyclerViewAdapterCount(REPOSITORIES.size))

        onView(allOfDisplayed(R.id.et_search))
                .check(matches(withText(query)))

        verify(repositoryDataSource).search(query, null)
        verifyNoMoreInteractions(repositoryDataSource)
    }

    fun doSearch(text: String) {
        onView(withId(R.id.et_search)).apply {
            perform(clearText(), replaceText(text))
            perform(pressImeActionButton())
        }
    }

    fun showRepositoriesPage() {
        onView(allOfDisplayed(R.id.vp_search))
                .perform(swipeLeft())
    }
}
