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

//회원가입 프래그먼트를 맡고있는 클래스, 실질적으로 파이어베이스를 사용하기 시작함.
class SignUpFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        //onCreatView의 매개변수
        inflater: LayoutInflater, //레이아웃 xml파일을 view 객체로변환하는데 사용
        container: ViewGroup?, //이 프래그먼트가 추가 될 부모 뷰
        savedInstanceState: Bundle? //이전 상태를 저장하는데 사용되는 key-value 쌍의 객체
    ): View? {
        //매개변수로 받아온 inflater 객체의 inflate 메소드를 사용해 fragment_signUp 레이아웃 파일을
        //실제 View 객체로 전환해줌.
        val view = inflater.inflate(R.layout.signup_fragment, container, false)

        //가입을 위한 auth 와 저장할 데이터베이스 firestore 객체를 가져온다.
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val emailEditText = view.findViewById<EditText>(R.id.SignUpEmailEditText)
        val passwordEditText = view.findViewById<EditText>(R.id.SignUpPasswordEditText)
        val nameEditText = view.findViewById<EditText>(R.id.nameEditText)
        val birthEditText = view.findViewById<EditText>(R.id.birthEditText)
        val signUpButton = view.findViewById<Button>(R.id.loginBtn)

        //signUp버튼을 눌렀을 때 수행되는 동작을 정의
        signUpButton.setOnClickListener {
            //입력필드들에서 받아온 텍스트들을 변수에 저장한다.
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val name = nameEditText.text.toString()
            val birth = birthEditText.text.toString()


            if (birth.length != 6) {
                Toast.makeText(context, "생년월일은 6자리로 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //두 조건이 모두 만족하면 firebase auth를 이용해 회원가입을 시도한다.
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // 회원가입 성공, Firestore에 사용자 정보 저장
                        //사용자 정보는 아래의 것들을 포함하는 해시맵이다.
                        val user = hashMapOf(
                            "email" to email,
                            "password" to password,
                            "name" to name,
                            "birth" to birth
                        )
                        //firestore db 내에 users라는 이름의 컬렉션에 접근
                        db.collection("users")
                            //사용자 이메일을 document id로 하여 사용자 정보를 저장한다.
                            .document(email)
                            //위에서 저장한 user해시맵 데이터를 새로운 문서로 추가
                            .set(user)
                            //db에 데이터 추가 작업이 성공했을 경우
                            .addOnSuccessListener {
                                // 자동로그인, 바텀내비게이션 액티비티로 이동
                                startActivity(Intent(context, ActivityNavigation::class.java))
                                activity?.finish()
                            }
                            //실패시 오류메시지를 토스트로 표시, 영어로 되어있던 것들을 한글로 변경
                            .addOnFailureListener {
                                // 저장 실패, 오류 메시지 표시
                                val message = when (it.message) {
                                    //"The email address is badly formatted." -> "이메일 형식이 올바르지 않습니다."
                                    //"The given password is invalid. [ Password should be at least 6 characters ]" -> "비밀번호는 최소 6자 이상이어야 합니다."
                                    //"The email address is already in use by another account." -> "이미 사용 중인 이메일 주소입니다."
                                    else -> "회원가입 실패: ${it.message}"
                                }
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        // 회원가입 실패, 오류 메시지 표시
                        val message = when (task.exception?.message) {
                            //"The email address is badly formatted." -> "이메일 형식이 올바르지 않습니다."
                            //"The given password is invalid. [ Password should be at least 6 characters ]" -> "비밀번호는 최소 6자 이상이어야 합니다."
                            //"The email address is already in use by another account." -> "이미 사용 중인 이메일 주소입니다."
                            else -> "회원가입 실패: ${task.exception?.message}"
                        }
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }
        }

        return view
    }
}
