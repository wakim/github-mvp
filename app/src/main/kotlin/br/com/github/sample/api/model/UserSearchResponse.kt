package br.com.github.sample.api.model

import br.com.github.sample.model.UserSearch

data class UserSearchResponse(val items: List<UserSearch>, val hasMore: Boolean)