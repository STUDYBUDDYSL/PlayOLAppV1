package com.taloslogy.playolapp.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
    private lateinit var myDataset: ArrayList<String>
    private lateinit var totalSet: ArrayList<String>

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
        val sName = jsonObject.getJSONObject(subName!!.split('/').last()).getString("name")
        subject_name.text = sName.replace('\n', ' ')

        // TODO change path here..
//        val files = fileUtils.getFilesFromPath("/storage/5E71-DBAD/$subName/ed",
//            onlyFolders = false
//        )
        val files = fileUtils.getFilesFromPath("/storage/5E71-DBAD/Courses/$subName",
            onlyFolders = false
        )

        thread { generateLessons(files, "/storage/5E71-DBAD/Courses/$subName", jsonObject) }

        search_text.setOnFocusChangeListener { _, b ->
            if(!b && search_text.text.toString().isEmpty()){
                search_text.setEms(5)
            }
            else {
                search_text.setEms(20)
            }
        }

        search_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(s.isNotEmpty()){
                    val filtered =  if (totalSet.isNotEmpty())
                        totalSet.filter { name -> name.contains(s) } else arrayListOf()
                    myDataset.clear()
                    myDataset.addAll(filtered)
                    viewAdapter.notifyDataSetChanged()
                }
                else {
                    myDataset.clear()
                    myDataset.addAll(totalSet)
                    viewAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    private fun generateLessons(files: List<File>, path: String, fileNames: JSONObject) {
        totalSet =  if (files.isNotEmpty())
            ArrayList(files.map{
                if(fileNames.has(it.name.dropLast(10))){
                    fileNames.getString(it.name.dropLast(10))
                }
                else{
                    it.name
                }
            }) else arrayListOf()

        myDataset = ArrayList(totalSet.toMutableList())
        viewManager = LinearLayoutManager(activity)
        viewAdapter = LessonAdapter(myDataset, path)

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