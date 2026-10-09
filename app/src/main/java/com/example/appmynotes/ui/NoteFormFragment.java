package com.example.appmynotes.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appmynotes.MainActivity;
import com.example.appmynotes.R;
import com.example.appmynotes.data.NotesDatabase;
import com.example.appmynotes.model.Note;
import com.example.appmynotes.model.Priority;
import com.example.appmynotes.util.InputValidator;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class NoteFormFragment extends Fragment {
    private static final String ARG_ID = "id";
    private NotesDatabase database;
    private EditText title;
    private EditText text;
    private Spinner prioritySpinner;
    private List<Priority> priorities;
    private long noteId;

    public static NoteFormFragment newInstance(long id) {
        NoteFormFragment fragment = new NoteFormFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    public boolean isEditing() {
        return getArguments() != null && getArguments().getLong(ARG_ID) != 0;
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        return inflater.inflate(R.layout.fragment_note_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        database = new NotesDatabase(requireContext());
        noteId = getArguments() == null ? 0 : getArguments().getLong(ARG_ID);
        title = view.findViewById(R.id.input_note_title);
        text = view.findViewById(R.id.input_note_text);
        prioritySpinner = view.findViewById(R.id.input_note_priority);
        priorities = database.getPriorities();
        prioritySpinner.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, priorities));
        if (noteId != 0) fillForm();
        view.findViewById(R.id.button_save_note).setOnClickListener(v -> save());
        view.findViewById(R.id.button_cancel_note).setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
    }

    private void fillForm() {
        Note note = database.getNote(noteId);
        if (note == null) return;
        title.setText(note.getTitle());
        text.setText(note.getText());
        for (int i = 0; i < priorities.size(); i++) {
            if (priorities.get(i).getId() == note.getPriority().getId()) prioritySpinner.setSelection(i);
        }
    }

    private void save() {
        String titleValue = title.getText().toString();
        String textValue = text.getText().toString();
        if (InputValidator.isBlank(titleValue)) {
            title.setError(getString(R.string.required_field));
            title.requestFocus();
            return;
        }
        if (InputValidator.isBlank(textValue)) {
            text.setError(getString(R.string.required_field));
            text.requestFocus();
            return;
        }
        Priority priority = (Priority) prioritySpinner.getSelectedItem();
        if (priority == null) {
            Snackbar.make(requireView(), R.string.create_priority_first, Snackbar.LENGTH_LONG).show();
            return;
        }
        database.saveNote(noteId, titleValue, textValue, priority.getId());
        ((MainActivity) requireActivity()).showNotes(true);
    }

    @Override public void onDestroy() {
        if (database != null) database.close();
        super.onDestroy();
    }
}
