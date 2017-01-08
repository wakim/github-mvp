package br.com.github.sample.ui.userdetail.model

import android.view.View
import android.widget.TextView
import br.com.github.sample.R
import br.com.github.sample.data.model.User
import com.airbnb.epoxy.EpoxyModel

class UserHeaderModel : EpoxyModel<View>() {

    var empty: Boolean = false

    var user: User? = null
        set(value) {
            field = value

            value?.let {
                id("user_$it.id".hashCode().toLong())
            }
        }

    override fun hashCode(): Int = id().toInt()

    override fun getDefaultLayout(): Int = R.layout.content_detail

    override fun bind(header: View) {
        val tvFollowers = header.findViewById(R.id.tv_followers) as TextView
        val tvFollowing = header.findViewById(R.id.tv_following) as TextView
        val tvPublicRepos = header.findViewById(R.id.tv_public_repos) as TextView
        val tvPublicGists = header.findViewById(R.id.tv_public_gists) as TextView
        val tvBio = header.findViewById(R.id.tv_bio) as TextView
        val tvBlog = header.findViewById(R.id.tv_blog) as TextView
        val tvEmail = header.findViewById(R.id.tv_email) as TextView
        val tvLocation = header.findViewById(R.id.tv_location) as TextView
        val tvHireable = header.findViewById(R.id.tv_hireable) as TextView
        val tvRepositoriesHeader = header.findViewById(R.id.tv_repositories_header) as TextView

        val context = tvRepositoriesHeader.context

        if (empty) {
            tvRepositoriesHeader.text = context.getString(R.string.no_repositories_found)
        }

        with (user?: return) {
            tvBio.text = bio

            tvFollowers.text = context.getString(R.string.followers, followers)
            tvFollowing.text = context.getString(R.string.following, following)

            tvPublicRepos.text = context.getString(R.string.public_repos, publicRepos)
            tvPublicGists.text = context.getString(R.string.public_gists, publicGists)

            tvEmail.text = email
            tvBlog.text = blog

            tvLocation.text = location
            tvHireable.text = if (hireable) "✓" else "×"
        }
    }
}