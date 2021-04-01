package com.taloslogy.playolapp.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.taloslogy.playolapp.R
import kotlinx.android.synthetic.main.fragment_user_detail.*

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
class UserDetailFragment : Fragment() {

    companion object {
        const val LOGIN_SUCCESSFUL = "LOGIN_SUCCESSFUL"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        val savedStateHandle = navController.previousBackStackEntry!!.savedStateHandle

        btn_complete_login.setOnClickListener {
            savedStateHandle.set(LOGIN_SUCCESSFUL, true)
            navController.navigate(R.id.action_login_done)
        }
    }

}