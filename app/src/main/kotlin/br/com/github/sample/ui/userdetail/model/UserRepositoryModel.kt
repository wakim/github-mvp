package br.com.github.sample.ui.userdetail.model

import br.com.github.sample.R
import br.com.github.sample.data.model.Repository
import br.com.github.sample.ui.search.repositorysearch.RepositoryView
import com.airbnb.epoxy.EpoxyModel

class UserRepositoryModel(val repository: Repository) : EpoxyModel<RepositoryView>("rep_$repository.id".hashCode().toLong()) {
    override fun getDefaultLayout(): Int = R.layout.list_item_repository
    override fun bind(view: RepositoryView) {
        view.bind(repository)
    }

    override fun shouldSaveViewState(): Boolean = true

    override fun hashCode(): Int = id().toInt()
}