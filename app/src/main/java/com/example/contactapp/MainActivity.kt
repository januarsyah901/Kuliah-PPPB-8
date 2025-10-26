package com.example.contactapp

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
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
            Toast.makeText(this, "Contact deleted", Toast.LENGTH_SHORT).show()
        })
        contactList.adapter = adapter
    }

    private fun showEditDialog(contact: Contact) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Contact")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val nameInput = EditText(this)
        nameInput.setText(contact.name)
        layout.addView(nameInput)

        val phoneInput = EditText(this)
        phoneInput.setText(contact.phone)
        layout.addView(phoneInput)

        val emailInput = EditText(this)
        emailInput.setText(contact.email)
        layout.addView(emailInput)

        builder.setView(layout)

        builder.setPositiveButton("Save") { _, _ ->
            val newName = nameInput.text.toString()
            val newPhone = phoneInput.text.toString()
            val newEmail = emailInput.text.toString()
            val newContact = contact.copy(name = newName, phone = newPhone, email = newEmail)
            val index = contacts.indexOf(contact)
            if (index != -1) {
                contacts[index] = newContact
                adapter.notifyItemChanged(index)
                Toast.makeText(this, "Contact updated", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel", null)

        builder.show()
    }
}