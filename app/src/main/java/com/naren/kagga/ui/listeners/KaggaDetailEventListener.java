package com.naren.kagga.ui.listeners;

import com.naren.kagga.data.Kagga;

public interface KaggaDetailEventListener {
    void check(Kagga kagga, boolean b);
    void copy(Kagga kagga);
    void share(Kagga kagga);
}
