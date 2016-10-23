package br.com.github.sample.tests

import br.com.github.sample.common.util.mock
import br.com.github.sample.common.util.toObservable
import br.com.github.sample.data.RepositoryDataSource
import br.com.github.sample.data.remote.model.RepositorySearchResponse
import br.com.github.sample.ui.search.repositorysearch.RepositorySearchContract
import br.com.github.sample.ui.search.repositorysearch.RepositorySearchPresenter
import br.com.github.sample.util.TestSchedulerProvider
import br.com.github.sample.util.newRepositoryList
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify

@Suppress("IllegalIdentifier")
class RepositorySearchPresenterTest {

    companion object {
        val REPOSITORIES = newRepositoryList(4)
    }

    val schedulerProvider = TestSchedulerProvider()

    var view: RepositorySearchContract.View = mock()
    var dataSource: RepositoryDataSource = mock()

    var presenter: RepositorySearchPresenter = RepositorySearchPresenter(view, schedulerProvider, dataSource)

    fun verifyLoading() {
        verify(view).showLoadingIndicator(true)
        verify(view).showLoadingIndicator(false)
    }

    @Before
    fun setup() {
        dataSource = mock()
        view = mock()

        presenter = RepositorySearchPresenter(view, schedulerProvider, dataSource)
    }

    @Test
    fun `should load repositories into view`() {
        `when`(dataSource.search("wakim", null))
                .thenReturn(RepositorySearchResponse(REPOSITORIES, null).toObservable())

        presenter.onSearch("wakim")

        verify(view).showRepositories(REPOSITORIES, null)
        verify(dataSource).search("wakim", null)

        verifyLoading()
    }

    @Test
    fun `should present error when load repositories into view`() {
        `when`(dataSource.search("wakim", null))
                .thenReturn(NullPointerException().toObservable())

        presenter.onSearch("wakim")

        verify(view).errorLoadingRepositories()
        verify(dataSource).search("wakim", null)

        verifyLoading()
    }

    @Test
    fun `should present empty view when no repositories into view`() {
        `when`(dataSource.search("wakim", null))
                .thenReturn(RepositorySearchResponse(emptyList(), null).toObservable())

        presenter.onSearch("wakim")

        verify(view).showEmptyList()
        verify(dataSource).search("wakim", null)

        verifyLoading()
    }
}