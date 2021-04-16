package com.taloslogy.playolapp.views.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.taloslogy.playolapp.R
import com.taloslogy.playolapp.adapters.GradeListAdapter
import com.taloslogy.playolapp.models.LoginPayload
import com.taloslogy.playolapp.utils.storage.PrefHelper
import com.taloslogy.playolapp.view_models.UserDetailViewModel
import com.taloslogy.playolapp.view_models.UserViewModel
import com.taloslogy.playolapp.view_models.UserViewModelFactory
import kotlinx.android.synthetic.main.fragment_user_detail.*

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
class UserDetailFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var viewModelFactory: UserViewModelFactory

    private val userDetailViewModel: UserDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userPref = PrefHelper.getInstance(requireActivity()).userPref
        viewModelFactory = UserViewModelFactory(userPref)
        userViewModel = ViewModelProvider(requireActivity().viewModelStore, viewModelFactory).get(UserViewModel::class.java)

        userViewModel.loginCycle.postValue(LoginPayload.LoginWaiting)
        name_field.setText(userViewModel.name)
        email_field.setText(userViewModel.email)

        val res = resources
        val grades = res.getStringArray(R.array.Grades)
        val gradeAdapter = GradeListAdapter(requireActivity(), R.layout.spinner_item, grades, res)
        grade_dropdown.adapter = gradeAdapter

        grade_dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                userDetailViewModel.grade.postValue(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        address_field.addTextChangedListener {
            userDetailViewModel.address.postValue(it.toString())
        }

        contact_field.addTextChangedListener {
            userDetailViewModel.phoneNumber.postValue(it.toString())
        }

        school_field.addTextChangedListener {
            userDetailViewModel.school.postValue(it.toString())
        }

        userDetailViewModel.valid.observe(viewLifecycleOwner, Observer {
            btn_update_user.isEnabled = it
        })

        btn_update_user.setOnClickListener {
            findNavController().navigate(R.id.action_activateCode)
        }
    }

}