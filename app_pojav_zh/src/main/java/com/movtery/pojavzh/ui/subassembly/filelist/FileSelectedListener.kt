package com.movtery.pojavzh.ui.subassembly.filelist;

import java.io.File;

public abstract class FileSelectedListener {
    public abstract void onFileSelected(File file, String path);

    public abstract void onItemLongClick(File file, String path);
}
