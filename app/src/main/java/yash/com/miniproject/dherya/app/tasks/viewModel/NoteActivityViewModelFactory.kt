package yash.com.miniproject.dherya.app.tasks.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import yash.com.miniproject.dherya.app.tasks.repository.NoteRepository

class NoteActivityViewModelFactory(private val repository: NoteRepository):
   ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NoteActivityViewModel(repository) as T
    }
}