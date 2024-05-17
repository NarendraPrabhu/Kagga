package com.naren.kagga.ui;

import static com.naren.kagga.db.DatabaseHelper.*;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.naren.kagga.R;
import com.naren.kagga.data.Kagga;
import com.naren.kagga.databinding.DetailLayoutBinding;
import com.naren.kagga.databinding.DetailsBinding;
import com.naren.kagga.db.DatabaseHelper;
import com.naren.kagga.ui.listeners.KaggaDetailEventListener;

import java.util.List;

/**
 * Created by narensmac on 26/02/18.
 */

public class KaggaDetailsActivity extends BaseActivity implements KaggaDetailEventListener, ViewPager.OnPageChangeListener{

    public static final String EXTRA_PARAM_IS_FAVORITE = "IS_FAVORITE";
    public static final String EXTRA_PARAM_IS_MANKUTIMMANA_KAGGA = "IS_MANKUTIMMA";
    public static final String EXTRA_PARAM_QUERY = "QUERY";
    public static final String EXTRA_PARAM_CURSOR_POSITION = "CURSOR_POSITION";

    private DetailLayoutBinding binding = null;
    private Cursor mCursor = null;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DetailLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final int currentPosition = getIntent().getIntExtra(EXTRA_PARAM_CURSOR_POSITION, 0);
        final boolean isFavorite = getIntent().getBooleanExtra(EXTRA_PARAM_IS_FAVORITE, false);
        final boolean isMankutimmanaKagga = getIntent().getBooleanExtra(EXTRA_PARAM_IS_MANKUTIMMANA_KAGGA, false);
        final String query = getIntent().getStringExtra(EXTRA_PARAM_QUERY);
        String value = getString(isMankutimmanaKagga ? R.string.title_mankutimmana_kagga : R.string.title_marulamuniyana_kagga);

        setTitle(value);

        KaggaDetailsAdapter adapter = new KaggaDetailsAdapter();
        binding.setAdapter(adapter);
        binding.setEvents(this);

        final ViewPager pager = findViewById(R.id.kagga_details_pager);
        pager.post(new Runnable() {
            @Override
            public void run() {
                pager.setCurrentItem(currentPosition, true);
            }
        });
        PageIndicatorView indicator = findViewById(R.id.indicator);
        pager.addOnPageChangeListener(indicator);
        pager.addOnPageChangeListener(this);
        mCursor = searchKaggas(this, isFavorite, query, value);
        onPageSelected(currentPosition);
    }

    @Override
    public void check(Kagga kagga, boolean b) {
        if(kagga != null) {
            setFavorite(this, kagga, b);
        }
    }

    @Override
    public void copy(Kagga kagga) {
        if(kagga != null) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Kagga", kagga.toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, R.string.warning_kagga_copied, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void share(Kagga kagga) {
        if(kagga != null) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/*");
            intent.putExtra(Intent.EXTRA_TEXT, kagga.toString());
            PackageManager pm = getPackageManager();
            if (pm != null) {
                List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
                if (infos == null) {
                    Toast.makeText(this, R.string.warning_no_app, Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(Intent.createChooser(intent, getString(R.string.select_app)));
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(mCursor != null) {
            Kagga kagga = get(position);
            binding.setKagga(kagga);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    
    private Kagga get(int position){
        mCursor.moveToPosition(position);
        String kagga = mCursor.getString(mCursor.getColumnIndex(COLUMN_KAGGA));
        String dividedWords = mCursor.getString(mCursor.getColumnIndex(COLUMN_DIVIDED_WORDS));
        String wordMeanings = mCursor.getString(mCursor.getColumnIndex(COLUMN_WORD_MEANINGS));
        String explanation = mCursor.getString(mCursor.getColumnIndex(COLUMN_EXPLANATION));
        int isFavorite = mCursor.getInt(mCursor.getColumnIndex(COLUMN_FAVORITE));
        String type = mCursor.getString(mCursor.getColumnIndex(COLUMN_TYPE));
        Kagga k = new Kagga(kagga, dividedWords, wordMeanings, explanation, isFavorite == 1, type);
        return k;
    }
    
    private class KaggaDetailsAdapter extends PagerAdapter{

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            DetailsBinding detailsBinding = DetailsBinding.inflate(getLayoutInflater());
            Kagga kagga = get(position);
            detailsBinding.setKagga(kagga);
            View v = detailsBinding.getRoot();
            container.addView(v);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        @Override
        public int getCount() {
            return mCursor != null ? mCursor.getCount() : 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
