package com.example.contactapp

import android.content.ContentValues
import android.content.Context
import android.util.Log

class ContactDAO(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // Untuk Create (Tambah kontak)
    fun addContact(contact: Contact): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NAME, contact.name)
            put(DatabaseHelper.COLUMN_PHONE, contact.phone)
            put(DatabaseHelper.COLUMN_EMAIL, contact.email)
        }
        val id = db.insert(DatabaseHelper.TABLE_CONTACTS, null, values)
        Log.d("ContactDAO", "Contact added with ID: $id") //untuk debug log nya
        db.close()
        return id
    }

    // Untuk Read (ngambil semua kontak e)
    fun getAllContact(): List<Contact> {
        val contactList = mutableListOf<Contact>()
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            DatabaseHelper.TABLE_CONTACTS,
            null, null, null, null, null,
            "${DatabaseHelper.COLUMN_NAME} ASC"
        )

        with(cursor) {
            Log.d("ContactDAO", "Total contacts in DB: $count") //untuk debug log nya
            while (moveToNext()) {
                val contact = Contact(
                    id = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                    name = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)),
                    phone = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHONE)),
                    email = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL))
                )
                contactList.add(contact)
            }
            close()
        }
        db.close()
        return contactList
    }

    // Read ngambil 1 kontak sesuai ID
    fun getContact(id: Int): Contact? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_CONTACTS,
            null,
            "${DatabaseHelper.COLUMN_ID}=?",
            arrayOf(id.toString()),
            null, null, null
        )

        var contact: Contact? = null
        if (cursor.moveToFirst()) {
            contact = Contact(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)),
                phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHONE)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL))
            )
        }
        cursor.close()
        db.close()
        return contact
    }

    // Update kontakk
    fun updateContact(contact: Contact): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NAME, contact.name)
            put(DatabaseHelper.COLUMN_PHONE, contact.phone)
            put(DatabaseHelper.COLUMN_EMAIL, contact.email)
        }
        val rowsAffected = db.update(
            DatabaseHelper.TABLE_CONTACTS,
            values,
            "${DatabaseHelper.COLUMN_ID}=?",
            arrayOf(contact.id.toString())
        )
        db.close()
        return rowsAffected
    }

    // Hapus kontakkk
    fun deleteContact(id: Int): Int {
        val db = dbHelper.writableDatabase
        val rowsDeleted = db.delete(
            DatabaseHelper.TABLE_CONTACTS,
            "${DatabaseHelper.COLUMN_ID}=?",
            arrayOf(id.toString())
        )
        Log.d("ContactDAO", "Deleted $rowsDeleted rows") //untuk debug log nya
        db.close()
        return rowsDeleted
    }
}