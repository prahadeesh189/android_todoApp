package com.example.mytodoapp;

import java.io.Serializable;

public class Task implements Serializable {

    String taskstring;
    boolean status;

    public Task(String Taskstring, boolean Stauts) {
        this.taskstring = Taskstring;
        this.status = Stauts;
    }

}
