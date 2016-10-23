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

@Suppress("IllegalIdentifier")
class RepositorySearchFragmentTest {

    companion object {
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
    fun `should appear hint when start`() {
        activityRule.launchActivity(Intent())

        onView(withId(R.id.et_search))
                .check(matches(withHint(R.string.search_user_or_repository)))
    }

    @Test
    fun `should list repositories`() {
        val query = "Teste"

        `when`(repositoryDataSource.search(query, null))
                .thenReturn(RepositorySearchResponse(REPOSITORIES, null).toObservable())

        activityRule.launchActivity(Intent())

        showRepositoriesPage()

        doSearch(query)

        REPOSITORIES.asSequence()
                .forEachIndexed { i, repository ->
                    onView(allOfDisplayed(R.id.recycler_view))
                            .perform(scrollToPosition<RecyclerViewAdapter.RecyclerViewHolder<*>>(i))

                    onView(withRecyclerViewTag("REPOSITORY_RECYCLERVIEW")
                            .atPositionOnView(i, R.id.tv_repository_name))
                            .check(matches(withText(repository.fullName)))
                }
    }

    @Test
    fun `should open browser when click on repository`() {
        val query = "Teste"

        `when`(repositoryDataSource.search(query, null))
                .thenReturn(RepositorySearchResponse(REPOSITORIES, null).toObservable())

        activityRule.launchActivity(Intent())

        showRepositoriesPage()

        doSearch(query)

        onView(withRecyclerViewTag("REPOSITORY_RECYCLERVIEW").atPosition(0))
                .perform(click())

        intended(hasAction(Intent.ACTION_VIEW))
    }

    @Test
    fun `should show empty view when no results`() {
        val query = "Teste"

        `when`(repositoryDataSource.search(query, null))
                .thenReturn(RepositorySearchResponse(emptyList(), null).toObservable())

        activityRule.launchActivity(Intent())

        showRepositoriesPage()

        doSearch(query)

        onView(withTag("REPOSITORY_RECYCLERVIEW"))
                .check(recyclerViewAdapterCount(0))

        onView(allOfDisplayed(R.id.tv_empty_view))
                .check(matches(allOf(isDisplayed(), withText(R.string.no_users_found))))

        verify(repositoryDataSource).search(query, null)
        verifyNoMoreInteractions(repositoryDataSource)
    }

    @Test
    fun `should load again when swipe to refresh`() {
        val query = "Teste"

        `when`(repositoryDataSource.search(query, null))
                .thenReturn(RepositorySearchResponse(REPOSITORIES, null).toObservable())

        activityRule.launchActivity(Intent())

        showRepositoriesPage()

        doSearch(query)

        onView(allOfDisplayed(R.id.recycler_view))
                .check(recyclerViewAdapterCount(REPOSITORIES.size))
        onView(allOfDisplayed(R.id.swipe_refresh_layout))
                .perform(swipeDown())

        verify(repositoryDataSource, times(2)).search(query, null)
        verifyNoMoreInteractions(repositoryDataSource)
    }

    @Test
    fun `should not update RecyclerView when swipe refresh error`() {
        val query = "Teste"

        `when`(repositoryDataSource.search(query, null))
                .thenReturn(RepositorySearchResponse(REPOSITORIES, null).toObservable(),
                        Observable.error<RepositorySearchResponse>(NullPointerException()))

        activityRule.launchActivity(Intent())

        showRepositoriesPage()

        doSearch(query)

        onView(allOfDisplayed(R.id.recycler_view))
                .check(recyclerViewAdapterCount(REPOSITORIES.size))

        onView(allOfDisplayed(R.id.swipe_refresh_layout))
                .perform(swipeDown())

        onView(withId(R.id.snackbar_text))
                .check(matches(allOf(isDisplayed(), withText(R.string.error_loading_users))))

        verify(repositoryDataSource, times(2)).search(query, null)
        verifyNoMoreInteractions(repositoryDataSource)
    }

    @Test
    fun `should load next page when reach list end`() {
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

        onView(allOfDisplayed(R.id.recycler_view))
                .perform(scrollToPosition<RecyclerViewAdapter.RecyclerViewHolder<*>>(newList.size - 1))

        onView(allOfDisplayed(R.id.recycler_view))
                .check(recyclerViewAdapterCount(newList.size + list.size))

        verify(repositoryDataSource).search(query, null)
        verify(repositoryDataSource).search(query, nextPage)

        verifyNoMoreInteractions(repositoryDataSource)
    }

    @Test
    fun `should save and restore instance state`() {
        val query = "Teste"

        `when`(repositoryDataSource.search(query, null))
                .thenReturn(RepositorySearchResponse(REPOSITORIES, null).toObservable())

        activityRule.launchActivity(Intent())

        showRepositoriesPage()

        doSearch(query)

        onView(allOfDisplayed(R.id.recycler_view))
                .check(recyclerViewAdapterCount(REPOSITORIES.size))

        activityRule.activity.rotateScreen()

        onView(allOfDisplayed(R.id.recycler_view))
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
