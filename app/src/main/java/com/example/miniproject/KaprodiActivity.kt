package com.example.miniproject

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class KaprodiActivity : AppCompatActivity() {

    private lateinit var dbRef: DatabaseReference
    private lateinit var listView: LinearLayout
    private lateinit var etNama: EditText
    private lateinit var etKode: EditText
    private lateinit var etSks: EditText
    private lateinit var etSemester: EditText
    private lateinit var btnTambah: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kaprodi)

        dbRef = FirebaseDatabase.getInstance().getReference("matakuliah")

        etNama = findViewById(R.id.etNamaMatkul)
        etKode = findViewById(R.id.etKodeMatkul)
        etSks = findViewById(R.id.etSks)
        etSemester = findViewById(R.id.etSemester)
        btnTambah = findViewById(R.id.btnTambahMatkul)
        listView = findViewById(R.id.listMatkul)

        btnTambah.setOnClickListener {
            tambahMatkul()
        }

        tampilkanMatkul()
    }

    private fun tambahMatkul() {
        val nama = etNama.text.toString()
        val kode = etKode.text.toString()
        val sks = etSks.text.toString()
        val semester = etSemester.text.toString()

        if (nama.isBlank() || kode.isBlank() || sks.isBlank() || semester.isBlank()) {
            Toast.makeText(this, "Isi semua kolom", Toast.LENGTH_SHORT).show()
            return
        }

        val id = dbRef.push().key ?: return
        val matkul = MataKuliah(kode, nama, semester, sks.toInt(), "")
        dbRef.child(id).setValue(matkul)

        Toast.makeText(this, "Mata kuliah ditambahkan", Toast.LENGTH_SHORT).show()
        etNama.text.clear()
        etKode.text.clear()
        etSks.text.clear()
        etSemester.text.clear()
    }

    private fun tampilkanMatkul() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listView.removeAllViews()
                for (item in snapshot.children) {
                    val matkul = item.getValue(MataKuliah::class.java)
                    if (matkul != null) {
                        val text = TextView(this@KaprodiActivity)
                        text.text = "${matkul.kode} - ${matkul.nama} (${matkul.sks} SKS)"
                        listView.addView(text)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@KaprodiActivity, "Gagal ambil data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}