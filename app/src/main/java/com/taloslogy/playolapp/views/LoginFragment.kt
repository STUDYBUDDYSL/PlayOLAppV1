package com.taloslogy.playolapp.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.taloslogy.playolapp.R
import kotlinx.android.synthetic.main.fragment_login.*

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
class LoginFragment : Fragment() {

    companion object {
        const val LOGIN_SUCCESSFUL = "LOGIN_SUCCESSFUL"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        val savedStateHandle = navController.previousBackStackEntry!!.savedStateHandle
//        savedStateHandle.set(LOGIN_SUCCESSFUL, false)

        btn_student_detail.setOnClickListener {
            navController.navigate(R.id.action_user_details)
        }
    }

}