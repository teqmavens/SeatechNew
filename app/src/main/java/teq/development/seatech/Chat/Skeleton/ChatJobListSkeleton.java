package teq.development.seatech.Chat.Skeleton;

public class ChatJobListSkeleton {

    public String getNewmsg() {
        return newmsg;
    }

    public void setNewmsg(String newmsg) {
        this.newmsg = newmsg;
    }

    public String newmsg;

    public String getJobid() {
        return jobid;
    }

    public void setJobid(String jobid) {
        this.jobid = jobid;
    }

    String jobid;
    String customer_type;
    String boat_make_year;

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    String customer_name;

    public String getCustomer_type() {
        return customer_type;
    }

    public void setCustomer_type(String customer_type) {
        this.customer_type = customer_type;
    }

    public String getBoat_make_year() {
        return boat_make_year;
    }

    public void setBoat_make_year(String boat_make_year) {
        this.boat_make_year = boat_make_year;
    }

    public String getBoat_name() {
        return boat_name;
    }

    public void setBoat_name(String boat_name) {
        this.boat_name = boat_name;
    }

    String boat_name;
}