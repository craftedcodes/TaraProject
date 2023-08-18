package com.example.abschlussaufgabe.ui

// Required imports for the class.
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
	
	// Firebase Firestore instance to access the database.
	private val db = FirebaseFirestore.getInstance()
	
	// Property to hold the binding object for this fragment.
	private lateinit var binding: FragmentProfileBinding
	
	// Holds the FirebaseAuth instance.
	private lateinit var auth: FirebaseAuth
	
	private var lastFetchTime: LocalDateTime? = null
	
	private val barChart: MutableList<Int> = mutableListOf()
	private val lineChart: MutableList<Int> = mutableListOf()
	
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
	): View {
		// Inflate the layout for this fragment using the inflate method of the FragmentProfileBinding class.
		binding = FragmentProfileBinding.inflate(inflater, container, false)
		
		// Get the FirebaseAuth instance.
		auth = FirebaseAuth.getInstance()
		
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
		// Call the super method to ensure proper initialization of the view.
		super.onViewCreated(view, savedInstanceState)
		
		save30DummyDataToFirestore()
		
		getCountEntryFromFirestore()
		
		// Set up listeners for the UI elements.
		setUpListeners()
	}
	
	/**
	 * Load contact data from SharedPreferences and set it to the UI elements.
	 */
	private fun loadContactDataFromSharedPreferences() {
		// Retrieve the shared preferences for the avatar settings.
		val avatarSharedPreferences =
			requireActivity().getSharedPreferences(AVATAR_PREFS, Context.MODE_PRIVATE)
		
		// Retrieve the shared preferences for the contact settings.
		val contactSharedPreferences =
			requireActivity().getSharedPreferences(CONTACT_PREFS, Context.MODE_PRIVATE)
		
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
	 * Delete contact data from SharedPreferences and update the UI accordingly.
	 */
	private fun deleteContactData() {
		// Retrieve the shared preferences for the contact settings.
		val contactSharedPreferences =
			requireActivity().getSharedPreferences(CONTACT_PREFS, Context.MODE_PRIVATE)
		
		// Edit the shared preferences to remove the contact details.
		contactSharedPreferences.edit().apply {
			// Remove the contact name, number, and message from the shared preferences.
			remove("contact_name")
			remove("contact_number")
			remove("contact_message")
			// Commit the changes to the shared preferences.
			apply()
		}
		
		// Hide the contact card from the UI.
		binding.contactCard.visibility = View.GONE
		// Display the attention card to indicate the absence of contact details.
		binding.attentionCard.visibility = View.VISIBLE
		
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
			auth.signOut()
			
			// Navigate the user to the desired fragment/screen after logout
			findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToHomeFragment())
		}
	}
	
	private fun shouldFetchData(): Boolean {
		val currentTime = LocalDateTime.now()
		if (lastFetchTime == null || Duration.between(lastFetchTime, currentTime).toHours() >= 1) {
			lastFetchTime = currentTime
			Log.d(PROFILE_TAG, "shouldFetchData result: true")
			return true
		}
		Log.d(PROFILE_TAG, "shouldFetchData result: false")
		return false
	}
	
	private fun getDatesInRange(startDate: LocalDate, endDate: LocalDate): List<String> {
		val dates = mutableListOf<String>()
		var currentDate = startDate
		while (!currentDate.isAfter(endDate)) {
			dates.add(currentDate.toString())
			currentDate = currentDate.plusDays(1)
		}
		return dates
	}
	
	/**
	 * Save the count of entries for a specific day to Firestore.
	 *
	 * @param count The count of entries for the day.
	 */
	private fun getCountEntryFromFirestore() {
		Log.d(PROFILE_TAG, "getCountEntryFromFirestore started")
		if (!shouldFetchData()) return
		
		val currentUser = Firebase.auth.currentUser ?: return
		if (currentUser == null) {
			Log.d(PROFILE_TAG, "Current user is null")
			return
		}
		val collection = db.collection(currentUser.uid)
		
		val endDate = LocalDate.now()
		val startDate = endDate.minusDays(60)
		val dateRange = getDatesInRange(startDate, endDate)
		
		val fetchedData = mutableListOf<Int>()
		var fetchedCount = 0
		
		for (date in dateRange) {
			collection.document(date).get().addOnSuccessListener { document ->
				if (document.exists()) {
					document.get("count")?.let {
						if (it is Int) {
							fetchedData.add(it)
							Log.d(PROFILE_TAG, "Date: $date, Count: $it")
							
						}
					}
				} else {
					fetchedData.add(0)
				}
				fetchedCount++
				if (fetchedCount == dateRange.size) {
					when {
						fetchedData.size >= 60 -> {
							barChart.addAll(fetchedData.takeLast(30))
							lineChart.addAll(fetchedData.take(30))
						}
						
						fetchedData.size in 30..59 -> {
							barChart.addAll(fetchedData.takeLast(30))
						}
						
						else -> {
							barChart.clear()
							lineChart.clear()
						}
					}
					setupChartData(barChart.toTypedArray(), lineChart.toTypedArray())
				}
			}.addOnFailureListener { exception ->
				Log.e(PROFILE_TAG, "Error getting document for date: $date", exception)
			}
		}
		Log.d(PROFILE_TAG, "getCountEntryFromFirestore finished")
	}
	
	
	/**
	 * Define and set up chart data.
	 * This method initializes the data for the bar and line charts and configures the chart's appearance and behavior.
	 */
	private fun setupChartData(barData: Array<Any>, lineData: Array<Any>) {
		if (barData.isEmpty() && lineData.isEmpty()) {
			binding.aaChartView.visibility = View.GONE
			binding.emptyDataTextView.visibility = View.VISIBLE
			binding.emptyDataTextView1.visibility = View.VISIBLE
			binding.emptyDataTextView2.visibility = View.VISIBLE
			return
		}
		
		binding.aaChartView.visibility = View.VISIBLE
		binding.emptyDataTextView.visibility = View.GONE
		binding.emptyDataTextView1.visibility = View.GONE
		binding.emptyDataTextView2.visibility = View.GONE
		
		val seriesList = mutableListOf<AASeriesElement>()
		seriesList.add(
			AASeriesElement().name("Current month").color("#FFDAD6").type(AAChartType.Column)
				.data(barData)
		)
		
		if (lineData.isNotEmpty()) {
			seriesList.add(
				AASeriesElement().name("Previous month").color("#FFE088").type(AAChartType.Line)
					.data(lineData)
			)
		}
		
		val aaChartModel = AAChartModel()
			.chartType(AAChartType.Column)
			.title(getString(R.string.your_gratitude_note_activity))
			.backgroundColor("#00000000")
			.dataLabelsEnabled(true)
			.series(seriesList.toTypedArray())
			.yAxisTitle("Amount of notes per day")
			.axesTextColor("#FFFFFF")
			.titleStyle(AAStyle().color("#FFFFFF"))
		
		binding.aaChartView.aa_drawChartWithChartModel(aaChartModel)
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
