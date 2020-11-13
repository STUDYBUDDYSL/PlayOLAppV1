package com.taloslogy.playolapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.taloslogy.playolapp.R
import com.taloslogy.playolapp.fragments.SubjectFragmentDirections

class LessonAdapter(private val myDataset: ArrayList<String>, private val path: String) :
    RecyclerView.Adapter<LessonAdapter.MyViewHolder>() {

    class MyViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)


    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyViewHolder {
        // create a new view
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.lesson_view, parent, false) as TextView

        return MyViewHolder(textView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textView.text = myDataset[position]
        holder.textView.setOnClickListener {
            val action = SubjectFragmentDirections.actionLesson(myDataset[position],position, path)
            Navigation.findNavController(it).navigate(action)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}