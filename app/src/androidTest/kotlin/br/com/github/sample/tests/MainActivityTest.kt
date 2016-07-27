package br.com.github.sample.tests

import android.content.Intent
import android.support.annotation.IdRes
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import br.com.github.sample.R
import br.com.github.sample.activity.MainActivity
import br.com.github.sample.api.model.SearchNextPage
import br.com.github.sample.api.model.SearchResponse
import br.com.github.sample.application.TestApplication
import br.com.github.sample.controller.ApiControllerSpec
import br.com.github.sample.model.Repository
import br.com.github.sample.model.User
import br.com.github.sample.model.UserSearch
import br.com.github.sample.util.RecyclerViewMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import rx.Single
import javax.inject.Inject

class MainActivityTest {

    companion object {
        val EMPTY_USERS = emptyList<User>()
        val EMPTY_REPOSITORIES = emptyList<Repository>()

        val USERS_SEARCH = arrayListOf(
                UserSearch("sample1", 10L, "http://www.nitwaa.in/media//1/profile_pictures/raghavender-mittapalli/raghavender-mittapalli-present.png", "https://www.github.com/sample1"),
                UserSearch("sample2", 11L, "http://www.nitwaa.in/media//1/profile_pictures/raghavender-mittapalli/raghavender-mittapalli-present.png", "https://www.github.com/sample2"),
                UserSearch("sample3", 12L, "http://www.nitwaa.in/media//1/profile_pictures/raghavender-mittapalli/raghavender-mittapalli-present.png", "https://www.github.com/sample3")
        )

        val REPOSITORIES = arrayListOf(
                Repository("Repository 1", "sample/repository 1", "Sample Repository 1",
                        "https://www.github.com/sample/repository1", 100, 100, 100, 0, "Kotlin"
                ),
                Repository("Repository 2", "sample/repository2", "Sample Repository 2",
                        "https://www.github.com/sample/repository2", 100, 100, 100, 0, "Kotlin"
                )
        )
    }

    @Rule @JvmField
    val activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(
            MainActivity::class.java,
            true, // initialTouchMode
            false)   // launchActivity. False so we can customize the intent per test method

    @Inject
    lateinit var apiController: ApiControllerSpec

    @Before
    fun setUp() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val app = instrumentation.targetContext.applicationContext as TestApplication

        (app.testAppComponent).inject(this)
    }

    @Test
    fun shouldAppearHintWhenStart() {
        activityRule.launchActivity(Intent())
        onView(withId(R.id.et_search)).check(matches(withHint(R.string.search_user_or_repository)))
    }

    @Test
    fun shouldListUsers() {
        val query = "Teste"
        `when`(apiController.search(query, null))
                .thenReturn(Single.just(SearchResponse(SearchNextPage(-1, -1), USERS_SEARCH)))

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
                .thenReturn(Single.just(SearchResponse(SearchNextPage(-1, -1), USERS_SEARCH + REPOSITORIES)))

        activityRule.launchActivity(Intent())

        onView(withId(R.id.et_search)).apply {
            perform(clearText(), replaceText(query))
            perform(pressImeActionButton())
        }

        USERS_SEARCH.asSequence()
                .forEachIndexed { i, userSearch ->
                    onView(withRecyclerView(R.id.recycler_view).atPositionOnView(i, R.id.tv_person_name))
                            .check(matches(withText(USERS_SEARCH[i].login)))
                }

        REPOSITORIES.asSequence()
                .forEachIndexed { i, repository ->
                    onView(withRecyclerView(R.id.recycler_view).atPositionOnView(USERS_SEARCH.size + i, R.id.tv_repository_name))
                            .check(matches(withText(repository.fullName)))
                }
    }
}

fun withRecyclerView(@IdRes id: Int) = RecyclerViewMatcher(id)
