package com.taloslogy.playolapp.views.login

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.DatePicker
import androidx.core.widget.addTextChangedListener
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
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.datatype.DatatypeConstants.MONTHS

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
class UserDetailFragment : Fragment() , DatePickerDialog.OnDateSetListener {

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

        val prefs = PrefHelper.getInstance(requireActivity())
        viewModelFactory = UserViewModelFactory(prefs)
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

        val cal = Calendar.getInstance()

        dob_input.setOnClickListener {

            val dpd = DatePickerDialog(requireActivity(), R.style.MySpinnerDatePickerStyle, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->

                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Display Selected date in textbox
                val myFormat = "dd-MMMM-yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                dob_input.text = sdf.format(cal.time)

            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))

            dpd.show()
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


    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        TODO("Not yet implemented")
    }

}