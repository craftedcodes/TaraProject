package com.example.abschlussaufgabe.ui

// Required imports for the class.
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
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
import java.time.LocalDate
import java.util.Calendar

// Constants for SharedPreferences keys.
private const val AVATAR_PREFS = "avatar_prefs"
private const val CONTACT_PREFS = "contact_prefs"

private const val TAG = "ProfileFragment"
/**
 * A Fragment representing the user's profile screen.
 * Displays user's gratitude note activity and emergency contact details.
 */
class ProfileFragment : Fragment() {
	
	private val db = FirebaseFirestore.getInstance()
	
	// Property to hold the binding object for this fragment.
	private lateinit var binding: FragmentProfileBinding
	
	// Holds the FirebaseAuth instance.
	private lateinit var auth: FirebaseAuth
	
	// Create an instance of SharedPreferences.
	private val countPref by lazy { activity?.getSharedPreferences("count_preferences", Context.MODE_PRIVATE) }
	
	// LiveData to hold bar chart data.
	private val barDataLiveData = MutableLiveData<Array<Any>>()
	
	// LiveData to hold line chart data.
	private val lineDataLiveData = MutableLiveData<Array<Any>>()
	
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
		
		// Generate chart data based on user's gratitude note activity.
		barDataLiveData.observe(viewLifecycleOwner) { data ->
			setupChartData(data, lineDataLiveData.value ?: arrayOf())
		}
		
		lineDataLiveData.observe(viewLifecycleOwner) { data ->
			setupChartData(barDataLiveData.value ?: arrayOf(), data)
		}
		
		getCountEntryToFirestore()
		
		// Generate chart data based on user's gratitude note activity.
		generateChartData()
		
		// Define and set up chart data.
		setupChartData()
		
		// Set up listeners for the UI elements.
		setUpListeners()
	}
	
	/**
	 * Load contact data from SharedPreferences and set it to the UI elements.
	 */
	private fun loadContactDataFromSharedPreferences() {
		// Retrieve the shared preferences for the avatar settings.
		val avatarSharedPreferences = requireActivity().getSharedPreferences(AVATAR_PREFS, Context.MODE_PRIVATE)

		// Retrieve the shared preferences for the contact settings.
		val contactSharedPreferences = requireActivity().getSharedPreferences(CONTACT_PREFS, Context.MODE_PRIVATE)

		// Fetch the avatar resource ID from the shared preferences. If not found, default to 'avatar01'.
		val avatarResource = avatarSharedPreferences.getInt("selected_avatar", R.drawable.avatar01)

		// Fetch the contact name, number, and message from the shared preferences.
		// Default values are provided in case the preferences do not contain the specified keys.
		val name = contactSharedPreferences.getString("contact_name", "")
		val number = contactSharedPreferences.getString("contact_number", "")
		val message = contactSharedPreferences.getString("contact_message", "I am in an emotional emergency. Please call me.")

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
		val contactSharedPreferences = requireActivity().getSharedPreferences(CONTACT_PREFS, Context.MODE_PRIVATE)

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
	
	/**
	 * Fetches the entry count for a specific day.
	 *
	 * @param dayKey The key representing the day.
	 * @return The entry count for the day.
	 */
	private fun getEntryCountForDay(dayKey: String): Int {
		return countPref?.getInt(dayKey, 0) ?: 0
	}
	
	/**
	 * Generates chart data for the last 60 days.
	 */
	private fun generateChartData() {
		// Get the current date instance.
		val calendar = Calendar.getInstance()

		// Initialize lists to store data for the bar and line charts.
		val barData = mutableListOf<Any>()
		val lineData = mutableListOf<Any>()

		// Loop through the last 30 days to generate data for the bar chart.
		for (i in 0 until 30) {
			// Create a unique key for each day in the format "day-month-year".
			val dayKey = "${calendar.get(Calendar.DAY_OF_MONTH)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.YEAR)}"
			
			// Add the entry count for the day to the beginning of the barData list.
			barData.add(0, getEntryCountForDay(dayKey))
			
			// Move the calendar date one day back.
			calendar.add(Calendar.DAY_OF_YEAR, -1)
		}

		// Loop through the previous 30 days (days 31 to 60) to generate data for the line chart.
		for (i in 0 until 30) {
			// Create a unique key for each day in the format "day-month-year".
			val dayKey = "${calendar.get(Calendar.DAY_OF_MONTH)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.YEAR)}"
			
			// Add the entry count for the day to the beginning of the lineData list.
			lineData.add(0, getEntryCountForDay(dayKey))
			
			// Move the calendar date one day back.
			calendar.add(Calendar.DAY_OF_YEAR, -1)
		}

		// Update the LiveData objects with the generated data.
		barDataLiveData.value = barData.toTypedArray()
		lineDataLiveData.value = lineData.toTypedArray()
	}
	
	/**
	 * Initializes the data for the bar and line charts.
	 */
	private fun setupChartData() {
		// Retrieve the bar data from LiveData; default to an empty array if null.
		val barData = barDataLiveData.value ?: arrayOf()

		// Retrieve the line data from LiveData; default to an empty array if null.
		val lineData = lineDataLiveData.value ?: arrayOf()

		// Set up the chart with the retrieved bar and line data.
		setupChartData(barData, lineData)
	}
	
	/**
	 * Save the count of entries for a specific day to Firestore.
	 *
	 * @param count The count of entries for the day.
	 */
	private fun getCountEntryToFirestore() {
		val collection = Firebase.auth.currentUser?.let { db.collection(it.uid) }
		collection?.document("${LocalDate.now()}")?.get()!!.addOnSuccessListener {
			Log.d(TAG, "DocumentSnapshot data: ${it.data?.get("count")}") }
	}
	
	/**
	 * Define and set up chart data.
	 * This method initializes the data for the bar and line charts and configures the chart's appearance and behavior.
	 */
	private fun setupChartData(barData: Array<Any>, lineData: Array<Any>) {
		// Set the bar series and line series with their respective parameters.
		val barSeriesElement = AASeriesElement().name("Last 30 days").color("#FFDAD6").type(AAChartType.Column).data(barData)
		val lineSeriesElement = AASeriesElement().name("Previous 30 days").color("#FFE088").type(AAChartType.Line).data(lineData)
		
		// Create an AAChartModel with the desired settings.
		val aaChartModel = AAChartModel()
			.chartType(AAChartType.Column)
			.title(getString(R.string.your_gratitude_note_activity))
			.backgroundColor("#00000000")
			.dataLabelsEnabled(true)
			.series(arrayOf(barSeriesElement, lineSeriesElement))
			.yAxisTitle("Amount of notes per day")
			.axesTextColor("#FFFFFF")
			.titleStyle(AAStyle().color("#FFFFFF"))
		
		// Draw the chart in the aaChartView using the aaChartModel.
		binding.aaChartView.aa_drawChartWithChartModel(aaChartModel)
	}
}

// TODO: Überdenken einer Lösung mit den Daten der lineChart und barChart. SharedPreferences ist keine Option.
// TODO: Handling über Firebase und Firestore? Benjamin fragen.
