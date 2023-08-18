package com.example.abschlussaufgabe.ui

// Required imports for the class.
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.abschlussaufgabe.data.EntryRepository
import com.example.abschlussaufgabe.data.datamodels.Entry
import com.example.abschlussaufgabe.data.local.LocalDatabase
import com.example.abschlussaufgabe.util.EntryAdapter
import com.example.abschlussaufgabe.viewModel.EntryViewModel
import com.schubau.tara.R
import com.schubau.tara.databinding.FragmentJournalGratitudeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.util.Calendar
import android.content.Intent
import android.widget.TextView
import androidx.core.content.FileProvider

const val JOURNAL_GRATITUDE_FRAGMENT_TAG = "JournalGratitudeFragment"

/**
 * Represents the JournalGratitudeFragment which allows users to view and filter gratitude journal entries.
 */
class JournalGratitudeFragment : Fragment() {
	// Declare a late-initialized variable for the FragmentJournalGratitudeBinding instance.
	private lateinit var binding: FragmentJournalGratitudeBinding
	
	// ViewModel instance to manage and store data related to journal entries.
	private val viewModel: EntryViewModel by viewModels()
	
	// Global variables for the AlertDialog and its content
	private lateinit var alertDialog: AlertDialog
	private lateinit var progressText: TextView
	
	/**
	 * Called when the activity is starting.
	 */
	override fun onStart() {
		// Call the superclass's implementation of onStart
		super.onStart()
		
		// Fetch all the journal entries asynchronously from the database.
		// This ensures that the UI remains responsive while the data is being fetched.
		viewModel.getAllEntriesAsync()
	}
	
