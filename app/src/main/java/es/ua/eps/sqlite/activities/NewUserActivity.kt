package es.ua.eps.sqlite.activities

import es.ua.eps.sqlite.sql.UserEntity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import es.ua.eps.sqlite.R
import es.ua.eps.sqlite.databinding.ActivityNewUserBinding
import es.ua.eps.sqlite.sql.AppDatabase

class NewUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityNewUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.newUserButton.setOnClickListener {

            // Login
            val login = binding.loginEditText.text.toString().trim()

            if(login.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese un nombre de usuario.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Password
            val password = binding.passwordEditText.text.toString().trim()

            if(password.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese una contraseña.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // User Name
            val username = binding.nameEditText.text.toString().trim()

            if(username.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese su nombre completo.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Email
            val email = binding.emailEditText.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese un correo electrónico.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (!isValidEmail(email)) {
                Toast.makeText(this, "El correo electrónico no es válido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create user

            // ---- SQLiteOpenHelper ----
            //val sqlManager = SQLManager.getInstance(this)
            //sqlManager.insertUser(login, password, username, email)

            // ---- Room ----
            val db = AppDatabase.getDatabase(this)
            val user = UserEntity(
                nombre_usuario = login,
                password = password,
                nombre_completo = username,
                email = email
            )
            db.userDao().insertUser(user)

            Toast.makeText(this, "Usuario creado exitosamente.", Toast.LENGTH_SHORT).show()

            resetFields()
        }
    }

    private fun resetFields() {
        binding.loginEditText.text.clear()
        binding.passwordEditText.text.clear()
        binding.nameEditText.text.clear()
        binding.emailEditText.text.clear()
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}