package com.taloslogy.playolapp.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.taloslogy.playolapp.view_models.UserViewModel
import com.taloslogy.playolapp.R


/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
class SplashFragment : Fragment() {

    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()

        userViewModel.checkLogin().observe(viewLifecycleOwner, Observer {
            if (!it){
                navController.navigate(R.id.action_login)
            }
            else{
                navController.navigate(R.id.action_home)
            }
        })

    }

}