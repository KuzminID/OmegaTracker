package com.example.omegatracker.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.omegatracker.R
import com.example.omegatracker.databinding.FragmentStatisticsBinding
import com.example.omegatracker.ui.base.BaseFragment

class StatisticsFragment : BaseFragment(), StatisticsView {
    private lateinit var binding: FragmentStatisticsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_statistics, container, false)
        binding = FragmentStatisticsBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.issueTimerBackBtn.setOnClickListener {
            navigateUp()
        }
    }
}