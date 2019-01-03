package com.dmt_winches.maintenance.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.dmt_winches.maintenance.R;

public class SelectBuilding extends AppCompatActivity {
    TextView mainBuildingText;
    TextView adminText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_building);

        LinearLayout secondHall = findViewById(R.id.secondHall);
        final TextView secondHallText = findViewById(R.id.secondHallTV);
        secondHall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectBuilding.this, RoomSelect.class);
                intent.putExtra("TaskBuilding", secondHallText.getText().toString());
                SelectBuilding.this.startActivity(intent);
            }
        });

        LinearLayout newHall = findViewById(R.id.newHall);
        final TextView newHallText = findViewById(R.id.newHallTV);
        newHall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectBuilding.this, RoomSelect.class);
                intent.putExtra("TaskBuilding", newHallText.getText().toString());
                SelectBuilding.this.startActivity(intent);

            }
        });

        LinearLayout carPark = findViewById(R.id.carPark);
        final TextView carParkText = findViewById(R.id.carParkTV);
        carPark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectBuilding.this, RoomSelect.class);
                intent.putExtra("TaskBuilding", carParkText.getText().toString());
                SelectBuilding.this.startActivity(intent);

            }
        });

        LinearLayout firstHall = findViewById(R.id.firstHall);
        final TextView firstHallText = findViewById(R.id.firstHallTV);
        firstHall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectBuilding.this, RoomSelect.class);
                intent.putExtra("TaskBuilding", firstHallText.getText().toString());
                SelectBuilding.this.startActivity(intent);

            }
        });

        LinearLayout mainBuilding = findViewById(R.id.mainBuilding);
        mainBuildingText = findViewById(R.id.mainBuildingTV);
        mainBuilding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog(mainBuildingText.getText().toString());
            }
        });

        LinearLayout admin = findViewById(R.id.admin);
        adminText = findViewById(R.id.adminTV);
        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog(adminText.getText().toString());
            }
        });
    }

    public void openDialog(final String textIntent) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
        TextView title = new TextView(this);
        title.setText("Choose the floor:");
        title.setPadding(10, 10, 10, 10);   // Set Position
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        title.setTextSize(20);
        alertDialog.setCustomTitle(title);

        final Spinner spinner = mView.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter;

        if(textIntent.equals("Main Building")) {
            String[] items = new String[]{"-1", "P", "1", "2", "3", "4"};
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        } else {
            //todo ask number of floors
            String[] items = new String[]{"P", "1", "2"};
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        }
        spinner.setAdapter(adapter);
        alertDialog.setView(spinner);

        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(SelectBuilding.this, RoomSelect.class);
                intent.putExtra("TaskBuilding", textIntent);
                intent.putExtra("TaskFloor", spinner.getSelectedItem().toString());
                SelectBuilding.this.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.setView(mView);
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }
}
