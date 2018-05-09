package com.naren.kagga.ui;

import android.app.DialogFragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.naren.kagga.R;
import com.naren.kagga.TextUtils;
import com.naren.kagga.data.Kagga;
import com.naren.kagga.db.DatabaseHelper;

import java.util.List;

/**
 * Created by narensmac on 26/02/18.
 */

public class KaggaFragment extends DialogFragment implements CompoundButton.OnCheckedChangeListener{

    public interface OnKaggaViewCompleteListener{
        void done(Kagga kagga);
    }

    public static final String EXTRA_KAGGA = "kagga";
    public static final String EXTRA_IS_KAGGA = "is_kagga";
    private Kagga mKagga = null;
    private boolean isKagga = false;
    private OnKaggaViewCompleteListener viewCompleteListener = null;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        mKagga = args.getParcelable(EXTRA_KAGGA);
        isKagga = args.getBoolean(EXTRA_IS_KAGGA);
    }

    public void setViewCompleteListener(OnKaggaViewCompleteListener viewCompleteListener) {
        this.viewCompleteListener = viewCompleteListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_kagga, null, false);
        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        DatabaseHelper.setFavorite(getActivity(), mKagga, b);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getView() != null){
            String title = isKagga ? mKagga.getType() : getString(R.string.title_info);
            getView().findViewById(R.id.detail_tools).setVisibility(isKagga ? View.VISIBLE : View.GONE);
            if(isKagga) {
                getView().findViewById(R.id.detail_tools_copy).setOnClickListener(onClickListener);
                getView().findViewById(R.id.detail_tools_share).setOnClickListener(onClickListener);
                ((CheckBox)getView().findViewById(R.id.detail_tools_favorite)).setOnCheckedChangeListener(this);
                ((CheckBox)getView().findViewById(R.id.detail_tools_favorite)).setChecked(mKagga.isFavorite());
            }
            if(!android.text.TextUtils.isEmpty(title)) {
                getDialog().setTitle(title);
            }
            setStyle(DialogFragment.STYLE_NORMAL, 0);
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            setDetailsText();
        }
    }

    private void setDetailsText(){
        if(isKagga) {
            String value = getString(R.string.detail_text);
            value = String.format(value, mKagga.getKagga(), mKagga.getDividedWords(), mKagga.getWordMeanings(), mKagga.getExplanation());
            ((TextView) getView().findViewById(R.id.detail_kagga)).setText(TextUtils.getText(value));
        }else{
            String value = getString(R.string.info_profile);
            ((TextView) getView().findViewById(R.id.detail_kagga)).setText(TextUtils.getText(value));
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String data = mKagga.getKagga();
            switch (view.getId()){
                case R.id.detail_tools_copy:
                    ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Kagga", data);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getActivity(), R.string.warning_kagga_copied, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.detail_tools_share:
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/*");
                    intent.putExtra(Intent.EXTRA_TEXT, data);
                    PackageManager pm = getActivity().getPackageManager();
                    if(pm != null){
                        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
                        if(infos == null){
                            Toast.makeText(getActivity(), R.string.warning_no_app, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        startActivity(Intent.createChooser(intent, getString(R.string.select_app)));
                    }
                    break;
            }
        }
    };

    @Override
    public void onDismiss(DialogInterface dialog) {
        if(viewCompleteListener != null){
            viewCompleteListener.done(mKagga);
        }
        super.onDismiss(dialog);
    }
}
