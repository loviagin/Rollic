package com.loviagin.rollic.helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "UserDB";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "Users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_AVATAR_URL = "avatarUrl";
    public static final String COLUMN_UID = "uid";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_UID + " TEXT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_TIME + " INTEGER,"
                + COLUMN_AVATAR_URL + " TEXT,"
                + COLUMN_MESSAGE + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean isUserExists(String name) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();

            String[] projection = {COLUMN_UID};

            String selection = COLUMN_UID + " = ?";

            String[] selectionArgs = {name};

            cursor = db.query(
                    TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            boolean exists = (cursor.getCount() > 0);

            return exists;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}