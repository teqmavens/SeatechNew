package teq.development.seatech.Dashboard.Skeleton;

import android.os.Parcel;
import android.os.Parcelable;

public class DashboardNotes_Skeleton implements Parcelable{

    String notes;
    String createdAt;
    String tech_id;
    String job_id;

    public DashboardNotes_Skeleton() {}

    protected DashboardNotes_Skeleton(Parcel in) {
        notes = in.readString();
        createdAt = in.readString();
        tech_id = in.readString();
        job_id = in.readString();
        type = in.readString();
        noteWriter = in.readString();
    }

    public static final Creator<DashboardNotes_Skeleton> CREATOR = new Creator<DashboardNotes_Skeleton>() {
        @Override
        public DashboardNotes_Skeleton createFromParcel(Parcel in) {
            return new DashboardNotes_Skeleton(in);
        }

        @Override
        public DashboardNotes_Skeleton[] newArray(int size) {
            return new DashboardNotes_Skeleton[size];
        }
    };

    public String getTechid() {
        return tech_id;
    }

    public void setTechid(String tech_id) {
        this.tech_id = tech_id;
    }

    public String getJobid() {
        return job_id;
    }

    public void setJobid(String job_id) {
        this.job_id = job_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    String type;
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getNoteWriter() {
        return noteWriter;
    }

    public void setNoteWriter(String noteWriter) {
        this.noteWriter = noteWriter;
    }

    String noteWriter;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(notes);
        dest.writeString(createdAt);
        dest.writeString(tech_id);
        dest.writeString(job_id);
        dest.writeString(type);
        dest.writeString(noteWriter);
    }
}
