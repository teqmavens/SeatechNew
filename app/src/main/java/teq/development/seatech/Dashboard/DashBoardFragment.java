package teq.development.seatech.Dashboard;

import android.app.DatePickerDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;
import java.util.Date;

import teq.development.seatech.Dashboard.Adapter.AdapterDashbrdUrgentMsg;
import teq.development.seatech.Dashboard.Adapter.AdapterJosbForYou;
import teq.development.seatech.Dashboard.Adapter.AdapterPickUpJobs;
import teq.development.seatech.JobDetail.JobDetailFragment;
import teq.development.seatech.JobDetail.NeedChangeOrderDialog;
import teq.development.seatech.R;
import teq.development.seatech.databinding.FrgmDashboardBinding;

public class DashBoardFragment extends Fragment {

    FrgmDashboardBinding binding;
    private DashBoardActivity activity;
    private Context context;
    private AdapterJosbForYou adapterjobs;
    //   private AdapterPickUpJobs adapterpickup;
    private AdapterDashbrdUrgentMsg adapterurgentmsg;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        activity = (DashBoardActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgm_dashboard, container, false);
        binding = DataBindingUtil.bind(rootView);
        binding.setFrgmdashboard(this);
        initViews();
        binding.map.onCreate(savedInstanceState);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        initViews(binding);
        return rootView;
    }

    private void initViews() {
        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                // updateLabel();
            }
        };
        binding.previousdate.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(context, R.drawable.leftarrow), null, null, null);
        binding.nextdate.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(context, R.drawable.rightarrow), null);
    }

    @Override
    public void onResume() {
        binding.map.onResume();
        super.onResume();
        binding.map.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng latlong1 = new LatLng(39.755812, 105.221218);
                LatLng latlong2 = new LatLng(39.6659845, 105.243887);
                LatLng latlong3 = new LatLng(39.6333213, 105.3172146);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(latlong2).zoom(8).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                googleMap.addMarker(new MarkerOptions().position(latlong1).title("Marker Title").snippet("Marker Description").
                        icon(BitmapDescriptorFactory.fromResource(R.drawable.mappinpng)));
                googleMap.addMarker(new MarkerOptions().position(latlong2).title("Marker Title").snippet("Marker Description").
                        icon(BitmapDescriptorFactory.fromResource(R.drawable.mappinpng)));
                googleMap.addMarker(new MarkerOptions().position(latlong3).title("Marker Title").snippet("Marker Description").
                        icon(BitmapDescriptorFactory.fromResource(R.drawable.mappinpng)));
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.map.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.map.onLowMemory();
    }

    private void initViews(FrgmDashboardBinding binding) {
        LinearLayoutManager lLManagerJobs = new LinearLayoutManager(getActivity());
        lLManagerJobs.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rcyviewJobs.setLayoutManager(lLManagerJobs);
        adapterjobs = new AdapterJosbForYou(context,DashBoardFragment.this);
        binding.rcyviewJobs.setAdapter(adapterjobs);

        LinearLayoutManager lLManagerUrgentJobs = new LinearLayoutManager(getActivity());
        lLManagerUrgentJobs.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rcyviewUrgentmsg.setLayoutManager(lLManagerUrgentJobs);
        adapterurgentmsg = new AdapterDashbrdUrgentMsg(context,DashBoardFragment.this);
        binding.rcyviewUrgentmsg.setAdapter(adapterurgentmsg);
    }

    public void OnClickStartTime() {
        // Toast.makeText(activity, String.valueOf(getNear15Minute()), Toast.LENGTH_SHORT).show();
       // Toast.makeText(activity, String.valueOf(getplusminus(getNear15Minute())), Toast.LENGTH_SHORT).show();
        //getplusminus(getNear15Minute());
        activity.replaceFragment(new JobDetailFragment());
    }

    public static int getNear15Minute() {
        Calendar calendar = Calendar.getInstance();
        int minutes = calendar.get(Calendar.MINUTE);
     //   calendar

        int mod = minutes % 15;
        int res = 0;
        if ((mod) >= 8) {
            res = minutes + (15 - mod);
        } else {
            res = minutes - mod;
        }
        return (res % 60);
    }

    public void getplusminus(int n) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.MINUTE, n);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.MINUTE,-15);
        Toast.makeText(activity, calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE), Toast.LENGTH_SHORT).show();

        calendar.add(Calendar.MINUTE,15);
        Toast.makeText(activity, calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE), Toast.LENGTH_SHORT).show();

        calendar.add(Calendar.MINUTE,15);
        Toast.makeText(activity, calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE), Toast.LENGTH_SHORT).show();
    }

    public Date getQuarter() {
        Calendar calendar = Calendar.getInstance();
        int mins = calendar.get(Calendar.MINUTE);
        if (mins >= 0 && mins < 8) {
            mins = 0;
        } else if (mins >= 8 && mins < 23) {
            mins = 15;
        } else if (mins >= 23 && mins < 38) {
            mins = 30;
        } else if (mins >= 38 && mins < 53) {
            mins = 45;
        } else {
            mins = 0;
        }
        calendar.set(Calendar.MINUTE, mins);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public void OnClickCalendar() {
        new DatePickerDialog(getActivity(), date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void onClickTicketNo(){
        activity.replaceFragment(new JobDetailFragment());
    }
    public void onClickNotes(){
        dialogTechViewNotes();
    }
    private void dialogTechViewNotes() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialogviewnotes");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        DialogFragment newFragment = ViewTechNotesDialog.newInstance(8);
        newFragment.show(ft, "dialogviewnotes");
    }
}
