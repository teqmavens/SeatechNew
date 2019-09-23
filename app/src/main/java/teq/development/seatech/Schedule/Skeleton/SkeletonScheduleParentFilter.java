package teq.development.seatech.Schedule.Skeleton;

import java.util.ArrayList;

import teq.development.seatech.Dashboard.Skeleton.ScheduleFilterSkeleton;

public class SkeletonScheduleParentFilter {
    ArrayList<ScheduleFilterSkeleton.RegionData> arrayListRegion;

    public ArrayList<ScheduleFilterSkeleton.CustomerData> getArrayListCustomer() {
        return arrayListCustomer;
    }

    public void setArrayListCustomer(ArrayList<ScheduleFilterSkeleton.CustomerData> arrayListCustomer) {
        this.arrayListCustomer = arrayListCustomer;
    }

    ArrayList<ScheduleFilterSkeleton.CustomerData> arrayListCustomer;

    public ArrayList<ScheduleFilterSkeleton.RegionData> getArrayListRegion() {
        return arrayListRegion;
    }

    public void setArrayListRegion(ArrayList<ScheduleFilterSkeleton.RegionData> arrayListRegion) {
        this.arrayListRegion = arrayListRegion;
    }

    public ArrayList<ScheduleFilterSkeleton.TechnicianData> getArrayListTechnician() {
        return arrayListTechnician;
    }

    public void setArrayListTechnician(ArrayList<ScheduleFilterSkeleton.TechnicianData> arrayListTechnician) {
        this.arrayListTechnician = arrayListTechnician;
    }

    ArrayList<ScheduleFilterSkeleton.TechnicianData> arrayListTechnician;

    public ArrayList<ScheduleFilterSkeleton.JobsData> getArrayListJobs() {
        return arrayListJobs;
    }

    public void setArrayListJobs(ArrayList<ScheduleFilterSkeleton.JobsData> arrayListJobs) {
        this.arrayListJobs = arrayListJobs;
    }

    ArrayList<ScheduleFilterSkeleton.JobsData> arrayListJobs;
}
