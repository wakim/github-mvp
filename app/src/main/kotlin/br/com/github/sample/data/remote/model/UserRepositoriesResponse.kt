package br.com.github.sample.data.remote.model

import br.com.github.sample.data.model.Repository

data class UserRepositoriesResponse(val items: List<Repository>, val nextPage: NextPage? = null)