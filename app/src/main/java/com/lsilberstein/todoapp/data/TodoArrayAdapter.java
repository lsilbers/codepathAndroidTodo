package com.lsilberstein.todoapp.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lsilberstein.todoapp.MainActivity;
import com.lsilberstein.todoapp.R;

import java.util.ArrayList;

/**
 * Created by lsilberstein on 9/22/15.
 */
public class TodoArrayAdapter extends ArrayAdapter<TodoItem>{

    public TodoArrayAdapter(Context context, ArrayList<TodoItem> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = getContext();
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_todo, parent, false);
        }
        TodoItem item = getItem(position);

        TextView tvPriority = (TextView) convertView.findViewById(R.id.tvPriority);
        TextView tvBody = (TextView) convertView.findViewById(R.id.tvBody);
        TextView tvDue = (TextView) convertView.findViewById(R.id.tvDue);

        String priority;
        int style = -1;
        switch (item.priority) {
            case 0:
                priority = context.getString(R.string.p1);
                style = R.style.priority1;
                break;
            case 1:
                priority = context.getString(R.string.p2);
                style = R.style.priority2;
                break;
            case 2:
                priority = context.getString(R.string.p3);
                style = R.style.priority3;
                break;
            default:
                priority = context.getString(R.string.p4);
                style = R.style.priority4;
                break;
        }
        tvPriority.setText(priority);
        tvPriority.setTextAppearance(context, style);
        tvBody.setText(item.shortName);
        tvDue.setText(MainActivity.formater.format(item.dueDate.getTime()));

        return convertView;
    }
}
