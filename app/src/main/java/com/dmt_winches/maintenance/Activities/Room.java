package com.dmt_winches.maintenance.Activities;

import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.dmt_winches.maintenance.R;

import java.util.ArrayList;
import java.util.List;

public class Room extends AppCompatActivity {
    private Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        TextView buildingTV = findViewById(R.id.buidlingTV);
        final TextView floorTV = findViewById(R.id.floorTV);
        String building = getIntent().getStringExtra("TaskBuilding");
        String floor = getIntent().getStringExtra("TaskFloor");
        String room = getIntent().getStringExtra("TaskRoom");
        String coordX = getIntent().getStringExtra("TaskCoordX");
        String coordY = getIntent().getStringExtra("TaskCoordY");
        buildingTV.setText(building);
        floorTV.setText(floor);

        if(buildingTV.getText().toString().equals("Main Building") || buildingTV.getText().toString().equals("Admin")){
            spinner = findViewById(R.id.spinner);

            List<String> list = new ArrayList<String>();
            list.add("P");
            list.add("1");
            list.add("2");
            list.add("3");
            list.add("4");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(dataAdapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    floorTV.setText(spinner.getSelectedItem().toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
        else{
            android.support.design.widget.AppBarLayout appbar = findViewById(R.id.appbar);
            appbar.setVisibility(View.GONE);
        }
    }
}
