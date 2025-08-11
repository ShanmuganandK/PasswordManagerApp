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
import com.example.passwordmanager.data.PasswordRepository
import com.example.passwordmanager.databinding.FragmentPasswordListBinding
import com.example.passwordmanager.model.PasswordEntry
import com.example.passwordmanager.util.CardColorUtil
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

        binding.addPasswordButton.setOnClickListener {
            requireParentFragment().findNavController().navigate(R.id.action_mainFragment_to_addPasswordFragment)
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
                .inflate(R.layout.password_card_preview, parent, false)
            return PasswordViewHolder(view)
        }

        override fun onBindViewHolder(holder: PasswordViewHolder, position: Int) {
            val passwordEntry = passwords[position]
            holder.bind(passwordEntry, position)
        }

        override fun getItemCount(): Int = passwords.size

        inner class PasswordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val contextText: TextView = itemView.findViewById(R.id.context_text)
            private val appNameText: TextView = itemView.findViewById(R.id.app_name_text)
            private val usernameText: TextView = itemView.findViewById(R.id.username_text)
            private val passwordText: TextView = itemView.findViewById(R.id.password_text)
            private val expiryText: TextView = itemView.findViewById(R.id.expiry_text)

            fun bind(passwordEntry: PasswordEntry, position: Int) {
                val backgroundRes = CardColorUtil.getGlassmorphismCardBackground(itemView.context, position)
                itemView.setBackgroundResource(backgroundRes)

                contextText.text = if (passwordEntry.context.isNotEmpty()) passwordEntry.context else "Context"
                appNameText.text = if (passwordEntry.context.isNotEmpty()) "${passwordEntry.context} App" else "App Name"
                usernameText.text = if (passwordEntry.username.isNotEmpty()) passwordEntry.username else "Username"
                passwordText.text = if (passwordEntry.password.isNotEmpty()) "•".repeat(passwordEntry.password.length.coerceAtMost(12)) else "••••••••"

                expiryText.text = try {
                    if (passwordEntry.expiryDate.isEmpty()) {
                        "Never"
                    } else {
                        val date = LocalDate.parse(passwordEntry.expiryDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        date.format(DateTimeFormatter.ofPattern("MM/yy"))
                    }
                } catch (e: Exception) {
                    "Never"
                }
                
                itemView.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putParcelable("passwordEntry", passwordEntry)
                    requireParentFragment().findNavController().navigate(R.id.action_mainFragment_to_editPasswordFragment, bundle)
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
