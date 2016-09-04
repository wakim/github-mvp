package br.com.github.sample.ui.search

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import br.com.github.sample.R
import br.com.github.sample.ui.BaseActivity
import br.com.github.sample.util.extensions.hideSoftKeyboard
import butterknife.bindView
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class SearchActivity: BaseActivity(), SearchSubjectProvider {

    val viewPager: ViewPager by bindView(R.id.vp_search)
    val tabLayout: TabLayout by bindView(R.id.tab_layout)
    val etSearch: EditText by bindView(R.id.et_search)

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