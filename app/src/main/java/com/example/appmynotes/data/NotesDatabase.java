package com.example.appmynotes.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.appmynotes.model.Note;
import com.example.appmynotes.model.Priority;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NotesDatabase extends SQLiteOpenHelper {
    public static final String SORT_ALPHABETICAL = "p.descricao COLLATE NOCASE ASC, a.titulo COLLATE NOCASE ASC";
    public static final String SORT_PRIORITY = "p.id ASC, a.titulo COLLATE NOCASE ASC";
    private static final String DATABASE_NAME = "app_my_notes.db";
    private static final int DATABASE_VERSION = 1;

    public NotesDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE prioridade_tab (id INTEGER PRIMARY KEY AUTOINCREMENT, descricao TEXT NOT NULL UNIQUE COLLATE NOCASE, cor TEXT NOT NULL)");
        db.execSQL("CREATE TABLE anotacao_tab (id INTEGER PRIMARY KEY AUTOINCREMENT, titulo TEXT NOT NULL, texto TEXT NOT NULL, prioridade_id INTEGER NOT NULL, FOREIGN KEY(prioridade_id) REFERENCES prioridade_tab(id) ON UPDATE CASCADE ON DELETE RESTRICT)");
        db.execSQL("CREATE INDEX idx_anotacao_prioridade ON anotacao_tab(prioridade_id)");
        insertDefaultPriority(db, "Alta", "#D32F2F");
        insertDefaultPriority(db, "Normal", "#F9A825");
        insertDefaultPriority(db, "Baixa", "#388E3C");
    }

    private void insertDefaultPriority(SQLiteDatabase db, String description, String color) {
        ContentValues values = new ContentValues();
        values.put("descricao", description);
        values.put("cor", color);
        db.insertOrThrow("prioridade_tab", null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS anotacao_tab");
        db.execSQL("DROP TABLE IF EXISTS prioridade_tab");
        onCreate(db);
    }

    public List<Priority> getPriorities() {
        List<Priority> result = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query("prioridade_tab", new String[]{"id", "descricao", "cor"}, null, null, null, null, "id ASC")) {
            while (cursor.moveToNext()) result.add(readPriority(cursor, 0));
        }
        return result;
    }

    public Priority getPriority(long id) {
        try (Cursor cursor = getReadableDatabase().query("prioridade_tab", new String[]{"id", "descricao", "cor"}, "id = ?", new String[]{String.valueOf(id)}, null, null, null)) {
            return cursor.moveToFirst() ? readPriority(cursor, 0) : null;
        }
    }

    public long savePriority(long id, String description, String color) {
        ContentValues values = new ContentValues();
        values.put("descricao", description.trim());
        values.put("cor", color.toUpperCase(Locale.ROOT));
        if (id == 0) return getWritableDatabase().insertOrThrow("prioridade_tab", null, values);
        getWritableDatabase().update("prioridade_tab", values, "id = ?", new String[]{String.valueOf(id)});
        return id;
    }

    public boolean deletePriority(long id) {
        return getWritableDatabase().delete("prioridade_tab", "id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean isPriorityInUse(long id) {
        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM anotacao_tab WHERE prioridade_id = ?", new String[]{String.valueOf(id)})) {
            return cursor.moveToFirst() && cursor.getInt(0) > 0;
        }
    }

    public List<Note> getNotes(String orderBy) {
        List<Note> result = new ArrayList<>();
        String sql = "SELECT a.id, a.titulo, a.texto, p.id, p.descricao, p.cor FROM anotacao_tab a JOIN prioridade_tab p ON p.id = a.prioridade_id ORDER BY " + orderBy;
        try (Cursor cursor = getReadableDatabase().rawQuery(sql, null)) {
            while (cursor.moveToNext()) {
                result.add(new Note(cursor.getLong(0), cursor.getString(1), cursor.getString(2), readPriority(cursor, 3)));
            }
        }
        return result;
    }

    public Note getNote(long id) {
        String sql = "SELECT a.id, a.titulo, a.texto, p.id, p.descricao, p.cor FROM anotacao_tab a JOIN prioridade_tab p ON p.id = a.prioridade_id WHERE a.id = ?";
        try (Cursor cursor = getReadableDatabase().rawQuery(sql, new String[]{String.valueOf(id)})) {
            return cursor.moveToFirst() ? new Note(cursor.getLong(0), cursor.getString(1), cursor.getString(2), readPriority(cursor, 3)) : null;
        }
    }

    public long saveNote(long id, String title, String text, long priorityId) {
        ContentValues values = new ContentValues();
        values.put("titulo", title.trim());
        values.put("texto", text.trim());
        values.put("prioridade_id", priorityId);
        if (id == 0) return getWritableDatabase().insertOrThrow("anotacao_tab", null, values);
        getWritableDatabase().update("anotacao_tab", values, "id = ?", new String[]{String.valueOf(id)});
        return id;
    }

    public boolean deleteNote(long id) {
        return getWritableDatabase().delete("anotacao_tab", "id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    private Priority readPriority(Cursor cursor, int offset) {
        return new Priority(cursor.getLong(offset), cursor.getString(offset + 1), cursor.getString(offset + 2));
    }
}
