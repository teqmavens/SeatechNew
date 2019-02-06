package teq.development.seatech.CustomCalendar;

import java.util.ArrayList;

public class CalTimeline_Skeleton {
    String techname;

    public String getTechname() {
        return techname;
    }

    public void setTechname(String techname) {
        this.techname = techname;
    }

    public ArrayList<EventSkeleton> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<EventSkeleton> arrayList) {
        this.arrayList = arrayList;
    }

    ArrayList<EventSkeleton> arrayList;
}
