package es.ua.eps.sqlite.activities

import es.ua.eps.sqlite.sql.UserEntity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import es.ua.eps.sqlite.R
import es.ua.eps.sqlite.databinding.ActivityUpdateUserBinding
import es.ua.eps.sqlite.sql.AppDatabase

class UpdateUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityUpdateUserBinding.inflate(layoutInflater)
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

        val intent = intent
        val selectedUser = intent.getIntExtra(SELECTED_USER_ID, -1)
        configureLayout(selectedUser)
    }

    private fun configureLayout(selectedUser: Int){
        if(selectedUser >= 0) {

            // ---- SQLiteOpenHelper ----
            //val user = SQLManager.getInstance(this).getUserById(selectedUser)

            // ---- Room ----
            val user = AppDatabase.getDatabase(this).userDao().getUserById(selectedUser)

            if(user != null) {
                binding.loginEditText.setText(user.nombre_usuario)
                binding.passwordEditText.setText(user.password)
                binding.usernameEditText.setText(user.nombre_completo)
                binding.emailEditText.setText(user.email)
            }
        }else{
            Toast.makeText(this, "Usuario no válido.", Toast.LENGTH_SHORT).show()
        }

        binding.updateUserButton.setOnClickListener {
            if(selectedUser > 0) {

                // Check fields are valid

                if(binding.loginEditText.text.isNotBlank() && binding.passwordEditText.text.isNotBlank()
                    && binding.usernameEditText.text.isNotBlank() && binding.emailEditText.text.isNotBlank()) {
                    if(android.util.Patterns.EMAIL_ADDRESS.matcher(binding.emailEditText.text.toString()).matches()) {

                        // ---- SQLiteOpenHelper ----
                        /*val sqlManager = SQLManager.getInstance(this)
                        sqlManager.updateUser(
                            selectedUser,
                            binding.loginEditText.text.toString(),
                            binding.passwordEditText.text.toString(),
                            binding.usernameEditText.text.toString(),
                            binding.emailEditText.text.toString()
                        )*/

                        // ---- Room ----
                        AppDatabase.getDatabase(this).userDao().updateUser(
                            UserEntity(
                                selectedUser,
                                binding.loginEditText.text.toString(),
                                binding.passwordEditText.text.toString(),
                                binding.usernameEditText.text.toString(),
                                binding.emailEditText.text.toString()
                            )
                        )
                        finish()
                    }else{
                        Toast.makeText(this, "Por favor, ingrese un correo electrónico válido.", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this, "Por favor, rellene todos los campos.", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Usuario no válido.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}