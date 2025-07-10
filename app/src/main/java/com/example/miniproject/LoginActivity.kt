package com.example.miniproject

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().reference

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener

                    db.child("users").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val role = snapshot.child("role").value?.toString()

                            when (role) {
                                "kaprodi" -> {
                                    startActivity(Intent(this@LoginActivity, KaprodiActivity::class.java))
                                    finish()
                                }
                                "dosen" -> {
                                    val kodeDosen = snapshot.child("kodeDosen").value?.toString()
                                    if (kodeDosen != null && kodeDosen.length == 5 && kodeDosen.startsWith("67")) {
                                        startActivity(Intent(this@LoginActivity, DosenActivity::class.java))
                                        finish()
                                    } else {
                                        Toast.makeText(this@LoginActivity, "Kode dosen tidak valid (harus 5 digit dan dimulai dengan '67')", Toast.LENGTH_LONG).show()
                                    }
                                }
                                "mahasiswa" -> {
                                    startActivity(Intent(this@LoginActivity, MahasiswaActivity::class.java))
                                    finish()
                                }
                                else -> {
                                    Toast.makeText(this@LoginActivity, "Role tidak dikenali", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@LoginActivity, "Gagal mengambil data user", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    Toast.makeText(this, "Login gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}