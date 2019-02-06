package teq.development.seatech.Dashboard.Skeleton;

import android.os.Parcel;
import android.os.Parcelable;

public class UploadImageNewSkeleton implements Parcelable{

    String url;

    public UploadImageNewSkeleton() {

    }

    protected UploadImageNewSkeleton(Parcel in) {
        url = in.readString();
        description = in.readString();
    }

    public static final Creator<UploadImageNewSkeleton> CREATOR = new Creator<UploadImageNewSkeleton>() {
        @Override
        public UploadImageNewSkeleton createFromParcel(Parcel in) {
            return new UploadImageNewSkeleton(in);
        }

        @Override
        public UploadImageNewSkeleton[] newArray(int size) {
            return new UploadImageNewSkeleton[size];
        }
    };

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    String description;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(description);
    }
}
