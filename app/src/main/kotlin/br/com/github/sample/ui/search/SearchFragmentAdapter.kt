package br.com.github.sample.ui.search

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import br.com.github.sample.R
import br.com.github.sample.ui.search.repositorysearch.RepositorySearchFragment
import br.com.github.sample.ui.search.usersearch.UserSearchFragment

class SearchFragmentAdapter(private val context: Context,
                            fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment =
            when (position) {
                0 -> UserSearchFragment()
                1 -> RepositorySearchFragment()
                else -> throw IllegalArgumentException("There is only 2 fragments.")
            }

    override fun getCount(): Int = 2

    override fun getPageTitle(position: Int): CharSequence =
            when (position) {
                0 -> context.getString(R.string.users)
                1 -> context.getString(R.string.repositories)
                else -> throw IllegalArgumentException("There is only 2 fragments.")
            }
}