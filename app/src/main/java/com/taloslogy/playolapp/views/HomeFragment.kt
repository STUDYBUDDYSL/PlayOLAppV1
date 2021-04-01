package com.taloslogy.playolapp.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.taloslogy.playolapp.R
import com.taloslogy.playolapp.utils.FileUtils
import com.taloslogy.playolapp.utils.StringUtils
import kotlinx.android.synthetic.main.fragment_home.*

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
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

        val navController = findNavController()
        val fileUtils = FileUtils()

        btn_grade10.setOnClickListener{
            if(fileUtils.checkGradeExists(StringUtils.getGrade10Name)){
                val action = HomeFragmentDirections.selectGrade(StringUtils.getGrade10Name)
                navController.navigate(action)
            }
            else {
                Toast.makeText(activity,
                    requireActivity().getText(R.string.incorrect_grade_toast),
                    Toast.LENGTH_SHORT).show()
            }
        }

        btn_grade11.setOnClickListener{
            if(fileUtils.checkGradeExists(StringUtils.getGrade11Name)){
                val action = HomeFragmentDirections.selectGrade(StringUtils.getGrade11Name)
                navController.navigate(action)
            }
            else {
                Toast.makeText(activity,
                    requireActivity().getText(R.string.incorrect_grade_toast),
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

}