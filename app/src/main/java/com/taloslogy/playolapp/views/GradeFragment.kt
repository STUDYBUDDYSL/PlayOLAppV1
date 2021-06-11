package com.taloslogy.playolapp.views

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.taloslogy.playolapp.R
import com.taloslogy.playolapp.utils.FileUtils
import com.taloslogy.playolapp.utils.StringUtils
import kotlinx.android.synthetic.main.fragment_grade.*
import org.json.JSONObject
import java.io.File
import kotlin.concurrent.thread
import kotlin.math.ceil

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
class GradeFragment : Fragment() {

    private val fUtils = FileUtils()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grade, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val grade = arguments?.let { GradeFragmentArgs.fromBundle(it).selectedGrade }
        val files = fUtils.getFilesFromPath("${StringUtils.getCoursePath}/$grade")

        grade_id.text = if(grade!!.contains("10")) "10 ශ්\u200Dරේණිය" else "11 ශ්\u200Dරේණිය"

        thread { generateTable(files, grade) }
    }

    private fun generateTable(files: List<File>, grade: String?) {

        val json = fUtils.readFileText(StringUtils.getJsonFileName, requireActivity())
        val jsonObject = JSONObject(json)

        if (files.isNotEmpty()) {
            // Create layout params for the table rows and children
            val lParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT,
                3f
            )

            val cParams = TableRow.LayoutParams(
                0,
                getPxFromDp(getBtnHeight(resources.displayMetrics.widthPixels)),
                1f
            )
            cParams.setMargins(10,10,10,10)

            // Set row count as per item count in files
            val rowCount = ceil(files.size.div(3.0)).toInt()

            for (x in 0 until rowCount) {
                // Create a new row with layout params
                val row = TableRow(activity)
                row.layoutParams = lParams

                // Assign children to the created row
                var y = 0
                while (y < 3) {
                    val i = (x * 3) + y
                    if (files.size > i && jsonObject.has(files[i].name)) {
                        // Create the fancy button
                        val sub = LayoutInflater.from(activity).inflate(R.layout.nice_button1, null)
                        sub.background = ContextCompat.getDrawable(requireActivity(), getBackground(files[i].name))
                        sub.layoutParams = cParams

                        sub.setOnClickListener {
                            val action =
                                GradeFragmentDirections.selectSubject(
                                    "$grade/${files[i].name}"
                                )
                            Navigation.findNavController(it).navigate(action)
                        }

                        row.addView(sub)
                    }
                    else {
                        val extra = TextView(activity)
                        extra.layoutParams = cParams
                        row.addView(extra)
                    }
                    y++
                }
                // Add the completed row to table
                requireActivity().runOnUiThread { table_view?.addView(row, lParams) }
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

    private fun getBtnHeight(width: Int) : Float {
        return when {
            width < 650 -> (width * 0.2).toFloat()
            width < 950 ->(width * 0.14).toFloat()
            else -> (width * 0.08).toFloat()
        }
    }

    private fun getBackground(sub: String): Int {
        return when(sub){
            "sinhala" -> R.drawable.s_sinhala
            "english" -> R.drawable.s_english
            "commerce" -> R.drawable.s_commerce
            "geography" -> R.drawable.s_geography
            "entrepreneurship" -> R.drawable.s_entrepreneurship
            "history" -> R.drawable.s_history
            "ict" -> R.drawable.s_ict
            "maths" -> R.drawable.s_maths
            "science" -> R.drawable.s_science
            else -> R.drawable.s_commerce
        }
    }
}