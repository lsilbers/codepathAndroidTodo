package com.lsilberstein.todoapp.data;

import android.database.Cursor;

import com.activeandroid.Cache;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * Created by lsilberstein on 9/22/15.
 */
@Table(name = "TodoItems")
public class TodoItem extends Model {
    @Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public UUID remoteId;

    @Column(name = "ShortName")
    public String shortName;

    @Column(name = "Details")
    public String details;

    @Column(name = "Priority")
    public Integer priority;

    @Column(name = "DueDate")
    public Calendar dueDate = Calendar.getInstance();

    // Make sure to have a default constructor for every ActiveAndroid model
    public TodoItem(){
        super();
    }

    public TodoItem(String shortName, String details, Integer priority, Calendar dueDate){
        super();
        this.remoteId = UUID.randomUUID();
        this.shortName = shortName;
        this.details = details;
        this.dueDate = dueDate;
        this.priority = priority;
    }

    public static TodoItem getItem(UUID remoteId) {
        return new Select()
                .from(TodoItem.class)
                .where("remote_id = ?", remoteId)
                .executeSingle();
    }

    public static List<TodoItem> getItems() {
        return new Select()
                .from(TodoItem.class)
                .orderBy("Priority ASC")
                .execute();
    }

    // Return cursor for TodoItems. Not using this since it sort of seems to defeat the point of ORM
    public static Cursor fetchResultCursor() {
        String tableName = Cache.getTableInfo(TodoItem.class).getTableName();
        // Query all items without any conditions
        String resultRecords = new Select(tableName + ".*, " + tableName + ".Id as _id").
                from(TodoItem.class).toSql();
        // Execute query on the underlying ActiveAndroid SQLite database
        Cursor resultCursor = Cache.openDatabase().rawQuery(resultRecords, null);
        return resultCursor;
    }
}
