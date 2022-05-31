package yash.com.miniproject.dherya.app.tasks.repository

import yash.com.miniproject.dherya.app.tasks.db.NoteDatabase
import yash.com.miniproject.dherya.app.tasks.model.Note

class NoteRepository(private val db: NoteDatabase) {

    fun getNote() = db.getNoteDao().getAllNote()

    fun searchNote(query: String) = db.getNoteDao().searchNotes(query)

    suspend fun addNote(note: Note) = db.getNoteDao().addNote(note)

    suspend fun updateNote(note: Note) = db.getNoteDao().updateNote(note)

    suspend fun deleteNote(note: Note) = db.getNoteDao().deleteNote(note)



}