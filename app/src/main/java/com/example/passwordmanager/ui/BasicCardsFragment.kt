package com.example.passwordmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.passwordmanager.R

class BasicCardsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BasicCardAdapter

    // Simple sample data
    private val sampleCards = listOf(
        BasicCard("HDFC Bank", "•••• 3768"),
        BasicCard("Emirates Islamic", "•••• 1234"),
        BasicCard("Dubai First", "•••• 5678")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_basic_cards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView(view)
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.basic_cards_recycler)
        
        adapter = BasicCardAdapter(sampleCards) { card ->
            Toast.makeText(context, "Selected: ${card.bankName}", Toast.LENGTH_SHORT).show()
        }
        
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    // Simple data class
    data class BasicCard(
        val bankName: String,
        val cardNumber: String
    )

    // Simple adapter
    class BasicCardAdapter(
        private val cards: List<BasicCard>,
        private val onCardClick: (BasicCard) -> Unit
    ) : RecyclerView.Adapter<BasicCardAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val bankName: android.widget.TextView = view.findViewById(R.id.bank_name)
            val cardNumber: android.widget.TextView = view.findViewById(R.id.card_number)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_basic_card, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val card = cards[position]
            holder.bankName.text = card.bankName
            holder.cardNumber.text = card.cardNumber
            
            holder.itemView.setOnClickListener {
                onCardClick(card)
            }
        }

        override fun getItemCount() = cards.size
    }
}