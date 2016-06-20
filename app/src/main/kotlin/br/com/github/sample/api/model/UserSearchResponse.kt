package br.com.github.sample.api.model

import br.com.github.sample.model.UserSearch
import com.google.gson.annotations.SerializedName

data class UserSearchResponse(@SerializedName("total_count") val totalCount: Int, val items: List<UserSearch>, val hasMore: Boolean)