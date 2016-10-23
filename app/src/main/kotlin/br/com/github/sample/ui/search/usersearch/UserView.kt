package br.com.github.sample.ui.search.usersearch

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import br.com.github.sample.R
import br.com.github.sample.data.model.UserSearch
import br.com.github.sample.view.AbstractView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide

class UserView : LinearLayout, AbstractView<UserSearch> {

    var userSearch: UserSearch? = null

    @BindView(R.id.tv_person_name) lateinit var tvPersonName: TextView

    @BindView(R.id.iv_avatar) lateinit var ivAvatar: ImageView

    @BindView(R.id.v_divider) lateinit var vDivider: View

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?): super(context, attrs)
    constructor(context: Context?): super(context)

    override fun onFinishInflate() {
        super.onFinishInflate()
        ButterKnife.bind(this)
    }

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

    override fun get(): UserSearch = userSearch!!
}