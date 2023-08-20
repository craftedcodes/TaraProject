package com.example.abschlussaufgabe.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

// Constant for logging purposes.
const val AUTH_VIEW_MODEL = "AUTH_VIEW_MODEL"

/**
 * ViewModel class for handling user authentication operations.
 */
class AuthViewModel : ViewModel() {
	
	// Instance of Firebase authentication.
	private val auth = FirebaseAuth.getInstance()
	
	// LiveData for holding the current authenticated user.
	private val _currentUser = MutableLiveData<FirebaseUser?>(auth.currentUser)
	val currentUser: LiveData<FirebaseUser?>
		get() = _currentUser
	
	// MutableLiveData for tracking the success of user registration.
	private val _registerSuccess = MutableLiveData<Boolean>()
	
	// Public LiveData for observing the success of user registration from outside the ViewModel.
	val registerSuccess: LiveData<Boolean>
		get() = _registerSuccess
	
	// MutableLiveData for tracking the success of user login.
	private val _loginSuccess = MutableLiveData<Boolean>()
	
	// Public LiveData for observing the success of user login from outside the ViewModel.
	val loginSuccess: LiveData<Boolean>
		get() = _loginSuccess
	
	// MutableLiveData for tracking the success of user account deletion.
	private val _deleteAccountSuccess = MutableLiveData<Boolean>()
	
	// Public LiveData for observing the success of user account deletion from outside the ViewModel.
	val deleteAccountSuccess: LiveData<Boolean>
		get() = _deleteAccountSuccess
	
	/**
	 * Registers a new user with the provided email and password.
	 *
	 * @param email The email address to be used for registration.
	 * @param password The password to be used for registration.
	 */
	fun registerUser(email: String, password: String) {
		// Create a new user with the given email and password.
		auth.createUserWithEmailAndPassword(email, password)
			.addOnCompleteListener { task ->
				if (task.isSuccessful) {
					// Send email verification to the newly registered user.
					auth.currentUser?.sendEmailVerification()?.addOnCompleteListener {
						_registerSuccess.value = true
					}
				} else {
					// Log the error and update the register success LiveData to false.
					_registerSuccess.value = false
					Log.e(AUTH_VIEW_MODEL, "createUserWithEmail:failure", task.exception)
				}
			}
	}
	
	/**
	 * Logs in a user with the provided email and password.
	 *
	 * @param email The email address of the user.
	 * @param password The password of the user.
	 */
	fun loginUser(email: String, password: String) {
		// Attempt to sign in the user with the provided email and password.
		auth.signInWithEmailAndPassword(email, password)
			.addOnCompleteListener { task ->
				// Check if the sign-in task was successful.
				if (task.isSuccessful) {
					// Check if the user's email address has been verified.
					if (auth.currentUser?.isEmailVerified == true) {
						// Update the current user LiveData with the authenticated user.
						_currentUser.value = auth.currentUser
						// Update the login success LiveData to true.
						_loginSuccess.value = true
					} else {
						// If the email is not verified, log the error and update the login success LiveData to false.
						_loginSuccess.value = false
						Log.e(AUTH_VIEW_MODEL, "Email not verified", task.exception)
					}
				} else {
					// If the sign-in task was not successful, log the error and update the login success LiveData to false.
					Log.e(AUTH_VIEW_MODEL, "Login failed")
					_loginSuccess.value = false
				}
			}
	}
	
	/**
	 * Sends a password reset email to the provided email address.
	 *
	 * @param email The email address to which the password reset link should be sent.
	 */
	fun sendPasswordResetEmail(email: String) {
		// Request Firebase Authentication to send a password reset email.
		auth.sendPasswordResetEmail(email)
			.addOnCompleteListener { task ->
				// Check if the task to send the password reset email was successful.
				if (task.isSuccessful) {
					// Log the success message.
					Log.d(AUTH_VIEW_MODEL, "Password reset email sent.")
				} else {
					// If the task was not successful, log the error.
					Log.w(AUTH_VIEW_MODEL, "Password reset email not sent.", task.exception)
				}
			}
	}
	
	/**
     * Logs out the currently authenticated user.
     */
	fun logoutUser() {
		auth.signOut()
		_currentUser.value = auth.currentUser
	}
	
	/**
	 * Deletes the currently authenticated user's account.
	 */
	fun deleteAccount() {
		// Get the currently authenticated user.
		val user = auth.currentUser
		
		// Check if the user is not null.
		if (user != null) {
			// Delete the user's account.
			user.delete()
				.addOnCompleteListener { task ->
					// Check if the account deletion task was successful.
					if (task.isSuccessful) {
						// Update the delete account success LiveData to true.
						_deleteAccountSuccess.value = true
						Log.d(AUTH_VIEW_MODEL, "User account deleted successfully.")
					} else {
						// If the account deletion task was not successful, log the error and update the delete account success LiveData to false.
						_deleteAccountSuccess.value = false
						Log.e(AUTH_VIEW_MODEL, "User account deletion failed.", task.exception)
					}
				}
		} else {
			// If the user is null, log the error and update the delete account success LiveData to false.
			_deleteAccountSuccess.value = false
			Log.e(AUTH_VIEW_MODEL, "No authenticated user found.")
		}
	}
}
