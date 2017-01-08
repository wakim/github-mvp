package br.com.github.sample.util

import br.com.github.sample.data.model.Repository
import br.com.github.sample.data.model.UserSearch

fun newUserSearchList(count: Int) =
        (0..count).map { number ->
            UserSearch("user_search$number", number.toLong(), "avatar$number", "url$number")
        }.toList()

fun newRepositoryList(count: Int) =
        (0..count).map { number ->
            Repository("repository$number", number.toLong(), "Repository $number", "Description of Repository $number",
                    "url$number", number, number, number, number, "Android")
        }.toList()