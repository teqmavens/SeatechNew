package teq.development.seatech.Dashboard.Skeleton;

import android.databinding.BaseObservable;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class AllJobsSkeleton implements Parcelable {

    String CustomerName;

    public AllJobsSkeleton(){}


    protected AllJobsSkeleton(Parcel in) {
        CustomerName = in.readString();
        job_completed = in.readString();
        boatmakeYear = in.readString();
        boatmodelLength = in.readString();
        boatName = in.readString();
        hullid = in.readString();
        captainname = in.readString();
        CustomerType = in.readString();
        JobmsgCount = in.readString();
        needpart = in.readString();
        havepart = in.readString();
        salesorder = in.readString();
        jobdescription = in.readString();
        supplyamount = in.readString();
        arrayListLaborPerf = in.createTypedArrayList(DashboardNotes_Skeleton.CREATOR);
        arrayListOffTheRecord = in.createTypedArrayList(DashboardNotes_Skeleton.CREATOR);
        arrayListImages = in.createTypedArrayList(UploadImageNewSkeleton.CREATOR);
        arrayList = in.createTypedArrayList(DashboardNotes_Skeleton.CREATOR);
        promisedate = in.readString();
        remainhr = in.readString();
        rep = in.readString();
        jobselection = in.readString();
        ifbid = in.readString();
        qcPerson = in.readString();
        JobticketNo = in.readString();
        JobType = in.readString();
        BoatLocation = in.readString();
        time = in.readString();
        TechSupervisor = in.readString();
        OtherMembers = in.readString();
        PartLocation = in.readString();
        joblatitude = in.readString();
        joblongitude = in.readString();
        flagtype = in.readString();
    }

    public static final Creator<AllJobsSkeleton> CREATOR = new Creator<AllJobsSkeleton>() {
        @Override
        public AllJobsSkeleton createFromParcel(Parcel in) {
            return new AllJobsSkeleton(in);
        }

        @Override
        public AllJobsSkeleton[] newArray(int size) {
            return new AllJobsSkeleton[size];
        }
    };

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getJob_completed() {
        return job_completed;
    }

    public void setJob_completed(String job_completed) {
        this.job_completed = job_completed;
    }

    public String getBoatmakeYear() {
        return boatmakeYear;
    }

    public void setBoatmakeYear(String boatmakeYear) {
        this.boatmakeYear = boatmakeYear;
    }

    public String getBoatmodelLength() {
        return boatmodelLength;
    }

    public void setBoatmodelLength(String boatmodelLength) {
        this.boatmodelLength = boatmodelLength;
    }

    public String getBoatName() {
        return boatName;
    }

    public void setBoatName(String boatName) {
        this.boatName = boatName;
    }

    public String getHullid() {
        return hullid;
    }

    public void setHullid(String hullid) {
        this.hullid = hullid;
    }

    public String getCaptainname() {
        return captainname;
    }

    public void setCaptainname(String captainname) {
        this.captainname = captainname;
    }

    public String getCustomerType() {
        return CustomerType;
    }

    public void setCustomerType(String customerType) {
        CustomerType = customerType;
    }

    public String getJobmsgCount() {
        return JobmsgCount;
    }

    public void setJobmsgCount(String jobmsgCount) {
        JobmsgCount = jobmsgCount;
    }

    public String getNeedpart() {
        return needpart;
    }

    public void setNeedpart(String needpart) {
        this.needpart = needpart;
    }

    public String getHavepart() {
        return havepart;
    }

    public void setHavepart(String havepart) {
        this.havepart = havepart;
    }

    public String getSalesorder() {
        return salesorder;
    }

    public void setSalesorder(String salesorder) {
        this.salesorder = salesorder;
    }

    public String getJobdescription() {
        return jobdescription;
    }

    public void setJobdescription(String jobdescription) {
        this.jobdescription = jobdescription;
    }

    public String getSupplyamount() {
        return supplyamount;
    }

    public void setSupplyamount(String supplyamount) {
        this.supplyamount = supplyamount;
    }

    public ArrayList<DashboardNotes_Skeleton> getArrayListLaborPerf() {
        return arrayListLaborPerf;
    }

    public void setArrayListLaborPerf(ArrayList<DashboardNotes_Skeleton> arrayListLaborPerf) {
        this.arrayListLaborPerf = arrayListLaborPerf;
    }

    public ArrayList<PartsSkeleton> getArrayListParts() {
        return arrayListParts;
    }

    public void setArrayListParts(ArrayList<PartsSkeleton> arrayListParts) {
        this.arrayListParts = arrayListParts;
    }

    public ArrayList<UrgentMsgSkeleton> getArrayListUrgent() {
        return arrayListUrgent;
    }

    public void setArrayListUrgent(ArrayList<UrgentMsgSkeleton> arrayListUrgent) {
        this.arrayListUrgent = arrayListUrgent;
    }

    public ArrayList<DashboardNotes_Skeleton> getArrayListOffTheRecord() {
        return arrayListOffTheRecord;
    }

    public void setArrayListOffTheRecord(ArrayList<DashboardNotes_Skeleton> arrayListOffTheRecord) {
        this.arrayListOffTheRecord = arrayListOffTheRecord;
    }

    public ArrayList<UploadImageNewSkeleton> getArrayListImages() {
        return arrayListImages;
    }

    public void setArrayListImages(ArrayList<UploadImageNewSkeleton> arrayListImages) {
        this.arrayListImages = arrayListImages;
    }

    public ArrayList<DashboardNotes_Skeleton> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<DashboardNotes_Skeleton> arrayList) {
        this.arrayList = arrayList;
    }

    public ArrayList<TimeSpentSkeleton> getArrayListLC() {
        return arrayListLC;
    }

    public void setArrayListLC(ArrayList<TimeSpentSkeleton> arrayListLC) {
        this.arrayListLC = arrayListLC;
    }

    public String getPromisedate() {
        return promisedate;
    }

    public void setPromisedate(String promisedate) {
        this.promisedate = promisedate;
    }

    public String getRemainhr() {
        return remainhr;
    }

    public void setRemainhr(String remainhr) {
        this.remainhr = remainhr;
    }

    public String getRep() {
        return rep;
    }

    public void setRep(String rep) {
        this.rep = rep;
    }

    public String getJobselection() {
        return jobselection;
    }

    public void setJobselection(String jobselection) {
        this.jobselection = jobselection;
    }

    public String getIfbid() {
        return ifbid;
    }

    public void setIfbid(String ifbid) {
        this.ifbid = ifbid;
    }

    public String getQcPerson() {
        return qcPerson;
    }

    public void setQcPerson(String qcPerson) {
        this.qcPerson = qcPerson;
    }

    public String getJobticketNo() {
        return JobticketNo;
    }

    public void setJobticketNo(String jobticketNo) {
        JobticketNo = jobticketNo;
    }

    public String getJobType() {
        return JobType;
    }

    public void setJobType(String jobType) {
        JobType = jobType;
    }

    public String getBoatLocation() {
        return BoatLocation;
    }

    public void setBoatLocation(String boatLocation) {
        BoatLocation = boatLocation;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTechSupervisor() {
        return TechSupervisor;
    }

    public void setTechSupervisor(String techSupervisor) {
        TechSupervisor = techSupervisor;
    }

    public String getOtherMembers() {
        return OtherMembers;
    }

    public void setOtherMembers(String otherMembers) {
        OtherMembers = otherMembers;
    }

    public String getPartLocation() {
        return PartLocation;
    }

    public void setPartLocation(String partLocation) {
        PartLocation = partLocation;
    }

    public String getJoblatitude() {
        return joblatitude;
    }

    public void setJoblatitude(String joblatitude) {
        this.joblatitude = joblatitude;
    }

    public String getJoblongitude() {
        return joblongitude;
    }

    public void setJoblongitude(String joblongitude) {
        this.joblongitude = joblongitude;
    }

    public String getFlagtype() {
        return flagtype;
    }

    public void setFlagtype(String flagtype) {
        this.flagtype = flagtype;
    }

    String job_completed;





    String boatmakeYear;
    String boatmodelLength;
    String boatName;
    String hullid;
    String captainname;
    String CustomerType;
    String JobmsgCount;
    String needpart;
    String havepart;






    String salesorder;
    String jobdescription;
    String supplyamount;
    ArrayList<DashboardNotes_Skeleton> arrayListLaborPerf;
    ArrayList<PartsSkeleton> arrayListParts;
    ArrayList<UrgentMsgSkeleton> arrayListUrgent;
    ArrayList<DashboardNotes_Skeleton> arrayListOffTheRecord;
    ArrayList<UploadImageNewSkeleton> arrayListImages;
    ArrayList<DashboardNotes_Skeleton> arrayList;
    ArrayList<TimeSpentSkeleton> arrayListLC;

    String promisedate,remainhr;
    String rep;
    String jobselection;
    String ifbid;
    String qcPerson;
    String JobticketNo;
    String JobType;
    String BoatLocation;
    String time;
    String TechSupervisor;
    String OtherMembers;
    String PartLocation;
    String joblatitude;
    String joblongitude,flagtype;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(CustomerName);
        dest.writeString(job_completed);
        dest.writeString(boatmakeYear);
        dest.writeString(boatmodelLength);
        dest.writeString(boatName);
        dest.writeString(hullid);
        dest.writeString(captainname);
        dest.writeString(CustomerType);
        dest.writeString(JobmsgCount);
        dest.writeString(needpart);
        dest.writeString(havepart);
        dest.writeString(salesorder);
        dest.writeString(jobdescription);
        dest.writeString(supplyamount);
        dest.writeTypedList(arrayListLaborPerf);
        dest.writeTypedList(arrayListOffTheRecord);
        dest.writeTypedList(arrayListImages);
        dest.writeTypedList(arrayList);
        dest.writeString(promisedate);
        dest.writeString(remainhr);
        dest.writeString(rep);
        dest.writeString(jobselection);
        dest.writeString(ifbid);
        dest.writeString(qcPerson);
        dest.writeString(JobticketNo);
        dest.writeString(JobType);
        dest.writeString(BoatLocation);
        dest.writeString(time);
        dest.writeString(TechSupervisor);
        dest.writeString(OtherMembers);
        dest.writeString(PartLocation);
        dest.writeString(joblatitude);
        dest.writeString(joblongitude);
        dest.writeString(flagtype);
    }
}
