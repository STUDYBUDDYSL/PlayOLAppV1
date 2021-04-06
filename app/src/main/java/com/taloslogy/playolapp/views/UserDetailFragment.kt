package com.taloslogy.playolapp.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.taloslogy.playolapp.R
import com.taloslogy.playolapp.adapters.GradeListAdapter
import kotlinx.android.synthetic.main.fragment_user_detail.*

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
class UserDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val res = resources
        val grades = res.getStringArray(R.array.Grades)
        val gradeAdapter = GradeListAdapter(requireActivity(), R.layout.spinner_item, grades, res)
        grade_dropdown.adapter = gradeAdapter

        btn_update_user.setOnClickListener {
            findNavController().navigate(R.id.action_activateCode)
        }
    }

}