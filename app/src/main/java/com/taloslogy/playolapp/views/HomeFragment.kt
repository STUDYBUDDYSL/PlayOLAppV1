package com.taloslogy.playolapp.views

import android.content.Context
import android.os.Bundle
import android.os.storage.StorageManager
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.fragment.findNavController
import com.taloslogy.playolapp.R
import com.taloslogy.playolapp.utils.FileUtils
import com.taloslogy.playolapp.utils.StringUtils
import kotlinx.android.synthetic.main.fragment_home.*
import java.io.File

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the course path
        val path = getSdCardPath()
        StringUtils.sdCardPath = path
    }

    private fun getSdCardPath() : String {
        val sm = requireActivity().getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val sdCard = sm.storageVolumes.find { it.isRemovable }
        if(sdCard != null){
            return "/storage/${sdCard.uuid}"
        }
        return ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        val fileUtils = FileUtils()

        // Set dynamic sizes
        val width = resources.displayMetrics.widthPixels

        val btnParams = FrameLayout.LayoutParams(
            getPxFromDp((width*0.26).toFloat()),
            getPxFromDp((width*0.26).toFloat())
        )
        btn10.layoutParams = btnParams
        btn11.layoutParams = btnParams

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

    private fun getPxFromDp(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        ).toInt()
    }

}