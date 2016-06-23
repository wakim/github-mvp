package br.com.github.sample.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import br.com.github.sample.R
import br.com.github.sample.model.Repository
import butterknife.bindView

class RepositoryView: LinearLayout, AbstractView<Repository> {

    var repository: Repository? = null

    val tvRepositoryName: TextView by bindView(R.id.tv_repository_name)
    val vDivider: View by bindView(R.id.v_divider)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int): super(context, attrs, defStyleAttr, defStyleRes)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?): super(context, attrs)
    constructor(context: Context?): super(context)

    override fun bind(t: Repository, position: Int, last: Boolean) {
        repository = t
        tvRepositoryName.text = t.name

        vDivider.visibility = if (last) View.GONE else View.VISIBLE
    }

    override fun get(): Repository {
        return repository!!
    }

    override fun performClick(): Boolean {
        if (super.performClick()) {
            return true
        }

        context.startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(repository!!.htmlUrl)
        })

        return true
    }
}