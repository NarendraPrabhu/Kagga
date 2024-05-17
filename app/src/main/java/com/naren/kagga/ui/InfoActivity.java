package com.naren.kagga.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.naren.kagga.R;
import com.naren.kagga.data.Kagga;
import com.naren.kagga.databinding.DetailsBinding;

public class InfoActivity extends BaseActivity{

    private DetailsBinding binding = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
