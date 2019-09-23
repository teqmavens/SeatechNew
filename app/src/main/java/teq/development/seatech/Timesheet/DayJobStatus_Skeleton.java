package teq.development.seatech.Timesheet;

public class DayJobStatus_Skeleton {

    public String getJob_id() {
        return job_id;
    }

    public void setJob_id(String job_id) {
        this.job_id = job_id;
    }

    String job_id;
    String labour_code;

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    String customer_name;

    public String getLabour_code() {
        return labour_code;
    }

    public void setLabour_code(String labour_code) {
        this.labour_code = labour_code;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    String hours;
    String starttime;

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    String endtime;
}
