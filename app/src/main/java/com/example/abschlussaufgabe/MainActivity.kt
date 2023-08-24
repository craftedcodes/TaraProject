package com.schubau.tara

import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.example.abschlussaufgabe.services.EncryptionService

/**
 * MainActivity is the primary activity of the application.
 * It handles the theme mode (dark/light) based on user preferences.
 */
class MainActivity : AppCompatActivity() {
    
    // Shared preferences instance to store and retrieve user settings.
    private lateinit var sharedPreferences: SharedPreferences
    
    /**
     * Called when the activity is starting.
     * This method sets up the theme and initializes the UI components.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down
     * then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     * Otherwise, it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        sharedPreferences = getSharedPreferences("first_run", MODE_PRIVATE)
        val isFirstRun = sharedPreferences.getBoolean("first_run", false)
        if (!isFirstRun) {
            EncryptionService.getInstance(applicationContext).generatePassphrase()
            sharedPreferences.edit().putBoolean("first_run", true).apply()
        }
    
        // Initialize the shared preferences with the name "mode_shared_prefs".
        sharedPreferences = getSharedPreferences("mode_shared_prefs", MODE_PRIVATE)
    
        // Set the content view to the main activity layout.
        setContentView(R.layout.activity_main)
    }
    
    /**
     * Called when the activity is first created.
     */
    override fun onResume() {
        super.onResume()
    
        // Retrieve the user's dark mode preference from shared preferences.
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        
        // Set the app's theme mode based on the user's preference.
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    
        val color = if (isDarkMode) {R.color.bg_dark} else {R.color.bg_light}
    
        window.navigationBarColor = ContextCompat.getColor(this, color)
    }
}

// TODO: Bezahlfunktion
