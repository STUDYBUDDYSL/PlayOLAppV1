package com.taloslogy.playolapp.views

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.taloslogy.playolapp.adapters.LessonAdapter
import com.taloslogy.playolapp.R
import com.taloslogy.playolapp.utils.FileUtils
import com.taloslogy.playolapp.utils.StringUtils
import kotlinx.android.synthetic.main.fragment_subject.*
import org.json.JSONObject
import java.io.File
import kotlin.concurrent.thread

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
class SubjectFragment : Fragment() {

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

        val json = fileUtils.readFileText(StringUtils.getJsonFileName, requireActivity())
        val jsonObject = JSONObject(json)

        val subName = arguments?.let { SubjectFragmentArgs.fromBundle(it).subject }
        val subType = arguments?.let { SubjectFragmentArgs.fromBundle(it).subType }

        if(subType!!){
            top_image.background = ContextCompat.getDrawable(requireActivity(), R.drawable.revision_img)
        }

        val sName = jsonObject.getJSONObject(subName!!.split('/').last()).getString("name")
        subject_name.text = sName.replace('\n', ' ')

        val files = fileUtils.getFilesFromPath("${StringUtils.getCoursePath}/$subName/ed",
            onlyFolders = false
        )

        thread {
            generateLessons(
                files,
                "${StringUtils.getCoursePath}/$subName/ed",
                jsonObject,
                subType
            )
        }

        search_text.setOnFocusChangeListener { _, b ->
            if(!b && search_text.text.toString().isEmpty()){
                search_text.setEms(5)
            }
            else {
                search_text.setEms(30)
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

    private fun generateLessons(
        files: List<File>,
        path: String, fileNames: JSONObject,
        subType: Boolean
    ) {
        totalSet =  if (files.isNotEmpty())
            ArrayList(files.map{
                val name = it.name.replace(".mp4.talos", "")
                if(fileNames.has(name)){
                    if (fileNames.getString(name).isNotEmpty()) fileNames.getString(name) else name
                }
                else{
                    name.replace("-360p", "")
                }
            }) else arrayListOf()

        myDataset = ArrayList(totalSet.toMutableList())
        viewManager = LinearLayoutManager(activity)
        viewAdapter = LessonAdapter(myDataset, path, subType)

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