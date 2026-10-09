package com.example.appmynotes.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.appmynotes.MainActivity;
import com.example.appmynotes.R;
import com.example.appmynotes.data.NotesDatabase;
import com.example.appmynotes.model.Note;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class NotesListFragment extends Fragment {
    private NotesDatabase database;
    private ListView listView;
    private View emptyView;
    private String order = NotesDatabase.SORT_PRIORITY;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        database = new NotesDatabase(requireContext());
        listView = view.findViewById(R.id.list);
        emptyView = view.findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);
        listView.setOnItemClickListener((parent, row, position, id) -> ((MainActivity) requireActivity()).showNoteForm(id));
        listView.setOnItemLongClickListener((parent, row, position, id) -> {
            Note note = (Note) parent.getItemAtPosition(position);
            confirmDelete(note);
            return true;
        });
        loadNotes();
    }

    private void loadNotes() {
        List<Note> notes = database.getNotes(order);
        listView.setAdapter(new NoteAdapter(requireContext(), notes));
    }

    private void confirmDelete(Note note) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_note)
                .setMessage(getString(R.string.delete_note_confirmation, note.getTitle()))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    database.deleteNote(note.getId());
                    loadNotes();
                    Snackbar.make(requireView(), R.string.note_deleted, Snackbar.LENGTH_SHORT).show();
                }).show();
    }

    public void sortByPriority() {
        order = NotesDatabase.SORT_PRIORITY;
        loadNotes();
        Snackbar.make(requireView(), R.string.sorted_by_priority, Snackbar.LENGTH_SHORT).show();
    }

    public void sortAlphabetically() {
        order = "a.titulo COLLATE NOCASE ASC";
        loadNotes();
        Snackbar.make(requireView(), R.string.sorted_alphabetically, Snackbar.LENGTH_SHORT).show();
    }

    @Override public void onDestroy() {
        if (database != null) database.close();
        super.onDestroy();
    }
}