	/**
	 * Inflates the fragment's layout and initializes the data binding.
	 */
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		// Inflate the layout for this fragment using the FragmentJournalGratitudeBinding class.
		binding =
			DataBindingUtil.inflate(inflater, R.layout.fragment_journal_gratitude, container, false)
		// Return the root view of the binding object.
		return binding.root
	}
	
	/**
	 * Called immediately after onCreateView() has returned, but before any saved state has been restored in the view.
	 * This gives subclasses a chance to initialize themselves once they know their view hierarchy has been completely created.
	 */
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		// Always call the superclasses implementation of this function.
		super.onViewCreated(view, savedInstanceState)
		
		// Initially, hide the reset button from the user's view.
		binding.resetBtn.visibility = View.GONE
		
		// Initially, set the filter button to be semi-transparent and disable its functionality.
		binding.filterBtn.alpha = 0.4f
		binding.filterBtn.isEnabled = false
		
		// Observe the entries from the ViewModel. When the data changes, update the RecyclerView's adapter.
		viewModel.entries.observe(viewLifecycleOwner) { entries ->
			binding.outerRvGratitudeJournal.adapter =
				EntryAdapter(requireContext(), entries, viewModel)
		}
		
		// Initialize and set up click listeners for various UI components.
		setUpListeners()
		
		// Set up text watchers to monitor changes in text fields, especially for date input validation.
		setUpTextWatchers()
	}
	
	/**
	 * Sets up click listeners for various UI components.
	 */
	private fun setUpListeners() {
		// Set up the click listener for the filter button.
		binding.filterBtn.setOnClickListener {
			// Extract the start and end date from the respective text fields.
			val from = binding.startDateTf.text.toString()
			val to = binding.endDateTf.text.toString()
			
			// Fetch and observe entries from the database within the specified date range.
			viewModel.getEntriesByDataRange(from, to).observe(viewLifecycleOwner) { entries ->
				// Update the RecyclerView adapter with the filtered entries.
				binding.outerRvGratitudeJournal.adapter =
					EntryAdapter(requireContext(), entries, viewModel)
			}
			
			// After filtering, the reset button should be visible and enabled.
			binding.resetBtn.visibility = View.VISIBLE
			binding.resetBtn.isEnabled = true
			
			// After filtering, the export button should be invisible.
			binding.exportBtn.visibility = View.GONE
		}
		
		// Set up the click listener for the reset button.
		binding.resetBtn.setOnClickListener {
			// Clear the content of the start and end date text fields.
			binding.startDateTf.text?.clear()
			binding.endDateTf.text?.clear()
			
			// Set the filter button to be semi-transparent and disable its functionality.
			// This indicates that no filter is currently applied.
			binding.filterBtn.alpha = 0.4f
			binding.filterBtn.isEnabled = false
			
			// Fetch all journal entries asynchronously from the database.
			// This action resets the view to display all entries without any applied filters.
			viewModel.getAllEntriesAsync()
			
			// Observe the entries from the ViewModel. When the data changes, update the RecyclerView's adapter.
			// This ensures that the displayed entries reflect the most recent data.
			viewModel.entries.observe(viewLifecycleOwner) { entries ->
				binding.outerRvGratitudeJournal.adapter =
					EntryAdapter(requireContext(), entries, viewModel)
			}
			
			
			// After resetting, the reset button should be invisible and disabled
			binding.resetBtn.visibility = View.GONE
			
			// After resetting, the export button should be visible.
			binding.exportBtn.visibility = View.VISIBLE
		}
		
		// Set up the click listener for the home button logo.
		// Navigate to the animation fragment when clicked.
		binding.homeBtnLogo.setOnClickListener {
			findNavController().navigate(JournalGratitudeFragmentDirections.actionJournalGratitudeFragmentToAnimationFragment())
		}
		
		// Set up the click listener for the home button text.
		// Navigate to the animation fragment when clicked.
		binding.homeBtnText.setOnClickListener {
			findNavController().navigate(JournalGratitudeFragmentDirections.actionJournalGratitudeFragmentToAnimationFragment())
		}
		
		// Set up the click listener for the profile button logo.
		// Navigate to the profile fragment when clicked.
		binding.profileBtnLogo.setOnClickListener {
			findNavController().navigate(JournalGratitudeFragmentDirections.actionJournalGratitudeFragmentToProfileFragment())
		}
		
		// Set up the click listener for the animationFabNavBtn.
		// Navigate to the animation fragment when clicked.
		binding.animationFabNavBtn.setOnClickListener {
			findNavController().navigate(JournalGratitudeFragmentDirections.actionJournalGratitudeFragmentToAnimationFragment())
		}
		
		// Set up the click listener for the newEntryFab button.
		binding.newEntryFab.setOnClickListener {
			createNewEntry()
		}
		
		// Set up the click listener for the export button.
		binding.exportBtn.setOnClickListener {
			showExportConfirmationDialog()
		}
	}
	
	/**
	 * Sets up text watchers for date input fields to validate the date format.
	 */
	private fun setUpTextWatchers() {
		// Regular expression pattern to match valid dates in the format DD.MM.YYYY.
		val datePattern = "(0[1-9]|[12][0-9]|3[01])\\.(0[1-9]|1[0-2])\\.\\d{4}".toRegex()
		
		// Lambda function to validate the start and end date fields.
		val validateDateFields = {
			// Retrieve the start date from the input field.
			val startDate = binding.startDateTf.text.toString()
			// Retrieve the end date from the input field.
			val endDate = binding.endDateTf.text.toString()
			
			/// Check if both the start and end dates match the valid date pattern.
			if (datePattern.matches(startDate) && datePattern.matches(endDate)) {
				// Split the dates into components
				val startParts = startDate.split(".")
				val endParts = endDate.split(".")
				
				// Create LocalDate objects for both dates
				val startLocalDate = LocalDate.of(startParts[2].toInt(), startParts[1].toInt(), startParts[0].toInt())
				val endLocalDate = LocalDate.of(endParts[2].toInt(), endParts[1].toInt(), endParts[0].toInt())
				
				// Check if the start date is before or equal to the end date
				if (!startLocalDate.isAfter(endLocalDate)) {
					// If valid, set the filter button to be fully opaque and enable it.
					binding.filterBtn.alpha = 1f
					binding.filterBtn.isEnabled = true
				} else {
					// If invalid, set the filter button to be semi-transparent and disable it.
					binding.filterBtn.alpha = 0.4f
					binding.filterBtn.isEnabled = false
				}
			} else {
				// If invalid, set the filter button to be semi-transparent and disable it.
				binding.filterBtn.alpha = 0.4f
				binding.filterBtn.isEnabled = false
			}
		}

// Add a text changed listener to the start date input field.
		binding.startDateTf.addTextChangedListener(object : TextWatcher {
			// This method is called before the text is changed.
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
			
			// This method is called when the text is being changed.
			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
			
			// This method is called after the text has been changed.
			override fun afterTextChanged(s: Editable) {
				// Validate the date fields after the text has changed.
				validateDateFields()
			}
		})

		// Add a text changed listener to the end date input field.
		binding.endDateTf.addTextChangedListener(object : TextWatcher {
			// This method is called before the text is changed.
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
			
			// This method is called when the text is being changed.
			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
			
			// This method is called after the text has been changed.
			override fun afterTextChanged(s: Editable) {
				// Validate the date fields after the text has changed.
				validateDateFields()
			}
		})
	}
	
	/**
	 * Displays a confirmation dialog to the user before exporting journal entries to a PDF.
	 * If the user confirms, the entries are exported to a PDF and then deleted from the device.
	 */
	private fun showExportConfirmationDialog() {
		// Create a new AlertDialog builder with the current context.
		AlertDialog.Builder(requireContext())
			// Set the title of the dialog.
			.setTitle("Export Entries")
			// Set the message to inform the user about the consequences of exporting.
			.setMessage("Do you really want to export the gratitude journal entries? If you confirm, all entries will be deleted from your device.")
			// Set the positive button to confirm the export action.
			.setPositiveButton("Confirm") { _, _ ->
				// Call the function to export entries to a PDF when the user confirms.
				exportEntriesToPdf()
			}
			// Set the negative button to cancel the export action.
			.setNegativeButton("Cancel", null)
			// Display the created dialog to the user.
			.show()
	}
	
	
	/**
	 * Exports journal entries to a PDF file.
	 */
	private fun exportEntriesToPdf() {
		// 1. Determine screen density and generate DIN A4 size in pixels.
		val metrics = resources.displayMetrics
		val density = metrics.densityDpi / 160f  // Convert DPI to scale factor
		
		// DIN A4 dimensions in millimeters.
		val widthMm = 210
		val heightMm = 297
		
		// Convert mm to pixels.
		val widthPixels = (widthMm * density * 160) / 25.4f
		val heightPixels = (heightMm * density * 160) / 25.4f
		
		// 2. Calculate how many entries fit on each page.
		
		// Fetch the list of entries from the ViewModel or default to an empty list if null.
		val entries = viewModel.entries.value ?: listOf()
		
		// Convert the list of entries to their corresponding view representations.
		val entryViews = (binding.outerRvGratitudeJournal.adapter as EntryAdapter).convertEntriesToViews(entries)
		
		// Initialize a list to hold the entries of the current page.
		var currentPageEntries = mutableListOf<View>()
		
		// Initialize a list to hold the lists of entries for each page.
		val pages = mutableListOf<List<View>>()
		
		// Iterate through each entry view to determine its placement on a page.
		for (entryView in entryViews) {
			// Calculate the height of the current entry view.
			val entryHeight = entryView.measuredHeight
			
			// Check if adding the current entry view to the current page would exceed the page height.
			if (currentPageEntries.sumOf { it.measuredHeight } + entryHeight <= heightPixels) {
				// If not, add the entry view to the current page.
				currentPageEntries.add(entryView)
			} else {
				// If yes, finalize the current page and start a new one.
				pages.add(currentPageEntries)
				currentPageEntries = mutableListOf(entryView)
			}
		}
		
		// Add any remaining entries to the last page.
		if (currentPageEntries.isNotEmpty()) {
			pages.add(currentPageEntries)
		}
		
		// 3. Generate the PDF file based on the calculated pages.
		val pdfDocument = PdfDocument()
		
		// Show alert dialog with the number of pages to be generated.
		showAlertDialog(pages.size)
		
		// Iterate through each page and its entries.
		for ((pageIndex, pageEntries) in pages.withIndex()) {
			// Create a new page with the specified width and height.
			val pageInfo = PdfDocument.PageInfo.Builder(widthPixels.toInt(), heightPixels.toInt(), pageIndex + 1).create()
			val page = pdfDocument.startPage(pageInfo)
			
			// Draw each entry view onto the page's canvas.
			for (entryView in pageEntries) {
				entryView.draw(page.canvas)
			}
			
			// Finalize the current page in the PDF document.
			pdfDocument.finishPage(page)
			
			// Update the progress dialog.
			updateAlertDialog(pageIndex + 1, pages.size)
		}
		
		// Dismiss the progress dialog.
		dismissAlertDialog()
		
		// Save the generated PDF to a file.
		val file = File(context?.filesDir, "entries.pdf")
		pdfDocument.writeTo(FileOutputStream(file))
		pdfDocument.close()
		
		// Send the PDF via email.
		sendPdfViaEmail(file)
	}
	
	/**
	 * Sends the PDF file via email.
	 * @param file The PDF file to be sent.
	 */
	private fun sendPdfViaEmail(file: File) {
		val emailIntent = Intent(Intent.ACTION_SEND).apply {
			type = "text/plain"
			putExtra(Intent.EXTRA_SUBJECT, "Gratitude Journal Entries")
			putExtra(Intent.EXTRA_TEXT, "Attached are the exported gratitude journal entries.")
			val fileUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", file)
			putExtra(Intent.EXTRA_STREAM, fileUri)
		}
		
		// Check if there's an app that can handle this intent.
		if (emailIntent.resolveActivity(requireActivity().packageManager) != null) {
			emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
			emailResultLauncher.launch(emailIntent)
		} else {
			// Handle the case where no email client is installed.
			Toast.makeText(context, "No email client found.", Toast.LENGTH_SHORT).show()
		}
	}
	
	/**
	 * A launcher to handle the result of sending an email.
	 */
	private val emailResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
		if (result.resultCode == Activity.RESULT_OK) {
			// Delete all entries if the email was sent successfully.
			viewModel.deleteAllEntries()
			
			// Notify the user that the entries were exported to a PDF.
			Toast.makeText(context, "Entries exported to PDF.", Toast.LENGTH_SHORT).show()
		} else {
			// Notify the user if there was an error sending the email.
			Toast.makeText(context, "Failed to send email.", Toast.LENGTH_SHORT).show()
		}
	}
	
	/**
	 * Initializes and displays the AlertDialog to show the progress of PDF generation.
	 *
	 * @param totalPages The total number of pages to be converted.
	 */
	@SuppressLint("SetTextI18n")
	private fun showAlertDialog(totalPages: Int) {
		val builder = AlertDialog.Builder(requireContext())
		val inflater = layoutInflater
		val view = inflater.inflate(R.layout.progress_dialog_layout, null)
		
		progressText = view.findViewById(R.id.progressText)
		progressText.text = "Page 1 of $totalPages converted..."
		
		builder.setView(view)
		// Prevents the user from closing the dialog
		builder.setCancelable(false)
		
		alertDialog = builder.create()
		alertDialog.show()
	}
	
	/**
	 * Updates the displayed text in the AlertDialog to reflect the current progress.
	 *
	 * @param pageNumber The current page number being converted.
	 * @param totalPages The total number of pages to be converted.
	 */
	@SuppressLint("SetTextI18n")
	private fun updateAlertDialog(pageNumber: Int, totalPages: Int) {
		progressText.text = "Page $pageNumber of $totalPages converted..."
	}
	
	/**
	 * Closes and dismisses the AlertDialog.
	 */
	private fun dismissAlertDialog() {
		alertDialog.dismiss()
	}
	
	/**
	 * Creates a new gratitude journal entry and saves it to the database.
	 */
	private fun createNewEntry() {
		// Get the current date
		Log.e(JOURNAL_GRATITUDE_FRAGMENT_TAG, "createNewEntry() is being called")
		val calendar = Calendar.getInstance()
		
		// Create a new Entry object with the entered date, text, and image.
		val entry = Entry(
			day = calendar.get(Calendar.DAY_OF_MONTH),
			month = calendar.get(Calendar.MONTH) + 1,
			year = calendar.get(Calendar.YEAR),
			text = null,
			image = null
		)
		
		// Obtain an instance of the local database for the current context.
		val database = LocalDatabase.getDatabase(requireContext())
		
		// Create a repository instance using the obtained local database.
		val repository = EntryRepository(database)
		
		// Launch a coroutine in the IO dispatcher for database operations.
		lifecycleScope.launch(Dispatchers.IO) {
			
			// Insert the new entry into the local database and retrieve its ID.
			// Insert the new entry into the repository and retrieve its ID.
			val entryId = repository.insertEntry(entry)
			
			// Access the shared preferences to get and update the count of entries.
			val countSharedPreferences =
				requireContext().getSharedPreferences("countPref", Context.MODE_PRIVATE)
			
			// Retrieve the current count of entries from shared preferences. Default to 0 if not found.
			var count = countSharedPreferences.getInt("count", 0)
			
			// Retrieve the date of the last entry from shared preferences. Default to the current date if not found.
			val date = countSharedPreferences.getString("date", LocalDate.now().toString())
			
			// Check if the date from shared preferences is different from the current date.
			// If it is, reset the count to 0, indicating a new day.
			if (date != LocalDate.now().toString()) {
				count = 0
			}
			
			// Increment the count of entries.
			count++
			
			// Update the date in shared preferences to the current date.
			countSharedPreferences.edit().putString("date", LocalDate.now().toString()).apply()
			
			// Update the count of entries in shared preferences.
			countSharedPreferences.edit().putInt("count", count).apply()
			
			// Increment the count for the specific day in Firestore.
			viewModel.saveCountEntryToFirestore(count)
			
			// Switch to the Main dispatcher to perform UI operations.
			withContext(Dispatchers.Main) {
				// Navigate to the entry gratitude fragment with the ID of the newly created entry.
				findNavController().navigate(
					JournalGratitudeFragmentDirections.actionJournalGratitudeFragmentToEntryGratitudeFragment(
						entryId = entryId
					)
				)
			}
		}
	}
}
