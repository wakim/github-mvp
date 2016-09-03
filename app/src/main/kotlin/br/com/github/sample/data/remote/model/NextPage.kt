package br.com.github.sample.data.remote.model

import android.os.Parcelable

interface NextPage: Parcelable {
    val hasMore: Boolean
}