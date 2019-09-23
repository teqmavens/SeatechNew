package teq.development.seatech.Schedule.Skeleton;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ScheduleWeekViewSkeleton {

    public ScheduleWeekViewSkeleton() {}

    @SerializedName("status")
    public String status;

    @SerializedName("data")
    public ArrayList<AllData> data = null;

    public class AllData {

        @SerializedName("day")
        public String day;

        @SerializedName("date")
        public String date;

        @SerializedName("schedules")
        public ArrayList<Scheduled> scheduled = null;
    }

    public static class Scheduled {

        public Scheduled() {}
        @SerializedName("job_id")
        public String jobid;

        @SerializedName("customer_name")
        public String customer_name;

        @SerializedName("tech_name")
        public String tech_name;

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

        @SerializedName("region_color")
        public String region_color;

        @SerializedName("region_text_color")
        public String region_text_color;

        @SerializedName("sales_order")
        public String sales_order;

        @SerializedName("duration")
        public String duration;

        @SerializedName("start_date_time")
        public String start_date_time;

        @SerializedName("frmt_start_time")
        public String frmt_start_time;
    }
}
