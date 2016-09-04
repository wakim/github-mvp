package br.com.github.sample.util

import br.com.github.sample.data.model.UserSearch

fun newUserSearch(number: Int) =
        UserSearch("user_search$number", number.toLong(), "avatar$number", "url$number")

fun newUserSearchList(count: Int) =
        (0..count).map { number ->
            UserSearch("user_search$number", number.toLong(), "avatar$number", "url$number")
        }.toList()