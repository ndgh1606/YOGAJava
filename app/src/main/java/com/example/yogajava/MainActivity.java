package com.example.yogajava;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Spinner spinnerDayOfWeek;
    private Spinner spinnerTime;
    private EditText editTextCapacity;
    private EditText editTextDuration;
    private EditText editTextPrice;
    private Spinner spinnerClassType;
    private EditText editTextDescription;
    private boolean isEditing = false;
    private YogaJava yogaClassToEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinnerDayOfWeek = findViewById(R.id.spinner_day_of_week);
        spinnerTime = findViewById(R.id.spinner_time);
        editTextCapacity = findViewById(R.id.edit_text_capacity);
        editTextDuration = findViewById(R.id.edit_text_duration);
        editTextPrice = findViewById(R.id.edit_text_price);
        spinnerClassType = findViewById(R.id.spinner_class_type);
        editTextDescription = findViewById(R.id.edit_text_description);
        Button buttonSave = findViewById(R.id.button_save);
        Button buttonBack = findViewById(R.id.button_back);
        String[] daysOfWeek = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};
        ArrayAdapter<String> adapterDayOfWeek = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, daysOfWeek);
        adapterDayOfWeek.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDayOfWeek.setAdapter(adapterDayOfWeek);
        List<String> timeOptions = new ArrayList<>();
        for (int hour = 7; hour <= 20; hour++) {
            timeOptions.add(String.format("%02d:00", hour));
        }
        ArrayAdapter<String> adapterTime = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeOptions);
        adapterTime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTime.setAdapter(adapterTime);
        String[] classTypes = {
                "Hatha Yoga",
                "Vinyasa Yoga",
                "Ashtanga Yoga",
                "Iyengar Yoga",
                "Kundalini Yoga",
                "Bikram Yoga",
                "Yin Yoga",
                "Restorative Yoga",
                "Prenatal Yoga"
        };
        ArrayAdapter<String> adapterClassType = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classTypes);
        adapterClassType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClassType.setAdapter(adapterClassType);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("Edit_Yoga_Course")) {
            isEditing = true;
            yogaClassToEdit = (YogaJava) intent.getSerializableExtra("Edit_Yoga_Course");
            for (int i = 0; i < daysOfWeek.length; i++) {
                if (daysOfWeek[i].equals(yogaClassToEdit.dayOfWeek)) {
                    spinnerDayOfWeek.setSelection(i);
                    break;
                }
            }
            for (int i = 0; i < timeOptions.size(); i++) {
                if (timeOptions.get(i).equals(yogaClassToEdit.time)) {
                    spinnerTime.setSelection(i);
                    break;
                }
            }
            editTextCapacity.setText(String.valueOf(yogaClassToEdit.capacity));
            editTextDuration.setText(String.valueOf(yogaClassToEdit.duration));
            editTextPrice.setText(String.valueOf(yogaClassToEdit.price));
            for (int i = 0; i < classTypes.length; i++) {
                if (classTypes[i].equals(yogaClassToEdit.classType)) {
                    spinnerClassType.setSelection(i);
                    break;
                }
            }
            editTextDescription.setText(yogaClassToEdit.description);
        }
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    String dayOfWeek = spinnerDayOfWeek.getSelectedItem().toString();
                    String time = spinnerTime.getSelectedItem().toString();
                    int capacity = Integer.parseInt(editTextCapacity.getText().toString());
                    int duration = Integer.parseInt(editTextDuration.getText().toString());
                    double price = Double.parseDouble(editTextPrice.getText().toString());
                    String classType = spinnerClassType.getSelectedItem().toString();
                    String description = editTextDescription.getText().toString();
                    YogaJava yogaClass = new YogaJava(dayOfWeek, time, capacity, duration, price, classType, description);
                    if (isEditing) {
                        yogaClass.id = yogaClassToEdit.id;
                        updateYogaClassInDatabase(yogaClass);
                    } else {

                        saveYogaClassToDatabase(yogaClass);
                    }
                    uploadDataToFirebase();
                }
            }
        });
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }
    private boolean validateInput() {
        if (editTextCapacity.getText().toString().isEmpty() ||
                editTextDuration.getText().toString().isEmpty() ||
                editTextPrice.getText().toString().isEmpty()) {
            Toast.makeText(this, "PLEASE ENTER FULL CONTENT", Toast.LENGTH_SHORT).show();
            return false;
        }
        String description = editTextDescription.getText().toString();
        if (!description.matches("[a-zA-Z\\s]*")) {
            Toast.makeText(this, "THERE IS ONLY ACCEPT BY LETTER", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void saveYogaClassToDatabase(YogaJava yogaClass) {
        YogaDbHelper dbHelper = new YogaDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = getValues(yogaClass);
        long newRowId = db.insert(YogaDbHelper.TABLE_YOGA_CLASSES, null, values);
        if (newRowId != -1) {
            Toast.makeText(this, "SUCCESS", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
        }
    }
    private static @NonNull ContentValues getValues(YogaJava yogaClass) {
        ContentValues values = new ContentValues();
        values.put(YogaDbHelper.COLUMN_DAY_OF_WEEK, yogaClass.dayOfWeek);
        values.put(YogaDbHelper.COLUMN_TIME, yogaClass.time);
        values.put(YogaDbHelper.COLUMN_CAPACITY, yogaClass.capacity);
        values.put(YogaDbHelper.COLUMN_DURATION, yogaClass.duration);
        values.put(YogaDbHelper.COLUMN_PRICE, yogaClass.price);
        values.put(YogaDbHelper.COLUMN_CLASS_TYPE, yogaClass.classType);
        values.put(YogaDbHelper.COLUMN_DESCRIPTION, yogaClass.description);
        return values;
    }
    private void updateYogaClassInDatabase(YogaJava yogaClass) {
        YogaDbHelper dbHelper = new YogaDbHelper(this);
        boolean success = dbHelper.updateYogaClass(yogaClass);
        if (success) {
            Toast.makeText(this, "SUCCESS", Toast.LENGTH_SHORT).show();
            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "ERROR");
        }
    }
    private void uploadDataToFirebase() {
        YogaDbHelper dbHelper = new YogaDbHelper(this);
        List<YogaJava> yogaCourses = dbHelper.getAllYogaClasses();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("yoga_classes");
        databaseReference.setValue(yogaCourses, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast.makeText(MainActivity.this, "ERROR: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Upload SUCCESS!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}