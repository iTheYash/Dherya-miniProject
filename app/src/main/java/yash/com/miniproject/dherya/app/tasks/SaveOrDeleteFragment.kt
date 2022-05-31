package yash.com.miniproject.dherya.app.tasks

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import yash.com.miniproject.dherya.R
import yash.com.miniproject.dherya.app.tasks.model.Note
import yash.com.miniproject.dherya.app.tasks.utils.hideKeyboard
import yash.com.miniproject.dherya.app.tasks.viewModel.NoteActivityViewModel
import yash.com.miniproject.dherya.databinding.FragmentSaveOrDeleteBinding
import yash.com.miniproject.dherya.databinding.FragmentTaskBinding
import java.text.SimpleDateFormat
import java.util.*

class SaveOrDeleteFragment : Fragment(R.layout.fragment_save_or_delete2) {

    private lateinit var navController: NavController
    private lateinit var contentBinding: FragmentSaveOrDeleteBinding
    private var note: Note? = null
    private var color = -1
    private val noteActivityViewModel: NoteActivityViewModel by activityViewModels()
    private val currentDate = SimpleDateFormat.getInstance().format(Date())
    private val job = CoroutineScope(Dispatchers.Main)
    private val args: SaveOrDeleteFragmentArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentBinding = FragmentSaveOrDeleteBinding.bind(view)
        navController = Navigation.findNavController(view)

        contentBinding.backButton.setOnClickListener {
            requireView().hideKeyboard()
            navController.popBackStack()
        }

        contentBinding.saveNote.setOnClickListener {
            saveNote()
            try {
                contentBinding.etNoteContent.setOnFocusChangeListener{_, hasFocus ->
                    if(hasFocus){
                        contentBinding.bottomBar.visibility = View.VISIBLE
                        contentBinding.etNoteContent.setStylesBar(contentBinding.styleBar)
                    } else {
                        contentBinding.bottomBar.visibility = View.GONE
                    }
                }
            } catch (e: Throwable) {
                Log.d("Error", e.stackTrace.toString())
            }

            contentBinding.fabColorPick.setOnClickListener(View.OnClickListener {
                val bottomSheetDialog=BottomSheetDialog(
                    requireContext(),
                    R.style.BottomSheetDialogTheme
                )

            })
        }
    }

    private fun saveNote() {
        if(contentBinding.etNoteContent.text.toString().isEmpty()||contentBinding.etTitle.text.toString().isEmpty()){
            Toast.makeText(requireContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show()
            return
        }
        else{
            note=args.note
            when(note){
                null-> {
                    noteActivityViewModel.saveNote(
                        Note(
                            0,
                            contentBinding.etTitle.text.toString(),
                            contentBinding.etNoteContent.getMD(),
                            currentDate,
                            color
                        )
                    )
                    navController.navigate(SaveOrDeleteFragmentDirections.actionSaveOrDeleteFragmentToTaskFragment())
                }
                else->{
                    //update
                }
            }

        }
    }
}