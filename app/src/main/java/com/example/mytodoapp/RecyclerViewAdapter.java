package com.example.mytodoapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    public static List<Task> mtaskslist;
    public static Context context;
    public static Set<Task> selecteditemsMap = new HashSet<Task>();


    public MainActivity m = new MainActivity();

    public RecyclerViewAdapter(List<Task> mtaskslists, Context CContext) {
        mtaskslist = mtaskslists;
        context = CContext;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView taskstext;
        CheckBox chechb;
        CardView cardrowview;
        ImageButton itemedit;
        LinearLayout rowViewLayout;
        View div;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            taskstext = itemView.findViewById(R.id.tasktext);
            chechb = itemView.findViewById(R.id.checkb);
            cardrowview = itemView.findViewById(R.id.cardrowview);
            itemedit = itemView.findViewById(R.id.itemedit);

            div = itemView.findViewById(R.id.div);

            rowViewLayout = itemView.findViewById(R.id.rowViewLayout);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    m.delmodeactivate();
                    return true;
                }
            });

        }
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowview, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewAdapter.ViewHolder holder, final int position) {

        if (mtaskslist.isEmpty()) {
            m.notasks.setVisibility(View.VISIBLE);
            m.rcv.setVisibility(View.INVISIBLE);
        } else {
            m.notasks.setVisibility(View.INVISIBLE);
            m.rcv.setVisibility(View.VISIBLE);
        }

        if (MainActivity.isDelModeEnabled) {
            if (selecteditemsMap.contains(mtaskslist.get(position))) {
                holder.rowViewLayout.setBackgroundColor( ContextCompat.getColor( context , R.color.colorPrimary ) );
                if (mtaskslist.get(position).status) {
                    holder.chechb.setChecked(true);
                    holder.taskstext.setTextColor(Color.parseColor("#ff000000"));
                    holder.taskstext.setPaintFlags(holder.taskstext.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
            } else {
                if (mtaskslist.get(position).status) {
                    holder.chechb.setChecked(true);
                    holder.taskstext.setTextColor(Color.parseColor("#f0777777"));
                    holder.taskstext.setPaintFlags(holder.taskstext.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
            }

            if (selecteditemsMap.size() == mtaskslist.size()) {
                MainActivity.selectcheckb.setChecked(true);
                MainActivity.no_selected.setText("ALL");
            } else {
                MainActivity.selectcheckb.setChecked(false);
                MainActivity.no_selected.setText("" + selecteditemsMap.size());
            }


        } else {
            if (mtaskslist.get(position).status) {
                holder.chechb.setChecked(true);
                holder.taskstext.setTextColor(Color.parseColor("#f0777777"));
                holder.taskstext.setPaintFlags(holder.taskstext.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }

        holder.taskstext.setText(mtaskslist.get(position).taskstring);
        holder.cardrowview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!MainActivity.isDelModeEnabled) {


                    if (mtaskslist.get(position).status) {
                        mtaskslist.get(position).status = false;

                        Task s = mtaskslist.get(position);
                        int cnt = 0;
                        for (Task _b : mtaskslist) {
                            if (_b.status) cnt++;
                        }
                        int _y = mtaskslist.size() - cnt - 1;
                        mtaskslist.remove(s);
                        mtaskslist.add(_y, s);
                    } else {
                        mtaskslist.get(position).status = true;

                        Task s = mtaskslist.get(position);
                        int cnt = 0;
                        for (Task _b : mtaskslist) {
                            if (_b.status) cnt++;
                        }
                        int _y = mtaskslist.size() - cnt;
                        mtaskslist.remove(s);
                        mtaskslist.add(_y, s);
                    }
                    notifyDataSetChanged();
                } else {

                    if (selecteditemsMap.contains(mtaskslist.get(position))) {
                        selecteditemsMap.remove(mtaskslist.get(position));
                        holder.rowViewLayout.setBackgroundColor(Color.rgb(255, 255, 255));
                        if (mtaskslist.get(position).status) {
                            holder.taskstext.setTextColor(Color.parseColor("#f0777777"));
                        } else {
                            holder.taskstext.setTextColor(Color.parseColor("#ff000000"));
                        }
                    } else {
                        selecteditemsMap.add(mtaskslist.get(position));
                        holder.rowViewLayout.setBackgroundColor( ContextCompat.getColor( context , R.color.colorPrimary ) );
                        holder.taskstext.setTextColor(Color.parseColor("#ff000000"));
                    }


                    if (selecteditemsMap.size() == mtaskslist.size()) {
                        MainActivity.selectcheckb.setChecked(true);
                        MainActivity.no_selected.setText("ALL");
                    } else {
                        MainActivity.selectcheckb.setChecked(false);
                        MainActivity.no_selected.setText("" + selecteditemsMap.size());
                    }
                }

            }
        });


//    holder.itemdelete.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
//        mtaskslist.remove(position);
//        if (mtaskslist.isEmpty()){
//          m.delmodedeactivate(mtaskslist);
//        }
//        notifyDataSetChanged();
//      }
//    });

        holder.itemedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                m.addtasklt.setVisibility(View.VISIBLE);
                m.floatingActionButton.setVisibility(View.GONE);
                m.addbutton.setVisibility(View.GONE);
                m.editbtn.setVisibility(View.VISIBLE);
                m.tasktext.setText(holder.taskstext.getText().toString());


                m.editbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mtaskslist.get(position).taskstring = m.tasktext.getText().toString();
                        notifyDataSetChanged();
                        m.addtasklt.setVisibility(View.GONE);
                        m.floatingActionButton.setVisibility(View.VISIBLE);
                        m.tasktext.setText("");
                    }
                });
            }
        });


        if (m.isDelModeEnabled) {
//      holder.itemdelete.setVisibility(View.VISIBLE);
            holder.itemedit.setVisibility(View.GONE);
            holder.div.setVisibility(View.GONE);

        } else {
//      holder.itemdelete.setVisibility(View.GONE);
            holder.itemedit.setVisibility(View.VISIBLE);
            holder.div.setVisibility(View.VISIBLE);
            holder.rowViewLayout.setBackgroundColor(Color.rgb(255, 255, 255));
        }


    }


    @Override
    public int getItemCount() {
        return mtaskslist.size();
    }

}


