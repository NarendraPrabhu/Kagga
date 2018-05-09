package com.naren.kagga.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.CursorAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SearchView;
import android.widget.TextView;

import com.naren.kagga.R;
import com.naren.kagga.data.Kagga;
import com.naren.kagga.db.DatabaseHelper;

public class KaggaAdapter extends CursorAdapter implements SearchView.OnQueryTextListener, Filterable {

    private Activity mActivity = null;
    private boolean isMankutimmaSelected = true;
    private boolean favorite = false;

    public KaggaAdapter(Activity activity) {
        super(activity, null, false);
        this.mActivity = activity;
    }

    public void setMankutimmaSelected(boolean mankutimmaSelected) {
        isMankutimmaSelected = mankutimmaSelected;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public Cursor getCursor(String query){
        String value = mActivity.getString(isMankutimmaSelected ? R.string.title_mankutimmana_kagga : R.string.title_marulamuniyana_kagga);
        return DatabaseHelper.searchKaggas(mActivity, favorite, query, value);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View v = mActivity.getLayoutInflater().inflate(R.layout.item_kagga, null);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String kagga = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_KAGGA));
        String dividedWords = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DIVIDED_WORDS));
        String wordMeanings = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_WORD_MEANINGS));
        String explanation = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EXPLANATION));
        int isFavorite = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_FAVORITE));
        String type = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TYPE));
        Kagga k = new Kagga(kagga, dividedWords, wordMeanings, explanation, isFavorite == 1, type);
        ((TextView)view.findViewById(R.id.kagga)).setText(k.getKagga());
        ((TextView)view.findViewById(R.id.kagga_type)).setText(k.getType());
        ((CheckedTextView)view.findViewById(R.id.kagga_favorite)).setChecked(isFavorite == 1);
        view.setTag(k);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if(android.text.TextUtils.isEmpty(s)){
            Cursor cursor = getCursor("");
            swapCursor(cursor);
        }else {
            getFilter().filter(s);
        }
        return true;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                Cursor cursor = getCursor(constraint.toString());
                swapCursor(cursor);
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                return new FilterResults();
            }
        };
    }
}