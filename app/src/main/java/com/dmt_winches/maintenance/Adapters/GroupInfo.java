package com.dmt_winches.maintenance.Adapters;

import org.json.JSONArray;

import java.util.ArrayList;

public class GroupInfo {

    private String status;

    private String task_name;
    private String task_description;
    private String task_id;
    private String task_building;
    private String task_floor;
    private String task_room;
    private String task_coord_x;
    private String task_coord_y;
    private String task_leader_id;
    private String leader_name;
    private String task_worker_id;
    private String woker_name;
    private String task_status;
    private String task_add_date;
    private String task_finish_date;
    private String work_id;

    ArrayList<JSONArray> work_times ;

    public GroupInfo(){


    }
    public String getStatus() { return status; }
    public String getTask_name() { return task_name; }
    public String getTask_description() { return task_description; }
    public String getTask_id() { return task_id; }
    public String getTask_building() { return task_building; }
    public String getTask_floor() { return task_floor; }
    public String getTask_room() { return task_room; }
    public String getTask_coord_x() { return task_coord_x; }
    public String getTask_coord_y() { return task_coord_y; }
    public String getTask_leader_id() { return task_leader_id; }
    public String getLeader_name() { return leader_name; }
    public String getTask_worker_id() { return task_worker_id; }
    public String getWoker_name() { return woker_name; }
    public String getTask_status() { return task_status; }
    public String getTask_add_date() { return task_add_date; }
    public String getTask_finish_date() { return task_finish_date; }
    public String getWork_id() { return work_id; }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTask_description(String task_description) {
        this.task_description = task_description;
    }



    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public void setTask_building(String task_building) {
        this.task_building = task_building;
    }

    public void setTask_floor(String task_floor) {
        this.task_floor = task_floor;
    }

    public void setTask_room(String task_room) {
        this.task_room = task_room;
    }

    public void setTask_coord_x(String task_coord_x) {
        this.task_coord_x = task_coord_x;
    }

    public void setTask_coord_y(String task_coord_y) {
        this.task_coord_y = task_coord_y;
    }

    public void setTask_leader_id(String task_leader_id) {
        this.task_leader_id = task_leader_id;
    }

    public void setLeader_name(String leader_name) {
        this.leader_name = leader_name;
    }

    public void setTask_worker_id(String task_worker_id) {
        this.task_worker_id = task_worker_id;
    }

    public void setWoker_name(String woker_name) {
        this.woker_name = woker_name;
    }

    public void setTask_status(String task_status) {
        this.task_status = task_status;
    }

    public void setTask_add_date(String task_add_date) {
        this.task_add_date = task_add_date;
    }

    public void setTask_finish_date(String task_finish_date) {
        this.task_finish_date = task_finish_date;
    }
    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public void setWork_id(String work_id){this.work_id=work_id;}


}