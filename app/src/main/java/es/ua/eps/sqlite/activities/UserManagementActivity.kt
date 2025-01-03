package es.ua.eps.sqlite.activities

import es.ua.eps.sqlite.sql.UserEntity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import es.ua.eps.sqlite.R
import es.ua.eps.sqlite.databinding.ActivityUserManagementBinding
import es.ua.eps.sqlite.sql.AppDatabase

const val SELECTED_USER_ID = "selectedUserID"

class UserManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserManagementBinding
    private var selectedUserEntity : UserEntity? = null
    // private var selectedUserEntity : User? = null ---- SQLiteOpenHelper ----

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityUserManagementBinding.inflate(layoutInflater)
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
            val intent = Intent(this, NewUserActivity::class.java)
            startActivity(intent)
        }

        binding.updateUserButton.setOnClickListener {
            if (selectedUserEntity != null) {
                val intent = Intent(this, UpdateUserActivity::class.java)
                intent.putExtra(SELECTED_USER_ID, selectedUserEntity!!.id)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Por favor, seleccione un usuario", Toast.LENGTH_SHORT).show()
            }
        }

        binding.deleteUserButton.setOnClickListener {
            val selectedUser = binding.userSpinner.selectedItem.toString()

            if (selectedUser.isNotEmpty()) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Delete User")
                builder.setMessage("¿Estás seguro de que quieres eliminar al usuario $selectedUser?")
                builder.setNegativeButton("CANCEL") { dialog, _ ->
                    dialog.dismiss()
                }
                builder.setPositiveButton("OK") { _, _ ->

                    // ---- SQLiteOpenHelper ----
                    //val sqlMan = SQLManager.getInstance(this)
                    //val user = sqlMan.getUserByUsername(selectedUser)

                    // ---- Room ----
                    val user = AppDatabase.getDatabase(this).userDao().getUserByUsername(selectedUser)

                    if(user != null) {
                        //val success = sqlMan.deleteUser(user.id) // ---- SQLiteOpenHelper ----
                        val success = AppDatabase.getDatabase(this).userDao().deleteUser(user) // ---- Room ----
                        if (success > 0) {
                            Toast.makeText(
                                this,
                                "Usuario eliminado correctamente",
                                Toast.LENGTH_SHORT
                            ).show()
                            configureSpinner()
                        } else {
                            Toast.makeText(this, "Error al eliminar al usuario", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else{
                        Toast.makeText(this, "No se ha encontrado el usuario", Toast.LENGTH_SHORT).show()
                    }
                }

                val dialog = builder.create()
                dialog.show()
            } else {
                Toast.makeText(this, "No se ha seleccionado ningún usuario", Toast.LENGTH_SHORT).show()
            }
        }

        binding.listUsersButton.setOnClickListener {
            val intent = Intent(this, UserListActivity::class.java)
            startActivity(intent)
        }

        configureSpinner()
    }

    override fun onResume() {
        super.onResume()
        configureSpinner()
    }

    private fun configureSpinner() {
        // ---- SQLiteOpenHelper ----
        //val userList = SQLManager.getInstance(this).getAllUsers()

        // ---- Room ----
        val userList = AppDatabase.getDatabase(this).userDao().getAllUsers()

        if (userList.isNotEmpty()) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                userList.map { it.nombre_usuario }
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.userSpinner.adapter = adapter
        } else {
            Toast.makeText(this, "No hay usuarios registrados", Toast.LENGTH_SHORT).show()
        }

        binding.userSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedUserEntity = userList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}