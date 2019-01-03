package com.dmt_winches.maintenance.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.dmt_winches.maintenance.Adapters.TaskInfo;
import com.dmt_winches.maintenance.Adapters.WorkTimes;
import com.dmt_winches.maintenance.Adapters.WorkerAdapter;
import com.dmt_winches.maintenance.Common.HttpRequest;
import com.dmt_winches.maintenance.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskView extends AppCompatActivity {

    private ArrayList<TaskInfo> taskList = new ArrayList<>();
    private ListView myList;
    private WorkerAdapter listAdapter;
    private String mUserId;
    private String mUserType;
    private String mUserName;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view);

        myList = findViewById(R.id.simpleListView);
        listAdapter = new WorkerAdapter(this, taskList);
        Toolbar toolbar = findViewById(R.id.toolbar);

        SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        mUserType = this.getIntent().getStringExtra("userType");
        mUserId = preferences.getString("userId", "");
        mUserName = preferences.getString("userName", "");

        if (mUserType.equals("leader")) {
            spinner = findViewById(R.id.spinner);
            List<String> list = new ArrayList<String>();
            list.add("All");
            list.add("Not Started");
            list.add("Started");
            list.add("Finished");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
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

            myList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                               final int pos, long id) {
                    AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(TaskView.this);
                    deleteBuilder.setMessage("Esti sigur ca vrei sa stergi acest task?")
                            .setCancelable(false)
                            .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    TaskInfo task = taskList.get(i);
                                    new DeletTask(task.getTaskId(), i).execute();
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

            FloatingActionButton fab = findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent createTask = new Intent(TaskView.this, SelectBuilding.class);
                    TaskView.this.startActivity(createTask);
                }
            });
        } //else {
            GetTaskList getList = new GetTaskList();
            getList.execute((Void) null);
            setSupportActionBar(toolbar);
//        }

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(TaskView.this, TaskDetails.class);
                TaskInfo task = taskList.get(i);
                intent.putExtra("Task", task.getTaskName());
                intent.putExtra("Description", task.getTaskDescription());
                intent.putExtra("LeaderName", task.getLeaderName());
                intent.putExtra("WorkerName", task.getWokerName());
                intent.putExtra("TaskStatus", task.getTaskStatus());
                intent.putExtra("TaskAddDate", task.getTaskAddDate());
                intent.putExtra("TaskEndDate", task.getTaskFinishDate());
                intent.putExtra("TaskBuilding", task.getTaskBuilding());
                intent.putExtra("TaskFloor", task.getTaskFloor());
                intent.putExtra("TaskRoom", task.getTaskRoom());
                intent.putExtra("TaskCoordX", task.getTaskCoordX());
                intent.putExtra("TaskCoordY", task.getTaskCoordY());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SearchView searchView;
        getMenuInflater().inflate(R.menu.menu, menu);
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

    //todo - add a get all task and talk to razvan
    public class GetTaskList extends AsyncTask<Void, Void, Boolean> {

        private String message;

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

            try {
                JSONObject json = new JSONObject(response);
                String success = json.getString("success");
                TaskInfo info = new TaskInfo();
                String taskname = "Task1";
                info.setTaskName(taskname);
                info.setTaskDescription("Lorem Ipsum este pur şi simplu o machetă pentru text a industriei tipografice. Lorem Ipsum a fost macheta standard a industriei încă din secolul al XVI-lea, când un tipograf anonim a luat o planşetă de litere şi le-a amestecat pentru a crea o carte demonstrativă pentru literele respective. Nu doar că a supravieţuit timp de cinci secole, dar şi a facut saltul în tipografia electronică practic neschimbată. ");
                info.setLeaderName("Dragos");
                info.setWorkerName("Andrei");
                info.setTaskAddDate("12/11/2018");
                info.setTaskFinishDate(null);
                info.setTaskStatus("Not Started");
                taskList.add(info);

                if (success.equals("1")) {
                    JSONArray taskArray = json.getJSONArray("tasks");
                    TaskInfo test = new TaskInfo();
                    test.setTaskStatus("not started");
                    test.setTaskName("AC Broke");
                    test.setTaskDescription("Need to repair the ac on the 4'th floor");
                    test.setTaskId("1");
                    test.setTaskBuilding("main");
                    test.setTaskFloor("4");
                    test.setTaskRoom("4");
                    test.setTaskCoordX("99");
                    test.setTaskCoordY("99");
                    test.setTaskLeaderId("1");
                    test.setLeaderName("Cosmin");
                    test.setTaskWorkerId("2");
                    test.setWorkerName("Stefan");
                    test.setTaskAddDate("24-05-2160");
                    test.setTaskFinishDate("0");
                    WorkTimes testTime = new WorkTimes();
                    testTime.setWorkId("1");
                    testTime.setAssignedWorker("2");
                    testTime.setWorkerName("Stefan");
                    testTime.setStartTime("12:23");
                    testTime.setEndTime("14:21");
                    taskList.add(test);

                    for (int j = 0; j < taskArray.length(); j++) {
                        JSONObject taskuri = taskArray.getJSONObject(j);

                        TaskInfo t = new TaskInfo();
                        ArrayList<WorkTimes> work_times = new ArrayList<>();

                        t.setTaskName(taskuri.getString("task_name"));
                        t.setTaskDescription(taskuri.getString("task_description"));
                        t.setTaskId(taskuri.getString("task_id"));
                        t.setTaskBuilding(taskuri.getString("task_building"));
                        t.setTaskFloor(taskuri.getString("task_floor"));
                        t.setTaskRoom(taskuri.getString("task_room"));
                        t.setTaskCoordX(taskuri.getString("task_coord_x"));
                        t.setTaskCoordY(taskuri.getString("task_coord_y"));
                        t.setTaskLeaderId(taskuri.getString("task_leader_id"));
                        t.setLeaderName(taskuri.getString("leader_name"));
                        t.setTaskWorkerId(taskuri.getString("task_worker_id"));
                        t.setWorkerName(taskuri.getString("worker_name"));
                        t.setTaskStatus(taskuri.getString("task_status"));
                        t.setTaskAddDate(taskuri.getString("task_add_date"));
                        t.setTaskFinishDate(taskuri.getString("task_finish_date"));

                        JSONArray arr = taskuri.getJSONArray("work_times");

                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject time = arr.getJSONObject(i);

                            WorkTimes work = new WorkTimes();

                            work.setWorkId(time.getString("work_id"));
                            work.setAssignedWorker(time.getString("work_worker_id"));
                            work.setWorkerName(time.getString("worker_name"));
                            work.setStartTime(time.getString("work_start_time"));
                            work.setEndTime(time.getString("work_end_time"));
                            work_times.add(work);
                        }
                        t.setWorkTimes(work_times);

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
        private String message;

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
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }

        protected void onPostExecute(final Boolean success) {
            if (!success) {
                Toast.makeText(TaskView.this, message, Toast.LENGTH_SHORT).show();
            } else {
                taskList.remove(poz);
                listAdapter.notifyDataSetChanged();
            }
        }
    }
}


