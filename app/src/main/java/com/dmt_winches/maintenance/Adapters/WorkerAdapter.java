package com.dmt_winches.maintenance.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dmt_winches.maintenance.Common.HttpRequest;
import com.dmt_winches.maintenance.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class WorkerAdapter extends ArrayAdapter<TaskInfo> {
    private Context context;
    private SharedPreferences preferences;
    private String workId;
    private TaskInfo task;
    private Runnable timerRunnable;
    private List<TaskInfo> originalData = null;
    private List<TaskInfo> filteredData = null;


    public static class ViewHolder {
        TextView heading, workerName, timp, status;
        private ImageView icon, icon2, icon3;
        long startTime;
        Handler timerHandler;
        long timeInMilliseconds = 0L;
        long timeSwapBuff = 0L;
        long updatedTime = 0L;
        boolean stopTimer;
        int secs, mins, hours;

    }

    private ViewHolder holder;

    public WorkerAdapter(Context context, ArrayList<TaskInfo> originalData) {
        super(context, R.layout.content_task_view, originalData);
        this.context = context;
        this.filteredData = originalData;
        this.originalData = originalData;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override

    public View getView(int position, View convertView, ViewGroup parent) {
        task = filteredData.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.group_items, null);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.timerHandler = new Handler();
        holder.timp = convertView.findViewById(R.id.cronometru);
        holder.heading = convertView.findViewById(R.id.heading);
        holder.workerName = convertView.findViewById(R.id.workerNameTV);
        holder.status = convertView.findViewById(R.id.statusTV);

        timerRunnable = new Runnable() {
            public void run() {
                //todo - change timer
                holder.timeInMilliseconds = SystemClock.uptimeMillis() - holder.startTime;
                holder.updatedTime = holder.timeSwapBuff + holder.timeInMilliseconds;
                holder.secs = (int) (holder.updatedTime / 1000);
                holder.mins = holder.secs / 60;
                holder.hours = holder.mins / 60;
                holder.secs = holder.secs % 60;
                String localtime = "" + String.format("%02d", holder.hours) + ":" + String.format("%02d", holder.mins) + ":" + String.format("%02d", holder.secs);
                holder.timp.setText(localtime);

                if (!holder.stopTimer)
                    holder.timerHandler.postDelayed(this, 0);
            }
        };

        holder.heading.setText(task.getTaskName().trim());
        holder.workerName.setText("Worker: "+task.getWokerName());
        holder.status.setText(task.getTaskStatus());

        holder.icon = convertView.findViewById(R.id.icon);
        holder.icon2 = convertView.findViewById(R.id.icon2);
        holder.icon3 = convertView.findViewById(R.id.icon3);

        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences = context.getSharedPreferences("loginPrefs", MODE_PRIVATE);
                holder.stopTimer = false;
                holder.startTime = SystemClock.uptimeMillis();
                holder.timerHandler.postDelayed(timerRunnable, 0);
                holder.icon.setVisibility(View.GONE);
                holder.icon2.setVisibility(View.VISIBLE);
                holder.icon3.setVisibility(View.VISIBLE);

                if (!holder.stopTimer) {
                    holder.timeSwapBuff += holder.timeInMilliseconds;
                }
                StartTask startTask = new StartTask(task.getTaskId(), preferences.getString("userId", ""));
//                startTask.execute();
            }
        });
        holder.icon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.stopTimer = true;
                holder.icon.setVisibility(View.VISIBLE);
                holder.icon2.setVisibility(View.GONE);
                holder.icon3.setVisibility(View.GONE);
                holder.timerHandler.removeCallbacks(timerRunnable);

                PauseTask pauseTask = new PauseTask(workId);
//                pauseTask.execute();
            }
        });
        holder.icon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.icon.setVisibility(View.GONE);
                holder.icon2.setVisibility(View.GONE);
                holder.icon3.setVisibility(View.GONE);
                holder.timerHandler.removeCallbacks(timerRunnable);

                FinishTask finishTask = new FinishTask(workId);
//                finishTask.execute();
            }
        });

        convertView.setTag(holder);
        return convertView;
    }

    public class StartTask extends AsyncTask<Void, Void, Boolean> {
        private String mTaskId;
        private String mUserId;
        String message;

        StartTask(String taskId, String userId) {
            mTaskId = taskId;
            mUserId = userId;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            HashMap<String, String> postParams = new HashMap<>();

            postParams.put("type", "start_job");
            postParams.put("task_id", mTaskId);
            postParams.put("user_id", mUserId);

            String response = new HttpRequest(postParams).connect();

            if (response.equals("Connection error")) {
                message = "Connection error";
                return false;
            }

            try {
                JSONObject json = new JSONObject(response);
                String success = json.getString("success");
                if (success.equals("1")) {
                    workId = json.getString("work_id");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }

        protected void onPostExecute(final Boolean success) {
            if (!success) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            } else {
                holder.stopTimer = false;
                holder.startTime = SystemClock.uptimeMillis();
                holder.timerHandler.postDelayed(timerRunnable, 0);
                holder.icon.setVisibility(View.GONE);
                holder.icon2.setVisibility(View.VISIBLE);
                holder.icon3.setVisibility(View.VISIBLE);

                if (!holder.stopTimer) {
                    holder.timeSwapBuff += holder.timeInMilliseconds;
                }
            }
        }
    }

    public class PauseTask extends AsyncTask<Void, Void, Boolean> {
        private String mWorkId;
        String message;

        PauseTask(String workId) {
            this.mWorkId = workId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            holder.stopTimer = true;
            holder.icon.setVisibility(View.VISIBLE);
            holder.icon2.setVisibility(View.GONE);
            holder.icon3.setVisibility(View.GONE);
            holder.timerHandler.removeCallbacks(timerRunnable);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            HashMap<String, String> postParams = new HashMap<>();

            postParams.put("type", "stop_job");
            postParams.put("work_id", mWorkId);

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
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class FinishTask extends AsyncTask<Void, Void, Boolean> {
        private String mWorkId;
        String message;

        FinishTask(String workId) {
            mWorkId = workId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            holder.icon.setVisibility(View.GONE);
            holder.icon2.setVisibility(View.GONE);
            holder.icon3.setVisibility(View.GONE);
            holder.timerHandler.removeCallbacks(timerRunnable);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            HashMap<String, String> postParams = new HashMap<>();

            postParams.put("type", "stop_job");
            postParams.put("work_id", mWorkId);

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
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty() || charString.equals("All")) {
                    filteredData = originalData;
                } else {
                    List<TaskInfo> filteredList = new ArrayList<>();
                    for (TaskInfo row : originalData) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getTaskName().toLowerCase().contains(charString.toLowerCase()) || row.getTaskName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                        if (row.getTaskStatus().toLowerCase().equals(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    filteredData = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredData;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredData = (ArrayList<TaskInfo>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}