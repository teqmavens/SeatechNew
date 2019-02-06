package teq.development.seatech.PickUpJobs;

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.TimePicker;

public class TimePickerDialogs extends TimePickerDialog {
    public TimePickerDialogs(Context context, int themeResId, OnTimeSetListener listener, int hourOfDay, int minute, boolean is24HourView) {
        super(context, themeResId, listener, hourOfDay, minute, is24HourView);
    }
   /* public TimePickerDialogs(Context context, OnTimeSetListener listener, int hourOfDay, int minute, boolean is24HourView) {
        super(context, listener, hourOfDay, minute, is24HourView);
    }*/

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        // TODO Auto-generated method stub
        //super.onTimeChanged(arg0, arg1, arg2);
        if (mIgnoreEvent)
            return;
        if (minute%TIME_PICKER_INTERVAL!=0){
            int minuteFloor=minute-(minute%TIME_PICKER_INTERVAL);
            minute=minuteFloor + (minute==minuteFloor+1 ? TIME_PICKER_INTERVAL : 0);
            if (minute==60)
                minute=0;
            mIgnoreEvent=true;
            view.setCurrentMinute(minute);
            mIgnoreEvent=false;
        }
    }

    private final int TIME_PICKER_INTERVAL=15;
    private boolean mIgnoreEvent=false;
}
