package com.taloslogy.playolapp.adapters

import android.content.Context;
import android.content.res.Resources;
import android.util.Log
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.taloslogy.playolapp.R


class GradeListAdapter(
    context: Context,
    textViewResourceId: Int,
    private val grades: Array<String>,
    resLocal: Resources
) : ArrayAdapter<String>(context, textViewResourceId, grades) {

    var res: Resources = resLocal
    var currRowVal: String? = null
    var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return getCustomView(position, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getSelectedView(position, parent)
    }

    private  fun getSelectedView(position: Int, parent: ViewGroup?): View {
        val row: View = inflater.inflate(R.layout.spinner_item, parent, false)
        currRowVal = null
        currRowVal = grades[position]
        val label = row.findViewById(R.id.spinnerItem) as TextView
        label.text = currRowVal
        label.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chevron_down, 0)
        if(position == 0){
            label.setTextColor(context.getColor(R.color.colorHint))
        }
        else {
            label.setTextColor(context.getColor(R.color.colorWhite))
        }
        return row
    }

    private fun getCustomView(position: Int, parent: ViewGroup?): View {
        val row: View = inflater.inflate(R.layout.spinner_item, parent, false)
        currRowVal = null
        currRowVal = grades[position]
        val label = row.findViewById(R.id.spinnerItem) as TextView
        label.text = currRowVal
        return row
    }

}