package com.example.todoist;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.todoist.model.Priority;
import com.example.todoist.model.SharedViewModel;
import com.example.todoist.model.Task;
import com.example.todoist.model.TaskViewModel;
import com.example.todoist.util.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.ViewModelProvider;

import java.util.Calendar;
import java.util.Date;

public class BottomSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    private EditText enterTodo;
    private ImageButton calendarButton;
    private ImageButton priorityButton;
    private RadioGroup priorityRadioGroup;
    private RadioButton selectedRadioButton;
    private int selectedButtonId;
    private ImageButton saveButton;
    private CalendarView calendarView;
    private Group calendarGroup;
    private Date dueDate;
    Calendar calendar = Calendar.getInstance();
    private SharedViewModel sharedViewModel;
    private boolean isEdit;
    private Priority priority;
    private boolean isDone;
    public BottomSheetFragment(){

    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);
        calendarGroup = view.findViewById(R.id.calendar_group);
        calendarView = view.findViewById(R.id.calendar_view);
        calendarButton = view.findViewById(R.id.today_calendar_button);
        enterTodo = view.findViewById(R.id.enter_todo_et);
        saveButton = view.findViewById(R.id.save_todo_button);
        priorityButton = view.findViewById(R.id.priority_todo_button);
        priorityRadioGroup = view.findViewById(R.id.radioGroup_priority);

        Chip todayChip = view.findViewById(R.id.today_chip);
        todayChip.setOnClickListener(this);
        Chip tomorrowChip = view.findViewById(R.id.tomorrow_chip);
        tomorrowChip.setOnClickListener(this);
        Chip nextWeekChip = view.findViewById(R.id.next_week_chip);
        nextWeekChip.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Task task = sharedViewModel.getSelectedItem().getValue();
        if (task != null){
            isEdit = sharedViewModel.getIsEdit();
            if (isEdit) {
                enterTodo.setText(task.getTask());
                switch (task.priority) {
                    case HIGH:
                        priorityRadioGroup.check(R.id.radioButton_high);
                        priority = Priority.HIGH;
                        break;
                    case MEDIUM:
                        priorityRadioGroup.check(R.id.radioButton_med);
                        priority = Priority.MEDIUM;
                        break;
                    default: {
                        priorityRadioGroup.check(R.id.radioButton_low);
                        priority = Priority.LOW;
                    }
                }
                calendarView.setDate(task.dueDate.getTime());
                dueDate = task.dueDate;
                isDone = task.isDone;
            }else
                clearBottomSheetFragment();
        }
    }

    private void clearBottomSheetFragment() {
        enterTodo.setText("");
        priorityRadioGroup.clearCheck();
        priority = Priority.LOW;
        calendarView.setDate(Calendar.getInstance().getTimeInMillis());
        dueDate = Calendar.getInstance().getTime();
        isDone = false;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity())
                .get(SharedViewModel.class);

        calendarButton.setOnClickListener(view1 -> {
            calendarGroup.setVisibility(calendarGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            Utils.hideSoftKeyboard(view1);
        });

        calendarView.setOnDateChangeListener((calendarView, year, month, dayOfMonth) -> {
            calendar.clear();
            calendar.set(year,month,dayOfMonth);
            dueDate = calendar.getTime();
        });
        priorityButton.setOnClickListener(view1 -> {
            Utils.hideSoftKeyboard(view1);
            priorityRadioGroup.setVisibility(
                    priorityRadioGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE
            );
            priorityRadioGroup.setOnCheckedChangeListener((radioGroup, checkedId) -> {
                if (priorityRadioGroup.getVisibility() == View.VISIBLE){
                    selectedButtonId = checkedId;
                    selectedRadioButton = view.findViewById(selectedButtonId);
                    if (selectedRadioButton.getId() == R.id.radioButton_high){
                        priority = Priority.HIGH;
                    } else if (selectedRadioButton.getId() == R.id.radioButton_med) {
                        priority = Priority.MEDIUM;
                    } else
                        priority = Priority.LOW;

                }else {
                    priority = Priority.LOW;
                }
            });
        });

        saveButton.setOnClickListener(view1 -> {
            String taskText = enterTodo.getText().toString().trim();
            if (!TextUtils.isEmpty(taskText) && dueDate != null && priority != null){
                Task myTask = new Task(taskText, priority,
                        dueDate, Calendar.getInstance().getTime(),
                        false);
                if (isEdit){
                    Task updateTask = sharedViewModel.getSelectedItem().getValue();
                    assert updateTask != null;
                    updateTask.setTask(taskText);
                    updateTask.setDateCreated(Calendar.getInstance().getTime());
                    updateTask.setPriority(priority);
                    updateTask.setDueDate(dueDate);
                    updateTask.setDone(isDone);
                    TaskViewModel.update(updateTask);
                    sharedViewModel.setIsEdit(false);
                }else
                    TaskViewModel.insert(myTask);
                if (this.isVisible()){
                    this.dismiss();
                }
            }else {
                //Snackbar.make(saveButton, R.string.empty_field, Snackbar.LENGTH_LONG).show();
                Toast.makeText(getActivity(), R.string.no_info, Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        calendar.clear();
        calendar.setTimeInMillis(Calendar.getInstance().getTimeInMillis());
        if (id == R.id.today_chip){
            calendar.add(Calendar.DAY_OF_YEAR, 0);
            calendarView.setDate(calendar.getTimeInMillis(),true,true);
            dueDate = calendar.getTime();
        }else if (id == R.id.tomorrow_chip){
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            calendarView.setDate(calendar.getTimeInMillis(),true,true);
            dueDate = calendar.getTime();
        }else if (id == R.id.next_week_chip){
            calendar.add(Calendar.DAY_OF_YEAR, 7);
            calendarView.setDate(calendar.getTimeInMillis(),true,true);
            dueDate = calendar.getTime();
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        sharedViewModel.setIsEdit(false);
        super.onDismiss(dialog);
    }
}