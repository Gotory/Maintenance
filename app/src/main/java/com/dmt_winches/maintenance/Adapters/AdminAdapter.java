package com.dmt_winches.maintenance.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.dmt_winches.maintenance.R;

import java.util.ArrayList;
import java.util.List;

public class AdminAdapter extends ArrayAdapter<TaskInfo> {
    private Context context;
    private SharedPreferences preferences;
    private String workId;
    private TaskInfo task;
    private Runnable timerRunnable;
    private List<TaskInfo> originalData = null;
    private List<TaskInfo> filteredData = null;


    public static class ViewHolder {
        TextView heading, childItem, timp, description;
        private ImageView icon, icon2, icon3, imagine;
        long startTime;
        Handler timerHandler;
        long timeInMilliseconds = 0L;
        long timeSwapBuff = 0L;
        long updatedTime = 0L;
        boolean stopTimer;
        int secs, mins, hours;

    }

    private ViewHolder holder;

    public AdminAdapter(Context context, ArrayList<TaskInfo> originalData) {
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

        timerRunnable = new Runnable() {
            public void run() {
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

        holder.heading = convertView.findViewById(R.id.heading);
        holder.heading.setText(task.getTaskName().trim());


        convertView.setTag(holder);
        return convertView;

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
