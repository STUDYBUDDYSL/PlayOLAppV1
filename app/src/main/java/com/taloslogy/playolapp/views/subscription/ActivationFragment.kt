package com.taloslogy.playolapp.views.subscription

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.taloslogy.playolapp.PERMISSION_REQUEST_CODE
import com.taloslogy.playolapp.view_models.UserViewModel
import com.taloslogy.playolapp.R
import com.taloslogy.playolapp.models.LoginRes
import com.taloslogy.playolapp.models.LoginResult
import com.taloslogy.playolapp.utils.SafetyNet
import com.taloslogy.playolapp.utils.storage.PrefHelper
import com.taloslogy.playolapp.view_models.SubscriptionViewModel
import com.taloslogy.playolapp.view_models.SubscriptionViewModelFactory
import com.taloslogy.playolapp.view_models.UserViewModelFactory
import kotlinx.android.synthetic.main.fragment_activation.*
import kotlin.concurrent.thread

const val CAMERA_PERMISSION_REQUEST_CODE = 3017

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!checkCameraPermission()) {
            requestPermission()
        }

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
                    subViewModel.activation.postValue(LoginResult(LoginRes.LoginWaiting))
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
            if (checkCameraPermission()) {
                findNavController().navigate(R.id.action_qr_scan)
            }
            else {
                requestPermission()
            }
        }

        btn_activate.setOnClickListener {
            subViewModel.activateSubscription(
                userViewModel.email!!, userViewModel.token!!
            )
        }
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("TEST_LOG", "Got permission..")
            } else {
                if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    showMessageOKCancel(resources.getString(R.string.cam_permission),
                        DialogInterface.OnClickListener { _, _ ->
                            requestPermission()
                        })
                }
            }
        }
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(requireActivity())
            .setMessage(message)
            .setPositiveButton(resources.getString(R.string.ok_text), okListener)
            .setNegativeButton(resources.getString(R.string.cancel_text), null)
            .create()
            .show()
    }
}