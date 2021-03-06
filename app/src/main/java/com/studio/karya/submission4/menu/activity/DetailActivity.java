package com.studio.karya.submission4.menu.activity;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import com.studio.karya.submission4.R;
import com.studio.karya.submission4.db.DML.ContentHelper;
import com.studio.karya.submission4.menu.widget.FavoriteWidget;
import com.studio.karya.submission4.model.Content;

import static com.studio.karya.submission4.BuildConfig.IMG_URL;
import static com.studio.karya.submission4.db.DDL.DatabaseContract.TABLE_MOVIE;
import static com.studio.karya.submission4.db.DDL.DatabaseContract.TABLE_TV;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String DATA = "data";
    public static final String TYPE = "type";
    public static final String EXTRA_TYPE = "extra_type";
    public static final String EXTRA_POSITION = "position";
    public static final String ORIGIN_FRAGMENT = "origin_fragment";
    public static final int RESULT_DELETE = 301;
    public static final int RESULT_TYPE = 401;
    public static final int REQUEST_UPDATE = 101;

    private ImageView imgBackdrop, imgPoster;
    private TextView tvTitle, tvPopularity, tvVoteCount, tvVoteAvg, tvOverview;
    private FloatingActionButton fabFav;
    private CoordinatorLayout coordinatorLayout;

    private String type;
    private String position_item;
    private Boolean isFavorite = false;
    private ContentHelper contentHelper;
    private Content content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        type = getIntent().getStringExtra(TYPE);
        position_item = getIntent().getStringExtra(EXTRA_POSITION);

        //content yang berasal dari content provider
        Uri uri = getIntent().getData();
        if (uri != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    content = new Content(cursor);
                }
                cursor.close();
            }
        }

        //content yang berasal dari parcelable
        content = getIntent().getParcelableExtra(DATA);

        settingToolbar(content);

        imgBackdrop = findViewById(R.id.img_backdrop);
        imgPoster = findViewById(R.id.img_poster_detail);
        tvTitle = findViewById(R.id.title_detail);
        tvPopularity = findViewById(R.id.popularity_detail);
        tvVoteCount = findViewById(R.id.vote_count_detail);
        tvVoteAvg = findViewById(R.id.vote_avg_detail);
        tvOverview = findViewById(R.id.overview_detail);
        fabFav = findViewById(R.id.fab_fav);
        coordinatorLayout = findViewById(R.id.main_detail);
        fabFav.setOnClickListener(this);

        bindContent(content);
        contentHelper = ContentHelper.getInstance(getApplicationContext());
        contentHelper.open();

        favoriteState();
        setFavorite();
    }

    //setting for toolbar
    private void settingToolbar(Content content) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        if (type.equals("movie")) {
            collapsingToolbarLayout.setTitle(content.getTitleFilm());
        } else {
            collapsingToolbarLayout.setTitle(content.getTitleTv());
        }
        collapsingToolbarLayout.setCollapsedTitleTextColor(
                ContextCompat.getColor(this, android.R.color.black));
        collapsingToolbarLayout.setExpandedTitleColor(
                ContextCompat.getColor(this, android.R.color.transparent));
    }

    //bind content
    private void bindContent(Content content) {
        Picasso.get().load(IMG_URL + content.getBackdropPath()).fit().into(imgBackdrop);
        Picasso.get().load(IMG_URL + content.getPosterPath()).fit().into(imgPoster);

        if (type.equals("movie")) {
            tvTitle.setText(content.getTitleFilm());
        } else {
            tvTitle.setText(content.getTitleTv());
        }
        Log.d("check_data", content.getPopularity() + " " + content.getVoteCount() + " " + content.getVoteAverage());
        tvPopularity.setText(content.getPopularity());
        tvVoteCount.setText(content.getVoteCount());
        tvVoteAvg.setText(content.getVoteAverage());
        tvOverview.setText(content.getOverview());
    }

    //menandai apakah konten sudah ada di database atau belum
    private void favoriteState() {
        if (type.equals("movie")) {
            boolean stateMovie = contentHelper.hasContent(TABLE_MOVIE, content.getId());
            if (stateMovie) isFavorite = true;
        } else {
            boolean stateTv = contentHelper.hasContent(TABLE_TV, content.getId());
            if (stateTv) isFavorite = true;
        }
    }

    //menandai ada atau tidak kontent di database dengan perubahan image bintang
    private void setFavorite() {
        if (isFavorite) {
            fabFav.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_added_to_favorites));
        } else {
            fabFav.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_add_to_favorites));
        }
    }

    //mengahapus kontent dari database
    private void removeFromFavorite() {
        if (type.equals("movie")) {
            contentHelper.deleteContent(content.getId(), TABLE_MOVIE);
        } else {
            contentHelper.deleteContent(content.getId(), TABLE_TV);
        }
        snackBar("data dihapus dari favorite");
    }

    //menambahkan kontent dari database
    private void addToFavorite() {
        if (type.equals("movie")) {
            contentHelper.insertContent(content, TABLE_MOVIE);
        } else {
            contentHelper.insertContent(content, TABLE_TV);
        }
        snackBar("data ditambahkan ke favorite");
    }

    //method mengirim result ke fragment onActivityResult
    private void deleteItem() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_POSITION, position_item);
        setResult(RESULT_DELETE, intent);
    }

    private void snackBar(String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    private void notifyToWidget() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(getApplicationContext(), FavoriteWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.stack_view);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab_fav) {

            notifyToWidget(); //notify to widget

            if (isFavorite) {  //first false
                removeFromFavorite();
            } else {
                addToFavorite();
            }
            isFavorite = !isFavorite; //first (false to true)
            setFavorite();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        contentHelper.close();
    }

    @Override
    public void onBackPressed() {
        if (!isFavorite) {
            deleteItem();
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TYPE, type);
        setResult(RESULT_TYPE, intent);
        super.onBackPressed();
    }
}