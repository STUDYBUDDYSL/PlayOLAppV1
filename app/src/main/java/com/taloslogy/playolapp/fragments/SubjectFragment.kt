package com.taloslogy.playolapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.taloslogy.playolapp.adapters.LessonAdapter
import com.taloslogy.playolapp.R
import com.taloslogy.playolapp.utils.FileUtils
import kotlinx.android.synthetic.main.fragment_subject.*
import org.json.JSONObject
import java.io.File
import kotlin.concurrent.thread

class SubjectFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var fileUtils: FileUtils = FileUtils()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_subject, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val json = fileUtils.readFileText("fileNames.json", requireActivity())
        val jsonObject = JSONObject(json)

        val subName = arguments?.let { SubjectFragmentArgs.fromBundle(it).subject }
        val sName = jsonObject.getJSONObject(subName!!).getString("name")
        subject_name.text = sName.replace('\n', ' ')

        // TODO change path here..
        val files = fileUtils.getFilesFromPath("/storage/5E71-DBAD/$subName/ed",
            onlyFolders = false
        )

        thread { generateLessons(files, subName, jsonObject) }
    }

    private fun generateLessons(files: List<File>, subject: String, fileNames: JSONObject) {
        val myDataset: Array<String> = if (files.isNotEmpty())
            files.map{
                if(fileNames.has(it.name.dropLast(4))){
                    fileNames.getString(it.name.dropLast(4))
                }
                else{
                    it.name
                }
            }.toTypedArray() else emptyArray()

        viewManager = LinearLayoutManager(activity)
        viewAdapter = LessonAdapter(myDataset, subject)

        requireActivity().runOnUiThread {
            my_recycler_view?.apply {
                // use this setting to improve performance if you know that changes
                // in content do not change the layout size of the RecyclerView
                setHasFixedSize(true)

                // use a linear layout manager
                layoutManager = viewManager

                // specify an viewAdapter (see also next example)
                adapter = viewAdapter

            }
        }
    }

}