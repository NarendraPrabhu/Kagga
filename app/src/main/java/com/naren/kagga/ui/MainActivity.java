package com.naren.kagga.ui;

import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.naren.kagga.R;
import com.naren.kagga.TextUtils;
import com.naren.kagga.data.Kagga;
import com.naren.kagga.db.DatabaseHelper;

public class MainActivity extends ListActivity {

    private final KaggaAdapter mAdapter = new KaggaAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setListAdapter(mAdapter);
        Cursor cursor = mAdapter.getCursor("");
        mAdapter.swapCursor(cursor);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(mAdapter);
        searchView.setQueryHint(getString(R.string.search_hint_kagga));
        return true;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Kagga k = (Kagga)v.getTag();
        if(k != null){
            Bundle b = new Bundle();
            b.putBoolean(KaggaFragment.EXTRA_IS_KAGGA, true);
            b.putParcelable(KaggaFragment.EXTRA_KAGGA, k);
            KaggaFragment kf = new KaggaFragment();
            kf.setArguments(b);
            kf.show(getFragmentManager(), "Kagga");
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_info){
            Bundle b = new Bundle();
            b.putBoolean(KaggaFragment.EXTRA_IS_KAGGA, false);
            KaggaFragment kf = new KaggaFragment();
            kf.setArguments(b);
            kf.show(getFragmentManager(), "Info");
        }
        return super.onOptionsItemSelected(item);
    }

    private class KaggaAdapter extends CursorAdapter implements SearchView.OnQueryTextListener, Filterable {

        public KaggaAdapter() {
            super(MainActivity.this, null, false);
        }

        private Cursor getCursor(String query){
            return DatabaseHelper.searchKaggas(MainActivity.this, query);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            View v = getLayoutInflater().inflate(R.layout.item_kagga, null);
            return v;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            String kagga = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_KAGGA));
            String dividedWords = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DIVIDED_WORDS));
            String wordMeanings = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_WORD_MEANINGS));
            String explanation = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EXPLANATION));
            int isFavorite = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_FAVORITE));
            Kagga k = new Kagga(kagga, dividedWords, wordMeanings, explanation, isFavorite == 1);
            ((TextView)view.findViewById(R.id.kagga)).setText(k.getKagga());
            view.setTag(k);
        }

        @Override
        public boolean onQueryTextSubmit(String s) {
            return true;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            if(android.text.TextUtils.isEmpty(s)){
                Cursor cursor = DatabaseHelper.searchKaggas(MainActivity.this, "");
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
                    Cursor cursor = DatabaseHelper.searchKaggas(MainActivity.this, constraint.toString());
                    swapCursor(cursor);
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    return new FilterResults();
                }
            };
        }
    }

    private Spanned getInfoProfile(){
        String text = getString(R.string.info_profile);
        return TextUtils.getText(text);
    }
    
}
