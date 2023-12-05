package com.example.market.SignInUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.market.R

class InitialFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.first_fragment, container, false)

        // NavController를 가져옵니다. StartActivity 에서 nav_host_fragment를 지정해뒀고
        // nav_graph에서 initialFragment를 설정해뒀기에 findNavController를 이용해서
        //navController를 가져올 수 있고 이걸 이용해서 navigate롤 화면 전환을 수행한다.
        val navController = findNavController()

        val btnLogin = view.findViewById<Button>(R.id.btn_login)
        val btnSignUp = view.findViewById<Button>(R.id.btn_sign_up)

        btnLogin.setOnClickListener {
            // 로그인 버튼을 눌렀을 때 로그인 화면으로 이동하는 코드, 액션 설정해둠!
            navController.navigate(R.id.action_initialFragment_to_loginFragment)
        }

        btnSignUp.setOnClickListener {
            // 회원가입 버튼을 눌렀을 때 회원가입 화면으로 이동하는 코드
            navController.navigate(R.id.action_initialFragment_to_signUpFragment)
        }

        return view
    }
}
