package br.com.github.sample.api.model

import android.os.Parcelable

interface NextPage: Parcelable {
    val hasMore: Boolean
}