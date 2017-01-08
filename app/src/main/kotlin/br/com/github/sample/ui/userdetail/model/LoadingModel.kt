package br.com.github.sample.ui.userdetail.model

import br.com.github.sample.R
import com.airbnb.epoxy.EpoxyModel

class LoadingModel() : EpoxyModel<Boolean>() {
    override fun getDefaultLayout(): Int = R.layout.list_item_loading
}