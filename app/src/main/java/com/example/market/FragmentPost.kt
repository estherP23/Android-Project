package com.example.market

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class FragmentPost : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.post_fragment, container, false)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val titleEditText = view.findViewById<EditText>(R.id.titleEditText)
        val contentEditText = view.findViewById<EditText>(R.id.contentEditText)
        val priceEditText = view.findViewById<EditText>(R.id.priceEditText)
        val postButton = view.findViewById<Button>(R.id.postButton)

        postButton.setOnClickListener {

            val title = titleEditText.text.toString()
            val content = contentEditText.text.toString()
            val priceString = priceEditText.text.toString()
            val sell = true
            val price = priceString.toInt()
            val id = UUID.randomUUID().toString()

            // 현재 로그인한 사용자의 이메일 가져오기
            val userEmail = auth.currentUser?.email
            if (userEmail == null) {
                // 이메일을 가져오는데 실패하면 오류 메시지를 표시하고 리턴
                Toast.makeText(context, "사용자 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firestore에서 사용자 정보 가져오기
            db.collection("users").document(userEmail)
                .get()
                .addOnSuccessListener { document ->
                    // 'name' 필드를 문자열로 가져오기
                    val seller = document.getString("name")
                    if (seller == null) {
                        // 'name' 필드를 가져오는데 실패하면 오류 메시지를 표시하고 리턴
                        Toast.makeText(context, "판매자 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    // 게시글 데이터 생성
                    val post = hashMapOf(
                        "id" to id,
                        "title" to title,
                        "content" to content,
                        "price" to price,
                        "seller" to seller,
                        "sellerEmail" to userEmail,
                        "sell" to sell
                    )

                    // 입력 검증
                    if (title.isEmpty() || content.isEmpty() || priceString.isEmpty()) {
                        Toast.makeText(context, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    // Firestore에 게시글 데이터 추가
                    db.collection("posts")
                        .add(post)
                        .addOnSuccessListener {
                            // 데이터 추가 성공시 로그 출력 및 액티비티 이동
                            Log.d("PostFragment", "글 등록 성공")
                            val intent = Intent(context, ActivityNavigation::class.java)
                            startActivity(intent)
                        }
                        .addOnFailureListener {
                            // 데이터 추가 실패시 오류 메시지 표시
                            Toast.makeText(context, "글 등록 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { exception ->
                    // 사용자 정보를 가져오는데 실패하면 오류 메시지 표시
                    Toast.makeText(context, "판매자 정보를 불러오는데 실패했습니다: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }

        return view
    }
}
