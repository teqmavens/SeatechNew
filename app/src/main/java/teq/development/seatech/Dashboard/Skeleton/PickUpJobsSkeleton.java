package teq.development.seatech.Dashboard.Skeleton;

public class PickUpJobsSkeleton {
    String customerName;
    String jobTicketNo;
    String CustomerType;

    public String getEstimate_hours() {
        return estimate_hours;
    }

    public void setEstimate_hours(String estimate_hours) {
        this.estimate_hours = estimate_hours;
    }

    String estimate_hours;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getJobTicketNo() {
        return jobTicketNo;
    }

    public void setJobTicketNo(String jobTicketNo) {
        this.jobTicketNo = jobTicketNo;
    }

    public String getCustomerType() {
        return CustomerType;
    }

    public void setCustomerType(String customerType) {
        CustomerType = customerType;
    }

    public String getRegionName() {
        return RegionName;
    }

    public void setRegionName(String regionName) {
        RegionName = regionName;
    }

    String RegionName;
}
