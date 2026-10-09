package com.example.appmynotes.ui;

import android.database.sqlite.SQLiteConstraintException;
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
import com.example.appmynotes.model.Priority;
import com.google.android.material.snackbar.Snackbar;

public class PrioritiesListFragment extends Fragment {
    private NotesDatabase database;
    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        database = new NotesDatabase(requireContext());
        listView = view.findViewById(R.id.list);
        listView.setEmptyView(view.findViewById(R.id.empty_view));
        listView.setOnItemClickListener((parent, row, position, id) -> ((MainActivity) requireActivity()).showPriorityForm(id));
        listView.setOnItemLongClickListener((parent, row, position, id) -> {
            confirmDelete((Priority) parent.getItemAtPosition(position));
            return true;
        });
        loadPriorities();
    }

    private void loadPriorities() {
        listView.setAdapter(new PriorityAdapter(requireContext(), database.getPriorities()));
    }

    private void confirmDelete(Priority priority) {
        if (database.isPriorityInUse(priority.getId())) {
            new AlertDialog.Builder(requireContext()).setTitle(R.string.cannot_delete)
                    .setMessage(R.string.priority_in_use).setPositiveButton(R.string.ok, null).show();
            return;
        }
        new AlertDialog.Builder(requireContext()).setTitle(R.string.delete_priority)
                .setMessage(getString(R.string.delete_priority_confirmation, priority.getDescription()))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    try {
                        database.deletePriority(priority.getId());
                        loadPriorities();
                        Snackbar.make(requireView(), R.string.priority_deleted, Snackbar.LENGTH_SHORT).show();
                    } catch (SQLiteConstraintException exception) {
                        Snackbar.make(requireView(), R.string.priority_in_use, Snackbar.LENGTH_LONG).show();
                    }
                }).show();
    }

    @Override public void onDestroy() {
        if (database != null) database.close();
        super.onDestroy();
    }
}
