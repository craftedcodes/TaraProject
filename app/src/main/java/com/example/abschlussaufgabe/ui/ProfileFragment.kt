package com.example.abschlussaufgabe.ui

// Required imports for the class.
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.abschlussaufgabe.data.datamodels.ChartColors
import com.example.abschlussaufgabe.viewModel.AuthViewModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAStyle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.schubau.tara.R
import com.schubau.tara.databinding.FragmentProfileBinding
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

// Constants for SharedPreferences keys.
private const val AVATAR_PREFS = "avatar_prefs"
private const val CONTACT_PREFS = "contact_prefs"

// TAG for the class.
private const val PROFILE_TAG = "ProfileFragment"

/**
 * A Fragment representing the user's profile screen.
 * Displays user's gratitude note activity and emergency contact details.
 */
class ProfileFragment : Fragment() {
	// ViewModel instance for authentication operations.
	private val viewModel: AuthViewModel by viewModels()
	
	// Firebase Firestore instance to access the database.
	private val db = FirebaseFirestore.getInstance()
	
	// Property to hold the binding object for this fragment.
	private lateinit var binding: FragmentProfileBinding
	
	// Holds the FirebaseAuth instance.
	private lateinit var auth: FirebaseAuth
	
	// Holds the timestamp of the last data fetch.
	private var lastFetchTime: LocalDateTime? = null
	
	// List to store data points for the bar chart visualization.
	private val barChart: MutableList<Int> = mutableListOf()
	
	// List to store data points for the line chart visualization.
	private val lineChart: MutableList<Int> = mutableListOf()
	
	// Lazy initialization of the main encryption key using AES256_GCM scheme.
	private val mainKey by lazy {
		MasterKey.Builder(requireContext())
			.setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
			.build()
	}
	
	// Lazy initialization of encrypted shared preferences for storing avatar data.
	private val avatarSharedPreferences by lazy {
		EncryptedSharedPreferences.create(
			requireContext(),
			AVATAR_PREFS,
			mainKey,
			EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
			EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
		)
	}
	
	// Lazy initialization of encrypted shared preferences for storing contact data.
	private val contactSharedPreferences by lazy {
		EncryptedSharedPreferences.create(
			requireContext(),
			CONTACT_PREFS,
			mainKey,
			EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
			EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
		)
	}
	
	/**
	 * Called to have the fragment instantiate its user interface view.
	 * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
	 * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
	 * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
	 * @return Return the View for the fragment's UI, or null.
	 */
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment using the inflate method of the FragmentProfileBinding class.
		binding = FragmentProfileBinding.inflate(inflater, container, false)
		
		// Get the FirebaseAuth instance.
		auth = FirebaseAuth.getInstance()
		
