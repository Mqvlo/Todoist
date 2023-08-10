package com.example.todoist;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoist.adapter.OnTodoClickListener;
import com.example.todoist.adapter.RecyclerViewAdapter;
import com.example.todoist.model.Task;
import com.example.todoist.model.TaskViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

public class DoneActivity extends AppCompatActivity implements OnTodoClickListener {
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);
        Toolbar toolbar = findViewById(R.id.toolbar_done);
        toolbar.setOnClickListener(v -> DoneActivity.super.finish());

        recyclerView = findViewById(R.id.recycler_view_done);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        TaskViewModel taskViewModel = new ViewModelProvider.AndroidViewModelFactory(
                this.getApplication())
                .create(TaskViewModel.class);

        taskViewModel.getTasksIsDone(true).observe(this, tasks -> {
            recyclerViewAdapter = new RecyclerViewAdapter(tasks,this);
            recyclerView.setAdapter(recyclerViewAdapter);
        });
    }

    @Override
    public void onTodoClick(Task task) {
        Snackbar.make(findViewById(android.R.id.content),"About to set the task as UnDone, are you sure?", Snackbar.LENGTH_LONG)
                .setAction("Ok", v -> {
                    task.setDateCreated(Calendar.getInstance().getTime());
                    task.setIsDone(false);
                    TaskViewModel.update(task);
                    if (recyclerViewAdapter.getItemCount() <= 1)
                        DoneActivity.super.finish();
                })
                .show();
    }
}