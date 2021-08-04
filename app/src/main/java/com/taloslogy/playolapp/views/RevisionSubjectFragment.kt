package com.taloslogy.playolapp.views

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.taloslogy.playolapp.R
import com.taloslogy.playolapp.utils.FileUtils
import com.taloslogy.playolapp.utils.StringUtils
import kotlinx.android.synthetic.main.fragment_revision_subject.*
import kotlinx.android.synthetic.main.fragment_revision_year.revLogo
import org.json.JSONObject
import java.io.File
import kotlin.concurrent.thread
import kotlin.math.ceil

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
class RevisionSubjectFragment : Fragment() {

    private val fUtils = FileUtils()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_revision_subject, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val height = resources.displayMetrics.heightPixels

        val iParams = LinearLayout.LayoutParams(
            getPxFromDp((getLogoHeight(height)*1.2).toFloat()),
            getPxFromDp(getLogoHeight(height))
        )
        iParams.setMargins(0,20,0,0)
        iParams.gravity = Gravity.CENTER
        revLogo.layoutParams = iParams

        val year = arguments?.let { RevisionSubjectFragmentArgs.fromBundle(it).selectedSubject }
        revisionYear.text = getString(R.string.revision_year, year)

        val path = "${StringUtils.getRevisionName}/$year"
        val files = fUtils.getFilesFromPath("${StringUtils.getCoursePath}/$path")
        thread { generateTable(files, path) }

    }

    private fun generateTable(files: List<File>, path: String) {

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
                            val action = RevisionSubjectFragmentDirections.selectSubject(
                                    "$path/${files[i].name}"
                                ).setSubType(true)
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
                requireActivity().runOnUiThread { rev_subjects?.addView(row, lParams) }
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

    private fun getLogoHeight(height: Int) : Float {
        return when {
            height < 1000 -> (height * 0.18).toFloat()
            height < 1800 -> (height * 0.13).toFloat()
            else -> (height * 0.08).toFloat()
        }
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
            "sinhala" -> R.drawable.r_sinhala
            "english" -> R.drawable.r_english
            "commerce" -> R.drawable.r_commerce
            "geography" -> R.drawable.r_geography
            "history" -> R.drawable.r_history
            "ict" -> R.drawable.r_ict
            "maths" -> R.drawable.r_maths
            "science" -> R.drawable.r_science
            else -> R.drawable.r_commerce
        }
    }

}