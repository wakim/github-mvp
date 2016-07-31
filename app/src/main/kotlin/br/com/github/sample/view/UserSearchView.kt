package br.com.github.sample.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import br.com.github.sample.R
import br.com.github.sample.model.UserSearch
import butterknife.bindView
import com.bumptech.glide.Glide

class UserSearchView : LinearLayout, AbstractView<UserSearch> {

    var userSearch: UserSearch? = null

    val tvPersonName: TextView by bindView(R.id.tv_person_name)
    val ivAvatar: ImageView by bindView(R.id.iv_avatar)
    val vDivider: View by bindView(R.id.v_divider)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int): super(context, attrs, defStyleAttr, defStyleRes)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?): super(context, attrs)
    constructor(context: Context?): super(context)

    override fun bind(t: UserSearch, position: Int, last: Boolean) {
        userSearch = t

        tvPersonName.text = t.login

        if (!t.avatarUrl.isNullOrBlank()) {
            Glide.with(context)
                    .load(t.avatarUrl)
                    .into(ivAvatar)
        } else {
            ivAvatar.setImageBitmap(null)
        }

        vDivider.visibility = if (last) View.GONE else View.VISIBLE
    }

    override fun get(): UserSearch {
        return userSearch!!
    }
}