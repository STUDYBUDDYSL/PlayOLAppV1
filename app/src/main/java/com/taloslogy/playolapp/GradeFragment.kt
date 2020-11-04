package com.taloslogy.playolapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_grade.*
import kotlinx.android.synthetic.main.nice_button1.view.*
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.io.File
import kotlin.concurrent.thread
import kotlin.math.ceil

class GradeFragment : Fragment() {

    val fUtils = FileUtils()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grade, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val files = fUtils.getFilesFromPath("/storage/5E71-DBAD/Courses/Grade 10")

        thread { generateTable(files) }
    }

    private fun generateTable(files: List<File>) {

        val json = fUtils.readFileText("fileNames.json", requireActivity())
        val jsonObject = JSONObject(json)

        if (files.isNotEmpty()) {
            // Create layout params for the table rows and children
            val lParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT,
                3f
            )
            lParams.setMargins(0,10,0,10)

            val cParams = TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
            )

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
                        val nb = LayoutInflater.from(activity).inflate(R.layout.nice_button1, null)

                        nb.btn.text = jsonObject.getJSONObject(files[i].name).getString("name")
                        nb.btn.maxLines = jsonObject.getJSONObject(files[i].name).getInt("lines")
                        nb.layoutParams = cParams

                        nb.btn.setOnClickListener {
                            val action = GradeFragmentDirections.selectSubject(files[i].name)
                            Navigation.findNavController(it).navigate(action)
                        }

                        row.addView(nb)
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
}