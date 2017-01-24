package br.com.github.sample.tests

import br.com.github.sample.common.util.mock
import br.com.github.sample.common.util.toObservable
import br.com.github.sample.data.UserDataSource
import br.com.github.sample.data.model.User
import br.com.github.sample.data.remote.model.UserRepositoriesResponse
import br.com.github.sample.ui.userdetail.UserDetailContract
import br.com.github.sample.ui.userdetail.UserDetailPresenter
import br.com.github.sample.util.TestSchedulerProvider
import br.com.github.sample.util.newRepositoryList
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import java.util.*

@Suppress("IllegalIdentifier")
class UserDetailPresenterTest {

    companion object {
        val REPOSITORIES = newRepositoryList(4)

        val imageUrl = "http://www.nitwaa.in/media//1/profile_pictures/raghavender-mittapalli/raghavender-mittapalli-present.png"

        val USERS: List<User> = Collections.unmodifiableList(listOf(
                User("Sample 1", "sample1", 1, imageUrl, "Company 1", "https://www.google.com", "Rio de Janeiro",
                        "1@sample.com", false, "User Sample 1", 10, 10, 10, 10, Date(), Date()),
                User("Sample 2", "sample2", 2, imageUrl, "Company 2", "https://www.google.com", "Rio de Janeiro",
                        "2@sample.com", false, "User Sample 2", 10, 10, 10, 10, Date(), Date()),
                User("Sample 3", "sample3", 3, imageUrl, "Company 3", "https://www.google.com", "Rio de Janeiro",
                        "3@sample.com", false, "User Sample 3", 10, 10, 10, 10, Date(), Date())
        ))
    }

    val schedulerProvider = TestSchedulerProvider()

    var view: UserDetailContract.View = mock()
    var dataSource: UserDataSource = mock()

    var presenter: UserDetailPresenter = UserDetailPresenter(schedulerProvider, dataSource)

    fun verifyLoading() {
        verify(view).showLoadingIndicator(true)
        verify(view).showLoadingIndicator(false)
    }

    @Before
    fun setup() {
        dataSource = mock()
        view = mock()

        presenter = UserDetailPresenter(schedulerProvider, dataSource)

        presenter.attachView(view)
    }

    @Test
    fun `should load user into view`() {
        val username = "username"
        val user = USERS.first()

        `when`(dataSource.getUser(username))
                .thenReturn((user to UserRepositoriesResponse(REPOSITORIES, null)).toObservable())

        presenter.showUser(username)

        verifyLoading()

        verify(view).showUser(user)
        verify(view).showRepositories(REPOSITORIES, null)

        verify(dataSource).getUser(username)
    }

    @Test
    fun `should present empty list when loading repositories into view`() {
        val username = "username"
        val user = USERS.first()

        `when`(dataSource.getUser(username))
                .thenReturn((user to UserRepositoriesResponse(emptyList(), null)).toObservable())

        presenter.showUser(username)

        verifyLoading()

        verify(view).showEmptyRepositories()

        verify(dataSource).getUser(username)
    }

    @Test
    fun `should present error when loading user into view`() {
        val username = "username"

        `when`(dataSource.getUser(username))
                .thenReturn(NullPointerException().toObservable())

        presenter.showUser(username)

        verifyLoading()

        verify(view).errorLoadingUser()

        verify(dataSource).getUser(username)
    }
}