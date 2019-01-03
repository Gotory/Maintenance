package com.dmt_winches.maintenance.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.dmt_winches.maintenance.R;

import java.util.ArrayList;
import java.util.List;

public class RoomSelect extends AppCompatActivity {
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //todo - use fragments!!!
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_select);
        TextView buildingTV = findViewById(R.id.buidlingTV);
        final TextView floorTV = findViewById(R.id.floorTV);
        String building = getIntent().getStringExtra("TaskBuilding");
        String floor = getIntent().getStringExtra("TaskFloor");
        String room = getIntent().getStringExtra("TaskRoom");
        String coordX = getIntent().getStringExtra("TaskCoordX");
        String coordY = getIntent().getStringExtra("TaskCoordY");
        buildingTV.setText(building);
        floorTV.setText(floor);

        switch (buildingTV.getText().toString()) {
            case "Main Building": {
                spinner = findViewById(R.id.spinner);

                List<String> list = new ArrayList<>();
                list.add("-1");
                list.add("P");
                list.add("1");
                list.add("2");
                list.add("3");
                list.add("4");
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
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
                break;
            }
            case "Admin": {
                spinner = findViewById(R.id.spinner);

                List<String> list = new ArrayList<>();
                list.add("P");
                list.add("1");
                list.add("2");
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
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
                break;
            }
            default:
                android.support.design.widget.AppBarLayout appbar = findViewById(R.id.appbar);
                appbar.setVisibility(View.GONE);
                break;
        }
    }
}
