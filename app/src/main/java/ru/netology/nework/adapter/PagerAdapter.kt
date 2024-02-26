package ru.netology.nework.adapter

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import okhttp3.internal.userAgent
import ru.netology.nework.fragment.JobsFragment
import ru.netology.nework.fragment.PostsFragment

class PagerAdapter(fragment: Fragment, private val userId: Long?) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                PostsFragment().apply {
                    arguments = bundleOf("userId" to userId)
                }
            }

            1 -> {
                JobsFragment.newInstance().apply {
                    arguments = bundleOf("userId" to userId)
                }
            }

            else -> {
                error("Unknown position")
            }
        }
    }
}