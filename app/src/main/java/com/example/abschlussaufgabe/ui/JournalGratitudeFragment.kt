package com.example.abschlussaufgabe.ui

// Required imports for the class.
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.pdf.PdfDocument
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
import android.graphics.Bitmap
import android.widget.TextView
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import android.graphics.Canvas
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView

const val JOURNAL_GRATITUDE_FRAGMENT_TAG = "JournalGratitudeFragment"

/**
 * Represents the JournalGratitudeFragment which allows users to view and filter gratitude journal entries.
 */
class JournalGratitudeFragment : Fragment() {
	// Declare a late-initialized variable for the FragmentJournalGratitudeBinding instance.
	private lateinit var binding: FragmentJournalGratitudeBinding
	
	// Holds the FirebaseAuth instance.
	private lateinit var auth: FirebaseAuth
	
	// ViewModel instance to manage and store data related to journal entries.
	private val viewModel: EntryViewModel by viewModels()
	
	private var journal = listOf<Entry>()
	
	// Global variables for the AlertDialog and its content
	private lateinit var alertDialog: AlertDialog
	private lateinit var progressText: TextView
	
	/**
	 * Called when the activity is starting.
	 */
	override fun onStart() {
		// Call the superclass's implementation of onStart
		super.onStart()
		
		// Get the FirebaseAuth instance.
		auth = FirebaseAuth.getInstance()
		
		if (auth.currentUser != null) {
			// Fetch all the journal entries asynchronously from the database.
			// This ensures that the UI remains responsive while the data is being fetched.
			viewModel.getAllEntriesAsync()
		} else {
			Toast.makeText(requireContext(), "Please log in first!", Toast.LENGTH_SHORT).show()
			findNavController().navigate(JournalGratitudeFragmentDirections.actionJournalGratitudeFragmentToHomeFragment())
		}
	}
	
	/**
	 * Inflates the fragment's layout and initializes data binding.
	 *
	 * @param inflater LayoutInflater to inflate the views.
	 * @param container ViewGroup to be the parent of the generated hierarchy.
	 * @param savedInstanceState Bundle to save the instance state.
	 * @return View representing the root of the inflated layout.
	 */
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment using the FragmentJournalGratitudeBinding class.
		binding =
			DataBindingUtil.inflate(inflater, R.layout.fragment_journal_gratitude, container, false)
		
		// Get the FirebaseAuth instance.
		auth = FirebaseAuth.getInstance()
		
