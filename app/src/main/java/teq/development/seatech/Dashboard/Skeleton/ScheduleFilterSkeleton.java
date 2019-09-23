package teq.development.seatech.Dashboard.Skeleton;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ScheduleFilterSkeleton {

    public ScheduleFilterSkeleton(){}

    @SerializedName("status")
    public String status;

    @SerializedName("region")
    public ArrayList<RegionData> regionData = null;

    @SerializedName("technicians")
    public ArrayList<TechnicianData> techniciansData = null;

    @SerializedName("jobs")
    public ArrayList<JobsData> jobsData = null;

    @SerializedName("schedules")
    public ArrayList<SchedulesData> schedulesData = null;

    @SerializedName("customers")
    public ArrayList<CustomerData> customerData = null;

    public class CustomerData {
        @SerializedName("id")
        public String id;

        @SerializedName("name")
        public String name;
    }

    public class RegionData {
        @SerializedName("id")
        public String id;

        @SerializedName("region_name")
        public String region_name;
    }

    public class TechnicianData {
        @SerializedName("id")
        public String id;

        @SerializedName("name")
        public String name;
    }

    public class JobsData {
        @SerializedName("job_id")
        public String jobid;

        @SerializedName("customer_name")
        public String customer_name;
    }

    public static class SchedulesData {

        public SchedulesData(){}
        @SerializedName("date")
        public String date;

        @SerializedName("day")
        public String day;

        @SerializedName("tech")
        public String tech;

        @SerializedName("event")
        public ArrayList<EventData> eventData = null;
    }

    public static class EventData {

        public EventData(){}

        @SerializedName("job_id")
        public String jobid;

        @SerializedName("customer_name")
        public String customer_name;

        @SerializedName("region_name")
        public String region_name;

        @SerializedName("appointment_type_symbol")
        public String appointment_type_symbol;

        @SerializedName("appointment_confirm_symbol")
        public String appointment_confirm_symbol;

        @SerializedName("urgent_symbol")
        public String urgent_symbol;

        @SerializedName("need_part")
        public String need_parts;

        @SerializedName("have_part")
        public String have_parts;

        @SerializedName("region_color")
        public String region_color;

        @SerializedName("region_text_color")
        public String region_text_color;

        @SerializedName("sales_order")
        public String sales_order;

        @SerializedName("duration")
        public String duration;

        @SerializedName("other_members")
        public String other_members;

        @SerializedName("start_date_time")
        public String start_date_time;
    }

}
