package yash.com.miniproject.dherya.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class Login_Register_DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "USER_RECORD";
    private static final String TABLE_NAME = "USER_DATA";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "NAME";
    private static final String COL_3 = "EMAIL";
    private static final String COL_4 = "PASSWORD";

    public Login_Register_DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    public Login_Register_DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public Login_Register_DBHelper(@Nullable Context context, @Nullable String name, int version, @NonNull SQLiteDatabase.OpenParams openParams) {
        super(context, name, version, openParams);
    }

    @Override
    public void onCreate(SQLiteDatabase mydb) {
        mydb.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, EMAIL TEXT, PASSWORD TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase mydb, int oldVersion, int newVersion) {
        mydb.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(mydb);
    }

    public boolean registerUser(String name, String email, String password){
        SQLiteDatabase mydb = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, email);
        contentValues.put(COL_4, password);

        long result = mydb.insert(TABLE_NAME, null, contentValues);

        if (result == -1){
            return false;
        }
        else {
            return true;
        }
    }

    public Boolean checkemail(String email, String password){
        SQLiteDatabase mydb = this.getWritableDatabase();

        String [] columns = {COL_1};
        String selection = COL_3 + "=?" + " and " + COL_4 + "=?";
        String [] selectionargs = {email, password};

        Cursor cursor = mydb.query(TABLE_NAME, columns, selection, selectionargs, null, null, null);
        int count = cursor.getCount();
        mydb.close();
        cursor.close();
        if (count > 0){
            return true;
        }
        else {
            return false;
        }
    }

}
