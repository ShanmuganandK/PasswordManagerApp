package com.example.passwordmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
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
            private val expiryText: TextView = itemView.findViewById(R.id.expiry_text)
            private val notesText: TextView = itemView.findViewById(R.id.notes_text)
            private val togglePasswordButton: ImageButton = itemView.findViewById(R.id.btn_toggle_password)
            
            private var isPasswordVisible = false
            private var currentPassword = ""

            fun bind(passwordEntry: PasswordEntry) {
                titleText.text = passwordEntry.context
                usernameText.text = if (passwordEntry.username.isNotEmpty()) "Username: ${passwordEntry.username}" else "Username: (not stored)"
                
                // Store the actual password and show masked version by default
                currentPassword = passwordEntry.password
                isPasswordVisible = false
                updatePasswordDisplay()
                
                expiryText.text = if (passwordEntry.expiryDate.isNotEmpty()) "Expires: ${formatExpiryDate(passwordEntry.expiryDate)}" else "Expires: Never"
                notesText.text = if (passwordEntry.notes.isNotEmpty()) "Notes: ${passwordEntry.notes}" else ""
                notesText.visibility = if (passwordEntry.notes.isNotEmpty()) View.VISIBLE else View.GONE
                
                // Set up password toggle button
                togglePasswordButton.setOnClickListener {
                    isPasswordVisible = !isPasswordVisible
                    updatePasswordDisplay()
                }
                
                // Add click listener for editing
                itemView.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putParcelable("passwordEntry", passwordEntry)
                    findNavController().navigate(R.id.action_passwordListFragment_to_editPasswordFragment, bundle)
                }
            }
            
            private fun updatePasswordDisplay() {
                if (isPasswordVisible) {
                    passwordText.text = "Password: $currentPassword"
                    togglePasswordButton.setImageResource(R.drawable.ic_visibility)
                } else {
                    passwordText.text = "Password: ${maskPassword(currentPassword)}"
                    togglePasswordButton.setImageResource(R.drawable.ic_visibility_off)
                }
            }
            
            private fun maskPassword(password: String): String {
                return if (password.length <= 2) {
                    "*".repeat(password.length)
                } else {
                    password.first() + "*".repeat(password.length - 2) + password.last()
                }
            }

            private fun formatExpiryDate(expiryDate: String): String {
                return try {
                    val date = java.time.LocalDate.parse(expiryDate, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    val monthName = date.month.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH)
                    "${monthName} ${date.year}"
                } catch (e: Exception) {
                    expiryDate
                }
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