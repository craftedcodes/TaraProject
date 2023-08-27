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
        // Call the superclass implementation of onCreate.
        super.onCreate(savedInstanceState)

// Initialize the SharedPreferences object for storing app settings.
// The SharedPreferences file is named "first_run" and operates in private mode.
        sharedPreferences = getSharedPreferences("first_run", MODE_PRIVATE)

// Retrieve the value of "first_run" from SharedPreferences, defaulting to false if it's not set.
        val isFirstRun = sharedPreferences.getBoolean("first_run", false)

// Check if this is the first run of the app.
        if (!isFirstRun) {
            // Generate a passphrase using the EncryptionService.
            EncryptionService.getInstance(applicationContext).generatePassphrase()
        
            // Update the "first_run" flag in SharedPreferences to indicate that the app has been run at least once.
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
            // Set the app's default night mode to dark mode.
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            // Set the app's default night mode to light mode.
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        // Determine the color to be used for the navigation bar based on the current mode.
        val color = if (isDarkMode) {
            R.color.bg_dark  // Use dark background color for dark mode.
        } else {
            R.color.bg_light  // Use light background color for light mode.
        }

        // Set the color of the navigation bar.
        window.navigationBarColor = ContextCompat.getColor(this, color)
    }
}

// TODO: Bezahlfunktion
