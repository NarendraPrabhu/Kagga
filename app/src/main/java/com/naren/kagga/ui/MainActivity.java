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
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.naren.kagga.R;
import com.naren.kagga.TextUtils;
import com.naren.kagga.data.Kagga;
import com.naren.kagga.db.DatabaseHelper;
import com.naren.kagga.ui.adapters.KaggaAdapter;

public class MainActivity extends ListActivity implements KaggaFragment.OnKaggaViewCompleteListener{

    private KaggaAdapter mAdapter = null;
    private boolean isMankutimmaSelected = true;
    private SearchView mSearchView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAdapter = new KaggaAdapter(this);
        getListView().addHeaderView(getListViewHeader());
        setListAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private View getListViewHeader(){
        View v = getLayoutInflater().inflate(R.layout.list_header, null);
        ((RadioGroup)v).setOnCheckedChangeListener(onCheckedChangeListener);
        return v;
    }

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            isMankutimmaSelected = (i == R.id.check_mankutimma);
            mAdapter.setMankutimmaSelected(isMankutimmaSelected);
            refresh();
        }
    };

    private void refresh(){
        String search = "";
        if(mSearchView != null && mSearchView.isShown()){
            search = mSearchView.getQuery().toString();
        }
        Cursor cursor = mAdapter.getCursor(search);
        mAdapter.swapCursor(cursor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        mSearchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        mSearchView.setOnQueryTextListener(mAdapter);
        mSearchView.setQueryHint(getString(R.string.search_hint_kagga));
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
            kf.setViewCompleteListener(this);
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
        if(item.getItemId() == R.id.menu_favorite){
            boolean isChecked = item.isChecked();
            isChecked = !isChecked;
            item.setChecked(isChecked);
            mAdapter.setFavorite(isChecked);
            refresh();
            item.setIcon(isChecked ? R.drawable.favorite_selected : R.drawable.favorite_normal);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void done(Kagga kagga) {
        refresh();
    }
}
