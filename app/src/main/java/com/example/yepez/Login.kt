package com.example.yepez

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.content.Context
import android.widget.EditText

interface LoginListener {
    fun onLoginButtonClicked(correo: String)
}


class Login : Fragment() {
    private var loginListener: LoginListener? = null

    fun setLoginListener(listener: LoginListener) {
        loginListener = listener
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LoginListener) {
            loginListener = context
        } else {
            throw RuntimeException("$context must implement LoginListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        val correoEditText = view.findViewById<EditText>(R.id.correo)
        val loginButton = view.findViewById<Button>(R.id.entrar)

        loginButton.setOnClickListener {
            val correo = correoEditText.text.toString()
            loginListener?.onLoginButtonClicked(correo)
        }

        return view
    }
}
