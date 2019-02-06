package teq.development.seatech.Dashboard;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.Observer;

import teq.development.seatech.R;
import teq.development.seatech.databinding.ActivityNotificationsBinding;

public class Notifications extends AppCompatActivity {

    VMNotifications viemodel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  setContentView(R.layout.activity_notifications);
        ActivityNotificationsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_notifications);
        //  binding.setViewmodelnotif(new VMNotifications());
        viemodel = new VMNotifications(this, binding);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viemodel.reset();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_lefttoright, R.anim.activity_righttoleft);
    }
}
