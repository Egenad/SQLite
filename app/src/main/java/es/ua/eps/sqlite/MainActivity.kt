package es.ua.eps.sqlite

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import es.ua.eps.sqlite.activities.UserDataActivity
import es.ua.eps.sqlite.activities.UserManagementActivity
import es.ua.eps.sqlite.databinding.ActivityMainBinding
import es.ua.eps.sqlite.sql.AppDatabase
import es.ua.eps.sqlite.sql.SQLManager
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

const val LOGGED_USER_NAME = "loggedUserName"
const val LOGGED_USER_FULL_NAME = "loggedUserFullName"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.closeButton.setOnClickListener {
            finishAffinity()
        }

        binding.loginButton.setOnClickListener {

            var success = false

            if(binding.usernameEditText.text.isNotBlank() && binding.passwordEditText.text.isNotBlank()) {

                // ---- SQLiteOpenHelper ----
                //val sqlManager = SQLManager.getInstance(this)

                // Check if user exists
                //val user = sqlManager.getUserByUsername(binding.usernameEditText.text.toString())

                // ---- Room ----
                val user = AppDatabase.getDatabase(this).userDao().getUserByUsername(binding.usernameEditText.text.toString())

                if (user != null) {

                    // Check if password is correct
                    if(binding.passwordEditText.text.toString() == user.password) {
                        success = true
                        val intent = Intent(this, UserDataActivity::class.java)

                        // Set logged user extra
                        intent.putExtra(LOGGED_USER_NAME, user.nombre_usuario)
                        intent.putExtra(LOGGED_USER_FULL_NAME, user.nombre_completo)

                        startActivity(intent)
                    }
                }
            }

            if(!success){
                Toast.makeText(this, "Error usuario/password incorrectos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_create_backup -> {
                //if(SQLManager.getInstance(this).backupDatabase(this)) ---- SQLiteOpenHelper ----
                if(SQLManager.getInstance(this).backupRoomDatabase(this))
                    Toast.makeText(this, "BackUp creado correctamente", Toast.LENGTH_SHORT).show()
                else Toast.makeText(this, "Error al crear BackUp", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_restore_backup -> {
                //if(SQLManager.getInstance(this).restoreDatabase(this)) ---- SQLiteOpenHelper ----
                if(SQLManager.getInstance(this).restoreRoomDatabase(this))
                    Toast.makeText(this, "BackUp restaurado correctamente", Toast.LENGTH_SHORT).show()
                else Toast.makeText(this, "Error al restaurar BackUp", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_manage_users -> {
                Intent(this, UserManagementActivity::class.java).also {
                    startActivity(it)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SQLManager.getInstance(this).close()
    }
}