package teq.development.seatech.PickUpJobs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.App;
import teq.development.seatech.LoginActivity;
import teq.development.seatech.PickUpJobs.Adapter.AdapterTechnician;
import teq.development.seatech.R;
import teq.development.seatech.Timesheet.Adapter.AdapterDayJobStatus;
import teq.development.seatech.Timesheet.Adapter.AdapterTSMonth;
import teq.development.seatech.Timesheet.Skeleton.TSMonthSkeleton;
import teq.development.seatech.Timesheet.TSMonthChildFragment;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.databinding.PopupScheduletechBinding;

public class ScheduleTechDialog extends DialogFragment {

    Dialog dialog;
    Calendar myCalendarStartDate;
    DatePickerDialog.OnDateSetListener dateStart;
    PopupScheduletechBinding binding;
    public static String estimatedhr, ticketNo;
    ArrayList<TechinicianSkeleton> arrayListTech;
    ArrayList<String> techincianSel_ID;
    AdapterTechnician adapter;
    String hoursNewValue, minNewValue, ampmNewValue, hoursNewValueDur, minNewValueDur = "";
    String selteq_id = "";
    //   StringBuilder sbid;


    static ScheduleTechDialog newInstance(String esthr, String ticketno) {
        ScheduleTechDialog f = new ScheduleTechDialog();
        Bundle args = new Bundle();
        estimatedhr = esthr;
        ticketNo = ticketno;
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.popup_scheduletech, container, false);
        binding = DataBindingUtil.bind(rootView);
        binding.setPopupscheduletech(this);
        initViews();
        return rootView;
    }

    private void initViews() {
        /*  sbid = new StringBuilder();*/
        techincianSel_ID = new ArrayList<>();
        myCalendarStartDate = Calendar.getInstance();
        binding.etEstimatedhr.setText(estimatedhr);
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.apptmnttype_array,
                android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.apptspinner.setAdapter(statusAdapter);

        dateStart = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendarStartDate.set(Calendar.YEAR, year);
                myCalendarStartDate.set(Calendar.MONTH, monthOfYear);
                myCalendarStartDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                if (view.isShown()) {
                    long selectedMilli = myCalendarStartDate.getTimeInMillis();
                    Date datePickerDate = new Date(selectedMilli);

                    SimpleDateFormat datePickerDate_f = new SimpleDateFormat("MM/dd/yyyy");
                    String formattedDate = datePickerDate_f.format(datePickerDate);
                    String formattedDatenew = datePickerDate_f.format(new Date());

                    if (formattedDate.split("/")[1].equalsIgnoreCase(formattedDatenew.split("/")[1])) {
                        binding.etStartdate.setText(HandyObject.getDateFromPickerNew(myCalendarStartDate.getTime()));
                    } else if (datePickerDate.before(new Date())) {
                        HandyObject.showAlert(getActivity(), "Past date is not Allowed");
                    } else {
                        binding.etStartdate.setText(HandyObject.getDateFromPicker(myCalendarStartDate.getTime()));
                    }
                }
            }
        };
    }


    @Override
    public void onResume() {
        super.onResume();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int width = dm.widthPixels;
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = width - 220;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        //params.height = height - 320;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public void OnClickSubmit() {
        // HandyObject.showAlert(getActivity(), "clickkk");

        if (binding.etSelecttech.getText().length() == 0) {
            binding.etSelecttech.requestFocus();
            HandyObject.showAlert(getActivity(), getString(R.string.pleaseseltec));
        } else if (binding.etStartdate.getText().length() == 0) {
            binding.etStartdate.requestFocus();
            HandyObject.showAlert(getActivity(), getString(R.string.startdatereq));
        }
//        else if (binding.etStarttime.getText().length() == 0) {
//            binding.etStarttime.requestFocus();
//            HandyObject.showAlert(getActivity(), getString(R.string.etStarttimereq));
//        } else if (binding.etDuration.getText().length() == 0) {
//            binding.etDuration.requestFocus();
//            HandyObject.showAlert(getActivity(), getString(R.string.durationreq));
//        }
        else if (binding.apptspinner.getSelectedItemPosition() == 0) {
            HandyObject.showAlert(getActivity(), getString(R.string.selectappttype));
        } else {
            if (HandyObject.checkInternetConnection(getActivity())) {
                ScheduleTechnicianSubmit(HandyObject.parseDateToYMDNew(binding.etStartdate.getText().toString()), binding.etStarttime.getText().toString(), binding.etDuration.getText().toString(), ticketNo, getAppointmentType(),
                        getAppointmentConfirm(), binding.etTechdashmemo.getText().toString());

                //HandyObject.showAlert(getActivity(), getAppointmentConfirm());
            } else {
                Toast.makeText(getActivity(), R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void ScheduleTechnicianSubmit(final String startDate,final String startTime,final String duration,final String job_ticket,final String appointment_type,final String appointment_confirm,final String notes) {
        Log.e("sbidddd", selteq_id);
        HandyObject.showProgressDialog(getActivity());
        HandyObject.getApiManagerMain().ScheduleTechnician(selteq_id, startDate, startTime, duration, job_ticket, appointment_type, appointment_confirm, notes, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String jsonResponse = response.body().string();
                            Log.e("responsScheduletech", jsonResponse);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            if (jsonObject.getString("status").toLowerCase().equals("success")) {
                                dialog.dismiss();
                                HandyObject.showAlert(getActivity(), jsonObject.getString("message"));

                                Intent intent = new Intent("schedulereciever");
                                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                            } else {
                                dialog.dismiss();
                                HandyObject.showAlert(getActivity(), jsonObject.getString("message"));
                                if (jsonObject.getString("message").equalsIgnoreCase("Session Expired")) {
                                    HandyObject.clearpref(getActivity());
                                    HandyObject.deleteAllDatabase(getActivity());
                                    App.appInstance.stopTimer();
                                    Intent intent_reg = new Intent(getActivity(), LoginActivity.class);
                                    startActivity(intent_reg);
                                    getActivity().finish();
                                    getActivity().overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            HandyObject.stopProgressDialog();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("responseError", t.getMessage());
                        HandyObject.stopProgressDialog();
                        ScheduleTechnicianSubmit(startDate,startTime,duration,job_ticket,appointment_type,appointment_confirm,notes);

                    }
                });

    }

    public void OnClickDuration() {
        displayDurationDialog();
        /*final Calendar myCalender = Calendar.getInstance();
        int hour = myCalender.get(Calendar.HOUR_OF_DAY);
        int minute = myCalender.get(Calendar.MINUTE);
        TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (view.isShown()) {
                    myCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    myCalender.set(Calendar.MINUTE, minute);
                    binding.etDuration.setText(hourOfDay + ":" + minute);
                }
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, true);
        timePickerDialog.setTitle("Select Duration:");
        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.show();*/
    }

    public void OnClickStartTime() {
    /*       final Calendar myCalender = Calendar.getInstance();
        int hour = myCalender.get(Calendar.HOUR_OF_DAY);
        int minute = myCalender.get(Calendar.MINUTE);
     new TimePickerDialogs(getActivity(), new TimePickerDialog.OnTimeSetListener(){

            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                // TODO Auto-generated method stub
                StringBuilder output = new StringBuilder().append(selectedHour).append(":").append(selectedMinute);
                System.out.println(output.toString());
            }
            }, hour, minute, true).show();*/


        /*final Calendar myCalender = Calendar.getInstance();
        int hour = myCalender.get(Calendar.HOUR_OF_DAY);
        int minute = myCalender.get(Calendar.MINUTE);
        TimePickerDialogs.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (view.isShown()) {
                    myCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    myCalender.set(Calendar.MINUTE, minute);
                    binding.etStarttime.setText(hourOfDay + ":" + minute);
                }
            }
        };
        TimePickerDialogs timePickerDialog = new TimePickerDialogs(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, true);
        timePickerDialog.setTitle("Select Time:");
        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.show();*/

        displayTimeDialog();
    }


    private void displayTimeDialog() {
        final Display display = getActivity().getWindowManager().getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();
        final Dialog mediaDialog = new Dialog(getActivity());
        mediaDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = mediaDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        mediaDialog.setContentView(R.layout.dialog_timepicker);
        LinearLayout approx_lay = (LinearLayout) mediaDialog.findViewById(R.id.approx_lay);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w - 280, (h / 3) - 10);
        approx_lay.setLayoutParams(params);
        final String[] hours = {"00","01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        final String[] mins = {"00", "15", "30", "45"};
        final String[] ampm = {"AM", "PM"};
        NumberPicker np_hour = (NumberPicker) mediaDialog.findViewById(R.id.np_hours);
        NumberPicker np_min = (NumberPicker) mediaDialog.findViewById(R.id.np_min);
        NumberPicker np_ampm = (NumberPicker) mediaDialog.findViewById(R.id.np_ampm);
        np_hour.setMinValue(0);
        np_hour.setMaxValue(hours.length - 1);
        np_hour.setDisplayedValues(hours);
        np_hour.setWrapSelectorWheel(true);

        np_min.setMinValue(0);
        np_min.setMaxValue(mins.length - 1);
        np_min.setDisplayedValues(mins);
        np_min.setWrapSelectorWheel(true);

        np_ampm.setMinValue(0);
        np_ampm.setMaxValue(ampm.length - 1);
        np_ampm.setDisplayedValues(ampm);
        np_ampm.setWrapSelectorWheel(true);
        hoursNewValue = "";
        minNewValue = "";
        ampmNewValue = "";
        //  String[] jdfh = np_hour.getDisplayedValues();
        //  HandyObject.showAlert(getActivity(),jdfh[0]);
        np_hour.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                hoursNewValue = hours[newVal];
            }
        });

        np_min.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                minNewValue = mins[newVal];
            }
        });

        np_ampm.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                ampmNewValue = ampm[newVal];
            }
        });


        TextView cancel = (TextView) mediaDialog.findViewById(R.id.cancel);
        TextView ok = (TextView) mediaDialog.findViewById(R.id.ok);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaDialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaDialog.dismiss();
                if (hoursNewValue.equalsIgnoreCase("")) {
                    hoursNewValue = "00";
                }
                if (minNewValue.equalsIgnoreCase("")) {
                    minNewValue = "00";
                }
                if (ampmNewValue.equalsIgnoreCase("")) {
                    ampmNewValue = "AM";
                }
                binding.etStarttime.setText(hoursNewValue + ":" + minNewValue + " " + ampmNewValue);
            }
        });
        mediaDialog.show();
    }

    private void displayDurationDialog() {
        final Display display = getActivity().getWindowManager().getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();
        final Dialog mediaDialog = new Dialog(getActivity());
        mediaDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = mediaDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        mediaDialog.setContentView(R.layout.dialog_duration);
        LinearLayout approx_lay = (LinearLayout) mediaDialog.findViewById(R.id.approx_lay);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w - 280, (h / 3) - 10);
        approx_lay.setLayoutParams(params);
        final String[] hours = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        final String[] mins = {"00", "15", "30", "45"};

        NumberPicker np_hour = (NumberPicker) mediaDialog.findViewById(R.id.np_hours);
        NumberPicker np_min = (NumberPicker) mediaDialog.findViewById(R.id.np_min);

        np_hour.setMinValue(0);
        np_hour.setMaxValue(hours.length - 1);
        np_hour.setDisplayedValues(hours);
        np_hour.setWrapSelectorWheel(true);

        np_min.setMinValue(0);
        np_min.setMaxValue(mins.length - 1);
        np_min.setDisplayedValues(mins);
        np_min.setWrapSelectorWheel(true);


        hoursNewValueDur = "";
        minNewValueDur = "";

        np_hour.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                hoursNewValueDur = hours[newVal];
            }
        });

        np_min.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                minNewValueDur = mins[newVal];
            }
        });


        TextView cancel = (TextView) mediaDialog.findViewById(R.id.cancel);
        TextView ok = (TextView) mediaDialog.findViewById(R.id.ok);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaDialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaDialog.dismiss();
                if (hoursNewValueDur.equalsIgnoreCase("")) {
                    hoursNewValueDur = "01";
                }
                if (minNewValueDur.equalsIgnoreCase("")) {
                    minNewValueDur = "00";
                }

                binding.etDuration.setText(hoursNewValueDur + ":" + minNewValueDur);
            }
        });
        mediaDialog.show();
    }

    public void OnClickStartDate() {
        new DatePickerDialog(getActivity(), dateStart, myCalendarStartDate
                .get(Calendar.YEAR), myCalendarStartDate.get(Calendar.MONTH),
                myCalendarStartDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void OnClickSelectTech() {
        FetchTechnicianList();
    }

    private void displaypopupaction(View anchorview) {
        for(int i=0;i<arrayListTech.size();i++) {
            for(int k=0; k<techincianSel_ID.size(); k++) {
                if(techincianSel_ID.get(k).equalsIgnoreCase(arrayListTech.get(i).getId())) {
                    arrayListTech.get(i).setStatus(true);
                }
            }
        }
        final PopupWindow popup = new PopupWindow(getActivity());
        //   View layout = context.getLayoutInflater().inflate(R.layout.popup_leave, null);
        View layout = LayoutInflater.from(getActivity()).inflate(R.layout.selecttechnician, null);
        popup.setContentView(layout);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setWidth(480);
        // popup.setHeight(500);
        //   popup.setWidth(420);
        ImageView cross = (ImageView) layout.findViewById(R.id.cross);
        final Button btndone = (Button) layout.findViewById(R.id.btndone);
        RecyclerView recyclerview = (RecyclerView) layout.findViewById(R.id.recyclerview);

        LinearLayoutManager lLManagerDashNotes = new LinearLayoutManager(getActivity());
        lLManagerDashNotes.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(lLManagerDashNotes);
        recyclerview.setAdapter(adapter);
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });

        btndone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                selteq_id = "";
                techincianSel_ID.clear();
                StringBuilder sb = new StringBuilder();
                StringBuilder sbid = new StringBuilder();
                boolean sbcheck, sbchecknew;
                //  sbcheck = false;
                sbchecknew = false;
                for (int i = 0; i < arrayListTech.size(); i++) {
                    if (arrayListTech.get(i).isStatus()) {
                        btndone.setVisibility(View.VISIBLE);
                        sb.append(arrayListTech.get(i).getName().split("-")[0] + ",");
                        sbid.append(arrayListTech.get(i).getId() + ",");
                        techincianSel_ID.add(arrayListTech.get(i).getId());
                        sbchecknew = true;
                    } else {
                        Log.e("dfsd", "sdfasd");
                        //   sb.append(",");
                        //  sbcheck = true;
                    }
                }
                if (sbchecknew == true) {
                    sb.setLength(sb.length() - 1);
                    sbid.setLength(sbid.length() - 1);
                }
                selteq_id = sbid.toString();
                //  HandyObject.showAlert(getActivity(), selteq_id);
                binding.etSelecttech.setText(sb.toString());
            }
        });

        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        popup.showAsDropDown(anchorview);
    }

    private void FetchTechnicianList() {
        //  HandyObject.showProgressDialog(getActivity());
        HandyObject.getApiManagerMain().TechnicianListing(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String jsonResponse = response.body().string();
                            Log.e("responseLC", jsonResponse);
                            arrayListTech = new ArrayList<>();
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            if (jsonObject.getString("status").toLowerCase().equals("success")) {
                                //   HandyObject.showAlert(getActivity(), jsonObject.getString("message"));
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                             //   for(int k=0; k<techincianSel_ID.size(); k++) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jobjinside = jsonArray.getJSONObject(i);
                                        TechinicianSkeleton ske = new TechinicianSkeleton();
                                        ske.setId(jobjinside.getString("id"));
                                        ske.setName(jobjinside.getString("name"));
                                        ske.setStatus(false);
//                                        for(int k=0; k<techincianSel_ID.size(); k++) {
//                                            if(techincianSel_ID.get(k).equalsIgnoreCase(jobjinside.getString("id"))) {
//                                                ske.setStatus(true);
//                                            } else {
//                                                ske.setStatus(false);
//                                            }
//                                        }
                                        arrayListTech.add(ske);
                                    }
                               // }

                                adapter = new AdapterTechnician(getActivity(), arrayListTech);
                                displaypopupaction(binding.etSelecttech);
                            } else {
                                HandyObject.showAlert(getActivity(), jsonObject.getString("message"));
                                if (jsonObject.getString("message").equalsIgnoreCase("Session Expired")) {
                                    HandyObject.clearpref(getActivity());
                                    App.appInstance.stopTimer();
                                    Intent intent_reg = new Intent(getActivity(), LoginActivity.class);
                                    startActivity(intent_reg);
                                    getActivity().finish();
                                    getActivity().overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            //   HandyObject.stopProgressDialog();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("responseError", t.getMessage());
                        //  HandyObject.stopProgressDialog();
                    }
                });
    }

    public void onClickCross() {
        dialog.dismiss();
    }

    private String getAppointmentType() {
        String scheck = "";
        if (binding.apptspinner.getSelectedItemPosition() == 0) {
            scheck = "1";
        } else if (binding.apptspinner.getSelectedItemPosition() == 1) {
            scheck = "2";
        } else if (binding.apptspinner.getSelectedItemPosition() == 2) {
            scheck = "3";
        }
        return scheck;
    }

    private String getAppointmentConfirm() {
        String scheck = "";
        if (binding.checkbox.isChecked()) {
            scheck = "1";
        } else {
            scheck = "0";
        }
        return scheck;
    }
}
