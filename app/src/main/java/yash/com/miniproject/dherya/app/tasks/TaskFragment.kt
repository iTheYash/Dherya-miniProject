package yash.com.miniproject.dherya.app.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.transition.MaterialElevationScale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import yash.com.miniproject.dherya.R
import yash.com.miniproject.dherya.app.tasks.utils.hideKeyboard
import yash.com.miniproject.dherya.app.tasks.viewModel.NoteActivityViewModel
import yash.com.miniproject.dherya.databinding.FragmentTaskBinding


class TaskFragment : Fragment(R.layout.fragment_task) {

    private lateinit var  taskBinding: FragmentTaskBinding
    private val noteActivityViewModel: NoteActivityViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title =
            resources.getString(R.string.fragment_name_notes)

        exitTransition = MaterialElevationScale(false).apply {
            duration = 350
        }
        exitTransition = MaterialElevationScale(true).apply {
            duration = 350
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskBinding = FragmentTaskBinding.bind(view)
        val navController = Navigation.findNavController(view)
        requireView().hideKeyboard()
        CoroutineScope(Dispatchers.Main).launch {
            delay(10)
        }

        taskBinding.addNoteFab.setOnClickListener {
            navController.navigate(TaskFragmentDirections.actionTaskFragmentToSaveOrDeleteFragment2())
        }
        taskBinding.innerFab.setOnClickListener {
            navController.navigate(TaskFragmentDirections.actionTaskFragmentToSaveOrDeleteFragment2())
        }
    }
}