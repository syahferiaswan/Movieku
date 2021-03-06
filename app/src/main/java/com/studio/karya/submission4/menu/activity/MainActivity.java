package com.studio.karya.submission4.menu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.studio.karya.submission4.R;
import com.studio.karya.submission4.menu.activity.reminder.ReminderActivity;
import com.studio.karya.submission4.menu.activity.search.SearchActivity;
import com.studio.karya.submission4.menu.fragment.content.ContentFragment;
import com.studio.karya.submission4.menu.fragment.fav.FavContainerFragment;

import static com.studio.karya.submission4.menu.activity.DetailActivity.RESULT_TYPE;
import static com.studio.karya.submission4.menu.activity.search.SearchActivity.HINT_SEARCH;
import static com.studio.karya.submission4.menu.activity.search.SearchActivity.SEARCH_TYPE;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView menuBottom;
    int position_menu = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ContentFragment contentFragment = new ContentFragment();
        final FavContainerFragment favFragment = new FavContainerFragment();

        final Bundle bundle = new Bundle();
        bundle.putString(ContentFragment.TYPE, "movie");
        contentFragment.setArguments(bundle);
        loadFragment(contentFragment);

        menuBottom = findViewById(R.id.menu_bottom);
        menuBottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.action_movie:
                        position_menu = 0;
                        bundle.putString(ContentFragment.TYPE, "movie");
                        fragment = new ContentFragment();
                        fragment.setArguments(bundle);
                        loadFragment(fragment);
                        invalidateOptionsMenu();
                        break;

                    case R.id.action_tv:
                        position_menu = 1;
                        bundle.putString(ContentFragment.TYPE, "tv");
                        fragment = new ContentFragment();
                        fragment.setArguments(bundle);
                        loadFragment(fragment);
                        invalidateOptionsMenu();
                        break;

                    case R.id.action_fav:
                        position_menu = 2;
                        loadFragment(favFragment);
                        invalidateOptionsMenu();
                        break;
                }
                return true;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, fragment, fragment.getClass().getSimpleName())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_toolbar, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        if (position_menu == 2) {
            menuItem.setVisible(false);
        } else {
            menuItem.setVisible(true);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_language:
                intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
                startActivityForResult(intent, 101);
                break;
            case R.id.action_search:
                intent = new Intent(this, SearchActivity.class);
                if (position_menu == 0) {
                    intent.putExtra(SEARCH_TYPE, "movie");
                    intent.putExtra(HINT_SEARCH, getString(R.string.hint_search_movie));
                } else if (position_menu == 1) {
                    intent.putExtra(SEARCH_TYPE, "tv");
                    intent.putExtra(HINT_SEARCH, getString(R.string.hint_search_tv));
                }
                startActivity(intent);
                break;
            case R.id.action_reminder:
                intent = new Intent(this, ReminderActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            menuBottom.setSelectedItemId(R.id.action_movie);
            if (resultCode == RESULT_TYPE) {
                if (data != null) {
                    switch (data.getStringExtra(DetailActivity.EXTRA_TYPE)) {
                        case "movie":
                            menuBottom.setSelectedItemId(R.id.action_movie);
                            break;
                        case "tv":
                            menuBottom.setSelectedItemId(R.id.action_tv);
                            break;
                    }
                }
            }
        }
    }
}