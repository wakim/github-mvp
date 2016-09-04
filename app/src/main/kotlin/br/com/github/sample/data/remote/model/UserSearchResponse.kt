package br.com.github.sample.data.remote.model

import br.com.github.sample.data.model.UserSearch

data class UserSearchResponse(val items: List<UserSearch>, val nextPage: NextPage?)