package com.example.contactapp

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: ContactAdapter
    private val contacts = mutableListOf(
        Contact("Iron Man", "123-456-7890", "ironman@avengers.com"),
        Contact("Captain America", "098-765-4321", "captainamerica@avengers.com"),
        Contact("Thor", "111-222-3333", "thor@avengers.com"),
        Contact("Hulk", "444-555-6666", "hulk@avengers.com"),
        Contact("Black Widow", "777-888-9999", "blackwidow@avengers.com"),
        Contact("Hawkeye", "000-111-2222", "hawkeye@avengers.com")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val username = intent.getStringExtra("USERNAME")

        val profileIcon = findViewById<ImageView>(R.id.profile_icon)
        profileIcon.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }

        val contactList = findViewById<RecyclerView>(R.id.contact_list)
        contactList.layoutManager = LinearLayoutManager(this)

        adapter = ContactAdapter(contacts, {
            showEditDialog(it)
        }, {
            contacts.remove(it)
            adapter.notifyDataSetChanged()
            ToastHelper.showCustomToast(this, "Contact deleted successfully", ToastHelper.ToastType.SUCCESS)
        })
        contactList.adapter = adapter
    }

    private fun showEditDialog(contact: Contact) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_contact, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val nameInput = dialogView.findViewById<EditText>(R.id.editName)
        val phoneInput = dialogView.findViewById<EditText>(R.id.editPhone)
        val emailInput = dialogView.findViewById<EditText>(R.id.editEmail)
        val btnSave = dialogView.findViewById<android.widget.Button>(R.id.btnSave)
        val btnCancel = dialogView.findViewById<android.widget.Button>(R.id.btnCancel)

        nameInput.setText(contact.name)
        phoneInput.setText(contact.phone)
        emailInput.setText(contact.email)

        btnSave.setOnClickListener {
            val newName = nameInput.text.toString()
            val newPhone = phoneInput.text.toString()
            val newEmail = emailInput.text.toString()
            val newContact = contact.copy(name = newName, phone = newPhone, email = newEmail)
            val index = contacts.indexOf(contact)
            if (index != -1) {
                contacts[index] = newContact
                adapter.notifyItemChanged(index)
                ToastHelper.showCustomToast(this, "Contact updated successfully", ToastHelper.ToastType.SUCCESS)
            }
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}