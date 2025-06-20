package com.mercu.intrain.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mercu.intrain.R
import com.mercu.intrain.ui.history.SessionHistoryFragment

class ChatHistoryFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private val viewModel: HistoryViewModel by activityViewModels()
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_history, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = HistoryAdapter(onChatClick = { chatSession ->
            val fragment = SessionHistoryFragment.newInstance(chatSession.sessionId)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, fragment)
                .addToBackStack(null)
                .commit()
        })
        recyclerView.adapter = adapter
        viewModel.chatHistory.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }
        viewModel.loadAllHistories()
    }
} 