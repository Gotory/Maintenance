package com.dmt_winches.maintenance.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dmt_winches.maintenance.Activities.TaskView;
import com.dmt_winches.maintenance.Common.HttpRequest;
import com.dmt_winches.maintenance.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


public class MyAdapter extends ArrayAdapter<GroupInfo>  {
    private Context context;
    private SharedPreferences preferences;
    private String workId;
    private GroupInfo task;
    private Runnable timerRunnable;
    private List<GroupInfo> originalData = null;
    private List<GroupInfo>filteredData = null;
    Spinner spinner;


    public static class ViewHolder {
        TextView heading,childItem, timp,description;
        private ImageView icon,icon2,icon3,imagine;
        long startTime;
        Handler timerHandler;
        long timeInMilliseconds = 0L;
        long timeSwapBuff = 0L;
        long updatedTime = 0L;
        boolean stopTimer ;
        int secs,mins,hours;

    }
    private ViewHolder holder;

    public MyAdapter(Context context, ArrayList<GroupInfo> originalData) {
        super(context, R.layout.content_task_view, originalData);
        this.context=context;
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
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.timerHandler = new Handler();
        holder.timp = convertView.findViewById(R.id.cronometru);
        //region regiuneCron
            timerRunnable = new Runnable() {
            public void run() {
                holder.timeInMilliseconds = SystemClock.uptimeMillis() - holder.startTime;
                holder.updatedTime = holder.timeSwapBuff + holder.timeInMilliseconds;
                holder.secs = (int) (holder.updatedTime / 1000);
                holder.mins = holder.secs / 60;
                holder.hours = holder.mins/60;
                holder.secs = holder.secs % 60;
                String localtime = "" + String.format("%02d", holder.hours) + ":" + String.format("%02d", holder.mins) + ":" + String.format("%02d", holder.secs);
                holder.timp.setText(localtime);
                /*if (mins == 1) {
                    holder.stopTimer = true;
                }*/
                if (!holder.stopTimer)
                    holder.timerHandler.postDelayed(this, 0);
            }

        };
              //endregion
        holder.heading = convertView.findViewById(R.id.heading);
        holder.heading.setText(task.getTask_name().trim());
        holder.icon = convertView.findViewById(R.id.icon);
        holder.icon2 = convertView.findViewById(R.id.icon2);
        holder.icon3 =  convertView.findViewById(R.id.icon3);

        //region FunctionalitateButoane
        holder.icon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                preferences = context.getSharedPreferences("loginPrefs", MODE_PRIVATE);
                StartTask startTask = new StartTask(task.getTask_id(), preferences.getString("userId",""));
                startTask.execute();
            }
        });
        holder.icon2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                PauseTask pauseTask = new PauseTask(workId);
                pauseTask.execute();
            }
        });
        holder.icon3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FinishTask finishTask = new FinishTask(workId);
                finishTask.execute();
            }
        });
//endregion


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
                if(success.equals("1")){
                    workId = json.getString("work_id");
                }

            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            return true;
        }

        protected void onPostExecute(final Boolean success){

            if (!success) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
            else{
                holder.stopTimer = false;
                holder.startTime = SystemClock.uptimeMillis();
                holder.timerHandler.postDelayed(timerRunnable, 0);
                holder.icon.setVisibility(View.GONE);
                holder.icon2.setVisibility(View.VISIBLE);
                holder.icon3.setVisibility(View.VISIBLE);

                if(!holder.stopTimer){
                    holder.timeSwapBuff += holder.timeInMilliseconds;
                }
            }
        }
    }

    public class PauseTask extends AsyncTask<Void, Void, Boolean> {

        private String mWorkId;
        String message;
        PauseTask(String workId) {
            this.mWorkId=workId;

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            holder.stopTimer = true;
            holder.icon.setVisibility(View.VISIBLE);
            holder.icon2.setVisibility(View.GONE);
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

            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            return true;
        }



        protected void onPostExecute(final Boolean success){

            if (!success) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class FinishTask extends AsyncTask<Void, Void, Boolean> {

        private String mWorkId;
        String message;
        FinishTask(String workId) {
            mWorkId=workId;

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

            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            return true;
        }

        protected void onPostExecute(final Boolean success){

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
                    List<GroupInfo> filteredList = new ArrayList<>();
                    for (GroupInfo row : originalData) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getTask_name().toLowerCase().contains(charString.toLowerCase()) || row.getTask_name().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                        if (row.getStatus().toLowerCase().equals(charString.toLowerCase())) {
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
                filteredData = (ArrayList<GroupInfo>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}