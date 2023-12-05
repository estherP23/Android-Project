package com.example.market.Profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.market.DetailFragmen
import com.example.market.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FragmentEdit : Fragment() {

    // Firebase Firestore 인스턴스 생성
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // fragment_edit_post 레이아웃을 인플레이트하여 뷰 생성
        val view = inflater.inflate(R.layout.product_edit_fragment, container, false)

        // 전달된 productId를 가져오기
        val productId = arguments?.getString("productId")
        // Firebase Firestore 인스턴스 초기화
        db = FirebaseFirestore.getInstance()

        // 만약 productId가 null이 아니면 실행
        if (productId != null) {

            // 뷰 요소 초기화
            val titleTextView = view.findViewById<TextView>(R.id.titleTextView)

            val contentTextView = view.findViewById<TextView>(R.id.contentTextView)

            val priceText = view.findViewById<TextView>(R.id.priceTextView)
            val editPriceText = view.findViewById<EditText>(R.id.priceEditText)
            val sellerTextView = view.findViewById<TextView>(R.id.sellerTextView)
            val availableCheckBox = view.findViewById<CheckBox>(R.id.isAvailableCheckBox)
            val editButton = view.findViewById<Button>(R.id.editButton)
            val titleEditText = view.findViewById<EditText>(R.id.titleEditText)
            val contentEditText = view.findViewById<EditText>(R.id.contentEditText)

            // Firebase Firestore에서 해당 productId의 데이터 가져오기
            db.collection("posts")
                .document(productId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val product = documentSnapshot.toObject(DetailFragmen::class.java)
                    if (product != null) {
                        // 제품 정보를 뷰에 설정
                        titleTextView.text = product.title
                        contentTextView.text = product.content
                        priceText.text = product.price.toString()
                        sellerTextView.text = product.seller
                        availableCheckBox.isChecked = product.sell

                        // 수정 버튼 클릭 리스너 설정
                        editButton.setOnClickListener {
                            if (editButton.text == "수정하기") {
                                // 수정하기 버튼 클릭 시, 편집 가능한 뷰로 변경
                                titleEditText.setText(titleTextView.text.toString())
                                contentEditText.setText(contentTextView.text.toString())
                                editPriceText.setText(priceText.text.toString())

                                // 뷰 가시성 설정
                                titleEditText.visibility = View.VISIBLE
                                contentEditText.visibility = View.VISIBLE
                                editPriceText.visibility = View.VISIBLE
                                availableCheckBox.visibility = View.VISIBLE
                                sellerTextView.visibility = View.VISIBLE

                                titleTextView.visibility = View.GONE
                                contentTextView.visibility = View.GONE
                                priceText.visibility = View.GONE

                                editButton.text = "수정완료"
                            } else if (editButton.text == "수정완료") {
                                // 수정완료 버튼 클릭 시, 입력된 데이터로 게시물 업데이트
                                val title = titleEditText.text.toString()
                                val content = contentEditText.text.toString()
                                val priceString = editPriceText.text.toString()
                                val isAvailable = availableCheckBox.isChecked
                                val price = priceString.toIntOrNull()

                                // 입력 값 유효성 검사
                                if (title.isEmpty() || content.isEmpty() || price == null) {
                                    Toast.makeText(context, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT)
                                        .show()
                                    return@setOnClickListener
                                }

                                // 현재 사용자의 이메일 가져오기
                                val sellerEmail = FirebaseAuth.getInstance().currentUser?.email

                                // 수정된 게시물 데이터 생성
                                val post = hashMapOf(
                                    "id" to productId,
                                    "title" to title,
                                    "content" to content,
                                    "price" to price,
                                    "sell" to isAvailable,
                                    "sellerEmail" to sellerEmail
                                )

                                // Firebase Firestore에 수정된 데이터 업데이트
                                db.collection("posts").document(productId)
                                    .update(post as Map<String, Any>)
                                    .addOnSuccessListener {
                                        // 성공적으로 수정된 경우, Toast 메시지 표시 및 뷰 업데이트
                                        Toast.makeText(context, "게시물이 수정 완료", Toast.LENGTH_SHORT)
                                            .show()

                                        titleEditText.visibility = View.GONE
                                        contentEditText.visibility = View.GONE
                                        editPriceText.visibility = View.GONE
                                        availableCheckBox.visibility = View.GONE

                                        titleTextView.visibility = View.VISIBLE
                                        contentTextView.visibility = View.VISIBLE
                                        priceText.visibility = View.VISIBLE

                                        titleTextView.text = title
                                        contentTextView.text = content
                                        priceText.text = price.toString()

                                        editButton.text = "수정하기"
                                    }
                                    .addOnFailureListener {
                                        // 수정 실패 시 Toast 메시지 표시
                                        Toast.makeText(
                                            context,
                                            "게시물 수정 실패: ${it.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        }
                    }
                }
        }
        return view // 생성된 뷰 반환
    }
}
