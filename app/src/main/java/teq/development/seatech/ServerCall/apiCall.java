package teq.development.seatech.ServerCall;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import teq.development.seatech.Dashboard.Skeleton.NotificationSkeleton;
import teq.development.seatech.Dashboard.Skeleton.ReadNotificationSkeleton;
import teq.development.seatech.Dashboard.Skeleton.ScheduleFilterSkeleton;
import teq.development.seatech.Schedule.Skeleton.ScheduleCalendarViewSkeleton;
import teq.development.seatech.Schedule.Skeleton.ScheduleDayViewSkeleton;
import teq.development.seatech.Schedule.Skeleton.ScheduleWeekViewSkeleton;
import teq.development.seatech.Timesheet.DayJobStatus_Skeleton;

/**
 * Created by vibrantappz on 12/18/2017.
 */

public interface apiCall {

    @FormUrlEncoded
    @POST("login.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ResponseBody> userLogin(
            @Field("username") String user,
            @Field("password") String password,
               @Field("device_udid") String device_udid
    );

    @FormUrlEncoded
    @POST("my-profile.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ResponseBody> getProfile(
            @Field("id") String id,
            @Header("sessionID") String sessionid
    );

    @FormUrlEncoded
    @POST("change-password.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ResponseBody> changePwd(
            @Field("old_password") String old_password,
            @Field("password1") String password1,
            @Field("password2") String password2,
            @Header("sessionID") String sessionid
    );

    @FormUrlEncoded
    @POST("logout.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ResponseBody> logout(
            @Field("id") String id,
            @Header("sessionID") String sessionid
    );

    @FormUrlEncoded
    @POST("forget-password.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ResponseBody> frgtPwd(
            @Field("email") String email,
            @Header("sessionID") String sessionid
    );

    @POST("edit.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ResponseBody> updateProfile(
            @Body RequestBody files,
            @Header("sessionID") String sessionid
    );

    @POST("technician/technician/save-job-uploads.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ResponseBody> addJobImages(
            @Body RequestBody imageData,
            @Header("sessionID") String sessionid
    );

    @FormUrlEncoded
    @POST("tech-scheduled-jobs-new.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ResponseBody> getDashboradData(
            @Field("tech_id") String email,
            @Field("schedule_date") String date,
            @Header("sessionID") String sessionid
    );

    @FormUrlEncoded
    @POST("technician/technician/save-tech-logs-old.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ResponseBody> submitLCdata(
          /*  @Field("tech_id") String techid,
            @Field("job_id") String jobid,
            @Field("labour_code") String labour_code,
            @Field("start_time") String start_time,
            @Field("end_time") String end_time,
            @Field("hours") String hours,
            @Field("hours_adjusted") String hours_adjusted,
            @Field("created_by") String created_by,*/
            @Field("techlog") String techlog,
            @Header("sessionID") String sessionid
    );

    @FormUrlEncoded
    @POST("technician/technician/save-job-notes-old.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ResponseBody> submitTechLaborPerf(
           /* @Field("created_by") String created_by,
            @Field("job_id") String jobid,
            @Field("notes") String notes,
            @Field("type") String type,*/
            @Field("jobnotes") String jobnotes,
            @Header("sessionID") String sessionid
    );

    @FormUrlEncoded
    @POST("jobs/job-activity-logs-old.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ResponseBody> JobStatusData(
           /* @Field("tech_id") String techid,
            @Field("job_id") String jobid,
            @Field("job_completed") String job_completed,
            @Field("captain_aware") String captain_aware,
            @Field("notes") String notes,
            @Field("supply_amount") String supply_amount,
            @Field("non_billable_hours") String non_billable_hours,
            @Field("non_billable_hours_description") String non_billable_hours_description,
            @Field("return_immediately") String return_immediately,
            @Field("already_scheduled") String already_scheduled,
            @Field("reason") String reason,
            @Field("description") String description,
            @Field("start_time") String start_time,
            @Field("end_time") String end_time,
            @Field("hours") String hours,
            @Field("hours_adjusted") String hours_adjusted,
            @Field("labour_code") String labour_code,*/
            @Field("jobactivitylog") String jobactivitylog,
            @Header("sessionID") String sessionid
    );

    @FormUrlEncoded
    @POST("jobs/tech-start-job.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ResponseBody> startJob(
            @Field("job_id") String jobid,
            @Header("sessionID") String sessionid
    );

