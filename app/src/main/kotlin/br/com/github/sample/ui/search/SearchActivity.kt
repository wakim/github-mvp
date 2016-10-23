package br.com.github.sample.ui.search

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import br.com.github.sample.R
import br.com.github.sample.ui.BaseActivity
import br.com.github.sample.util.extensions.hideSoftKeyboard
import butterknife.BindView
import butterknife.ButterKnife
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class SearchActivity: BaseActivity(), SearchSubjectProvider {

    @BindView(R.id.vp_search) lateinit var viewPager: ViewPager

    @BindView(R.id.tab_layout) lateinit var tabLayout: TabLayout

    @BindView(R.id.et_search) lateinit var etSearch: EditText

    var query: String = ""

    override val subject: Subject<String> = PublishSubject.create<String>().toSerialized()

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.let {
            it.putString("QUERY", query)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search)
        ButterKnife.bind(this)

        query = savedInstanceState?.getString("QUERY") ?: ""

        setupEditText()
        setupAdapter()
    }

    fun setupEditText() {
        etSearch.setText(query)

        etSearch.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_SEARCH && textView.text.isNotBlank()) {
                doSearch(textView.text.toString())
                hideSoftKeyboard(etSearch)

                return@setOnEditorActionListener true
            }

            false
        }
    }

    fun doSearch(userQuery: String) {
        if (query == userQuery) {
            return
        }

        query = userQuery

        subject.onNext(query)
    }

    fun setupAdapter() {
        viewPager.adapter = SearchFragmentAdapter(this, supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager, true)
    }
}