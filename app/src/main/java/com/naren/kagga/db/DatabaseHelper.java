package com.naren.kagga.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;

import com.naren.kagga.R;
import com.naren.kagga.data.Kagga;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by narensmac on 15/03/18.
 */

public class DatabaseHelper {

    private static final int version = 1;
    private static final String DB_NAME = "1.vc";
    private static final String TABLE_KAGGA = "Kagga";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_KAGGA = "kagga";
    public static final String COLUMN_DIVIDED_WORDS = "divided_words";
    public static final String COLUMN_WORD_MEANINGS = "meanings";
    public static final String COLUMN_EXPLANATION = "explanation";
    public static final String COLUMN_FAVORITE = "favorite";
    public static final String COLUMN_TYPE = "type";

    public static boolean copyFile(Context context){
        boolean value = false;

        File file = new File(context.getFilesDir().getAbsolutePath(), DB_NAME);
        if(isVersionDifferent(context)){
            if(file.exists()){
                file.delete();
            }
        }
        if(file.exists() && file.length() > 0){
            value = true;
        }else {
            try {
                InputStream is = context.getAssets().open("1.vc");
                FileOutputStream fos = context.openFileOutput(DB_NAME, Context.MODE_APPEND);
                value = (IOUtils.copy(is, fos) > 0);
                IOUtils.closeQuietly(is);
                IOUtils.closeQuietly(fos);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return value;
    }

    private static boolean isVersionDifferent(Context context){
        boolean value = false;
        SharedPreferences prefs = context.getSharedPreferences("Kagga", Context.MODE_PRIVATE);
        int version = prefs.getInt("version", 0);
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            value = (version != info.versionCode);
            prefs.edit().putInt("version", info.versionCode).commit();
        }catch (PackageManager.NameNotFoundException nnfe){
            nnfe.printStackTrace();
        }
        return value;
    }

    private static SQLiteDatabase getDB(Context context){
        File file = new File(context.getFilesDir(), DB_NAME);
        SQLiteDatabase db = null;
        try{
            db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, 0);
        }catch (SQLiteException se){
            se.printStackTrace();
        }
        return db;
    }

    public static Cursor searchKaggas(Context context, boolean favorite, String value, String... types){
        String selection = null;
        String type = COLUMN_TYPE+" in (%s)";
        String typeValue = "";
        if(types != null && types.length > 0){
            typeValue = "'"+types[0]+"'";
            if(types.length > 1){
                typeValue += ", '"+types[1]+"'";
            }
        }

        if(TextUtils.isEmpty(typeValue)){
            typeValue = String.format("'%s', '%s'", context.getString(R.string.title_mankutimmana_kagga), context.getString(R.string.title_marulamuniyana_kagga));
        }
        type = String.format(type, typeValue);

        if(!TextUtils.isEmpty(value)){
            selection = COLUMN_KAGGA+" LIKE '%"+value+"%' AND "+type;
        }else{
            selection = type;
        }

        if(favorite) {
            if (TextUtils.isEmpty(selection)) {
                selection = "";
            } else {
                selection += " AND ";
            }
            selection += COLUMN_FAVORITE+" = "+(favorite ? 1 : 0);
        }

        SQLiteDatabase db = getDB(context);
        if(db == null){
            return null;
        }
        return db.query(TABLE_KAGGA, new String[]{COLUMN_ID, COLUMN_KAGGA, COLUMN_DIVIDED_WORDS, COLUMN_WORD_MEANINGS, COLUMN_EXPLANATION, COLUMN_FAVORITE, COLUMN_TYPE}, selection, null, null, null, COLUMN_TYPE+", "+COLUMN_KAGGA);
    }

    public static boolean setFavorite(Context context, Kagga kagga, boolean favorite){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_FAVORITE, favorite ? 1 : 0);
        return (getDB(context).update(TABLE_KAGGA, cv, COLUMN_KAGGA+" LIKE '%"+kagga.getKagga()+"%'", null) > 0);
    }

}
