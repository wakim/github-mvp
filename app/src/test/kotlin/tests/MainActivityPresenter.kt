package tests

import br.com.github.sample.data.UserDataSource
import br.com.github.sample.ui.search.SearchContract
import br.com.github.sample.ui.search.SearchPresenter
import org.junit.Before
import org.junit.Rule
import util.RxSchedulersOverrideRule
import util.mock

class MainActivityPresenter() {

    @Rule @JvmField
    val rxField = RxSchedulersOverrideRule()

    lateinit var view: SearchContract.View
    lateinit var dataSource: UserDataSource

    lateinit var presenter: SearchPresenter

    @Before
    fun setup() {
        view = mock()
        dataSource = mock()

        presenter = SearchPresenter(view, dataSource)
    }
}