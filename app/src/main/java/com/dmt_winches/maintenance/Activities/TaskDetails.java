package com.dmt_winches.maintenance.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dmt_winches.maintenance.R;

public class TaskDetails extends AppCompatActivity {
    TextView taskTV;
    TextView descriptionTV;
    TextView leaderNameTV;
    TextView workerNameTV;
    TextView statusTV;
    TextView addDateTV;
    TextView finishDateTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        taskTV = findViewById(R.id.taskNameTV);
        descriptionTV = findViewById(R.id.taskDescriptionTV);
        leaderNameTV = findViewById(R.id.taskLeaderNameTV);
        workerNameTV = findViewById(R.id.taskWorkerNameTV);
        statusTV = findViewById(R.id.taskStatusTV);
        addDateTV = findViewById(R.id.taskAddDateTV);
        finishDateTV = findViewById(R.id.taskFinishDateTV);
        String task = getIntent().getStringExtra("Task");
        String description = getIntent().getStringExtra("Description");
        String leaderName = getIntent().getStringExtra("LeaderName");
        String workerName = getIntent().getStringExtra("WorkerName");
        String status = getIntent().getStringExtra("TaskStatus");
        String addDate = getIntent().getStringExtra("TaskAddDate");
        String finishDate = getIntent().getStringExtra("TaskEndDate");

        final String building = getIntent().getStringExtra("TaskBuilding");
        final String floor = getIntent().getStringExtra("TaskFloor");
        final String room = getIntent().getStringExtra("TaskRoom");
        final String coordX = getIntent().getStringExtra("TaskCoordX");
        final String coordY = getIntent().getStringExtra("TaskCoordY");

        Button roomBtn = findViewById(R.id.roomBTN);

        roomBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TaskDetails.this, Room.class);
                intent.putExtra("TaskBuilding", building);
                intent.putExtra("TaskFloor", floor);
                intent.putExtra("TaskRoom", room);
                intent.putExtra("TaskCoordX", coordX);
                intent.putExtra("TaskCoordY", coordY);
                startActivity(intent);

            }
        });

        taskTV.setText(task);
        descriptionTV.setText(description);
        leaderNameTV.setText(leaderName);
        workerNameTV.setText(workerName);
        statusTV.setText(status);
        addDateTV.setText(addDate);
        finishDateTV.setText(finishDate);

    }
}
