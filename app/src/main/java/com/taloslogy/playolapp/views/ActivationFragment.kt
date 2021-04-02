package com.taloslogy.playolapp.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.taloslogy.playolapp.view_models.UserViewModel
import com.taloslogy.playolapp.R
import kotlinx.android.synthetic.main.fragment_activation.*

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
class ActivationFragment : Fragment() {

    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_activation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_activate.setOnClickListener {
            //userViewModel.loginComplete()
            findNavController().navigate(R.id.action_qr_scan)
        }
    }

}