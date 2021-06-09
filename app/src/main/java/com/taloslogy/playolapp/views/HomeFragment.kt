package com.taloslogy.playolapp.views

import android.content.Context
import android.os.Bundle
import android.os.storage.StorageManager
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
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

        val btnEngParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            getPxFromDp((width*0.28).toFloat())
        )
        btnEngParams.topToBottom = btn_table.id
        btnEng.layoutParams = btnEngParams
        setMargins(btnEng, 20, 0, 20, 0)

        val logoHeight = resources.displayMetrics.heightPixels * 0.09

        val greentelParams = LinearLayout.LayoutParams(
            getPxFromDp((logoHeight*500/418).toFloat()),
            getPxFromDp(logoHeight.toFloat())
        )
        greentel.layoutParams = greentelParams
        setMargins(greentel, 0, 0, 20, 0)

        val learnTVParams = LinearLayout.LayoutParams(
            getPxFromDp((logoHeight*687/418).toFloat()),
            getPxFromDp(logoHeight.toFloat())
        )
        learn_tv.layoutParams = learnTVParams
        setMargins(learn_tv, 20, 0, 0, 0)


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

        btnEng.setOnClickListener{
            if(fileUtils.checkGradeExists(StringUtils.getEnglishName)){
                navController.navigate(R.id.select_english)
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

    private fun setMargins(
        view: View,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        if (view.layoutParams is ViewGroup.MarginLayoutParams) {
            val p = view.layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(left, top, right, bottom)
            view.requestLayout()
        }
    }

}