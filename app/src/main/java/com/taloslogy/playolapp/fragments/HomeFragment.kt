package com.taloslogy.playolapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.taloslogy.playolapp.R
import com.taloslogy.playolapp.utils.FileUtils
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fileUtils = FileUtils()

        btn_grade10.setOnClickListener{
            if(fileUtils.checkGradeExists("Grade 10")){
                val action = HomeFragmentDirections.selectGrade("Grade 10")
                Navigation.findNavController(it).navigate(action)
            }
            else {
                Toast.makeText(activity, "Incorrect SDcard is used. Please check and try again!", Toast.LENGTH_SHORT).show()
            }
        }

        btn_grade11.setOnClickListener{
            if(fileUtils.checkGradeExists("Grade 11")){
                val action = HomeFragmentDirections.selectGrade("Grade 11")
                Navigation.findNavController(it).navigate(action)
            }
            else {
                Toast.makeText(activity, "Incorrect SDcard is used. Please check and try again!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}