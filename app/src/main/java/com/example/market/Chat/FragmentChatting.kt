package com.example.market.Chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.market.Home.ChattingAdapter
import com.example.market.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FragmentChatting : Fragment() {

    // Firebase Authentication 및 Firestore 인스턴스 변수 선언
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // 채팅 목록을 표시하는 RecyclerView 및 어댑터 선언
    private lateinit var chatView: RecyclerView
    private lateinit var chatAdapter: ChattingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 프래그먼트의 레이아웃을 확장(inflate)합니다.
        val view = inflater.inflate(R.layout.chat_fragment, container, false)

        // Firebase 인스턴스 초기화
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // RecyclerView 및 어댑터 초기화
        chatView = view.findViewById(R.id.messageView)
        chatAdapter = ChattingAdapter(this, emptyList())

        // RecyclerView에 LinearLayoutManager 및 어댑터 설정
        chatView.layoutManager = LinearLayoutManager(context)
        chatView.adapter = chatAdapter

        // 메시지를 불러오는 함수 호출
        loadMessage()
        return view
    }

    // Firestore에서 메시지를 불러와 RecyclerView에 표시하는 함수
    private fun loadMessage() {
        // 현재 사용자 이메일 가져오기
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        if (currentUserEmail != null) {
            // Firestore에서 "seller" 필드가 현재 사용자 이메일과 일치하는 채팅 목록을 가져옵니다.
            db.collection("chat")
                .whereEqualTo("seller", currentUserEmail)
                .get()
                .addOnSuccessListener { documents ->
                    val chats = mutableListOf<Chatting>()

                    // 각 문서를 Chat 객체로 변환하여 리스트에 추가합니다.
                    for (document in documents) {
                        val chat = document.toObject(Chatting::class.java)
                        chats.add(chat)
                    }
                    // 어댑터에 가져온 채팅 목록 설정
                    chatAdapter.setChats(chats)
                }
                .addOnFailureListener { e ->
                    // 메시지 불러오기 실패 시 로그에 오류 메시지 출력
                    Log.e("ChatFragment", "메시지 error", e)
                }
        }
    }
}
