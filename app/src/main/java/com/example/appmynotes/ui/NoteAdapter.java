package com.example.appmynotes.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.appmynotes.R;
import com.example.appmynotes.model.Note;

import java.util.List;

class NoteAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final List<Note> notes;

    NoteAdapter(Context context, List<Note> notes) {
        inflater = LayoutInflater.from(context);
        this.notes = notes;
    }

    @Override public int getCount() { return notes.size(); }
    @Override public Note getItem(int position) { return notes.get(position); }
    @Override public long getItemId(int position) { return getItem(position).getId(); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView == null ? inflater.inflate(R.layout.item_note, parent, false) : convertView;
        Note note = getItem(position);
        TextView title = view.findViewById(R.id.note_title);
        TextView text = view.findViewById(R.id.note_text);
        TextView priority = view.findViewById(R.id.note_priority);
        View color = view.findViewById(R.id.priority_color);
        title.setText(note.getTitle());
        text.setText(note.getText());
        priority.setText(note.getPriority().getDescription());
        int parsedColor = Color.parseColor(note.getPriority().getColor());
        title.setTextColor(parsedColor);
        color.setBackgroundColor(parsedColor);
        return view;
    }
}
