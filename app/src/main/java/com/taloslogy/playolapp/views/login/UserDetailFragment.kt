package com.taloslogy.playolapp.views.login

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.taloslogy.playolapp.R
import com.taloslogy.playolapp.adapters.GradeListAdapter
import com.taloslogy.playolapp.models.LoginRes
import com.taloslogy.playolapp.models.LoginResult
import com.taloslogy.playolapp.utils.StringUtils
import com.taloslogy.playolapp.utils.storage.PrefHelper
import com.taloslogy.playolapp.view_models.UserDetailViewModel
import com.taloslogy.playolapp.view_models.UserViewModel
import com.taloslogy.playolapp.view_models.UserViewModelFactory
import kotlinx.android.synthetic.main.fragment_user_detail.*
import java.text.SimpleDateFormat
import java.util.*

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
class UserDetailFragment : Fragment(){

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

        userViewModel.loginCycle.postValue(LoginResult(LoginRes.LoginWaiting))
        name_field.setText(userViewModel.name)
        email_field.setText(userViewModel.email)

        val gradeAdapter = GradeListAdapter(requireActivity(), R.layout.spinner_item, StringUtils.grades, resources)
        grade_dropdown.adapter = gradeAdapter

        grade_dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                userDetailViewModel.grade.postValue(position)
                grade_label.visibility =  if(position != 0) View.VISIBLE else View.INVISIBLE
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        val districtAdapter = GradeListAdapter(requireActivity(), R.layout.spinner_item, StringUtils.districts, resources)
        district_dropdown.adapter = districtAdapter

        district_dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                userDetailViewModel.district.postValue(position)
                district_label.visibility =  if(position != 0) View.VISIBLE else View.INVISIBLE
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        val cal = Calendar.getInstance()
        dob_label.visibility = if(dob_input.text.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE

        userDetailViewModel.dob.observe(viewLifecycleOwner, Observer {
            dob_input.text = it
        })

        dob_input.setOnClickListener {

            val dpd = DatePickerDialog(requireActivity(), R.style.MySpinnerDatePickerStyle, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->

                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Display Selected date in textbox
                val myFormat = "yyyy-MM-dd"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                userDetailViewModel.dob.postValue(sdf.format(cal.time))

                dob_label.visibility = View.VISIBLE

            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))

            dpd.show()
        }

        val builder = AlertDialog.Builder(context)
        builder.setCancelable(false)
        builder.setView(R.layout.progress)
        val dialog = builder.create()

        userDetailViewModel.apiCall.observe(viewLifecycleOwner, Observer {
            when(it.type) {
                LoginRes.LoginLoading -> dialog.show()
                LoginRes.LoginError -> {
                    dialog.dismiss()
                    Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
                }
                LoginRes.LoginSuccess -> {
                    dialog.dismiss()
                    findNavController().navigate(R.id.action_activateCode)
                    userDetailViewModel.apiCall.postValue(LoginResult(LoginRes.LoginWaiting))
                }
                LoginRes.LoginWaiting -> {}
            }
        })

        address_field.addTextChangedListener {
            userDetailViewModel.address.postValue(it.toString())
        }

        city_field.addTextChangedListener {
            userDetailViewModel.city.postValue(it.toString())
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
            userDetailViewModel.updateUserDetails(
                userViewModel.email!!, userViewModel.token!!
            )
        }
    }

}