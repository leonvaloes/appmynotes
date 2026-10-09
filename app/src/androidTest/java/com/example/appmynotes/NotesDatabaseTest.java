package com.example.appmynotes;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.appmynotes.data.NotesDatabase;
import com.example.appmynotes.model.Note;
import com.example.appmynotes.model.Priority;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class NotesDatabaseTest {
    private Context context;
    private NotesDatabase database;

    @Before public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.deleteDatabase("app_my_notes.db");
        database = new NotesDatabase(context);
    }

    @After public void tearDown() {
        database.close();
        context.deleteDatabase("app_my_notes.db");
    }

    @Test public void completeCrudKeepsPriorityRelationship() {
        List<Priority> defaults = database.getPriorities();
        assertEquals(3, defaults.size());
        long priorityId = defaults.get(0).getId();

        long noteId = database.saveNote(0, "Comprar pão", "Passar na padaria", priorityId);
        Note inserted = database.getNote(noteId);
        assertNotNull(inserted);
        assertEquals("Comprar pão", inserted.getTitle());
        assertTrue(database.isPriorityInUse(priorityId));

        database.saveNote(noteId, "Comprar café", "Pacote de 500 g", priorityId);
        assertEquals("Comprar café", database.getNote(noteId).getTitle());
        assertTrue(database.deleteNote(noteId));
        assertFalse(database.isPriorityInUse(priorityId));
    }
}
