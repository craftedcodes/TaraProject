package com.schubau.tara

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate

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
        
        // Initialize the shared preferences with the name "mode_shared_prefs".
        sharedPreferences = getSharedPreferences("mode_shared_prefs", MODE_PRIVATE)
        
        // Retrieve the user's dark mode preference from shared preferences.
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        
        // Set the app's theme mode based on the user's preference.
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        
        // Set the content view to the main activity layout.
        setContentView(R.layout.activity_main)
    }
}

// TODO: Bezahlfunktion
