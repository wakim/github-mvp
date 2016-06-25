package br.com.github.sample.api.model

import br.com.github.sample.model.Repository

data class RepositorySearchResponse(val items: List<Repository>, val hasMore: Boolean)