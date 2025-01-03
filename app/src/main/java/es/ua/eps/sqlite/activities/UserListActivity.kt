package es.ua.eps.sqlite.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import es.ua.eps.sqlite.R
import es.ua.eps.sqlite.adapter.UserAdapter
import es.ua.eps.sqlite.adapter.UserAdapterRoom
import es.ua.eps.sqlite.databinding.ActivityUserListBinding
import es.ua.eps.sqlite.sql.AppDatabase
import es.ua.eps.sqlite.sql.SQLManager
import es.ua.eps.sqlite.sql.User

class UserListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityUserListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        createRecycledView()

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun createRecycledView(){

        // ---- SQLiteOpenHelper ----
        //val userList = SQLManager.getInstance(this).getAllUsers()

        // ---- Room ----
        val userList = AppDatabase.getDatabase(this).userDao().getAllUsers()

        binding = ActivityUserListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.itemAnimator = DefaultItemAnimator()

        // ---- SQLiteOpenHelper ----
        //val recyclerAdapter = UserAdapter(userList)

        // ---- Room ----
        val recyclerAdapter = UserAdapterRoom(userList)

        binding.recyclerView.adapter = recyclerAdapter
    }
}