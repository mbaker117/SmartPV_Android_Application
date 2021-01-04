package com.psut.smartpv.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.transition.Visibility
import android.view.View
import android.widget.Toast
import com.psut.smartpv.R
import com.psut.smartpv.constant.DataConstant
import com.psut.smartpv.constant.UrlConstant
import com.psut.smartpv.databinding.ActivityLoginBinding
import com.psut.smartpv.handler.CustomProgressDialogHandler
import com.psut.smartpv.service.VolleyService
import com.psut.smartpv.util.SharedPreferencesUtil

class Login : AppCompatActivity() {
    companion object {
        private const val SPLASH_TIME_OUT = 2500L
        private const val LOGO_FADE_IN_TIME_OUT = 500L
        private const val EMAIL_KEY = "email"
        private const val PASSWORD_KEY = "password"
    }

    private val progressDialog = CustomProgressDialogHandler()
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater);
        setContentView(binding.root)



        Handler().postDelayed({
            if (SharedPreferencesUtil.contain(this, DataConstant.EMAIL)) {
                startActivity(Intent(this, Home::class.java))
                finish()
            } else
                binding.loginLayout.visibility = View.VISIBLE
        }, SPLASH_TIME_OUT)

        Handler().postDelayed({
            binding.mainLayout.visibility = View.VISIBLE

        }, LOGO_FADE_IN_TIME_OUT)

        binding.loginBtn.setOnClickListener {
            login(binding.emailEdTxt.text.toString(), binding.passwordEdTxt.text.toString())
        }
        binding.signUpBtn.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
        }
    }

    private fun login(email: String, password: String) {
        progressDialog.show(this)
        val params = HashMap<String, String>()
        params[EMAIL_KEY] = email
        params[PASSWORD_KEY] = password
        VolleyService.getInstance()
            .postStringRequest(UrlConstant.LOGIN_PATH, params, this, { response ->
                if (response != null) {
                    if (response == "true") {

                        success(email);


                    } else {
                        Toast.makeText(this, getString(R.string.login_error), Toast.LENGTH_LONG)
                            .show()
                        binding.emailEdTxt.error = getString(R.string.login_email_error)
                        binding.passwordEdTxt.error = getString(R.string.login_password_error)
                    }


                } else {
                    Toast.makeText(this, getString(R.string.error), Toast.LENGTH_LONG).show()
                }
                progressDialog.dialog.dismiss()
            }, {
                Toast.makeText(this, getString(R.string.error), Toast.LENGTH_LONG).show()
                progressDialog.dialog.dismiss()
            })


    }

    private fun success(email: String) {
        Toast.makeText(this, getString(R.string.login_welcome), Toast.LENGTH_LONG)
            .show()
        SharedPreferencesUtil.addStringToSharedPreferences(this, DataConstant.EMAIL, email)
        startActivity(Intent(this, Home::class.java))
        finish()
    }
}