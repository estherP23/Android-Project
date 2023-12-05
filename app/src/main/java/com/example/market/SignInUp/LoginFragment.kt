package com.example.market.SignInUp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.market.ActivityNavigation
import com.example.market.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //매개변수로 받아온 inflater 객체의 inflate 메소드를 사용해 fragment_signUp 레이아웃 파일을
        //실제 View 객체로 전환해줌.
        val view = inflater.inflate(R.layout.login_fragment, container, false)

        //로그인을 위한 auth 와 저장이 되어있는지 확인하기위한 데이터베이스 firestore 객체를 가져온다.
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val emailEditText = view.findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = view.findViewById<EditText>(R.id.PasswordEditText)
        val loginBtn = view.findViewById<Button>(R.id.loginBtn)

        //로그인 버튼을 눌렀을 때 행하는 동작 구현
        loginBtn.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            //auth의 이 메소드를 이용해서 이메일과 패스워드를 주고 로그인을 시도
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // 로그인 성공, 메인 화면으로 이동
                        val intent = Intent(context, ActivityNavigation::class.java)
                        startActivity(intent)
                    } else {
                        // 로그인 실패, 오류 메시지 표시
                        Toast.makeText(context, "아이디/비밀번호 오류", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        return view
    }
}
