package br.com.github.sample.ui.search.repositorysearch

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import br.com.github.sample.R
import br.com.github.sample.data.model.Repository
import br.com.github.sample.view.AbstractView
import butterknife.BindView
import butterknife.ButterKnife

class RepositoryView: LinearLayout, AbstractView<Repository> {

    var repository: Repository? = null

    @BindView(R.id.tv_repository_name) lateinit var tvRepositoryName: TextView

    @BindView(R.id.v_divider) lateinit var vDivider: View

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int): super(context, attrs, defStyleAttr, defStyleRes)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?): super(context, attrs)
    constructor(context: Context?): super(context)

    override fun onFinishInflate() {
        super.onFinishInflate()
        ButterKnife.bind(this)
    }

    override fun bind(t: Repository, position: Int, last: Boolean) {
        repository = t
        tvRepositoryName.text = t.fullName

        vDivider.visibility = if (last) View.GONE else View.VISIBLE
    }

    override fun get(): Repository = repository!!
}