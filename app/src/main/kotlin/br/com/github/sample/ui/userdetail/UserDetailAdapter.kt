package br.com.github.sample.ui.userdetail

import br.com.github.sample.data.model.Repository
import br.com.github.sample.data.model.User
import br.com.github.sample.ui.userdetail.model.LoadingModel
import br.com.github.sample.ui.userdetail.model.UserHeaderModel
import br.com.github.sample.ui.userdetail.model.UserRepositoryModel
import com.airbnb.epoxy.EpoxyAdapter
import java.util.*

class UserDetailAdapter : EpoxyAdapter() {

    val userHeaderModel: UserHeaderModel by lazy {
        val model = UserHeaderModel()

        model
    }

    val loadingModel = LoadingModel()

    var repositoryModels = mutableListOf<UserRepositoryModel>()
    var items = ArrayList<Repository>()

    var isLoading: Boolean = false
        set(value) {
            field = value
            loadingModel.show(value)
        }

    init {
        enableDiffing()
        addModels(loadingModel)

        loadingModel.hide()
    }

    fun setUser(user: User) {
        removeModel(userHeaderModel)
        userHeaderModel.user = user

        insertModelBefore(userHeaderModel, repositoryModels.firstOrNull() ?: loadingModel)
    }

    fun addAll(repositories: List<Repository>) {
        val newModels: List<UserRepositoryModel> = repositories.map(::UserRepositoryModel)

        repositoryModels.addAll(newModels)
        items.addAll(repositories)

        newModels.forEach {
            insertModelBefore(it, loadingModel)
        }
    }

    fun showEmptyRepositories() {
        userHeaderModel.empty = true
        notifyModelChanged(userHeaderModel)
    }
}