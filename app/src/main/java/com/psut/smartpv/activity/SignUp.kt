package com.psut.smartpv.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.psut.smartpv.R
import com.psut.smartpv.constant.DataConstant
import com.psut.smartpv.constant.UrlConstant
import com.psut.smartpv.databinding.ActivitySignUpBinding
import com.psut.smartpv.handler.CustomProgressDialogHandler
import com.psut.smartpv.service.VolleyService
import com.psut.smartpv.util.SharedPreferencesUtil

class SignUp : AppCompatActivity() {
    companion object {
        private const val SPLASH_TIME_OUT = 800L
        private const val LOGO_FADE_IN_TIME_OUT = 500L
        private const val EMAIL_KEY = "email"
        private const val PASSWORD_KEY = "password"
        private const val FIRST_NAME_KEY = "firstName"
        private const val LAST_NAME_KEY = "lastName"
    }

    private lateinit var binding: ActivitySignUpBinding
    private val progressDialog = CustomProgressDialogHandler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler().postDelayed({
            binding.loginLayout.visibility = View.VISIBLE
        }, SPLASH_TIME_OUT)

        Handler().postDelayed({
            binding.mainLayout.visibility = View.VISIBLE
        }, LOGO_FADE_IN_TIME_OUT)


        binding.signUpBtn.setOnClickListener {
            progressDialog.show(this)
            val firstName = binding.firstNameEdTxt.text.toString().trim()
            val lastName = binding.lastNameEdTxt.text.toString().trim()
            val email = binding.emailEdTxt.text.toString().trim()
            val password = binding.passwordEdTxt.text.toString().trim()
            val confirmPassword = binding.confirmPasswordEdTxt.text.toString().trim()

            signUp(firstName, lastName, email, password, confirmPassword)
            progressDialog.dialog.dismiss()

        }
    }

    private fun signUp(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        if (!validate(firstName, lastName, email, password, confirmPassword))
            return
        val params = prepareParams(firstName, lastName, email, password)
        VolleyService.getInstance().postStringRequest(UrlConstant.SIGN_UP_PATH, params, this,
            { response ->
                if (response != null) {
                    when (response) {
                        "ok" -> success(email)
                        else -> {

                            Toast.makeText(this, response, Toast.LENGTH_LONG).show()
                            binding.emailEdTxt.error = response
                        }

                    }
                }


            }, { error ->
                Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
            })


    }

    private fun validate(
        firstName: String, lastName: String, email: String,
        password: String, confirmPassword: String
    ): Boolean {
        var flag = true
        if (TextUtils.isEmpty(firstName)) {
            binding.firstNameEdTxt.error = getString(R.string.empty_first_name)
            flag = false
        }

        if (TextUtils.isEmpty(lastName)) {
            binding.lastNameEdTxt.error = getString(R.string.empty_last_name)
            flag = false
        }

        if (TextUtils.isEmpty(email)) {
            binding.emailEdTxt.error = getString(R.string.empty_email)
            flag = false
        }
        if (TextUtils.isEmpty(password)) {
            binding.passwordEdTxt.error = getString(R.string.empty_password)
            flag = false
        }
        if(TextUtils.isEmpty(confirmPassword)){
            binding.confirmPasswordEdTxt.error = getString(R.string.empty_password)
            flag = false
        }
        if (password != confirmPassword) {
            binding.passwordEdTxt.error = getString(R.string.unmatched_password)
            binding.confirmPasswordEdTxt.error = getString(R.string.unmatched_password)
            flag = false
        }

        return flag
    }

    private fun prepareParams(
        firstName: String, lastName: String, email: String,
        password: String
    ): MutableMap<String, String> {
        val params = HashMap<String, String>()
        params[EMAIL_KEY] = email
        params[PASSWORD_KEY] = password
        params[FIRST_NAME_KEY] = firstName
        params[LAST_NAME_KEY] = lastName

        return params
    }

    private fun success(email: String){
        Toast.makeText(this, getString(R.string.login_welcome), Toast.LENGTH_LONG)
            .show()
        SharedPreferencesUtil.addStringToSharedPreferences(this, DataConstant.EMAIL,email)
        startActivity(Intent(this,Home::class.java))
        finish()
    }
}