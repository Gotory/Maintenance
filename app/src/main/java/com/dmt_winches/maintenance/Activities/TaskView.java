package com.dmt_winches.maintenance.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.dmt_winches.maintenance.Adapters.GroupInfo;
import com.dmt_winches.maintenance.Adapters.MyAdapter;
import com.dmt_winches.maintenance.Common.HttpRequest;
import com.dmt_winches.maintenance.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskView extends AppCompatActivity {

    private ArrayList<GroupInfo> taskList = new ArrayList<>();
    private ListView myList;
    private MyAdapter listAdapter;
    private SharedPreferences preferences;
    private String mUserId;
    private String mUserType;
    private String mUserName;
    private String message;
    private Toolbar toolbar;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view);

        myList = findViewById(R.id.simpleListView);
        listAdapter = new MyAdapter(this, taskList);
        toolbar = findViewById(R.id.toolbar);
        spinner = findViewById(R.id.spinner);
        List<String> list = new ArrayList<String>();
        list.add("All");
        list.add("Not Started");
        list.add("Started");
        list.add("Finished");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                listAdapter.getFilter().filter(spinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createTask = new Intent(TaskView.this, SelectBuilding.class);
                TaskView.this.startActivity(createTask);
            }
        });
        // setOnChildClickListener listener for child row click

        // setOnGroupClickListener listener for group heading click
        preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        GetTaskList taskTry = new GetTaskList(preferences.getString("userId",""), preferences.getString("userName", ""),this.getIntent().getStringExtra("userType"));
        taskTry.execute((Void) null);
        setSupportActionBar(toolbar);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(TaskView.this, TaskDetails.class);
                GroupInfo task = taskList.get(i);
                intent.putExtra("Task",task.getTask_name());
                intent.putExtra("Description", task.getTask_description());
                intent.putExtra("LeaderName",task.getLeader_name());
                intent.putExtra("WorkerName",task.getWoker_name());
                intent.putExtra("TaskStatus",task.getStatus());
                intent.putExtra("TaskAddDate", task.getTask_add_date());
                intent.putExtra("TaskEndDate", task.getTask_finish_date());
                intent.putExtra("TaskBuilding", task.getTask_building());
                intent.putExtra("TaskFloor", task.getTask_floor());
                intent.putExtra("TaskRoom", task.getTask_room());
                intent.putExtra("TaskCoordX", task.getTask_coord_x());
                intent.putExtra("TaskCoordY",task.getTask_coord_y());
                startActivity(intent);
            }
        });
        if(mUserType.equals("leader"))
        {
            myList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                               final int pos, long id) {
                    // TODO Auto-generated method stub

                    //Toast.makeText(TaskView.this, "Merge!!!", Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(TaskView.this);
                    deleteBuilder.setMessage("Esti sigur ca vrei sa stergi acest task?")
                            .setCancelable(false)
                            .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    GroupInfo task = taskList.get(i);
                                    new DeletTask(task.getTask_id(), i).execute();
                                }
                            })
                            .setNegativeButton("Nu", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            })
                            .show();


                    return true;
                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SearchView searchView;
        getMenuInflater().inflate(R.menu.menu,menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                listAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                listAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    public class GetTaskList extends AsyncTask<Void, Void, Boolean> {



        GetTaskList(String userId, String userName, String userType) {
            mUserId = userId;
            mUserName = userName;
            mUserType = userType;

        }

        @Override
        protected Boolean doInBackground(Void... params) {

            HashMap<String, String> postParams = new HashMap<>();

            postParams.put("type", "tasks_list");
            postParams.put("user_id", mUserId);
            postParams.put("user_type", mUserType);
            postParams.put("user_name", mUserName);

            String response = new HttpRequest(postParams).connect();

            if (response.equals("Connection error")) {
                message = "Connection error";
                return false;
            }

            Log.d("+++", response);

            try {
                JSONObject json = new JSONObject(response);
                String success = json.getString("success");
                GroupInfo info = new GroupInfo();
                String taskname = "Task1";
                info.setTask_name(taskname);
                info.setTask_description("Lorem Ipsum este pur şi simplu o machetă pentru text a industriei tipografice. Lorem Ipsum a fost macheta standard a industriei încă din secolul al XVI-lea, când un tipograf anonim a luat o planşetă de litere şi le-a amestecat pentru a crea o carte demonstrativă pentru literele respective. Nu doar că a supravieţuit timp de cinci secole, dar şi a facut saltul în tipografia electronică practic neschimbată. ");
                info.setLeader_name("Dragos");
                info.setWoker_name("Andrei");
                info.setTask_add_date("12/11/2018");
                info.setTask_finish_date(null);
                info.setStatus("Not Started");
                taskList.add(info);


                if (success.equals("1")) {
                    JSONArray taskArray = json.getJSONArray("tasks");
                    for(int j = 0; j<taskArray.length(); j++)
                    {
                        JSONObject taskuri = taskArray.getJSONObject(j);

                        GroupInfo t = new GroupInfo();

                        String status = taskuri.getString("status");
                        String task_name = taskuri.getString("task_name");
                        String task_description = taskuri.getString("task_description");
                        String task_id = taskuri.getString("task_id");
                        String task_building = taskuri.getString("task_building");
                        String task_floor = taskuri.getString("task_floor");
                        String task_room = taskuri.getString("task_room");
                        String task_coord_x = taskuri.getString("task_coord_x");
                        String task_coord_y = taskuri.getString("task_coord_y");
                        String task_leader_id = taskuri.getString("task_leader_id");
                        String leader_name = taskuri.getString("leader_name");
                        String task_worker_id = taskuri.getString("task_worker_id");
                        String woker_name = taskuri.getString("woker_name");
                        String task_status = taskuri.getString("task_status");
                        String task_add_date = taskuri.getString("task_add_date");
                        String task_finish_date = taskuri.getString("task_finish_date");
                        JSONArray arr = taskuri.getJSONArray("work_times");

                        t.setTask_name(task_name);

                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject time = arr.getJSONObject(i);
                            String work_id = time.getString("work_id");
                            String work_worker_id = time.getString("work_worker_id");
                            String worker_name = time.getString("worker_name");
                            String work_start_time = time.getString("work_start_time");
                            String work_end_time = time.getString("work_end_time");
                        }

                        taskList.add(t);

                    }

                } else {
                    message = json.getString("message");
                    return false;
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return true;
        }
        @Override
       protected void onPostExecute(final Boolean success) {

            if (success) {
                myList.setAdapter(listAdapter);
            } else {
                Toast.makeText(TaskView.this, message, Toast.LENGTH_SHORT).show();
            }
        }


    }

    public class DeletTask extends AsyncTask<Void, Void, Boolean> {

        private String mTaskId;
        private int poz;
        DeletTask(String taskId, int poz) {
            mTaskId = taskId;
            this.poz = poz;

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            HashMap<String, String> postParams = new HashMap<>();

            postParams.put("type", "delete_tasks");
            postParams.put("task_id", mTaskId);

            String response = new HttpRequest(postParams).connect();

            if (response.equals("Connection error")) {
                message = "Connection error";
                return false;
            }

            try {
                JSONObject json = new JSONObject(response);
                String success = json.getString("success");
                return success.equals("1");
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }

        protected void onPostExecute(final Boolean success) {

            if (!success) {
                Toast.makeText(TaskView.this, message, Toast.LENGTH_SHORT).show();
            }
            else{
                taskList.remove(poz);
                listAdapter.notifyDataSetChanged();
            }
        }
    }



}


