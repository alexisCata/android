package com.cathedralsw.schoolparent.classes;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by alexis on 18/10/17.
 */

public class TasksList {
    private Date date = null;
    private ArrayList<SchoolNotification> tasks = new ArrayList<>();

    public TasksList() {
    }

    public TasksList(Date date, ArrayList<SchoolNotification> tasks) {
        this.date = date;
        this.tasks = tasks;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ArrayList<SchoolNotification> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<SchoolNotification> tasks) {
        this.tasks = tasks;
    }
}
