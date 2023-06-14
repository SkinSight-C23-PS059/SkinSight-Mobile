package com.capstone.skinsight.ui.heal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.capstone.skinsight.databinding.FragmentHealBinding

class HealFragment : Fragment() {

    private var _binding: FragmentHealBinding? = null
    private val binding get() = _binding!!

    private val healViewModel: HealViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHealBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView1: TextView = binding.tvTitle1
        val textView2: TextView = binding.tvTitle2
        val textView3: TextView = binding.tvTitle3

        healViewModel.text1.observe(viewLifecycleOwner) {
            textView1.text = it
        }
        healViewModel.text2.observe(viewLifecycleOwner) {
            textView2.text = it
        }
        healViewModel.text3.observe(viewLifecycleOwner) {
            textView3.text = it
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
