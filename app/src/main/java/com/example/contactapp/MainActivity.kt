package com.example.contactapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var contactDAO: ContactDAO
    private lateinit var adapter: ContactAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("MainActivity", "========== APP STARTED ==========")

        val username = intent.getStringExtra("USERNAME")

        // Profile icon
        val profileIcon = findViewById<ImageView>(R.id.profile_icon)
        profileIcon.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }

        // MEnghubungkan ke databasenya
        contactDAO = ContactDAO(this)
        Log.d("MainActivity", "ContactDAO initialized")

        // tes database make dummy sek
        testDatabase()
        Log.d("MainActivity", "testDatabase() completed")

        // recyclerView setup
        recyclerView = findViewById(R.id.contact_list)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Setup adapter
        setupAdapter()

        // Load contacts dari database
        loadContacts()

        // FAB untuk tambah contact anyar
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)
        fabAdd.setOnClickListener {
            showAddContactDialog()
        }
    }

    // tes database e make dummy, ini dipake nek database e kosong
    private fun testDatabase() {
        val existingContacts = contactDAO.getAllContact()

        // Kalau database kosong, tambahkan dummy data
        if (existingContacts.isEmpty()) {
            Log.d("MainActivity", "Database empty, inserting dummy data...")

            val dummyContacts = listOf(
                Contact(name = "Iron Man", phone = "123-456-7890", email = "ironman@avengers.com"),
                Contact(name = "Captain America", phone = "098-765-4321", email = "captainamerica@avengers.com"),
                Contact(name = "Thor", phone = "111-222-3333", email = "thor@avengers.com"),
                Contact(name = "Hulk", phone = "444-555-6666", email = "hulk@avengers.com"),
                Contact(name = "Black Widow", phone = "777-888-9999", email = "blackwidow@avengers.com"),
                Contact(name = "Hawkeye", phone = "000-111-2222", email = "hawkeye@avengers.com")
            )

            dummyContacts.forEach { contact ->
                val id = contactDAO.addContact(contact)
                Log.d("MainActivity", "Inserted contact: ${contact.name} with ID: $id")
            }
        } else {
            Log.d("MainActivity", "Database already has ${existingContacts.size} contacts")
        }
    }

    private fun setupAdapter() {
        adapter = ContactAdapter(
            contacts = emptyList(),
            onEdit = { contact ->
                // Tombol Edit diklik → Show dialog
                showEditDialog(contact)
            },
            onDelete = { contact ->
                // Tombol Delete diklik → Show confirmation
                showDeleteDialog(contact)
            }
        )
        recyclerView.adapter = adapter
    }

    // load data dari database
    private fun loadContacts() {
        val contacts = contactDAO.getAllContact()
        Log.d("MainActivity", "Loaded ${contacts.size} contacts from database")
        adapter.updateData(contacts)
    }

    // Dialog untuk edit contact
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

        // Pre-fill dengan data lama
        nameInput.setText(contact.name)
        phoneInput.setText(contact.phone)
        emailInput.setText(contact.email)

        btnSave.setOnClickListener {
            val newName = nameInput.text.toString().trim()
            val newPhone = phoneInput.text.toString().trim()
            val newEmail = emailInput.text.toString().trim()

            if (newName.isEmpty()) {
                ToastHelper.showCustomToast(this, "Name cannot be empty", ToastHelper.ToastType.ERROR)
                return@setOnClickListener
            }

            // Update di database
            val updatedContact = contact.copy(
                name = newName,
                phone = newPhone,
                email = newEmail
            )
            contactDAO.updateContact(updatedContact)

            // Refresh list
            loadContacts()

            ToastHelper.showCustomToast(this, "Contact updated successfully", ToastHelper.ToastType.SUCCESS)
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    // Dialog konfirmasi delete
    private fun showDeleteDialog(contact: Contact) {
        AlertDialog.Builder(this)
            .setTitle("Delete Contact")
            .setMessage("Are you sure you want to delete ${contact.name}?")
            .setPositiveButton("Delete") { _, _ ->
                // Hapus dari database
                contactDAO.deleteContact(contact.id)

                // Refresh list
                loadContacts()

                ToastHelper.showCustomToast(this, "Contact deleted successfully", ToastHelper.ToastType.SUCCESS)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // Dialog untuk tambah contact baru
    private fun showAddContactDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_contact, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val nameInput = dialogView.findViewById<EditText>(R.id.addName)
        val phoneInput = dialogView.findViewById<EditText>(R.id.addPhone)
        val emailInput = dialogView.findViewById<EditText>(R.id.addEmail)
        val btnSave = dialogView.findViewById<android.widget.Button>(R.id.btnSaveAdd)
        val btnCancel = dialogView.findViewById<android.widget.Button>(R.id.btnCancelAdd)

        btnSave.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()
            val email = emailInput.text.toString().trim()

            // Validasi: Nama tidak boleh kosong
            if (name.isEmpty()) {
                ToastHelper.showCustomToast(this, "Name cannot be empty", ToastHelper.ToastType.ERROR)
                return@setOnClickListener
            }

            // Buat contact baru dan simpan ke database
            val newContact = Contact(
                name = name,
                phone = phone,
                email = email
            )

            val id = contactDAO.addContact(newContact)

            if (id > 0) {
                // Refresh list
                loadContacts()

                ToastHelper.showCustomToast(this, "Contact added successfully", ToastHelper.ToastType.SUCCESS)
                dialog.dismiss()
            } else {
                ToastHelper.showCustomToast(this, "Failed to add contact", ToastHelper.ToastType.ERROR)
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    // Refresh data setiap kali kembali ke activity
    override fun onResume() {
        super.onResume()
        loadContacts()
    }
}
