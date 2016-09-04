package br.com.github.sample.data.remote.model

import br.com.github.sample.data.model.Repository

data class RepositorySearchResponse(val items: List<Repository>, val nextPage: NextPage?)