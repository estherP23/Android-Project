package com.example.market.SignInUp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.market.DetailFragmen
import com.example.market.R
import com.example.market.Chat.FragmentSend
import com.google.firebase.firestore.FirebaseFirestore

class SellDetailFragment : Fragment() {
    private lateinit var db: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.message, container, false)

        //ProductViewHolder 에서 홈프래그먼트에서 셀디테일프레그먼트로 이동할때 id를
        //넘겨주는 코드가 있다. 그걸 받아오는 거임
        val productId = arguments?.getString("productId")
        db = FirebaseFirestore.getInstance()

        if (productId != null) {
            val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
            val contentTextView = view.findViewById<TextView>(R.id.contentTextView)
            val priceTextView = view.findViewById<TextView>(R.id.priceTextView)
            val sellerTextView = view.findViewById<TextView>(R.id.sellerTextView)
            val isAvailableTextView = view.findViewById<TextView>(R.id.isAvailableTextView)

            db.collection("posts")
                .whereEqualTo("id", productId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        for (document in querySnapshot.documents) {
                            // 각 문서의 데이터를 가져와서 사용
                            val productDetail = document.toObject(DetailFragmen::class.java)
                            if (productDetail != null) {
                                Log.d("SellDetailFragment", "ProductDetail: $productDetail")
                                titleTextView.text = productDetail.title
                                contentTextView.text = productDetail.content
                                priceTextView.text = productDetail.price.toString()
                                sellerTextView.text = productDetail.seller
                                isAvailableTextView.text = if (productDetail.sell) "판매중" else "판매완료"

                                val sendMessageButton = view.findViewById<Button>(R.id.messageButton)
                                sendMessageButton.setOnClickListener {
                                    //productDetail.seller 에게 채팅
                                    Log.d("SellDetailFragment", productDetail.sellerEmail)

                                    // FragmentTransaction 시작
                                    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()

                                    // Bundle에 데이터 담기
                                    val bundle = Bundle().apply {
                                        putString("sellerEmail", productDetail.sellerEmail)
                                    }

                                    // 프래그먼트 인스턴스 생성 및 bundle 전달
                                    val sendFragment = FragmentSend()
                                    sendFragment.arguments = bundle

                                    // 프래그먼트 추가 및 transaction 커밋
                                    fragmentTransaction.replace(R.id.fragment_container, sendFragment)
                                    fragmentTransaction.addToBackStack(null)
                                    fragmentTransaction.commit()
                                }
                            } else {
                                Log.d("SellDetailFragment", "ProductDetail is null")
                            }
                        }
                    } else {
                        Log.d("SellDetailFragment", "No matching documents")
                    }
                }
                .addOnFailureListener { exception ->
                    // 쿼리 실패시 예외 처리
                    Log.e("SellDetailFragment", "Error querying documents", exception)
                }
        }
        return view
    }
}
