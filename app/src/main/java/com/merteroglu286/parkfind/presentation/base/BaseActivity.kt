package com.merteroglu286.parkfind.presentation.base

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.google.android.material.button.MaterialButton
import com.merteroglu286.parkfind.R
import com.merteroglu286.parkfind.utility.LoadingDialog
import com.merteroglu286.parkfind.utility.extension.cast
import java.lang.reflect.ParameterizedType

abstract class BaseActivity<VB : ViewBinding, VM : BaseViewModel> : AppCompatActivity() {

    abstract fun getViewBinding(): VB

    open fun setListeners() {}

    open fun setReceivers() {}

    open fun initUI() {}

    private lateinit var viewModel: VM

    lateinit var binding: VB


    private var loadingDialog: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = getViewBinding()
        setContentView(binding.root)

        val clazz = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1]
            .cast<Class<VM>>()

        if (!::viewModel.isInitialized) {
            viewModel = ViewModelProvider(this)[clazz]
        }

        initUI()

    }

    fun showLoading() {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog(this)
        }

        loadingDialog?.apply {
            if (isShowing.not()) {
                show()
            }
        }
    }

    fun hideLoading() {
        loadingDialog?.dismiss()
    }

    fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }

    fun showErrorPopup(errorCode: Int, errorMessage: String) {
        val dialog = Dialog(this, R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.error_layout)
        dialog.window?.let {
            it.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            it.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            it.setDimAmount(0.85f)
        }

        with(dialog) {
            findViewById<TextView>(R.id.messageEditText).text = errorMessage

            findViewById<MaterialButton>(R.id.okButton).setOnClickListener { dismiss() }
        }

        dialog.show()

    }

    fun showConfirmPopup(message: String, yesButton: () -> Unit, noButton: () -> Unit) {
        val dialog = Dialog(this, R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.confirm_layout)
        dialog.window?.let {
            it.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            it.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            it.setDimAmount(0.85f)
        }

        with(dialog) {
            findViewById<TextView>(R.id.titleTextview).text = message

            findViewById<MaterialButton>(R.id.yesButton).setOnClickListener {
                dialog.dismiss()
                yesButton()
            }
            findViewById<MaterialButton>(R.id.noButton).setOnClickListener {
                dialog.dismiss()
                noButton()
            }
        }
        dialog.show()

    }

}