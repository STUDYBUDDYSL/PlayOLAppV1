package com.taloslogy.playolapp.views

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.taloslogy.playolapp.R
import com.taloslogy.playolapp.utils.FileUtils
import com.taloslogy.playolapp.utils.StringUtils
import kotlinx.android.synthetic.main.fragment_english.*
import kotlinx.android.synthetic.main.stage_btn.view.*
import org.json.JSONObject
import java.io.File
import kotlin.concurrent.thread

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
class EnglishFragment : Fragment() {

    private val fUtils = FileUtils()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_english, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val height = resources.displayMetrics.heightPixels

        val iParams = LinearLayout.LayoutParams(
            getPxFromDp((height*0.2).toFloat()),
            getPxFromDp((height*0.2).toFloat())
        )
        iParams.setMargins(0,20,0,0)
        iParams.gravity = Gravity.CENTER
        lesson_logo.layoutParams = iParams

        val files = fUtils.getFilesFromPath(
                "${StringUtils.getCoursePath}/${StringUtils.getEnglishName}")
        thread { generateTable(files) }

    }

    private fun generateTable(files: List<File>) {

        val json = fUtils.readFileText(StringUtils.getJsonFileName, requireActivity())
        val jsonObject = JSONObject(json)

        val width = resources.displayMetrics.widthPixels

        val lParams = LinearLayout.LayoutParams(
            getPxFromDp((width*0.6).toFloat()),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        lParams.setMargins(0,10,0,10)
        lParams.gravity = Gravity.END

        for (x in files) {
            val btnS = LayoutInflater.from(activity).inflate(R.layout.stage_btn, null)
            btnS.stage_btn.text = jsonObject.getJSONObject(x.name).getString("stage")
            btnS.layoutParams = lParams

            btnS.setOnClickListener {
                val action = EnglishFragmentDirections
                        .selectSubject("${StringUtils.getEnglishName}/${x.name}")
                Navigation.findNavController(it).navigate(action)
            }

            // Add the completed row to table
            requireActivity().runOnUiThread {
                stages.addView(btnS)
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