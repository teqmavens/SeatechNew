package teq.development.seatech.Dashboard.Skeleton;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NotificationSkeleton {

    @SerializedName("status")
    public String status;

    @SerializedName("data")
    public ArrayList<NotificationData> data = null;

    public class NotificationData {

        @SerializedName("notification_id")
        public String notification_id;

        @SerializedName("message")
        public String message;

        @SerializedName("have_read")
        public String have_read;

        @SerializedName("created")
        public String created;

    }
}
