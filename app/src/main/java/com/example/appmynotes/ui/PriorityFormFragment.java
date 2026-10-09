package com.example.appmynotes.ui;

import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appmynotes.MainActivity;
import com.example.appmynotes.R;
import com.example.appmynotes.data.NotesDatabase;
import com.example.appmynotes.model.Priority;
import com.example.appmynotes.util.InputValidator;
import com.google.android.material.snackbar.Snackbar;

public class PriorityFormFragment extends Fragment {
    private static final String ARG_ID = "id";
    private NotesDatabase database;
    private EditText description;
    private EditText color;
    private View preview;
    private long priorityId;

    public static PriorityFormFragment newInstance(long id) {
        PriorityFormFragment fragment = new PriorityFormFragment();
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
        return inflater.inflate(R.layout.fragment_priority_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        database = new NotesDatabase(requireContext());
        priorityId = getArguments() == null ? 0 : getArguments().getLong(ARG_ID);
        description = view.findViewById(R.id.input_priority_description);
        color = view.findViewById(R.id.input_priority_color);
        preview = view.findViewById(R.id.color_preview);
        if (priorityId != 0) fillForm(); else color.setText(R.string.default_priority_color);
        updatePreview();
        color.setOnFocusChangeListener((v, hasFocus) -> { if (!hasFocus) updatePreview(); });
        view.findViewById(R.id.button_save_priority).setOnClickListener(v -> save());
        view.findViewById(R.id.button_cancel_priority).setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
    }

    private void fillForm() {
        Priority priority = database.getPriority(priorityId);
        if (priority == null) return;
        description.setText(priority.getDescription());
        color.setText(priority.getColor());
    }

    private void updatePreview() {
        String value = color.getText().toString();
        if (InputValidator.isHexColor(value)) preview.setBackgroundColor(Color.parseColor(value));
    }

    private void save() {
        String descriptionValue = description.getText().toString();
        String colorValue = color.getText().toString().trim();
        if (InputValidator.isBlank(descriptionValue)) {
            description.setError(getString(R.string.required_field));
            description.requestFocus();
            return;
        }
        if (!InputValidator.isHexColor(colorValue)) {
            color.setError(getString(R.string.invalid_color));
            color.requestFocus();
            return;
        }
        try {
            database.savePriority(priorityId, descriptionValue, colorValue);
            ((MainActivity) requireActivity()).showPriorities(true);
        } catch (SQLiteConstraintException exception) {
            Snackbar.make(requireView(), R.string.duplicate_priority, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override public void onDestroy() {
        if (database != null) database.close();
        super.onDestroy();
    }
}
