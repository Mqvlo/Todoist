package com.example.todoist.adapter;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoist.R;
import com.example.todoist.model.Task;
import com.example.todoist.util.Utils;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements Filterable {
    private final List<Task> taskList;
    private List<Task> filteredTaskList;
    private final OnTodoClickListener todoClickListener;

    public RecyclerViewAdapter(List<Task> taskList, OnTodoClickListener onTodoClickListener) {
        this.taskList = taskList;
        this.filteredTaskList = taskList;
        this.todoClickListener = onTodoClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todo_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = filteredTaskList.get(position);
        String formatted = Utils.formatDate(task.getDueDate());

        ColorStateList colorStateList = new ColorStateList(new int[][]{
                new int[] {-android.R.attr.state_enabled},
                new int[] {android.R.attr.state_enabled}
        },
                new int[]{
                        Color.LTGRAY, //disabled
                        Utils.priorityColor(task)
                });

        holder.task.setText(task.getTask());
        holder.todayChip.setText(formatted);
        holder.todayChip.setTextColor(Utils.priorityColor(task));
        holder.todayChip.setChipIconTint(colorStateList);
    }

    @Override
    public int getItemCount() {
        return (taskList != null)? filteredTaskList.size() : 0 ;
//        return taskList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint == null || constraint.length() == 0){
                    filteredTaskList = taskList;
                }else{
                    List<Task> tempList = new ArrayList<>();
                    for (Task task :taskList) {
                        if(task.getTask().toLowerCase().contains(constraint.toString().toLowerCase())){
                            tempList.add(task);
                        }
                    }
                    filteredTaskList = tempList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredTaskList;
                filterResults.count = filteredTaskList.size();
                return filterResults;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredTaskList = ((List<Task>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public AppCompatTextView task;
        public Chip todayChip;

        OnTodoClickListener onTodoClickListener;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            task = itemView.findViewById(R.id.todo_row_todo);
            todayChip = itemView.findViewById(R.id.todo_row_chip);
            this.onTodoClickListener = todoClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Task currTask = taskList.get(getAdapterPosition());
            int id = view.getId();
            if (id == R.id.todo_row_layout){
                onTodoClickListener.onTodoClick(currTask);
            }
        }
    }
    public Task getTaskAt(int position){
        return taskList.get(position);
    }
}
