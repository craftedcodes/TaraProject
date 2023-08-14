package com.schubau.tara

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
// TODO: REGEX für Passwortsicherheit einrichten (mindestens 12 Zeichen, Großbuchstaben, Kleinbuchstaben, Zahlen, Sonderzeichen)
// TODO: Email Bestätigung
// TODO: Passwort vergessen Funktion
// TODO: Logout Funktion
// TODO: Account löschen Funktion
// TODO: Bezahlfunktion
// TODO: Tagesdaten speichern in Firestore
