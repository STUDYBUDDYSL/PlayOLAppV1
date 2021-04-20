package com.taloslogy.playolapp.views.subscription

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.taloslogy.playolapp.view_models.UserViewModel
import com.taloslogy.playolapp.R
import com.taloslogy.playolapp.models.LoginRes
import com.taloslogy.playolapp.models.LoginResult
import com.taloslogy.playolapp.utils.storage.PrefHelper
import com.taloslogy.playolapp.view_models.SubscriptionViewModel
import com.taloslogy.playolapp.view_models.SubscriptionViewModelFactory
import com.taloslogy.playolapp.view_models.UserViewModelFactory
import kotlinx.android.synthetic.main.fragment_activation.*

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
class ActivationFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var viewModelFactory: UserViewModelFactory
    private lateinit var subViewModel: SubscriptionViewModel
    private lateinit var subVMFactory: SubscriptionViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_activation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = PrefHelper.getInstance(requireActivity())
        viewModelFactory = UserViewModelFactory(prefs)
        userViewModel = ViewModelProvider(requireActivity().viewModelStore, viewModelFactory).get(UserViewModel::class.java)
        subVMFactory = SubscriptionViewModelFactory(prefs)
        subViewModel = ViewModelProvider(requireActivity().viewModelStore, subVMFactory).get(SubscriptionViewModel::class.java)

        val builder = AlertDialog.Builder(context)
        builder.setCancelable(false)
        builder.setView(R.layout.progress)
        val dialog = builder.create()

        subViewModel.activation.observe(viewLifecycleOwner, Observer {
            when(it.type) {
                LoginRes.LoginLoading -> dialog.show()
                LoginRes.LoginError -> {
                    dialog.dismiss()
                    Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
                }
                LoginRes.LoginSuccess -> {
                    dialog.dismiss()
                    findNavController().navigate(R.id.action_loginComplete)
                    subViewModel.activation.postValue(LoginResult(LoginRes.LoginWaiting))
                }
                LoginRes.LoginWaiting -> {}
            }
        })

        subViewModel.setQR.observe(viewLifecycleOwner, Observer {
            qr_field.setText(it)
        })

        subViewModel.qrCode.observe(viewLifecycleOwner, Observer {
            btn_activate.isEnabled = !it.isNullOrBlank()
        })

        qr_field.addTextChangedListener{
            subViewModel.qrCode.postValue(it.toString())
        }

        btn_qr_scan.setOnClickListener {
            findNavController().navigate(R.id.action_qr_scan)
        }

        btn_activate.setOnClickListener {
            subViewModel.activateSubscription(
                userViewModel.email!!, userViewModel.token!!
            )
        }
    }

}