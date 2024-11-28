package com.example.yogajava;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private ListView listViewCourses;
    private YogaClassAdapter adapter;
    private List<YogaJava> courses;
    private Spinner spinnerDayOfWeekSearch;
    private DatabaseReference yogaClassesRef; // Tham chiếu đến node "yoga_classes" trên Firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        listViewCourses = findViewById(R.id.list_view_courses);
        Button buttonAddCourse = findViewById(R.id.button_add_course);
        SearchView searchView = findViewById(R.id.search_view);
        spinnerDayOfWeekSearch = findViewById(R.id.spinner_day_of_week_search);


        FirebaseDatabase database = FirebaseDatabase.getInstance("https://yogajava-5b04a-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference("path/to/your/data");


        String[] daysOfWeek = {"---", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};
        ArrayAdapter<String> adapterDayOfWeek = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, daysOfWeek);
        adapterDayOfWeek.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDayOfWeekSearch.setAdapter(adapterDayOfWeek);

        buttonAddCourse.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
        });

        listViewCourses.setOnItemClickListener((parent, view, position, id) -> {
            YogaJava selectedCourse = (YogaJava) parent.getItemAtPosition(position);
            if (selectedCourse != null) {

                String courseInfo = "DATE: " + selectedCourse.dayOfWeek + "\n" +
                        "TIME: " + selectedCourse.time + "\n" +
                        "COURSE: " + selectedCourse.classType + "\n" +
                        "DESCRIPTION: " + selectedCourse.description;
                Toast.makeText(HomeActivity.this, courseInfo, Toast.LENGTH_LONG).show();
            }
        });


        listViewCourses.setOnItemLongClickListener((parent, view, position, id) -> {
            YogaJava selectedCourse = (YogaJava) parent.getItemAtPosition(position);
            if (selectedCourse != null) {
                showPopupMenu(view, selectedCourse, position);
            }
            return true;
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    updateCourseList();
                } else {
                    searchYogaClasses(newText);
                }
                return true;
            }
        });


        spinnerDayOfWeekSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedDayOfWeek = daysOfWeek[position];
                if (selectedDayOfWeek.equals("---")) {
                    updateCourseList();
                } else {
                    searchYogaClassesByDayOfWeek(selectedDayOfWeek);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

        updateCourseList();
        uploadDataToFirebase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCourseList();
    }

    private void updateCourseList() {
        YogaDbHelper dbHelper = new YogaDbHelper(this);
        courses = dbHelper.getAllYogaClasses();
        adapter = new YogaClassAdapter(this, courses);
        listViewCourses.setAdapter(adapter);
    }

    private void showPopupMenu(View view, YogaJava selectedCourse, int position) {
        PopupMenu popupMenu = new PopupMenu(HomeActivity.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu()); // Inflate menu từ layout

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.action_edit) {
                // Xử lý sửa khóa học
                Intent editIntent = new Intent(HomeActivity.this, MainActivity.class);
                editIntent.putExtra("Edit_Yoga_Course", selectedCourse);
                startActivity(editIntent);
                return true;
            } else if (itemId == R.id.action_delete) {
                // Xử lý xóa khóa học
                YogaDbHelper dbHelper = new YogaDbHelper(HomeActivity.this);
                if (dbHelper.deleteYogaClass(selectedCourse.getId())) {
                    courses.remove(position); // Xóa khỏi danh sách
                    adapter.notifyDataSetChanged(); // Cập nhật ListView
                    Toast.makeText(HomeActivity.this, "DELETE SUCCESS", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HomeActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                }
                return true;
            } else if (itemId == R.id.action_add_detail) {
                // Xử lý thêm buổi học
                showAddClassInstanceDialog(selectedCourse);
                return true;
            } else {
                return false;
            }
        });

        popupMenu.show();
    }

    private void showAddClassInstanceDialog(YogaJava yogaClass) {
        YogaDbHelper dbHelper = new YogaDbHelper(this);
        List<ClassInstance> classInstances = dbHelper.getClassInstancesForCourse(yogaClass.getId());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("LIST YOGA COURSE");

        ArrayAdapter<ClassInstance> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, classInstances);
        builder.setAdapter(adapter, null);

        builder.setPositiveButton("ADD MORE COURSE", (dialog, which) -> {

        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void searchYogaClasses(String keyword) {
        YogaDbHelper dbHelper = new YogaDbHelper(this);
        List<YogaJava> searchResults = dbHelper.searchYogaClassesByDayOfWeek(keyword); // Tìm kiếm theo ngày trong tuần

        // Cập nhật ListView với kết quả tìm kiếm
        adapter = new YogaClassAdapter(this, searchResults);
        listViewCourses.setAdapter(adapter);
    }

    private void searchYogaClassesByDayOfWeek(String dayOfWeek) {
        YogaDbHelper dbHelper = new YogaDbHelper(this);
        List<YogaJava> searchResults = dbHelper.searchYogaClassesByDayOfWeek(dayOfWeek);

        // Cập nhật ListView với kết quả tìm kiếm
        adapter = new YogaClassAdapter(this, searchResults);
        listViewCourses.setAdapter(adapter);
    }

    private void uploadDataToFirebase() {
        YogaDbHelper dbHelper = new YogaDbHelper(this);
        List<YogaJava> yogaCourses = dbHelper.getAllYogaClasses();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("yoga_classes");

        databaseReference.setValue(yogaCourses, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    // Xử lý lỗi upload
                    Toast.makeText(HomeActivity.this, "ERROR When upload date: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    // Upload thành công
                    Toast.makeText(HomeActivity.this, "Upload Success!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}