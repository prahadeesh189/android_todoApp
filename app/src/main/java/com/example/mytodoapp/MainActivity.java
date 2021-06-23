package com.example.mytodoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Task> tasks_list = new ArrayList<Task>();
    DBHelper dbHelper = new DBHelper(this);

    static FloatingActionButton floatingActionButton;
    static CardView addtasklt;
    static Button addbutton;
    static Button cancelbutton;
    static EditText tasktext;
    static Button editbtn;
    static RecyclerView rcv;
    static TextView notasks;
    static boolean isDelModeEnabled = false;

    static Toolbar toolbar;
    static ImageButton deleteDone;
    static ImageButton deleteAll;
    static LinearLayout deleteOptions;
    static LinearLayout selectNoLayout;
    static LinearLayout toolbartitle;
    static CheckBox selectcheckb;
    static TextView no_selected;


    private static RecyclerView mrecyclerview;
    private static RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> mrvadapter;
    private static RecyclerView.LayoutManager mrvlayoutmanager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mrecyclerview = findViewById(R.id.recyclerview);
        mrecyclerview.setHasFixedSize(true);
        mrvlayoutmanager = new LinearLayoutManager(this);
        mrvadapter = new RecyclerViewAdapter(tasks_list, this);
        mrecyclerview.setLayoutManager(mrvlayoutmanager);
        mrecyclerview.setAdapter(mrvadapter);
        mrecyclerview.getRecycledViewPool().setMaxRecycledViews(0, 0);

        addbutton = findViewById(R.id.addbutton);
        cancelbutton = findViewById(R.id.cancel_button);
        tasktext = findViewById(R.id.tasktext);
        editbtn = findViewById(R.id.editbtn);
        notasks = findViewById(R.id.notasks);
        rcv = findViewById(R.id.recyclerview);

        selectNoLayout = findViewById(R.id.selectNoLayout);
        toolbartitle = findViewById(R.id.toolbartitle);
        selectcheckb = findViewById(R.id.selectcheckb);
        no_selected = findViewById(R.id.no_selected);

        toolbar = findViewById(R.id.action_toolbar);
        setSupportActionBar(toolbar);
        deleteDone = findViewById(R.id.deleteAll);
        deleteAll = findViewById(R.id.deleteDone);
        deleteAll = findViewById(R.id.deleteDone);
        deleteOptions = findViewById(R.id.deleteOptions);

        List<Task> _temparr = extractingDataFromDB();
        for (int i = 0; i < _temparr.size(); i++) {
            tasks_list.add(_temparr.get(i));
        }

        if (tasks_list.isEmpty()) {
            notasks.setVisibility(View.VISIBLE);
            rcv.setVisibility(View.INVISIBLE);
        } else {
            notasks.setVisibility(View.INVISIBLE);
            rcv.setVisibility(View.VISIBLE);
        }


        addtasklt = findViewById(R.id.addtasklt);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editmode();
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(v.getWindowToken(), 0, 0);

            }
        });

        selectNoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tasks_list.size() == RecyclerViewAdapter.selecteditemsMap.size() && selectcheckb.isChecked()) {
                    selectcheckb.setChecked(false);
                    RecyclerViewAdapter.selecteditemsMap.clear();
                    no_selected.setText("" + RecyclerViewAdapter.selecteditemsMap.size());
                    mrvadapter.notifyDataSetChanged();
                } else {
                    selectcheckb.setChecked(true);
                    for (Task t : tasks_list) {
                        if (!RecyclerViewAdapter.selecteditemsMap.contains(t)) {
                            RecyclerViewAdapter.selecteditemsMap.add(t);
                        }
                    }
                    no_selected.setText("ALL");
                    mrvadapter.notifyDataSetChanged();
                }

            }
        });




        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int cnt = 0;
                for (Task _b : tasks_list) {
                    if (_b.status) cnt++;
                }
                int _y = tasks_list.size() - cnt;

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                tasks_list.add(_y, new Task(tasktext.getText().toString(), false));
                mrvadapter.notifyDataSetChanged();
                addtasklt.setVisibility(View.GONE);
                floatingActionButton.setVisibility(View.VISIBLE);
                tasktext.setText("");
                tasktext.clearFocus();
            }
        });

        cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                tasktext.setText("");
                tasktext.clearFocus();
                addtasklt.setVisibility(View.GONE);
                floatingActionButton.setVisibility(View.VISIBLE);
            }
        });


        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int _h = tasks_list.size() - 1;
                while (_h >= 0) {
                    if (!tasks_list.get(tasks_list.size() - 1).status) {
                        break;
                    }
                    tasks_list.remove(tasks_list.size() - 1);
                    _h--;
                }
                if (tasks_list.isEmpty()) {
                    delmodedeactivate(tasks_list);
                }
                Toast.makeText(getApplicationContext(), "Finished tasks are deleted", Toast.LENGTH_SHORT).show();
                RecyclerViewAdapter.selecteditemsMap.clear();
                no_selected.setText("" + RecyclerViewAdapter.selecteditemsMap.size());
                mrvadapter.notifyDataSetChanged();
            }
        });


        deleteDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (Task t : RecyclerViewAdapter.selecteditemsMap) {
                    tasks_list.remove(t);
                }

                Toast.makeText(getApplicationContext(), no_selected.getText().toString() + " tasks deleted", Toast.LENGTH_SHORT).show();
                if (tasks_list.isEmpty()) {
                    delmodedeactivate(tasks_list);
                }
                RecyclerViewAdapter.selecteditemsMap.clear();
                no_selected.setText("" + RecyclerViewAdapter.selecteditemsMap.size());
                mrvadapter.notifyDataSetChanged();
            }
        });
    }



    public List<Task> extractingDataFromDB() {

        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from TasksArrayTable", null);

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            return convert_Byte_to_Arraylist(cursor.getBlob(0));
        } else {
            System.out.println("cursor.getCount() is 0");
            return new ArrayList<Task>();
        }
    }

    public long insertingDataIntoDB(List<Task> tasksl) {
        SQLiteDatabase db_writter = dbHelper.getWritableDatabase();
        SQLiteDatabase db_reader = dbHelper.getReadableDatabase();

        try {
            Cursor cursor = db_reader.rawQuery("select * from TasksArrayTable", null);
            if (cursor.getCount() > 0) {
                db_writter.execSQL("delete from TasksArrayTable");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        byte[] _b = convert_Arraylist_to_Byte(tasksl);
        ContentValues values = new ContentValues();
        values.put("TaskArray", _b);
        long row_1 = db_writter.insert("TasksArrayTable", null, values);
        return row_1;
    }

    public byte[] convert_Arraylist_to_Byte(List<Task> taskl) {

        byte[] result = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(taskl.toArray());
            result = baos.toByteArray();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<Task> convert_Byte_to_Arraylist(byte[] result) {

        List<Task> taskArrayList = new ArrayList<Task>();
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(result);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object obj = ois.readObject();

            List<Object> list = new ArrayList<>();
            if (obj.getClass().isArray()) {
                list = Arrays.asList((Object[]) obj);
            } else if (obj instanceof Collection) {
                list = new ArrayList<>((Collection<Object>) obj);
            }

            for (int i = 0; i < list.size(); i++) {
                taskArrayList.add(((Task) list.get(i)));
            }

            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return taskArrayList;
    }


    @Override
    protected void onStart() {


        super.onStart();
    }

    @Override
    protected void onStop() {
        insertingDataIntoDB(tasks_list);
        super.onStop();
    }

    public void delmodeactivate() {

        isDelModeEnabled = true;
        deleteOptions.setVisibility(View.VISIBLE);
        selectNoLayout.setVisibility(View.VISIBLE);
        toolbartitle.setVisibility(View.GONE);
        toolbar.setBackgroundColor(Color.rgb(255, 255, 255));
        mrvadapter.notifyDataSetChanged();
        floatingActionButton.setVisibility(View.GONE);

    }

    public void delmodedeactivate(List l) {

        isDelModeEnabled = false;
        deleteOptions.setVisibility(View.GONE);
        selectNoLayout.setVisibility(View.GONE);
        toolbartitle.setVisibility(View.VISIBLE);
        toolbar.setBackgroundColor(ContextCompat.getColor(this , R.color.colorPrimary ));
        RecyclerViewAdapter.selecteditemsMap.clear();
        no_selected.setText("" + RecyclerViewAdapter.selecteditemsMap.size());
        selectcheckb.setChecked(false);
        mrvadapter.notifyDataSetChanged();
        floatingActionButton.setVisibility(View.VISIBLE);
        if (l.isEmpty()) {
            notasks.setVisibility(View.VISIBLE);
            rcv.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onBackPressed() {

        if (isDelModeEnabled) {
            delmodedeactivate(tasks_list);
        } else if (addtasklt.getVisibility() == View.VISIBLE) {
            addtasklt.setVisibility(View.GONE);
            floatingActionButton.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
            finish();
        }
    }

    public void editmode() {

        if (addtasklt.getVisibility() == View.GONE) {
            addtasklt.setVisibility(View.VISIBLE);
            floatingActionButton.setVisibility(View.GONE);
            addbutton.setVisibility(View.VISIBLE);
            editbtn.setVisibility(View.GONE);
            tasktext.setText("");
            tasktext.requestFocus();
        } else {
            addtasklt.setVisibility(View.GONE);
            floatingActionButton.setVisibility(View.VISIBLE);
        }
    }
}
