package com.dmt_winches.maintenance.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dmt_winches.maintenance.R;

public class Room extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        String building = getIntent().getStringExtra("TaskBuilding");
        String floor = getIntent().getStringExtra("TaskFloor");
        String room = getIntent().getStringExtra("TaskRoom");
        String coordX = getIntent().getStringExtra("TaskCoordX");
        String coordY = getIntent().getStringExtra("TaskCoordY");
    }
}
