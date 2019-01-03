package com.dmt_winches.maintenance.Adapters;

import java.util.ArrayList;

public class TaskInfo {
    private String taskName;
    private String taskDescription;
    private String taskId;
    private String taskBuilding;
    private String taskFloor;
    private String taskRoom;
    private String taskCoordX;
    private String taskCoordY;
    private String taskLeaderId;
    private String leaderName;
    private String taskWorkerId;
    private String workerName;
    private String taskStatus;
    private String taskAddDate;
    private String taskFinishDate;
    private String workId;
    private ArrayList<WorkTimes> workTimes;

    public ArrayList<WorkTimes> getWorkTimes() {
        return workTimes;
    }

    public void setWorkTimes(ArrayList<WorkTimes> work_times) {
        this.workTimes = work_times;
    }

    public TaskInfo() {
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getTaskBuilding() {
        return taskBuilding;
    }

    public String getTaskFloor() {
        return taskFloor;
    }

    public String getTaskRoom() {
        return taskRoom;
    }

    public String getTaskCoordX() {
        return taskCoordX;
    }

    public String getTaskCoordY() {
        return taskCoordY;
    }

    public String getTaskLeaderId() {
        return taskLeaderId;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public String getTaskWorkerId() {
        return taskWorkerId;
    }

    public String getWokerName() {
        return workerName;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public String getTaskAddDate() {
        return taskAddDate;
    }

    public String getTaskFinishDate() {
        return taskFinishDate;
    }

    public String getWorkId() {
        return workId;
    }

    public void setTaskDescription(String task_description) {
        this.taskDescription = task_description;
    }

    public void setTaskId(String task_id) {
        this.taskId = task_id;
    }

    public void setTaskBuilding(String task_building) {
        this.taskBuilding = task_building;
    }

    public void setTaskFloor(String task_floor) {
        this.taskFloor = task_floor;
    }

    public void setTaskRoom(String task_room) {
        this.taskRoom = task_room;
    }

    public void setTaskCoordX(String task_coord_x) {
        this.taskCoordX = task_coord_x;
    }

    public void setTaskCoordY(String task_coord_y) {
        this.taskCoordY = task_coord_y;
    }

    public void setTaskLeaderId(String task_leader_id) {
        this.taskLeaderId = task_leader_id;
    }

    public void setLeaderName(String leader_name) {
        this.leaderName = leader_name;
    }

    public void setTaskWorkerId(String task_worker_id) {
        this.taskWorkerId = task_worker_id;
    }

    public void setWorkerName(String woker_name) {
        this.workerName = woker_name;
    }

    public void setTaskStatus(String task_status) {
        this.taskStatus = task_status;
    }

    public void setTaskAddDate(String task_add_date) {
        this.taskAddDate = task_add_date;
    }

    public void setTaskFinishDate(String task_finish_date) {
        this.taskFinishDate = task_finish_date;
    }

    public void setTaskName(String task_name) {
        this.taskName = task_name;
    }

    public void setWorkId(String work_id) {
        this.workId = work_id;
    }
}