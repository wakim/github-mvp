package br.com.github.sample.tests

import br.com.github.sample.common.util.mock
import br.com.github.sample.common.util.toObservable
import br.com.github.sample.data.UserDataSource
import br.com.github.sample.data.remote.model.UserSearchResponse
import br.com.github.sample.ui.search.usersearch.UserSearchContract
import br.com.github.sample.ui.search.usersearch.UserSearchPresenter
import br.com.github.sample.util.TestSchedulerProvider
import br.com.github.sample.util.newUserSearchList
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify

class UserSearchPresenterTest {

    companion object {
        val USERS = newUserSearchList(4)
    }

    val schedulerProvider = TestSchedulerProvider()

    var view: UserSearchContract.View = mock()
    var dataSource: UserDataSource = mock()

    var presenter: UserSearchPresenter = UserSearchPresenter(view, schedulerProvider, dataSource)

    fun verifyLoading() {
        verify(view).showLoadingIndicator(true)
        verify(view).showLoadingIndicator(false)
    }

    @Before
    fun setup() {
        dataSource = mock()
        view = mock()

        presenter = UserSearchPresenter(view, schedulerProvider, dataSource)
    }

    @Test
    fun shouldLoadUsersIntoView() {
        `when`(dataSource.search("wakim", null))
                .thenReturn(UserSearchResponse(USERS, null).toObservable())

        presenter.onSearch("wakim")

        verify(view).showUsers(USERS, null)
        verify(dataSource).search("wakim", null)

        verifyLoading()
    }

    @Test
    fun shouldPresentErrorWhenLoadUsersIntoView() {
        `when`(dataSource.search("wakim", null))
                .thenReturn(NullPointerException().toObservable())

        presenter.onSearch("wakim")

        verify(view).errorLoadingUsers()
        verify(dataSource).search("wakim", null)

        verifyLoading()
    }

    @Test
    fun shouldPresentEmptyViewWhenNoUsersIntoView() {
        `when`(dataSource.search("wakim", null))
                .thenReturn(UserSearchResponse(emptyList(), null).toObservable())

        presenter.onSearch("wakim")

        verify(view).showEmptyList()
        verify(dataSource).search("wakim", null)

        verifyLoading()
    }
}