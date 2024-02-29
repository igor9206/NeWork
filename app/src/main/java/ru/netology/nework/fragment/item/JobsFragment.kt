package ru.netology.nework.fragment.item

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.CardJobBinding
import ru.netology.nework.databinding.FragmentJobsBinding
import ru.netology.nework.util.AppKey
import ru.netology.nework.viewmodel.AuthViewModel
import ru.netology.nework.viewmodel.JobViewModel
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class JobsFragment : Fragment() {
    private lateinit var binding: FragmentJobsBinding
    private val jobViewModel: JobViewModel by viewModels()
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentJobsBinding.inflate(inflater, container, false)

        val userId = arguments?.getLong(AppKey.USER_ID)
        jobViewModel.getJobs(userId)

        binding.buttonNewJob.isVisible = userId == authViewModel.dataAuth.value?.id

        jobViewModel.data.observe(viewLifecycleOwner) { jobs ->
            binding.containerJob.removeAllViews()
            jobs.forEach { job ->
                CardJobBinding.inflate(layoutInflater, binding.containerJob, true).apply {
                    name.text = job.name
                    startFinish.text = buildString {
                        append(job.start.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                        append(" - ")
                        append(job.finish.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                    }
                    position.text = job.position

                    buttonRemoveJob.isVisible = userId == authViewModel.dataAuth.value?.id
                    buttonRemoveJob.setOnClickListener {
                        jobViewModel.deleteJob(job.id)
                    }
                }.root
            }
        }

        binding.buttonNewJob.setOnClickListener {
            findNavController().navigate(R.id.action_detailUserFragment_to_newJobFragment)
        }

        return binding.root
    }


    companion object {
        fun newInstance() = JobsFragment()
    }
}