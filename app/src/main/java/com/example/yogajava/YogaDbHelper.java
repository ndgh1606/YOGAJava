package com.example.yogajava;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;


public class YogaDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "YogaDbHelper";
    private static final String DATABASE_NAME = "yoga.db";
    private static final int DATABASE_VERSION = 1;

    public static final String
    TABLE_YOGA_CLASSES = "yoga_classes";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DAY_OF_WEEK = "dayOfWeek";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_CAPACITY = "capacity";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_CLASS_TYPE = "classType";
    public static final String COLUMN_DESCRIPTION = "description";

    public static final String TABLE_CLASS_INSTANCES = "class_instances";
    public static final String COLUMN_INSTANCE_ID = "_id";
    public static final String COLUMN_COURSE_ID = "courseId";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TEACHER = "teacher";
    public static final String COLUMN_COMMENTS = "comments";

    private static final String SQL_CREATE_YOGA_CLASSES =
            "CREATE TABLE " + TABLE_YOGA_CLASSES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_DAY_OF_WEEK + " TEXT, " +
                    COLUMN_TIME + " TEXT, " +
                    COLUMN_CAPACITY + " INTEGER, " +
                    COLUMN_DURATION + " INTEGER, " +
                    COLUMN_PRICE + " REAL, " +
                    COLUMN_CLASS_TYPE + " TEXT, " +
                    COLUMN_DESCRIPTION + " TEXT)";

    private static final String SQL_CREATE_CLASS_INSTANCES =
            "CREATE TABLE " + TABLE_CLASS_INSTANCES + " (" +
                    COLUMN_INSTANCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_COURSE_ID + " INTEGER, " +
                    COLUMN_DATE + " TEXT, " +
                    COLUMN_TEACHER + " TEXT, " +
                    COLUMN_COMMENTS + " TEXT)";

    public YogaDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_YOGA_CLASSES);

        db.execSQL(SQL_CREATE_CLASS_INSTANCES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_YOGA_CLASSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS_INSTANCES);
        onCreate(db);
    }

    public long insertYogaClass(YogaJava yogaClass) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DAY_OF_WEEK, yogaClass.dayOfWeek);
        values.put(COLUMN_TIME, yogaClass.time);
        values.put(COLUMN_CAPACITY, yogaClass.capacity);
        values.put(COLUMN_DURATION, yogaClass.duration);
        values.put(COLUMN_PRICE, yogaClass.price);
        values.put(COLUMN_CLASS_TYPE, yogaClass.classType);
        values.put(COLUMN_DESCRIPTION, yogaClass.description);
        long newRowId = db.insert(TABLE_YOGA_CLASSES, null, values);
        if (newRowId == -1) {
            Log.e(TAG, "Error when update YogaClass");
        }
        return newRowId;
    }

    public List<YogaJava> getAllYogaClasses() {
        List<YogaJava> yogaClassList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_YOGA_CLASSES;

        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.rawQuery(selectQuery, null)) {
            if (cursor.moveToFirst()) {
                do {
                    YogaJava yogaClass = new YogaJava(
                            cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAY_OF_WEEK)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CAPACITY)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURATION)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_TYPE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                    );
                    yogaClassList.add(yogaClass);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error When take data YogaClass", e);
        }
        return yogaClassList;
    }

    public boolean deleteYogaClass(long courseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(TABLE_YOGA_CLASSES, COLUMN_ID + " = ?", new String[]{String.valueOf(courseId)});
        if (deletedRows == 0) {
            Log.e(TAG, "Error when delete YogaClass");
            return false;
        }
        return true;
    }

    public long insertClassInstance(long courseId, String date, String teacher, String comments) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COURSE_ID, courseId);
        values.put(COLUMN_DATE,
                date);
        values.put(COLUMN_TEACHER, teacher);
        values.put(COLUMN_COMMENTS, comments);
        long newRowId = db.insert(TABLE_CLASS_INSTANCES, null, values);
        if (newRowId == -1) {
            Log.e(TAG, "Error when take ClassInstance");
        }
        return newRowId;
    }

    public List<ClassInstance> getClassInstancesForCourse(long courseId) {
        List<ClassInstance> classInstances = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_CLASS_INSTANCES +
                " WHERE " + COLUMN_COURSE_ID + " = " + courseId;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst())
            {
                do {
                    ClassInstance instance = new ClassInstance(
                            cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_INSTANCE_ID)),
                            cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_COURSE_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMMENTS))
                    );
                    classInstances.add(instance);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error when take ClassInstance for YogaClass", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return classInstances;
    }


    public boolean updateYogaClass(YogaJava yogaClass) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DAY_OF_WEEK, yogaClass.dayOfWeek);
        values.put(COLUMN_TIME, yogaClass.time);
        values.put(COLUMN_CAPACITY, yogaClass.capacity);
        values.put(COLUMN_DURATION, yogaClass.duration);
        values.put(COLUMN_PRICE, yogaClass.price);
        values.put(COLUMN_CLASS_TYPE, yogaClass.classType);
        values.put(COLUMN_DESCRIPTION, yogaClass.description);
        int updatedRows = db.update(TABLE_YOGA_CLASSES, values, COLUMN_ID + " = ?", new String[]{String.valueOf(yogaClass.id)});
        if (updatedRows == 0) {
            Log.e(TAG, "Error when update YogaClass");
        }
        return updatedRows > 0;
    }

    public boolean updateClassInstance(ClassInstance classInstance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COURSE_ID, classInstance.courseId);
        values.put(COLUMN_DATE, classInstance.date);
        values.put(COLUMN_TEACHER, classInstance.teacher);
        values.put(COLUMN_COMMENTS, classInstance.comments);
        int updatedRows = db.update(TABLE_CLASS_INSTANCES, values, COLUMN_INSTANCE_ID + " = ?", new String[]{String.valueOf(classInstance.id)});
        if (updatedRows == 0) {
            Log.e(TAG, "Error when update ClassInstance");
        }
        return updatedRows > 0;
    }

    public boolean deleteClassInstance(long instanceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(TABLE_CLASS_INSTANCES, COLUMN_INSTANCE_ID + " = ?", new String[]{String.valueOf(instanceId)});
        return deletedRows > 0;
    }

    public List<YogaJava> searchYogaClassesByDayOfWeek(String dayOfWeek) {
        List<YogaJava> yogaClassList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_YOGA_CLASSES +
                " WHERE " + COLUMN_DAY_OF_WEEK + " = '" + dayOfWeek + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.rawQuery(selectQuery, null)) {
            if (cursor.moveToFirst()) {
                do {
                    YogaJava yogaClass = new YogaJava(
                            cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAY_OF_WEEK)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CAPACITY)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURATION)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_TYPE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                    );
                    yogaClassList.add(yogaClass);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error when search YogaClass", e);
        }

        return yogaClassList;
    }
}