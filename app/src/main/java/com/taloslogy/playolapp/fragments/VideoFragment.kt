package com.taloslogy.playolapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.taloslogy.playolapp.R
import kotlinx.android.synthetic.main.fragment_video.*

class VideoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val position = arguments?.let { VideoFragmentArgs.fromBundle(it).lessonNumber }
        val name = arguments?.let { VideoFragmentArgs.fromBundle(it).lessonName }
        lesson_number.text = (position!! + 1).toString()
        lesson_name.text = name!!
    }

}