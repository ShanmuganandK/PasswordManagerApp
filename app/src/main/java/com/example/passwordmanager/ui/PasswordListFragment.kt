package com.example.passwordmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.passwordmanager.R
import com.example.passwordmanager.databinding.FragmentPasswordListBinding
import com.example.passwordmanager.data.PasswordRepository
import com.example.passwordmanager.model.PasswordEntry

class PasswordListFragment : Fragment() {

    private var _binding: FragmentPasswordListBinding? = null
    private val binding get() = _binding!!
    private lateinit var passwordRepository: PasswordRepository
    private lateinit var passwordAdapter: PasswordAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPasswordListBinding.inflate(inflater, container, false)
        passwordRepository = PasswordRepository(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.passwordRecyclerView.layoutManager = LinearLayoutManager(context)
        loadPasswords()
        
        // Set up navigation buttons
        binding.addPasswordButton.setOnClickListener {
            findNavController().navigate(R.id.action_passwordListFragment_to_addPasswordFragment)
        }
        
        binding.creditCardButton.setOnClickListener {
            findNavController().navigate(R.id.action_passwordListFragment_to_creditCardListFragment)
        }
    }

    private fun loadPasswords() {
        val passwords: List<PasswordEntry> = passwordRepository.getAllPasswords()
        passwordAdapter = PasswordAdapter(passwords)
        binding.passwordRecyclerView.adapter = passwordAdapter
    }

    inner class PasswordAdapter(private val passwords: List<PasswordEntry>) :
        RecyclerView.Adapter<PasswordAdapter.PasswordViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasswordViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_password, parent, false)
            return PasswordViewHolder(view)
        }

        override fun onBindViewHolder(holder: PasswordViewHolder, position: Int) {
            val passwordEntry = passwords[position]
            holder.bind(passwordEntry)
        }

        override fun getItemCount(): Int = passwords.size

        inner class PasswordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val titleText: TextView = itemView.findViewById(R.id.title_text)
            private val usernameText: TextView = itemView.findViewById(R.id.username_text)
            private val passwordText: TextView = itemView.findViewById(R.id.password_text)

            fun bind(passwordEntry: PasswordEntry) {
                titleText.text = passwordEntry.context
                usernameText.text = "Username: (not stored)"
                passwordText.text = "Password: ${passwordEntry.password}"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadPasswords()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}