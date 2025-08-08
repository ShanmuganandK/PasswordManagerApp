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
            findNavController().navigate(R.id.action_mainFragment_to_addPasswordFragment)
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
            holder.bind(passwordEntry, position)
        }

        override fun getItemCount(): Int = passwords.size

        inner class PasswordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val iconImage: ImageView = itemView.findViewById(R.id.icon_image)
            private val titleText: TextView = itemView.findViewById(R.id.title_text)
            private val subtitleText: TextView = itemView.findViewById(R.id.subtitle_text)
            private val usernameText: TextView = itemView.findViewById(R.id.username_text)
            private val passwordText: TextView = itemView.findViewById(R.id.password_text)
            private val copyUsernameButton: ImageButton =
                itemView.findViewById(R.id.btn_copy_username)
            private val copyPasswordButton: ImageButton =
                itemView.findViewById(R.id.btn_copy_password)
            private val togglePasswordButton: ImageButton =
                itemView.findViewById(R.id.btn_toggle_password)

            private var isPasswordVisible = false
            private var currentPassword = ""

            fun bind(passwordEntry: PasswordEntry, position: Int) {
                itemView.background = CardColorUtil.getCardGradient(itemView.context, position)
                titleText.text = toCamelCase(passwordEntry.context)
                subtitleText.text = toCamelCase(passwordEntry.notes)
                usernameText.text = passwordEntry.username
                currentPassword = passwordEntry.password
                updatePasswordDisplay()

                copyUsernameButton.setOnClickListener {
                    copyToClipboard(passwordEntry.username)
                }

                copyPasswordButton.setOnClickListener {
                    copyToClipboard(currentPassword)
                }

                togglePasswordButton.setOnClickListener {
                    isPasswordVisible = !isPasswordVisible
                    updatePasswordDisplay()
                }

                itemView.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putParcelable("passwordEntry", passwordEntry)
                    findNavController().navigate(
                        R.id.action_mainFragment_to_editPasswordFragment,
                        bundle
                    )
                }
            }

            private fun updatePasswordDisplay() {
                if (isPasswordVisible) {
                    passwordText.text = currentPassword
                    togglePasswordButton.setImageResource(R.drawable.ic_visibility)
                } else {
                    passwordText.text = "••••••••••"
                    togglePasswordButton.setImageResource(R.drawable.ic_visibility_off)
                }
            }

            private fun copyToClipboard(text: String) {
                val clipboard =
                    itemView.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("password", text)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(itemView.context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
            }

            private fun toCamelCase(text: String): String {
                return text.split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
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
