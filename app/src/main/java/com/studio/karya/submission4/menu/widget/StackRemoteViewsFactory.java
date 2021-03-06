package com.studio.karya.submission4.menu.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.studio.karya.submission4.R;
import com.studio.karya.submission4.db.DML.ContentHelper;
import com.studio.karya.submission4.model.Content;
import com.studio.karya.submission4.utils.ParcelableUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.studio.karya.submission4.BuildConfig.IMG_URL;
import static com.studio.karya.submission4.db.DDL.DatabaseContract.TABLE_MOVIE;

public class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private final ArrayList<Content> listContent = new ArrayList<>();
    private final Context mContext;
    private ContentHelper contentHelper;

    StackRemoteViewsFactory(Context context) {
        this.mContext = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

        Log.d("datadat", "datachange");

        contentHelper = ContentHelper.getInstance(mContext);
        listContent.clear();
        contentHelper.open();
        listContent.addAll(contentHelper.getAllContent(TABLE_MOVIE));
    }

    @Override
    public void onDestroy() {
        if (contentHelper != null) {
            contentHelper.close();
        }
    }

    @Override
    public int getCount() {
        if (listContent.size() > 0) {
            return listContent.size();
        } else {
            return 0;
        }
    }

    @Override
    public RemoteViews getViewAt(int position) {

        Log.d("data_content", listContent.get(position).getTitleFilm() + " tes");

        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
        if (!listContent.isEmpty()) {
            rv.setImageViewBitmap(R.id.imageView, getBitmapFromURL(IMG_URL + listContent.get(position).getBackdropPath()));
            rv.setTextViewText(R.id.textView, listContent.get(position).getTitleFilm());

            byte[] parceAble = ParcelableUtil.marshall(listContent.get(position));

            Bundle extras = new Bundle();
            extras.putString(FavoriteWidget.TYPE_DATA, "movie");
            extras.putInt(FavoriteWidget.EXTRA_ITEM, position);
            extras.putByteArray(FavoriteWidget.EXTRA_DATA, parceAble);
       /* extras.putParcelable(FavoriteWidget.EXTRA_DATA, listContent.get(position));
        extras.putString(FavoriteWidget.TYPE_DATA, "movie");*/

            Intent fillIntent = new Intent();
            fillIntent.putExtras(extras);
        /*fillIntent.putExtra(FavoriteWidget.EXTRA_DATA, listContent.get(position));
        fillIntent.putExtra(FavoriteWidget.TYPE_DATA, "movie");*/

            rv.setOnClickFillInIntent(R.id.imageView, fillIntent);
        }
        return rv;
    }

    private static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
