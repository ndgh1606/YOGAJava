package com.example.yogajava;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class YogaClassAdapter extends ArrayAdapter<YogaJava> {

    public YogaClassAdapter(Context context, List<YogaJava> yogaClasses) {
        super(context, 0, yogaClasses);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        YogaJava yogaClass = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_yoga_class, parent, false);
        }

        TextView tvCourseName = convertView.findViewById(R.id.tv_course_name);
        TextView tvDayOfWeek = convertView.findViewById(R.id.tv_day_of_week);
        TextView tvTime = convertView.findViewById(R.id.tv_time);
        TextView tvPrice = convertView.findViewById(R.id.tv_price);
        TextView tvDescription = convertView.findViewById(R.id.tv_description);


        tvCourseName.setText(yogaClass.classType);
        tvDayOfWeek.setText("On: " + yogaClass.dayOfWeek);
        tvTime.setText("Time: " + yogaClass.time);
        tvPrice.setText("$ " + yogaClass.price);
        tvDescription.setText("Details: " + yogaClass.description);


        return convertView;
    }
}