    //Get All Manufacturers Api
    @GET("settings/manufacturers.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ResponseBody> GetManufacturerData();

    //Need Part Api
    @POST("jobs/save-part-request-old.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ResponseBody> NeedPartData(
            @Body RequestBody files,
            @Header("sessionID") String sessionid
    );

    @FormUrlEncoded
    @POST("report/get-tech-day-report.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ResponseBody> DayJobStatusDetail(
            @Field("tech_id") String techid,
            @Field("date") String date,
            @Header("sessionID") String sessionid
    );

    @FormUrlEncoded
    @POST("report/get-report-submission-data.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ResponseBody> TSMonthData(
            @Field("tech_id") String techid,
            @Field("start_date") String start_date,
            @Field("end_date") String end_date,
            @Header("sessionID") String sessionid
    );

    @FormUrlEncoded
    @POST("report/get-tech-weekly-report.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ResponseBody> TSWeekData(
            @Field("tech_id") String techid,
            @Field("start_date") String start_date,
            @Field("end_date") String end_date,
            @Header("sessionID") String sessionid
    );

    @FormUrlEncoded
    @POST("jobs/get-pick-up-jobs.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ResponseBody> PickUpJobs(
            @Field("tech_id") String techid,
            @Header("sessionID") String sessionid
    );

    @FormUrlEncoded
    @POST("settings/get-technicians-list.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ResponseBody> TechnicianListing(
            @Field("tech_id") String techid
    );

    @FormUrlEncoded
    @POST("schedule/schedule-pick-up.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ResponseBody> ScheduleTechnician(
            @Field("tech_id") String techid,
            @Field("start_date") String start_date,
            @Field("start_time") String start_time,
            @Field("duration") String duration,
            @Field("job_ticket") String job_ticket,
            @Field("appointment_type") String appointment_type,
            @Field("appointment_confirm") String appointment_confirm,
            @Field("tech_dashboard_note") String tech_dashboard_note,
            @Header("sessionID") String sessionid
    );

    @FormUrlEncoded
    @POST("jobs/get-estimation-request-old.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ResponseBody> NeedEstimate(
           /*@Field("tech_id") String techid,
            @Field("message") String message,
            @Field("job_id") String jobid,
            @Field("urgent") String urgent,*/
            @Field("jobrequest") String jobrequest,
            @Header("sessionID") String sessionid
    );

    // Jobdetail page static
    @FormUrlEncoded
    @POST("jobs/job-details.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ResponseBody> getTicketData(
            @Field("tech_id") String techid,
            @Field("job_id") String jobid,
            @Header("sessionID") String sessionid
    );

    //Chat Apis
    @FormUrlEncoded
    @POST("jobs/chatJobList.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ResponseBody> LeftChatJobList(
            @Field("techid") String jobid,
            @Header("sessionID") String sessionid
    );

    @FormUrlEncoded
    @POST("users/employeesList.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ResponseBody> AllEmployeeList(
            @Field("user_id") String userid,
            @Header("sessionID") String sessionid
    );

    @FormUrlEncoded
    @POST("jobs/chatJobMessages.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ResponseBody> LoadChatMessages(
            @Field("user_id") String userid,
            @Field("job_id") String jobid,
            @Field("offset") String offset,
            @Header("sessionID") String sessionid
    );

    @FormUrlEncoded
    @POST("jobs/JobList.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ResponseBody> AllJobsList(
            @Field("tech_id") String techid,
            @Header("sessionID") String sessionid
    );

   /* @FormUrlEncoded
    @POST("notifications/notificationList.json")
    @Headers("tokenID: 1234567890234dfg456")
    Observable<NotificationSkeleton> NotificationsList(
            @Field("user_id") String userid,
            @Header("sessionID") String sessionid
    );*/

    @FormUrlEncoded
    @POST("notifications/notificationList.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<NotificationSkeleton> NotificationsList(
            @Field("user_id") String userid,
            @Header("sessionID") String sessionid
    );


    /*@FormUrlEncoded
    @POST("notifications/readNotification.json")
    @Headers("tokenID: 1234567890234dfg456")
    Observable<ReadNotificationSkeleton> NotificationsRead(
            @Field("notifification_id") String notifification_id,
            @Header("sessionID") String sessionid
    );*/

    @FormUrlEncoded
    @POST("notifications/readNotification.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ReadNotificationSkeleton> NotificationsRead(
            @Field("notifification_id") String notifification_id,
            @Header("sessionID") String sessionid
    );

    //For Schedule Section ------------
    //Get All filter data with current week scheduled data
    @FormUrlEncoded
    @POST("schedule/schedule-filter-api.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ScheduleFilterSkeleton> GetScheduleFilterData(
            @Field("start_time") String start_time,
            @Field("end_time") String end_time,
            @Field("customer_ids") String customer_ids,
            @Field("tech_ids") String tech_ids,
            @Field("region_id") String region_id,
            @Field("ticket_id") String ticket_id
    );

    //Get Week Data for schedule section
    @FormUrlEncoded
    @POST("schedule/week-view-api.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ScheduleWeekViewSkeleton> GetScheduleWeekViewData(
            @Field("start_date") String start_date,
            @Field("end_date") String end_date,
            @Field("customer_ids") String customer_ids,
            @Field("tech_ids") String tech_ids,
            @Field("region_id") String region_id,
            @Field("ticket_id") String ticket_id
    );

    //Get DayView Data for schedule section
    @FormUrlEncoded
    @POST("schedule/day-view-api.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ScheduleDayViewSkeleton> GetScheduleDayViewData(
            @Field("to_date") String to_date,
            @Field("customer_ids") String customer_ids,
            @Field("tech_ids") String tech_ids,
            @Field("region_id") String region_id,
            @Field("ticket_id") String ticket_id
    );

    //Get Calendar Data for schedule section
    @FormUrlEncoded
    @POST("schedule/month-view-api.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ScheduleCalendarViewSkeleton> GetScheduleCalendarViewData(
            @Field("start_date") String start_date,
            @Field("end_date") String end_date,
            @Field("customer_ids") String customer_ids,
            @Field("tech_ids") String tech_ids,
            @Field("region_id") String region_id,
            @Field("ticket_id") String ticket_id
    );
}
