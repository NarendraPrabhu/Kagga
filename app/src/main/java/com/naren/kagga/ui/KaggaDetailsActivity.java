package com.naren.kagga.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.naren.kagga.R;
import com.naren.kagga.data.Kagga;
import com.naren.kagga.databinding.DetailKaggaBinding;
import com.naren.kagga.db.DatabaseHelper;
import com.naren.kagga.ui.listeners.KaggaDetailEventListener;

import java.util.List;

/**
 * Created by narensmac on 26/02/18.
 */

public class KaggaDetailsActivity extends Activity implements KaggaDetailEventListener{

    public static final String EXTRA_KAGGA = "kagga";
    private DetailKaggaBinding binding = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
        Kagga kagga = getIntent().getParcelableExtra(EXTRA_KAGGA);
        binding = DetailKaggaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.setKagga(kagga);
        binding.setEvents(this);
        String title = (kagga != null) ? kagga.getType() : getString(R.string.title_info);
        if(!android.text.TextUtils.isEmpty(title)) {
            setTitle(title);
        }
    }

    @Override
    public void check(Kagga kagga, boolean b) {
        DatabaseHelper.setFavorite(this, kagga, b);
    }

    @Override
    public void copy(Kagga kagga) {
        ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Kagga", kagga.toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, R.string.warning_kagga_copied, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void share(Kagga kagga) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/*");
        intent.putExtra(Intent.EXTRA_TEXT, kagga.toString());
        PackageManager pm = getPackageManager();
        if(pm != null){
            List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
            if(infos == null){
                Toast.makeText(this, R.string.warning_no_app, Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(Intent.createChooser(intent, getString(R.string.select_app)));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

}