		// Check if the user is null.
		if (auth.currentUser == null) {
			findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToHomeFragment())
			return null
		}
		
		// Load the emergency contact details from SharedPreferences.
		loadContactDataFromSharedPreferences()
		
		// Return the root view of the inflated layout.
		return binding.root
	}
	
	/**
	 * Called immediately after onCreateView() has returned, but before any saved state has been restored in to the view.
	 * @param view The View returned by onCreateView().
	 * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
	 */
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		// Invoke the parent class's onViewCreated method to ensure the view is correctly initialized.
		super.onViewCreated(view, savedInstanceState)

		// Populate Firestore with 30 dummy data entries for testing or demonstration purposes.
		//save30DummyDataToFirestore()

		// Retrieve a specific count entry from Firestore for further processing or display.
		getCountEntryFromFirestore()

		// Initialize and assign event listeners to the relevant UI components in the view.
		setUpListeners()
	}
	
	/**
	 * Load contact data from SharedPreferences and set it to the UI elements.
	 */
	private fun loadContactDataFromSharedPreferences() {
		
		// Fetch the avatar resource ID from the shared preferences. If not found, default to 'avatar01'.
		val avatarResource = avatarSharedPreferences.getInt("selected_avatar", R.drawable.avatar01)
		
		// Fetch the contact name, number, and message from the shared preferences.
		// Default values are provided in case the preferences do not contain the specified keys.
		val name = contactSharedPreferences.getString("contact_name", "")
		val number = contactSharedPreferences.getString("contact_number", "")
		val message = contactSharedPreferences.getString(
			"contact_message",
			"I am in an emotional emergency. Please call me."
		)
		
		// Set the avatar image, contact name, number, and message to the respective UI elements.
		binding.avatarIv.setImageResource(avatarResource)
		binding.nameTv.text = name
		binding.numberTv.text = number
		binding.messageTv.text = message
		Log.e(PROFILE_TAG, "Contact Name: $name, Contact Number: $number")
		
		// Check if both the name and number are provided.
		// If they are, display the contact card and hide the attention card.
		if (name != "" && number != "") {
			binding.contactCard.visibility = View.VISIBLE
			binding.attentionCard.visibility = View.GONE
		}
		
		// The onClickListener for the edit button.
		// When clicked, the user is navigated to the emergency contact fragment.
		binding.editBtn.setOnClickListener {
			findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToEmergencyContactFragment())
		}
		
		// The onClickListener for the delete button.
		// When clicked, the contact details are being deleted from SharedPreferences.
		binding.deleteBtn.setOnClickListener {
			deleteContactData()
		}
	}
	
	/**
	 * Deletes contact data from SharedPreferences and updates the UI accordingly.
	 * If the user is not logged in, a toast message is shown and the user is navigated to the home fragment.
	 */
	private fun deleteContactData() {
		// Check if the user is currently logged in.
		if (auth.currentUser != null ) {
			// Begin editing the shared preferences.
			contactSharedPreferences.edit().apply {
				// Remove the contact name from the shared preferences.
				remove("contact_name")
				// Remove the contact number from the shared preferences.
				remove("contact_number")
				// Remove the contact message from the shared preferences.
				remove("contact_message")
				// Commit the changes to the shared preferences.
				apply()
			}
			
			// Set the contact card's visibility to GONE, hiding it from the UI.
			binding.contactCard.visibility = View.GONE
			// Set the attention card's visibility to VISIBLE, indicating the absence of contact details.
			binding.attentionCard.visibility = View.VISIBLE
		} else {
			// Display a toast message indicating the user is not logged in.
			Toast.makeText(context, "You are not logged in.", Toast.LENGTH_SHORT).show()
			// Navigate the user to the home fragment.
			findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToHomeFragment())
		}
	}
	
	/**
	 * Set up click listeners for the UI elements.
	 * This method defines the actions to be taken when various UI elements are clicked.
	 */
	private fun setUpListeners() {
		// The onClickListener for the back button.
		// When the button is clicked, the user is taken to the previous screen in the back stack.
		binding.backBtn.setOnClickListener {
			findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToAnimationFragment())
		}
		
		// The onClickListener for the home button logo.
		// When the logo is clicked, the user is navigated to the animation fragment.
		binding.homeBtnLogo.setOnClickListener {
			findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToAnimationFragment())
		}
		
		// The onClickListener for the home button text.
		// When the text is clicked, it also navigates the user to the animation fragment.
		binding.homeBtnText.setOnClickListener {
			findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToAnimationFragment())
		}
		
		// The onClickListener for the add contact button.
		// When this button is clicked, the user is navigated to the emergency contact fragment.
		binding.addContactBtn.setOnClickListener {
			findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToEmergencyContactFragment())
		}
		
		// The onClickListener for the logout button.
		// When clicked, the user is navigated to the home fragment, effectively logging them out.
		binding.logoutBtn.setOnClickListener {
			// Log the user out from Firebase
			viewModel.logoutUser()
		}
		
		// Observes the current user and navigates to the home fragment.
		viewModel.currentUser.observe(viewLifecycleOwner) { user ->
			if (user == null) {
				// Navigate the user to the desired fragment/screen after logout
				findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToHomeFragment())
			}
		}
		
		binding.settingsBtn.setOnClickListener {
			findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToSettingsFragment())
		}
	}
	
	/**
	 * Determines whether data should be fetched based on the time since the last fetch.
	 *
	 * @return Boolean indicating if data should be fetched.
	 */
	private fun shouldFetchData(): Boolean {
		// Get the current time.
		val currentTime = LocalDateTime.now()
		
		// Check if the last fetch time is null or if the duration since the last fetch is greater than or equal to 1 hour.
		if (lastFetchTime == null || Duration.between(lastFetchTime, currentTime).toHours() >= 1) {
			// Update the last fetch time to the current time.
			lastFetchTime = currentTime
			
			// Log the result of the check.
			Log.d(PROFILE_TAG, "shouldFetchData result: true")
			
			// Return true indicating data should be fetched.
			return true
		}
		
		// Log the result of the check.
		Log.d(PROFILE_TAG, "shouldFetchData result: false")
		
		// Return false indicating data should not be fetched.
		return false
	}
	
	/**
	 * Generates a list of dates in string format between the provided start and end dates.
	 *
	 * @param startDate The starting date of the range.
	 * @param endDate The ending date of the range.
	 * @return List of dates in string format.
	 */
	private fun getDatesInRange(startDate: LocalDate, endDate: LocalDate): List<String> {
		// Initialize a mutable list to store the dates.
		val dates = mutableListOf<String>()
		
		// Start with the provided start date.
		var currentDate = startDate
		
		// Loop until the current date surpasses the end date.
		while (!currentDate.isAfter(endDate)) {
			// Add the current date to the list.
			dates.add(currentDate.toString())
			
			// Move to the next day.
			currentDate = currentDate.plusDays(1)
		}
		
		// Return the list of dates.
		return dates
	}
	
	/**
	 * Save the count of entries for a specific day to Firestore.
	 *
	 * @param count The count of entries for the day.
	 */
	private fun getCountEntryFromFirestore() {
		// Log the start of the fetch operation.
		Log.d(PROFILE_TAG, "getCountEntryFromFirestore started")
		
		// Check if data should be fetched. If not, exit the function.
		if (!shouldFetchData()) return
		
		// Get the current user. If the user is null, log the information and exit.
		val currentUser = Firebase.auth.currentUser ?: return
		if (currentUser == null) {
			Log.d(PROFILE_TAG, "Current user is null")
			return
		}
		
		// Get the collection associated with the current user's UID.
		val collection = db.collection(currentUser.uid)
		
		// Define the date range for which data should be fetched.
		val endDate = LocalDate.now()
		val startDate = endDate.minusDays(60)
		val dateRange = getDatesInRange(startDate, endDate)
		Log.d(PROFILE_TAG, "startdate = $startDate, enddate = $endDate")
		// Initialize lists to store fetched data and a counter for fetched documents.
		val fetchedData = mutableListOf<Int>()
		var fetchedCount = 0
		
		// Iterate through each date in the specified date range.
		for (date in dateRange) {
			Log.e(PROFILE_TAG, "Goes into for loop data range")
			// Fetch the document corresponding to the current date.
			collection.document(date).get().addOnSuccessListener { document ->
				// Check if the document exists in the collection.
				if (document.exists()) {
					Log.e(PROFILE_TAG, "Document exists")
					// Retrieve the 'count' field from the document.
					document.get("count")?.let {
						var it = it.toString().toInt()
						// Ensure the retrieved data is of type Int.
						if (it is Int) {
							Log.e(PROFILE_TAG, "Ganz dummer Log")
							// Add the fetched count to the fetchedData list.
							fetchedData.add(it)
							// Log the fetched count for the current date.
							Log.d(PROFILE_TAG, "Date: $date, Count: $it")
						}
					}
				} else {
					// If the document doesn't exist for the current date, add a count of 0.
					fetchedData.add(0)
				}
				
				// Increment the fetched count and check if all documents have been fetched.
				fetchedCount++
				if (fetchedCount == dateRange.size) {
					// Update the charts based on the size of the fetched data.
					when {
						// If there are 60 or more data points fetched.
						fetchedData.size >= 60 -> {
							Log.d(PROFILE_TAG, "$fetchedData")
							// Add the last 30 data points to the bar chart.
							barChart.addAll(fetchedData.takeLast(30))
							// Add the first 30 data points to the line chart.
							lineChart.addAll(fetchedData.take(30))
						}
						// If the number of data points fetched is between 30 and 59.
						fetchedData.size in 30..59 -> {
							// Add the last 30 data points to the bar chart.
							barChart.addAll(fetchedData.takeLast(30))
						}
						// For any other number of data points fetched.
						else -> {
							// Clear both the bar and line charts.
							barChart.clear()
							lineChart.clear()
						}
					}
					// Setup the chart data.
					setupChartData(barChart.toTypedArray(), lineChart.toTypedArray())
				}
			}.addOnFailureListener { exception ->
				// Log any errors encountered while fetching a document.
				Log.e(PROFILE_TAG, "Error getting document for date: $date", exception)
			}
		}
		
		// Log the completion of the fetch operation.
		Log.d(PROFILE_TAG, "getCountEntryFromFirestore finished")
	}
	
	/**
	 * Define and set up chart data.
	 * This method initializes the data for the bar and line charts and configures the chart's appearance and behavior.
	 */
	private fun setupChartData(barData: Array<Any>, lineData: Array<Any>) {
		// Check if both barData and lineData are empty.
		if (barData.isEmpty() && lineData.isEmpty()) {
			// Hide the chart view if there's no data to display.
			setViewVisibility(binding.aaChartView, false)
			// Show the primary empty data message.
			setViewVisibility(binding.emptyDataTextView, true)
			// Show the secondary empty data message.
			setViewVisibility(binding.emptyDataTextView1, true)
			// Show the tertiary empty data message.
			setViewVisibility(binding.emptyDataTextView2, true)
			// Exit the function early since there's no data to process.
			return
		}

		// Display the chart view since there's data to show.
		setViewVisibility(binding.aaChartView, true)
		// Hide the primary empty data message.
		setViewVisibility(binding.emptyDataTextView, false)
		// Hide the secondary empty data message.
		setViewVisibility(binding.emptyDataTextView1, false)
		// Hide the tertiary empty data message.
		setViewVisibility(binding.emptyDataTextView2, false)

		// Retrieve the appropriate colors based on the current theme (light/dark mode).
		val (barColor, lineColor, axesTextColor, titleColor) = getChartColors()

		// Initialize a list to hold the data series for the chart.
		val seriesList = mutableListOf<AASeriesElement>()
			.apply {
				// Add the current month's data as a column chart series.
				add(AASeriesElement().name("Current month").color(barColor).type(AAChartType.Column).data(barData))
				// If there's data for the previous month, add it as a line chart series.
				if (lineData.isNotEmpty()) {
					add(AASeriesElement().name("Previous month").color(lineColor).type(AAChartType.Line).data(lineData))
				}
			}

		// Configure the chart model with the desired settings.
		val aaChartModel = AAChartModel()
			.chartType(AAChartType.Column)
			.title(getString(R.string.your_gratitude_note_activity))
			.backgroundColor("#00000000")
			.dataLabelsEnabled(true)
			.series(seriesList.toTypedArray())
			.yAxisTitle("Amount of notes per day")
			.axesTextColor(axesTextColor)
			.titleStyle(AAStyle().color(titleColor))

		// Draw the chart with the configured model.
		binding.aaChartView.aa_drawChartWithChartModel(aaChartModel)
	}
	
	/**
	 * Sets the visibility of a given view.
	 *
	 * @param view The view whose visibility needs to be set.
	 * @param isVisible A boolean indicating whether the view should be visible or not.
	 */
	private fun setViewVisibility(view: View, isVisible: Boolean) {
		// Set the view's visibility to VISIBLE if isVisible is true, otherwise set it to GONE.
		view.visibility = if (isVisible) View.VISIBLE else View.GONE
	}
	
	/**
	 * Determines the appropriate chart colors based on the current theme mode (dark or light).
	 *
	 * @return ChartColors An instance containing the colors for the bar, line, axes text, and title.
	 */
	private fun getChartColors(): ChartColors {
		// Check if the app is currently in dark mode.
		val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
		
		// Return the appropriate color set based on the theme mode.
		return if (isDarkMode) {
			// Colors for Dark Mode.
			ChartColors("#A06F60", "#A07F40", "#FFFFFF", "#FFFFFF")
		} else {
			// Colors for Light Mode.
			ChartColors("#FFDAD6", "#FFE088", "#000000", "#000000")
		}
	}
	
	/**
	 * Save dummy data for the last 60 days to Firestore.
	 */
	private fun save60DummyDataToFirestore() {
		val currentUser = Firebase.auth.currentUser ?: return
		val collection = db.collection(currentUser.uid)
		
		val endDate = LocalDate.now()
		val startDate = endDate.minusDays(59)
		val dateRange = getDatesInRange(startDate, endDate)
		
		// Dummy data values for the last 60 days.
		val dummyDataValues = listOf(
			2,
			1,
			2,
			1,
			2,
			1,
			2,
			1,
			3,
			2,
			1,
			3,
			4,
			2,
			1,
			4,
			5,
			3,
			2,
			1,
			6,
			4,
			3,
			2,
			6,
			5,
			4,
			3,
			7,
			6,
			5,
			4,
			8,
			7,
			6,
			8,
			9,
			7,
			6,
			10,
			8,
			7,
			10,
			11,
			9,
			8,
			12,
			11,
			10,
			12,
			13,
			11,
			10,
			13,
			14,
			12,
			11,
			14,
			15,
			13,
			12
		)
		
		for (i in dateRange.indices) {
			val date = dateRange[i]
			val count = dummyDataValues[i]
			val data = hashMapOf("count" to count)
			collection.document(date).set(data)
		}
	}
	
	/**
	 * Save dummy data for the last 30 days to Firestore.
	 */
	private fun save30DummyDataToFirestore() {
		val currentUser = Firebase.auth.currentUser ?: return
		val collection = db.collection(currentUser.uid)
		
		val endDate = LocalDate.now()
		val startDate = endDate.minusDays(29)
		val dateRange = getDatesInRange(startDate, endDate)
		
		// Dummy data values for the last 30 days.
		val dummyDataValues = listOf(
			2,
			1,
			2,
			1,
			2,
			1,
			2,
			1,
			3,
			2,
			1,
			3,
			4,
			2,
			1,
			4,
			5,
			3,
			2,
			1,
			6,
			4,
			3,
			2,
			6,
			5,
			4,
			3,
			7,
			6
		)
		
		for (i in dateRange.indices) {
			val date = dateRange[i]
			val count = dummyDataValues[i]
			val data = hashMapOf("count" to count)
			collection.document(date).set(data)
		}
	}
}
