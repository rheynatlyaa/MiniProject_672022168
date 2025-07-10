package com.example.miniproject

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MahasiswaActivity : AppCompatActivity() {

    private lateinit var db: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var uid: String

    private lateinit var tvWelcome: TextView
    private lateinit var btnRegUlang: Button
    private lateinit var btnDaftarMatkul: Button
    private lateinit var btnAbsensi: Button
    private lateinit var btnLihatNilai: Button
    private lateinit var tvOutput: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mahasiswa)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        uid = auth.currentUser?.uid ?: ""

        tvWelcome = findViewById(R.id.tvWelcomeMhs)
        tvOutput = findViewById(R.id.tvOutputMhs)
        btnRegUlang = findViewById(R.id.btnRegUlang)
        btnDaftarMatkul = findViewById(R.id.btnDaftarMatkul)
        btnAbsensi = findViewById(R.id.btnAbsensi)
        btnLihatNilai = findViewById(R.id.btnLihatNilai)

        tvWelcome.text = "Mahasiswa UID: $uid"

        btnRegUlang.setOnClickListener {
            db.getReference("registrasi").child(uid).setValue(true)
            tvOutput.text = "Registrasi ulang berhasil"
        }

        btnDaftarMatkul.setOnClickListener {
            db.getReference("daftar_matkul").child(uid).child("matkul1").setValue(true)
            tvOutput.text = "Berhasil daftar ke matkul1"
        }

        btnAbsensi.setOnClickListener {
            val key = db.getReference("absensi").child("matkul1").child(uid).push().key!!
            db.getReference("absensi").child("matkul1").child(uid).child(key).setValue(true)
            tvOutput.text = "Absensi berhasil"
        }

        btnLihatNilai.setOnClickListener {
            db.getReference("nilai").orderByChild("mahasiswa_id").equalTo(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val result = StringBuilder("Nilai:\n")
                        for (item in snapshot.children) {
                            val nilai = item.getValue(Nilai::class.java)
                            nilai?.let {
                                result.append("- ${it.matakuliah_id}: UTS ${it.uts}, UAS ${it.uas}, Tugas ${it.tugas}\n")
                            }
                        }
                        tvOutput.text = result.toString()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        tvOutput.text = "Gagal ambil nilai"
                    }
                })
        }
    }
}