package com.example.contactapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(
    private var contacts: List<Contact>,  // ← Ubah dari MutableList jadi List
    private val onEdit: (Contact) -> Unit,
    private val onDelete: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.name.text = contact.name
        holder.phone.text = contact.phone
        holder.email.text = contact.email

        // Set initial letter for avatar
        holder.initial.text = if (contact.name.isNotEmpty()) {
            contact.name.first().uppercase()
        } else {
            "?"
        }

        holder.editButton.setOnClickListener { onEdit(contact) }
        holder.deleteButton.setOnClickListener { onDelete(contact) }
    }

    override fun getItemCount() = contacts.size

    // ✅ Method untuk update data dari MainActivity
    fun updateData(newContacts: List<Contact>) {
        contacts = newContacts
        notifyDataSetChanged()
    }

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val initial: TextView = itemView.findViewById(R.id.contact_initial)
        val name: TextView = itemView.findViewById(R.id.contact_name)
        val phone: TextView = itemView.findViewById(R.id.contact_phone)
        val email: TextView = itemView.findViewById(R.id.contact_email)
        val editButton: Button = itemView.findViewById(R.id.edit_button)
        val deleteButton: Button = itemView.findViewById(R.id.delete_button)
    }
}