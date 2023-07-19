package com.example.abschlussaufgabe.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.abschlussaufgabe.data.EmergencyContactRepository
import com.example.abschlussaufgabe.data.datamodels.EmergencyContact
import com.example.abschlussaufgabe.data.local.LocalDatabase
import kotlinx.coroutines.launch

// Define a constant for logging
val EMERGENCY_CONTACT_TAG = "EmergencyContactViewModel"

// EmergencyContactViewModel class that extends AndroidViewModel with
class EmergencyContactViewModel(application: Application) : AndroidViewModel(application) {
	//Initialize the repository
	val repository = EmergencyContactRepository(LocalDatabase.getDatabase(application))
	
	// Define LiveData object for the data from the repository
	val emergencyContact = repository.emergencyContact
	
	// Define MutableLiveData objects for the loading, error, and done states
	private val _loading = MutableLiveData<ApiStatus>()
	val loading: LiveData<ApiStatus>
		get() = _loading
	private val _error = MutableLiveData<String>()
	val error: LiveData<String>
		get() = _error
	private val _done = MutableLiveData<Boolean>()
	val done: LiveData<Boolean>
		get() = _done
	
	// Define a MutableLiveData object for the delete status
	val _deleteStatus = MutableLiveData<Boolean>()
	
	// Define function to load the emergency contact from the repository
	fun loadEmergencyContact() {
		viewModelScope.launch {
			_loading.value = ApiStatus.LOADING
			try {
				repository.getEmergencyContact()
			} catch (e: Exception) {
				Log.e(EMERGENCY_CONTACT_TAG, "Error getting emergency contact")
				_loading.value = ApiStatus.ERROR
			}
		}
	}
	
	// Define function to update an emergency contact in the repository
	fun updateEmergencyContact(emergencyContact: EmergencyContact) {
		viewModelScope.launch {
			repository.updateEmergencyContact(emergencyContact)
			_loading.value = ApiStatus.DONE
		}
	}
}
