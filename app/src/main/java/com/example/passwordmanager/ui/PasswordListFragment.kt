package com.example.passwordmanager.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
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

        binding.passwordRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        loadPasswords()

        binding.addPasswordButton.setOnClickListener {
            // Navigate from parent fragment's NavController
            requireParentFragment().findNavController().navigate(R.id.action_mainFragment_to_addPasswordFragment)
        }
    }

    private fun loadPasswords() {
        val passwords: List<PasswordEntry> = passwordRepository.getAllPasswords()
        passwordAdapter = PasswordAdapter(passwords)
        binding.passwordRecyclerView.adapter = null
        binding.passwordRecyclerView.adapter = passwordAdapter
        passwordAdapter.notifyDataSetChanged()
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
            private val contextText: TextView? = itemView.findViewById(R.id.context_text)
            private val appNameText: TextView? = itemView.findViewById(R.id.app_name_text)
            private val usernameText: TextView? = itemView.findViewById(R.id.username_text)
            private val passwordText: TextView? = itemView.findViewById(R.id.password_text)
            private val expiryText: TextView? = itemView.findViewById(R.id.expiry_text)

            fun bind(passwordEntry: PasswordEntry, position: Int) {
                val backgroundRes = CardColorUtil.getGlassmorphismCardBackground(itemView.context, position)
                itemView.setBackgroundResource(backgroundRes)
                
                // Debug: Check if views are found
                android.util.Log.d("PasswordCard", "Views found: context=${contextText != null}, app=${appNameText != null}, user=${usernameText != null}, pass=${passwordText != null}, exp=${expiryText != null}")
                
                contextText?.text = passwordEntry.context
                appNameText?.text = "${passwordEntry.context} App"
                usernameText?.text = if (passwordEntry.username.isNotEmpty()) passwordEntry.username else "test@email.com"
                passwordText?.text = "•".repeat(8)
                
                // Format expiry date
                expiryText?.text = "12/25"

                itemView.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putParcelable("passwordEntry", passwordEntry)
                    requireParentFragment().findNavController().navigate(
                        R.id.action_mainFragment_to_editPasswordFragment,
                        bundle
                    )
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
