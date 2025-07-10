package com.example.miniproject

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DosenActivity : AppCompatActivity() {

    private lateinit var db: FirebaseDatabase
    private lateinit var nilaiRef: DatabaseReference
    private lateinit var matkulRef: DatabaseReference
    private lateinit var uid: String

    private lateinit var etMhsId: EditText
    private lateinit var etMatkulId: EditText
    private lateinit var etUTS: EditText
    private lateinit var etUAS: EditText
    private lateinit var etTugas: EditText
    private lateinit var btnSimpan: Button
    private lateinit var tvInfo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dosen)

        uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        db = FirebaseDatabase.getInstance()
        nilaiRef = db.getReference("nilai")

        etMhsId = findViewById(R.id.etMahasiswaId)
        etMatkulId = findViewById(R.id.etMatkulId)
        etUTS = findViewById(R.id.etUTS)
        etUAS = findViewById(R.id.etUAS)
        etTugas = findViewById(R.id.etTugas)
        btnSimpan = findViewById(R.id.btnSimpanNilai)
        tvInfo = findViewById(R.id.tvInfoDosen)

        tvInfo.text = "Dosen UID: $uid"

        btnSimpan.setOnClickListener {
            simpanNilai()
        }
    }

    private fun simpanNilai() {
        val mhsId = etMhsId.text.toString()
        val matkulId = etMatkulId.text.toString()
        val uts = etUTS.text.toString().toIntOrNull()
        val uas = etUAS.text.toString().toIntOrNull()
        val tugas = etTugas.text.toString().toIntOrNull()

        if (mhsId.isBlank() || matkulId.isBlank() || uts == null || uas == null || tugas == null) {
            Toast.makeText(this, "Isi semua nilai dengan benar", Toast.LENGTH_SHORT).show()
            return
        }

        val nilaiId = nilaiRef.push().key ?: return
        val nilai = Nilai(mhsId, matkulId, uts, uas, tugas)

        nilaiRef.child(nilaiId).setValue(nilai)
        Toast.makeText(this, "Nilai berhasil disimpan", Toast.LENGTH_SHORT).show()

        etMhsId.text.clear()
        etMatkulId.text.clear()
        etUTS.text.clear()
        etUAS.text.clear()
        etTugas.text.clear()
    }
}
