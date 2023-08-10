package com.example.abschlussaufgabe.ui

// Required imports for the class.
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.abschlussaufgabe.R
import com.example.abschlussaufgabe.databinding.FragmentProfileBinding
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAStyle

// A class that represents the profile fragment in the application.
class ProfileFragment : Fragment() {
	
	// Property to hold the binding object for this fragment.
	private lateinit var binding: FragmentProfileBinding
	
	// The onCreateView function is used to create and return the view hierarchy
	// associated with the fragment.
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		
		// Inflate the layout for this fragment using the inflate method of the FragmentProfileBinding class.
		binding = FragmentProfileBinding.inflate(inflater, container, false)
		
		// Return the root view of the inflated layout.
		return binding.root
	}
	
	// onViewCreated is called after onCreateView, and it is where additional setup for the fragment's view takes place.
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		// Define data for the bar chart and line chart.
		val barData = arrayOf<Any>(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30)
		val lineData = arrayOf<Any>(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30)
		
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
		
		// The onClickListener for the back button.
		// When the button is clicked, the user is taken to the previous screen in the back stack.
		binding.backBtn.setOnClickListener {
			findNavController().popBackStack()
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
			findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToEmergencyContactFragment(avatarImage = 0))
		}
		
		// The onClickListener for the logout button.
		// When clicked, the user is navigated to the home fragment, effectively logging them out.
		binding.logoutBtn.setOnClickListener {
			findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToHomeFragment())
		}
	}
}
