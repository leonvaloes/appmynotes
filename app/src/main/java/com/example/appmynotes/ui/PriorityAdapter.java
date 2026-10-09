package com.example.appmynotes.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.appmynotes.R;
import com.example.appmynotes.model.Priority;

import java.util.List;

class PriorityAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final List<Priority> priorities;

    PriorityAdapter(Context context, List<Priority> priorities) {
        inflater = LayoutInflater.from(context);
        this.priorities = priorities;
    }

    @Override public int getCount() { return priorities.size(); }
    @Override public Priority getItem(int position) { return priorities.get(position); }
    @Override public long getItemId(int position) { return getItem(position).getId(); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView == null ? inflater.inflate(R.layout.item_priority, parent, false) : convertView;
        Priority priority = getItem(position);
        ((TextView) view.findViewById(R.id.priority_description)).setText(priority.getDescription());
        ((TextView) view.findViewById(R.id.priority_hex)).setText(priority.getColor());
        view.findViewById(R.id.priority_swatch).setBackgroundColor(Color.parseColor(priority.getColor()));
        return view;
    }
}
