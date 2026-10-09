package com.example.appmynotes;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.activity.OnBackPressedCallback;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.appmynotes.ui.NoteFormFragment;
import com.example.appmynotes.ui.NotesListFragment;
import com.example.appmynotes.ui.PrioritiesListFragment;
import com.example.appmynotes.ui.PriorityFormFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private FloatingActionButton fab;
    private boolean notesSection = true;
    private boolean listScreen = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigation = findViewById(R.id.navigation_view);
        navigation.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            if (notesSection) showNoteForm(0); else showPriorityForm(0);
        });

        getSupportFragmentManager().addOnBackStackChangedListener(this::syncUiWithCurrentFragment);

        if (savedInstanceState == null) {
            navigation.setCheckedItem(R.id.nav_notes);
            showNotes(false);
        } else {
            findViewById(R.id.fragment_container).post(this::syncUiWithCurrentFragment);
        }
    }

    private void syncUiWithCurrentFragment() {
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listScreen = current instanceof NotesListFragment || current instanceof PrioritiesListFragment;
        if (current instanceof NotesListFragment || current instanceof NoteFormFragment) {
            notesSection = true;
            if (current instanceof NotesListFragment) {
                setTitle(R.string.notes);
            } else {
                setTitle(((NoteFormFragment) current).isEditing() ? R.string.edit_note : R.string.new_note);
            }
        } else if (current instanceof PrioritiesListFragment || current instanceof PriorityFormFragment) {
            notesSection = false;
            if (current instanceof PrioritiesListFragment) {
                setTitle(R.string.priorities);
            } else {
                setTitle(((PriorityFormFragment) current).isEditing() ? R.string.edit_priority : R.string.new_priority);
            }
        }
        fab.setVisibility(listScreen ? View.VISIBLE : View.GONE);
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean showSorting = notesSection && listScreen;
        menu.findItem(R.id.action_sort_priority).setVisible(showSorting);
        menu.findItem(R.id.action_sort_alphabetical).setVisible(showSorting);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment instanceof NotesListFragment) {
            NotesListFragment list = (NotesListFragment) fragment;
            if (item.getItemId() == R.id.action_sort_priority) {
                list.sortByPriority();
                return true;
            }
            if (item.getItemId() == R.id.action_sort_alphabetical) {
                list.sortAlphabetically();
                return true;
            }
        }
        if (item.getItemId() == R.id.action_exit) {
            finishAffinity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_notes) showNotes(false);
        else if (item.getItemId() == R.id.nav_priorities) showPriorities(false);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showNotes(boolean fromForm) {
        notesSection = true;
        showRoot(new NotesListFragment(), getString(R.string.notes), fromForm);
    }

    public void showPriorities(boolean fromForm) {
        notesSection = false;
        showRoot(new PrioritiesListFragment(), getString(R.string.priorities), fromForm);
    }

    private void showRoot(Fragment fragment, String title, boolean fromForm) {
        if (fromForm) getSupportFragmentManager().popBackStack();
        else getSupportFragmentManager().popBackStackImmediate(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        setTitle(title);
        listScreen = true;
        fab.show();
        invalidateOptionsMenu();
    }

    public void showNoteForm(long id) {
        notesSection = true;
        openForm(NoteFormFragment.newInstance(id), id == 0 ? R.string.new_note : R.string.edit_note);
    }

    public void showPriorityForm(long id) {
        notesSection = false;
        openForm(PriorityFormFragment.newInstance(id), id == 0 ? R.string.new_priority : R.string.edit_priority);
    }

    private void openForm(Fragment fragment, int titleRes) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack("form").commit();
        setTitle(titleRes);
        listScreen = false;
        fab.hide();
        invalidateOptionsMenu();
    }

}