		// Check if the user is null.
		if (auth.currentUser == null) {
			findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToHomeFragment())
			return null
		}
		
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
		
		// Observe the entries from the ViewModel. When the data changes, update the RecyclerView's adapter.
		viewModel.entries.observe(viewLifecycleOwner) { entries ->
			binding.outerRvGratitudeJournal.adapter =
				EntryAdapter(requireContext(), entries, viewModel)
			journal = entries
		}
		
		// Initially, hide the reset button from the user's view.
		binding.resetBtn.visibility = View.GONE
		
		// Initially, set the filter button to be semi-transparent and disable its functionality.
		binding.filterBtn.alpha = 0.4f
		binding.filterBtn.isEnabled = false
		
		// After setting the adapter, enqueue the Runnable to get the heights of the entryViews.
		binding.outerRvGratitudeJournal.post {
			processEntryViews()
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
			filterAndDisplayEntries()
		}
		
		// Set up the click listener for the reset button.
		binding.resetBtn.setOnClickListener {
			resetJournalView()
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
		
		// Add a text changed listener to the start date input field.
		binding.startDateTf.addTextChangedListener(object : TextWatcher {
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
			override fun afterTextChanged(s: Editable) {
				validateDateFields(datePattern)
			}
		})
		
		// Add a text changed listener to the end date input field.
		binding.endDateTf.addTextChangedListener(object : TextWatcher {
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
			override fun afterTextChanged(s: Editable) {
				validateDateFields(datePattern)
			}
		})
	}
	
	/**
	 * Fetches and displays entries from the database within the specified date range.
	 * After fetching, it adjusts the UI elements accordingly.
	 */
	private fun filterAndDisplayEntries() {
		if (auth.currentUser != null) {
			// Fetch and observe entries from the database within the specified date range.
			viewModel.getEntriesByDataRange(
				binding.startDateTf.text.toString(),
				binding.endDateTf.text.toString()
			).observe(viewLifecycleOwner) { entries ->
				// Update the RecyclerView adapter with the filtered entries.
				binding.outerRvGratitudeJournal.adapter =
					EntryAdapter(requireContext(), entries, viewModel)
			}
			
			// Adjust UI elements after filtering.
			binding.apply {
				resetBtn.apply {
					visibility = View.VISIBLE
					isEnabled = true
				}
				exportBtn.visibility = View.GONE
			}
		} else {
			Toast.makeText(requireContext(), "Please log in first!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(JournalGratitudeFragmentDirections.actionJournalGratitudeFragmentToHomeFragment())
		}
		
		// Observe the entries from the ViewModel. When the data changes, update the RecyclerView's adapter.
		viewModel.entries.observe(viewLifecycleOwner) { entries ->
			binding.outerRvGratitudeJournal.adapter =
				EntryAdapter(requireContext(), entries, viewModel)
		}
	}
	
	/**
	 * Resets the journal view by clearing any applied filters and fetching all entries.
	 *
	 * This function is triggered when the reset button is clicked. It clears the content of the start
	 * and end date text fields, sets the filter button to be semi-transparent to indicate no filter is
	 * applied, and fetches all journal entries from the database. The RecyclerView's adapter is then
	 * updated to reflect the most recent data.
	 */
	private fun resetJournalView() {
		// Clear the content of the start and end date text fields.
		binding.startDateTf.text?.clear()
		binding.endDateTf.text?.clear()
		
		// Set the filter button to be gone.
		binding.filterBtn.apply {
			alpha = 0.4f
			isEnabled = false
		}
		
		// Set the export button to be visible.
		binding.exportBtn.apply {
			visibility = View.VISIBLE
			isEnabled = true
		}
		
		// Set the reset button to be invisible.
		binding.resetBtn.apply {
			visibility = View.GONE
		}
		
		// Fetch all journal entries asynchronously from the database.
		viewModel.getAllEntriesAsync()
		
		// Observe the entries from the ViewModel. When the data changes, update the RecyclerView's adapter.
		viewModel.entries.observe(viewLifecycleOwner) { entries ->
			binding.outerRvGratitudeJournal.adapter =
				EntryAdapter(requireContext(), entries, viewModel)
		}
	}
	
	/**
	 * Validates the start and end date fields against a given date pattern.
	 *
	 * @param datePattern The regular expression pattern to validate against.
	 */
	private fun validateDateFields(datePattern: Regex) {
		val startDate = binding.startDateTf.text.toString()
		val endDate = binding.endDateTf.text.toString()
		
		if (datePattern.matches(startDate) && datePattern.matches(endDate)) {
			if (isValidDateRange(startDate, endDate)) {
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
	
	/**
	 * Checks if the start date is before or equal to the end date.
	 *
	 * @param startDate The start date in the format DD.MM.YYYY.
	 * @param endDate The end date in the format DD.MM.YYYY.
	 * @return True if the start date is before or equal to the end date, false otherwise.
	 */
	private fun isValidDateRange(startDate: String, endDate: String): Boolean {
		val startParts = startDate.split(".")
		val endParts = endDate.split(".")
		
		val startLocalDate =
			LocalDate.of(startParts[2].toInt(), startParts[1].toInt(), startParts[0].toInt())
		val endLocalDate =
			LocalDate.of(endParts[2].toInt(), endParts[1].toInt(), endParts[0].toInt())
		
		return !startLocalDate.isAfter(endLocalDate)
	}
	
	/**
	 * Displays a confirmation dialog to the user before exporting journal entries to a PDF.
	 * If the user confirms, the entries are exported to a PDF and then deleted from the device.
	 */
	private fun showExportConfirmationDialog() {
		if (auth.currentUser != null) {
			// Create a new AlertDialog builder with the current context.
			AlertDialog.Builder(requireContext())
				// Set the title of the dialog.
				.setTitle("Export Entries")
				// Set the message to inform the user about the consequences of exporting.
				.setMessage("Do you really want to export the gratitude journal entries? If you confirm, all entries will be deleted from your device.")
				// Set the positive button to confirm the export action.
				.setPositiveButton("Confirm") { _, _ ->
					// Process the entry views and get the pages
					val pages = processEntryViews()
					
					// Begin the PDF creation process
					exportEntriesToPdf(pages)
					
					// Show alert dialog with the number of pages to be generated.
					showAlertDialog(pages.size)
				}
				// Set the negative button to cancel the export action.
				.setNegativeButton("Cancel", null)
				// Display the created dialog to the user.
				.show()
		} else {
			Toast.makeText(requireContext(), "Please log in first!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(JournalGratitudeFragmentDirections.actionJournalGratitudeFragmentToHomeFragment())
		}
	}
	
	/**
	 * Processes the entryViews to determine how they should be placed on the pages.
	 */
	private fun processEntryViews(): List<List<View>> {
		
		// Retrieve the height in pixels of a DIN A4 paper size. The underscore (_) is used to ignore the width value.
		val (_, heightPixels) = getDinA4SizeInPixels()
		
		// Retrieve the list of entries from the ViewModel. If the value is null, default to an empty list.
		//val entries = viewModel.entries.value ?: listOf()
		val entries = journal
		Log.e(JOURNAL_GRATITUDE_FRAGMENT_TAG, "Break down $entries")
		
		// Retrieve the adapter associated with the RecyclerView that displays the gratitude journal entries.
		val adapter = binding.outerRvGratitudeJournal.adapter
		
		// Check if the adapter is an instance of EntryAdapter.
		// If it is, convert the list of entries to a list of entry views using the adapter's method.
		// Otherwise, default to an empty list of views.
		val entryViews = if (adapter is EntryAdapter) {
			adapter.convertEntriesToViews(entries)
		} else {
			listOf<View>()
		}
		
		// Calculate how the entry views should be distributed across the pages based on the height in pixels of a DIN A4 paper size.
		return calculatePages(entryViews, heightPixels)
	}
	
	/**
	 * Exports journal entries to a PDF file.
	 */
	private fun exportEntriesToPdf(pages: List<List<View>>) {
		if (auth.currentUser!= null) {
			// 1. Get DIN A4 size in pixels.
			val (widthPixels, heightPixels) = getDinA4SizeInPixels()
			
			// 2. Show alert dialog with the number of pages to be generated.
			showAlertDialog(pages.size)
			
			// 3. Generate the PDF file based on the provided pages.
			val pdfDocument = viewModel.generatePDF(binding.outerRvGratitudeJournal)
			//generatePdfFromPages(pages, pdfDocument, widthPixels, heightPixels)
			
			// 4. Save and send the generated PDF.
			saveAndSendPdf(pdfDocument)
		} else {
			Toast.makeText(requireContext(), "Please log in first!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(JournalGratitudeFragmentDirections.actionJournalGratitudeFragmentToHomeFragment())
		}
	}
	
	/**
	 * Generates a PDF document from the provided pages.
	 *
	 * @param pages The list of pages with their entries.
	 * @param pdfDocument The PDF document to be generated.
	 * @param widthPixels The width in pixels for the PDF pages.
	 * @param heightPixels The height in pixels for the PDF pages.
	 */
	private fun generatePdfFromPages(
		pages: List<List<View>>,
		pdfDocument: PdfDocument,
		widthPixels: Float,
		heightPixels: Float
	) {
		for ((pageIndex, pageEntries) in pages.withIndex()) {
			val pageInfo = PdfDocument.PageInfo.Builder(
				widthPixels.toInt(),
				heightPixels.toInt(),
				pageIndex + 1
			).create()
			val page = pdfDocument.startPage(pageInfo)
			
			drawEntriesOnPage(pageEntries, page.canvas, widthPixels, heightPixels)
			
			pdfDocument.finishPage(page)
			
			updateAlertDialog(pageIndex + 1, pages.size)
		}
	}
	
	/**
	 * Draws the provided entries on the given canvas.
	 *
	 * @param entries The list of entry views to be drawn.
	 * @param canvas The canvas of the PDF page.
	 * @param widthPixels The width in pixels for the entries.
	 * @param heightPixels The height in pixels for the entries.
	 */
	private fun drawEntriesOnPage(
		entries: List<View>,
		canvas: Canvas,
		widthPixels: Float,
		heightPixels: Float
	) {
		for (entryView in entries) {
			entryView.measure(
				View.MeasureSpec.makeMeasureSpec(widthPixels.toInt(), View.MeasureSpec.EXACTLY),
				View.MeasureSpec.makeMeasureSpec(heightPixels.toInt(), View.MeasureSpec.AT_MOST)
			)
			var tAxis = 0
			if (entryView != entries.first()) {
				tAxis += entryView.measuredHeight + 8
			}
			
			measureAndLayoutEntryView(entryView, widthPixels, heightPixels, tAxis)
			entryView.draw(canvas)
		}
	}
	
	/**
	 * Measures and layouts the provided entry view.
	 *
	 * @param entryView The view to be measured and laid out.
	 * @param widthPixels The width in pixels for the entry view.
	 * @param heightPixels The height in pixels for the entry view.
	 */
	private fun measureAndLayoutEntryView(
		entryView: View,
		widthPixels: Float,
		heightPixels: Float,
		tAxis: Int
	) {
		entryView.measure(
			View.MeasureSpec.makeMeasureSpec(widthPixels.toInt(), View.MeasureSpec.EXACTLY),
			View.MeasureSpec.makeMeasureSpec(heightPixels.toInt(), View.MeasureSpec.AT_MOST)
		)
		val bAxis = entryView.measuredHeight + tAxis
		entryView.layout(0, tAxis, entryView.measuredWidth, bAxis)
		Log.d(
			"PDF_EXPORT",
			"Drawing view with width: ${entryView.measuredWidth} and height: ${entryView.measuredHeight}"
		)
	}
	
	/**
	 * Saves the generated PDF to a file and sends it via email.
	 *
	 * @param pdfDocument The generated PDF document.
	 */
	private fun saveAndSendPdf(pdfDocument: PdfDocument) {
		dismissAlertDialog()
		
		val file = File(context?.filesDir, "entries.pdf")
		pdfDocument.writeTo(FileOutputStream(file))
		pdfDocument.close()
		
		sendPdfViaEmail(file)
	}
	
	/**
	 * Sends the PDF file via email.
	 * @param file The PDF file to be sent.
	 */
	private fun sendPdfViaEmail(file: File) {
		// Create an email intent.
		val emailIntent = Intent(Intent.ACTION_SEND).apply {
			// Set the MIME type for the email content.
			type = "text/plain"
			
			// Set the email subject.
			putExtra(Intent.EXTRA_SUBJECT, "Gratitude Journal Entries")
			
			// Set the email body text.
			putExtra(Intent.EXTRA_TEXT, "Attached are the exported gratitude journal entries.")
			
			putExtra(Intent.EXTRA_EMAIL, arrayOf(auth.currentUser!!.email))
			
			// Convert the file to a content URI using FileProvider to ensure secure sharing of the file.
			val fileUri = FileProvider.getUriForFile(
				requireContext(),
				"${requireContext().packageName}.fileprovider",
				file
			)
			
			// Attach the file to the email.
			putExtra(Intent.EXTRA_STREAM, fileUri)
		}
		
		// Check if there's an app installed that can handle the email intent.
		if (emailIntent.resolveActivity(requireActivity().packageManager) != null) {
			// Grant read permission for the content URI to the recipient app.
			emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
			
			// Launch the email client using the custom result launcher.
			emailResultLauncher.launch(emailIntent)
		} else {
			// If no email client is installed, display a toast message to the user.
			Toast.makeText(context, "No email client found.", Toast.LENGTH_SHORT).show()
		}
	}
	
	/**
	 * A launcher to handle the result of sending an email.
	 */
	private val emailResultLauncher =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
			Log.e(JOURNAL_GRATITUDE_FRAGMENT_TAG, "${result.resultCode} == ${Activity.RESULT_OK}")
			// Create a new AlertDialog builder with the current context.
			val alertDialogBuilder = AlertDialog.Builder(requireContext())
			
			// Set the title and message for the AlertDialog.
			alertDialogBuilder.setTitle("Export Confirmation")
			alertDialogBuilder.setMessage("Was the export successful? Do you want to delete all entries from your device?")
			
			// Set the positive button to confirm the deletion.
			alertDialogBuilder.setPositiveButton("Yes, Delete") { _, _ ->
				// Call the deleteAll() function from the viewModel to delete all entries.
				viewModel.deleteAllEntries()
				Toast.makeText(context, "All entries have been deleted.", Toast.LENGTH_SHORT).show()
			}
			
			// Set the negative button to cancel the deletion.
			alertDialogBuilder.setNegativeButton("Cancel", null)
			
			// Display the created AlertDialog to the user.
			alertDialogBuilder.show()
			
			// Close the AlertDialog
			dismissAlertDialog()
		}
	
	/**
	 * Displays an AlertDialog to the user indicating the progress of the PDF conversion.
	 *
	 * @param totalPages The total number of pages that will be converted.
	 */
	@SuppressLint("SetTextI18n")
	private fun showAlertDialog(totalPages: Int) {
		// Create a new AlertDialog builder with the current context.
		val builder = AlertDialog.Builder(requireContext())
		
		// Obtain the layout inflater to inflate the custom dialog layout.
		val inflater = layoutInflater
		
		// Inflate the custom dialog layout.
		val view = inflater.inflate(R.layout.progress_dialog_layout, null)
		
		// Find and initialize the progress text view from the inflated layout.
		progressText = view.findViewById(R.id.progressText)
		
		// Set the initial progress text indicating the first page conversion.
		progressText.text = "Page 1 of $totalPages converted..."
		
		// Set the custom view for the AlertDialog.
		builder.setView(view)
		
		// Prevent the user from closing the dialog by pressing outside or back button.
		builder.setCancelable(false)
		
		// Create the AlertDialog.
		alertDialog = builder.create()
		
		// Display the AlertDialog to the user.
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
	 * Calculates the size of a DIN A4 page in pixels.
	 *
	 * @return Pair of width and height in pixels.
	 */
	private fun getDinA4SizeInPixels(): Pair<Float, Float> {
		
		// Retrieve the display metrics associated with the resources of the current context.
		val metrics = resources.displayMetrics
		
		// Calculate the density scale factor by dividing the device's DPI (dots per inch) by the base density (160 DPI).
		// This scale factor is used to convert device-independent pixels (dp) to pixels.
		val density = metrics.densityDpi / 160f  // Convert DPI to scale factor
		
		// Define the standard dimensions of a DIN A4 paper in millimeters.
		val widthMm = 210
		val heightMm = 297
		
		// Convert the width and height from millimeters to pixels.
		// The conversion uses the previously calculated density scale factor and a constant (160/25.4) to convert from millimeters to inches.
		val widthPixels = (widthMm * density * 160) / 25.4f
		val heightPixels = (heightMm * density * 160) / 25.4f
		
		// Return the calculated width and height in pixels as a pair.
		return Pair(widthPixels, heightPixels)
	}
	
	/**
	 * Calculates the distribution of entry views across multiple pages based on the given height constraint.
	 *
	 * @param entryViews List of entry views to be distributed.
	 * @param heightPixels Maximum height constraint for each page.
	 * @return List of pages, where each page is represented by a list of entry views.
	 */
	private fun calculatePages(entryViews: List<View>, heightPixels: Float): List<List<View>> {
		
		// Initialize a mutable list to store the pages of entry views.
		val pages = mutableListOf<List<View>>()
		
		// Initialize a mutable list to store the current page's entry views.
		var currentPageEntries = mutableListOf<View>()
		
		// Iterate over each entry view in the provided list.
		for (entryView in entryViews) {
			
			// Get the height of the current entry view.
			val entryHeight = entryView.measuredHeight
			
			// Check if the total height of the current page's entry views plus the height of the current entry view is within the height constraint.
			if (currentPageEntries.sumOf { it.measuredHeight } + entryHeight <= heightPixels) {
				
				// If within the height constraint, add the current entry view to the current page's entry views.
				currentPageEntries.add(entryView)
			} else {
				
				// If exceeding the height constraint, add the current page's entry views to the pages list.
				pages.add(currentPageEntries)
				
				// Start a new page with the current entry view as its first entry.
				currentPageEntries = mutableListOf(entryView)
			}
		}
		
		// After iterating over all entry views, check if there are any remaining entry views in the current page's list.
		if (currentPageEntries.isNotEmpty()) {
			
			// If there are remaining entry views, add them as a new page to the pages list.
			pages.add(currentPageEntries)
		}
		
		// Return the list of pages.
		return pages
	}
	
	/**
	 * Creates a new gratitude journal entry and saves it to the database.
	 */
	private fun createNewEntry() {
		if (auth.currentUser != null) {
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
				
				val userId = auth.currentUser!!.uid
				// Access the shared preferences to get and update the count of entries.
				val countSharedPreferences =
					requireContext().getSharedPreferences(userId, Context.MODE_PRIVATE)
				
				
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
		} else {
			Toast.makeText(context, "You must be signed in to create a gratitude journal entry.", Toast.LENGTH_SHORT).show()
			findNavController().navigate(JournalGratitudeFragmentDirections.actionJournalGratitudeFragmentToHomeFragment())
		}
	}
	
}
