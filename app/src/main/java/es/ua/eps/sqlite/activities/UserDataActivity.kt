package es.ua.eps.sqlite.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import es.ua.eps.sqlite.LOGGED_USER_FULL_NAME
import es.ua.eps.sqlite.LOGGED_USER_NAME
import es.ua.eps.sqlite.R
import es.ua.eps.sqlite.databinding.ActivityUserDataBinding

class UserDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityUserDataBinding.inflate(layoutInflater)
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

        // Obtain intent extras
        val intent = intent
        val login = intent.getStringExtra(LOGGED_USER_NAME)
        val fullName = intent.getStringExtra(LOGGED_USER_FULL_NAME)
        binding.welcomeTextValue.text = login
        binding.usernameTextValue.text = fullName
    }
}