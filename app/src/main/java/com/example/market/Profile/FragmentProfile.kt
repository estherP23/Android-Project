package com.example.market.Profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.market.Home.Products
import com.example.market.Home.ProductAdapter
import com.example.market.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FragmentProfile : Fragment() {
    // 뷰 및 Firebase 관련 객체 초기화
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.profile, container, false)
        // 리사이클러뷰 설정
        recyclerView = view.findViewById(R.id.profileFragment)
        recyclerView.layoutManager = LinearLayoutManager(context)
        // 어댑터 설정
        adapter = ProductAdapter(requireContext(), mutableListOf(), findNavController(), true)
        recyclerView.adapter = adapter

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("ProfileFragment", "User not logged in")
            activity?.finish() // 로그인하지 않은 사용자가 이 프래그먼트에 접근하려 할 때 액티비티를 종료합니다.
            return view
        }
        // currentUser.email이 null이 아닐 때만 loadUserPosts 함수를 호출합니다.
        currentUser.email?.let { userEmail ->
            loadUserPosts(userEmail)
        }
        return view
    }
    private fun loadUserPosts(userEmail: String) {
        // Firestore에서 userEmail과 일치하는 게시물을 조회
        db.collection("posts")
            .whereEqualTo("sellerEmail",userEmail)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val productList = mutableListOf<Products>()
                for (document in querySnapshot.documents) {
                    //Product 객체로 변환
                    val product = document.toObject(Products::class.java)
                    if (product != null) {
                        // Product 객체의 id 필드에 Firestore 문서의 ID 설정
                        product.id = document.id
                        // Product 객체를 리스트에 추가
                        productList.add(product)
                    }
                }
                // 어댑터에 데이터 설정
                adapter.setData(productList)
            }
            .addOnFailureListener { exception ->
                // 게시물 불러오기 실패 시 로그 출력
                Log.e("ProfileFragment", "Error getting user posts", exception)
            }
    }

}
