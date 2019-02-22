package teq.development.seatech.JobDetail;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.App;
import teq.development.seatech.JobDetail.Adapter.AdapterJobTime;
import teq.development.seatech.JobDetail.Skeleton.JobStatusSkeleton;
import teq.development.seatech.JobDetail.Skeleton.JobTimeSkeleton;
import teq.development.seatech.LoginActivity;
import teq.development.seatech.R;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.database.ParseOpenHelper;
import teq.development.seatech.databinding.DialogJobstatusBinding;

public class JobStatusDialog extends DialogFragment {

    Dialog dialog;
    static String coming_jobid;
    DialogJobstatusBinding binding;
    EditText et_notbilled;
    EditText et_description;
    ArrayList<JobTimeSkeleton> arralistJobTime;
    private AdapterJobTime adapterjobTime;
   // static int count = 0;
    static String selposi_ticketid;
    public static boolean crossclick;
    public static String allValues, editvalues;
    public String jobstatus;
    Context context;
    int timerindex_prev = 0;
    private SQLiteDatabase database;
    public String get_needtoknow, returnimmediate, alreadyScheduled, reason, description = "";
    String hoursNewValue, minNewValue, hoursNewValueDur, minNewValueDur = "";
    boolean popupsubmit;

    static JobStatusDialog newInstance(String jobid, String selecposition_ticketid, String values, String evalues) {
        JobStatusDialog f = new JobStatusDialog();
        Bundle args = new Bundle();
        coming_jobid = jobid;
       // count = selecposition;
        selposi_ticketid = selecposition_ticketid;
        allValues = values;
        editvalues = evalues;

        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int mNum = getArguments().getInt("num");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_jobstatus, container, false);
        binding = DataBindingUtil.bind(rootView);
        binding.setJobstatusdialog(this);
        initViews(binding);
        return rootView;
    }

    private void initViews(final DialogJobstatusBinding binding) {
        context = getActivity();
        binding.checkboxyes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.checkboxno.setChecked(false);
                    binding.checkboxmaybe.setChecked(false);
                    returnimmediate = "";
                    alreadyScheduled = "";
                    get_needtoknow = "";
                    reason = "";
                    description = "";
                    displayPopupwindow(binding.checkboxyes, getString(R.string.doesowner_needtoknow), "yes");
                }
            }
        });

        binding.checkboxno.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.checkboxyes.setChecked(false);
                    binding.checkboxmaybe.setChecked(false);
                    returnimmediate = "";
                    alreadyScheduled = "";
                    get_needtoknow = "";
                    reason = "";
                    description = "";
                    displayPopupwindowNo(binding.checkboxno, "no");
                }
            }
        });

        binding.checkboxmaybe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.checkboxyes.setChecked(false);
                    binding.checkboxno.setChecked(false);
                    returnimmediate = "";
                    alreadyScheduled = "";
                    get_needtoknow = "";
                    reason = "";
                    description = "";
                    displayPopupwindow(binding.checkboxmaybe, getString(R.string.whodecide), "May be");
                }
            }
        });

        binding.checkboxYesowner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.checkboxNoowner.setChecked(false);
                }
            }
        });

        binding.checkboxNoowner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.checkboxYesowner.setChecked(false);
                }
            }
        });

        binding.checkboxYesbillable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.checkboxNobillable.setChecked(false);
                    // PopupNotBillable(binding.checkboxYesbillable);
                    PopupNotBillable(binding.checkboxyes);
                }
            }
        });

        binding.checkboxNobillable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.checkboxYesbillable.setChecked(false);
                }
            }
        });

        updateTimeSpinner();
    }

    private void updateTimeSpinner() {
        arralistJobTime = new ArrayList<>();
        getplusminus(getNear15Minute());
    }

    private String manageMinutes(int min) {
        if (min == 0) {
            return "00";
        } else {
            return String.valueOf(min);
        }
    }

    public void getplusminus(int n) {
        List<String> listtime = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
//        if (isJobRunning() == true) {
//            Log.e("plusminusrunning", "running");
//            calendar.set(Calendar.MINUTE, Integer.parseInt(HandyObject.getPrams(context, AppConstants.JOBRUNNING_CENTERTIME).split(":")[1]));
//            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(HandyObject.getPrams(context, AppConstants.JOBRUNNING_CENTERTIME).split(":")[0]));
//        } else {
            Log.e("notrunning", "notrunning");
            if (n == 0) {
                String withoutzero = String.valueOf(calendar.get(Calendar.MINUTE)).replaceFirst("^0+(?!$)", "");
                if (Integer.parseInt(withoutzero) > 7) {
                    calendar.add(Calendar.HOUR_OF_DAY, 1);
                }
            }
            calendar.set(Calendar.MINUTE, n);
        //}
        calendar.set(Calendar.SECOND, 0);


//        if (HandyObject.getNear15MinuteLB(Integer.parseInt(manageMinutes(calendar.get(Calendar.MINUTE)))) == 0) {
//            String withoutzero = String.valueOf(running_mins).replaceFirst("^0+(?!$)", "");calendar.get(Calendar.MINUTE)
//            if (Integer.parseInt(withoutzero) > 7 && Integer.parseInt(runinngtime.split(":")[0]) == 0) {
//                calendar.add(Calendar.HOUR_OF_DAY, 1);
//            }
//        }

        if(is15WIggle().equalsIgnoreCase("yes")) {
            calendar.add(Calendar.MINUTE, -15);
            listtime.add(calendar.get(Calendar.HOUR_OF_DAY) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
            JobTimeSkeleton jobtimeske = new JobTimeSkeleton();
            jobtimeske.setHrminutes(String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
            jobtimeske.setParsedate(HandyObject.ParseDateJobTime(calendar.getTime()));
            arralistJobTime.add(jobtimeske);

            calendar.add(Calendar.MINUTE, 15);
            listtime.add(calendar.get(Calendar.HOUR_OF_DAY) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
            JobTimeSkeleton jobtimeske2 = new JobTimeSkeleton();
            jobtimeske2.setHrminutes(String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
            jobtimeske2.setParsedate(HandyObject.ParseDateJobTime(calendar.getTime()));
            arralistJobTime.add(jobtimeske2);

            calendar.add(Calendar.MINUTE, 15);
            listtime.add(calendar.get(Calendar.HOUR_OF_DAY) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
            JobTimeSkeleton jobtimeske3 = new JobTimeSkeleton();
            jobtimeske3.setHrminutes(String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
            jobtimeske3.setParsedate(HandyObject.ParseDateJobTime(calendar.getTime()));
            arralistJobTime.add(jobtimeske3);
            adapterjobTime = new AdapterJobTime(context, arralistJobTime);
            binding.timespinner.setAdapter(adapterjobTime);

            binding.timespinner.setOnItemSelectedListener(new TimeItemSelectedListener());
            binding.timespinner.setSelection(1);

        } else {
            calendar.add(Calendar.MINUTE, -60);
            listtime.add(calendar.get(Calendar.HOUR_OF_DAY) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
            JobTimeSkeleton jobtimeske = new JobTimeSkeleton();
            jobtimeske.setHrminutes(String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
            jobtimeske.setParsedate(HandyObject.ParseDateJobTime(calendar.getTime()));
            arralistJobTime.add(jobtimeske);

            calendar.add(Calendar.MINUTE, 15);
            listtime.add(calendar.get(Calendar.HOUR_OF_DAY) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
            JobTimeSkeleton jobtimeske1 = new JobTimeSkeleton();
            jobtimeske1.setHrminutes(String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
            jobtimeske1.setParsedate(HandyObject.ParseDateJobTime(calendar.getTime()));
            arralistJobTime.add(jobtimeske1);

            calendar.add(Calendar.MINUTE, 15);
            listtime.add(calendar.get(Calendar.HOUR_OF_DAY) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
            JobTimeSkeleton jobtimeske2 = new JobTimeSkeleton();
            jobtimeske2.setHrminutes(String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
            jobtimeske2.setParsedate(HandyObject.ParseDateJobTime(calendar.getTime()));
            arralistJobTime.add(jobtimeske2);

            calendar.add(Calendar.MINUTE, 15);
            listtime.add(calendar.get(Calendar.HOUR_OF_DAY) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
            JobTimeSkeleton jobtimeske3 = new JobTimeSkeleton();
            jobtimeske3.setHrminutes(String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
            jobtimeske3.setParsedate(HandyObject.ParseDateJobTime(calendar.getTime()));
            arralistJobTime.add(jobtimeske3);

            calendar.add(Calendar.MINUTE, 15);
            listtime.add(calendar.get(Calendar.HOUR_OF_DAY) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
            JobTimeSkeleton jobtimeske_e = new JobTimeSkeleton();
            jobtimeske_e.setHrminutes(String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
            jobtimeske_e.setParsedate(HandyObject.ParseDateJobTime(calendar.getTime()));
            arralistJobTime.add(jobtimeske_e);




            calendar.add(Calendar.MINUTE, 15);
            listtime.add(calendar.get(Calendar.HOUR_OF_DAY) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
            JobTimeSkeleton jobtimeske4 = new JobTimeSkeleton();
            jobtimeske4.setHrminutes(String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
            jobtimeske4.setParsedate(HandyObject.ParseDateJobTime(calendar.getTime()));
            arralistJobTime.add(jobtimeske4);

            calendar.add(Calendar.MINUTE, 15);
            listtime.add(calendar.get(Calendar.HOUR_OF_DAY) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
            JobTimeSkeleton jobtimeske5 = new JobTimeSkeleton();
            jobtimeske5.setHrminutes(String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
            jobtimeske5.setParsedate(HandyObject.ParseDateJobTime(calendar.getTime()));
            arralistJobTime.add(jobtimeske5);

            calendar.add(Calendar.MINUTE, 15);
            listtime.add(calendar.get(Calendar.HOUR_OF_DAY) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
            JobTimeSkeleton jobtimeske6 = new JobTimeSkeleton();
            jobtimeske6.setHrminutes(String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
            jobtimeske6.setParsedate(HandyObject.ParseDateJobTime(calendar.getTime()));
            arralistJobTime.add(jobtimeske6);

            calendar.add(Calendar.MINUTE, 15);
            listtime.add(calendar.get(Calendar.HOUR_OF_DAY) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
            JobTimeSkeleton jobtimeske7 = new JobTimeSkeleton();
            jobtimeske7.setHrminutes(String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
            jobtimeske7.setParsedate(HandyObject.ParseDateJobTime(calendar.getTime()));
            arralistJobTime.add(jobtimeske7);

            adapterjobTime = new AdapterJobTime(context, arralistJobTime);
            binding.timespinner.setAdapter(adapterjobTime);

            binding.timespinner.setOnItemSelectedListener(new TimeItemSelectedListener());
            binding.timespinner.setSelection(4);

        }
    }

    //Adjust Hours to -15min & +15min
    public int HoursAdjusted(int n) {
        int res = 0;
        if(is15WIggle().equalsIgnoreCase("yes")) {
            if (n == 0) {
                res = -15;
            } else if (n == 2) {
                res = 15;
            }
        } else {
            if (n == 0) {
                res = -60;
            } else if (n == 1) {
                res = -45;
            } else if (n == 2) {
                res = -30;
            } else if (n == 3) {
                res = -15;
            } else if (n == 5) {
                res = 15;
            } else if (n == 6) {
                res = 30;
            } else if (n == 7) {
                res = 45;
            } else if (n == 8) {
                res = 60;
            }
        }

        return res;
    }

    private String is15WIggle() {
        String check="";
        if(HandyObject.getPrams(context, AppConstants.LOGINTEQ_WIGGLEROOM).equalsIgnoreCase("15")) {
            check = "yes";
        } else {
            check = "no";
        }

        return check;
    }

    //Will trigger on the creation of fragment & when select any time from time spinner
    public class TimeItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
            // HandyObject.showAlert(context, String.valueOf(position));
              timerindex_prev = position;
        //    String runinngtime = binding.uploadimage.getText().toString();
        //    int running_mins = Integer.parseInt(runinngtime.split(":")[1]);
//            if (position == 2) {
//                if (running_mins < 15) {
//                    HandyObject.showAlert(context, context.getString(R.string.cantselect_futtime));
//                    binding.timespinner.setSelection(1);
//                }
//            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    public static int getNear15Minute() {
        Calendar calendar = Calendar.getInstance();
        int minutes = calendar.get(Calendar.MINUTE);
        int mod = minutes % 15;
        int res = 0;
        if ((mod) >= 8) {
            res = minutes + (15 - mod);
        } else {
            res = minutes - mod;
        }
        return (res % 60);
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

    @Override
    public void onResume() {
        super.onResume();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int width = dm.widthPixels;
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = width - 260;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        ;
       /* params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;*/
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    private void displayPopupwindow(View anchorview, String text, final String jobstatus) {
        final PopupWindow popup = new PopupWindow(getActivity());
        View layout = getLayoutInflater().inflate(R.layout.popupwd_jcyes, null);
        popup.setContentView(layout);
        //  popup.setOutsideTouchable(false);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageView cross = (ImageView) layout.findViewById(R.id.cross);
        TextView toptext = (TextView) layout.findViewById(R.id.toptext);
        final EditText et_needtoknow = (EditText) layout.findViewById(R.id.et_needtoknow);
        Button submit = (Button) layout.findViewById(R.id.submit);
        toptext.setText(text);

        //   et_needtoknow.requestFocus();

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popup.dismiss();
                if (jobstatus.equalsIgnoreCase("May be")) {
                    binding.checkboxmaybe.setChecked(false);
                } else {
                    binding.checkboxyes.setChecked(false);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (et_needtoknow.getText().toString().length() == 0) {
                    et_needtoknow.requestFocus();
                    HandyObject.showAlert(getActivity(), "This field is empty");
                } else {
                    get_needtoknow = et_needtoknow.getText().toString();
                    if (jobstatus.equalsIgnoreCase("May be")) {
                        binding.checkboxmaybe.setChecked(true);
                    } else {
                        binding.checkboxyes.setChecked(true);
                    }
                    popupsubmit = true;
                    popup.dismiss();
                }
            }
        });

        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (popupsubmit == true) {
                    popupsubmit = false;
                } else {
                    if (jobstatus.equalsIgnoreCase("May be")) {
                        binding.checkboxmaybe.setChecked(false);
                    } else {
                        binding.checkboxyes.setChecked(false);
                    }
                }
            }
        });

        popup.setOutsideTouchable(false);
        popup.setFocusable(true);
        // popup.update();
        popup.showAsDropDown(anchorview);
    }


    private void PopupNotBillable(final View anchorview) {
        final PopupWindow popup = new PopupWindow(getActivity());
        View layout = getLayoutInflater().inflate(R.layout.popupwd_notbillable, null);
        popup.setContentView(layout);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageView cross = (ImageView) layout.findViewById(R.id.cross);
        Button submit = (Button) layout.findViewById(R.id.submit);
        et_notbilled = (EditText) layout.findViewById(R.id.et_notbilled);
        et_description = (EditText) layout.findViewById(R.id.et_description);

        et_notbilled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayTimeDialog();
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_notbilled.getText().toString().length() == 0) {
                    et_notbilled.requestFocus();
                    HandyObject.showAlert(getActivity(), getString(R.string.fieldempty));
                } else if (et_description.getText().toString().length() == 0) {
                    et_description.requestFocus();
                    HandyObject.showAlert(getActivity(), getString(R.string.fieldempty));
                } else {
                    popup.dismiss();
                }
            }
        });

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.checkboxYesbillable.setChecked(false);
                popup.dismiss();
            }
        });
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        popup.showAsDropDown(anchorview);
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
        mediaDialog.setContentView(R.layout.dialog_timepickernew);
        LinearLayout approx_lay = (LinearLayout) mediaDialog.findViewById(R.id.approx_lay);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w - 380, (h / 3) - 40);
        approx_lay.setLayoutParams(params);
        final String[] hours = {"00","01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
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
        hoursNewValue = "";
        minNewValue = "";
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
                et_notbilled.setText(hoursNewValue + ":" + minNewValue);
            }
        });
        mediaDialog.show();
    }


    private void displayPopupwindowNo(final View anchorview, final String jobstatus) {
        final PopupWindow popup = new PopupWindow(getActivity());
        View layout = getLayoutInflater().inflate(R.layout.popupwd_jcno, null);
        popup.setContentView(layout);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageView cross = (ImageView) layout.findViewById(R.id.cross);
        final AppCompatCheckBox checkboxyes = (AppCompatCheckBox) layout.findViewById(R.id.checkboxyes);
        final AppCompatCheckBox checkboxno = (AppCompatCheckBox) layout.findViewById(R.id.checkboxno);

        checkboxyes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkboxno.setChecked(false);
                    returnimmediate = "yes";
                    displayPopupwindowNoYes(anchorview, getActivity(), popup, jobstatus);
                }
            }
        });

        checkboxno.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkboxyes.setChecked(false);
                    returnimmediate = "no";
                    displayPopupwindowNoNo(anchorview, getActivity(), popup, jobstatus);
                }
            }
        });

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                binding.checkboxno.setChecked(false);
            }
        });
        popup.setOutsideTouchable(false);
        // popup.setFocusable(true);
        popup.showAsDropDown(anchorview);
    }

    private void displayPopupwindowNoYes(View anchorview, Context context, final PopupWindow pop, final String jobstatus) {
        final PopupWindow popup = new PopupWindow(context);
        View layout = getLayoutInflater().inflate(R.layout.popupwd_jcnoyes, null);
        popup.setContentView(layout);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageView cross = (ImageView) layout.findViewById(R.id.cross);

        final AppCompatCheckBox checkboxyes = (AppCompatCheckBox) layout.findViewById(R.id.checkboxyes);
        final AppCompatCheckBox checkboxno = (AppCompatCheckBox) layout.findViewById(R.id.checkboxno);

        checkboxyes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkboxno.setChecked(false);
                    popup.dismiss();
                    pop.dismiss();
                    alreadyScheduled = "yes";
                }
            }
        });

        checkboxno.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkboxyes.setChecked(false);
                    popup.dismiss();
                    pop.dismiss();
                    alreadyScheduled = "no";
                }
            }
        });
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });

        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        popup.showAsDropDown(anchorview);
    }

    private void displayPopupwindowNoNo(final View anchorview, Context context, final PopupWindow popupnono, final String jobstatus) {
        final PopupWindow popup = new PopupWindow(context);
        View layout = getLayoutInflater().inflate(R.layout.popupwd_jcnono, null);
        popup.setContentView(layout);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageView cross = (ImageView) layout.findViewById(R.id.cross);
        final AppCompatCheckBox cbx_fcrtyrpr = (AppCompatCheckBox) layout.findViewById(R.id.cbx_fcrtyrpr);
        final AppCompatCheckBox cbx_wtngparts = (AppCompatCheckBox) layout.findViewById(R.id.cbx_wtngparts);
        final AppCompatCheckBox cbx_wtngcstmr = (AppCompatCheckBox) layout.findViewById(R.id.cbx_wtngcstmr);
        final AppCompatCheckBox cbx_otherreason = (AppCompatCheckBox) layout.findViewById(R.id.cbx_otherreason);
        final AppCompatCheckBox cbx_exception = (AppCompatCheckBox) layout.findViewById(R.id.cbx_exception);

        cbx_fcrtyrpr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbx_wtngcstmr.setChecked(false);
                    cbx_wtngparts.setChecked(false);
                    cbx_exception.setChecked(false);
                    popupnono.dismiss();
                    popup.dismiss();
                    reason = "Factory Repair";
                    returnimmediate = "no";
                    description = "";
                }
            }
        });

        cbx_wtngparts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbx_wtngcstmr.setChecked(false);
                    cbx_fcrtyrpr.setChecked(false);
                    cbx_exception.setChecked(false);
                    popupnono.dismiss();
                    popup.dismiss();
                    reason = "Waiting on Parts";
                    returnimmediate = "no";
                    description = "";
                }
            }
        });

        cbx_wtngcstmr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbx_fcrtyrpr.setChecked(false);
                    cbx_wtngparts.setChecked(false);
                    cbx_exception.setChecked(false);
                    popupnono.dismiss();
                    popup.dismiss();
                    //  displayPopupWtngCutomer(anchorview, popupnono, popup, jobstatus);
                    reason = "Waiting for customer";
                    returnimmediate = "no";
                    description = "";
                }
            }
        });

        cbx_otherreason.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbx_fcrtyrpr.setChecked(false);
                    cbx_wtngparts.setChecked(false);
                    cbx_exception.setChecked(false);
                    //  displayPopupWtngCutomer(anchorview, popupnono, popup, jobstatus);
                    displayPopupWtngcutomerReason(anchorview, getString(R.string.otherreason), popupnono, popup, jobstatus);
                }
            }
        });

        cbx_exception.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbx_fcrtyrpr.setChecked(false);
                    cbx_wtngparts.setChecked(false);
                    cbx_wtngcstmr.setChecked(false);
                    displayPopupExceptionPolicy(anchorview, getString(R.string.exceptiontoplcy), popupnono, popup, jobstatus);
                }
            }
        });

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        popup.showAsDropDown(anchorview);
    }


    public void onClickCross() {
        dialog.dismiss();
        Intent intent = new Intent("updateJob");
     //   intent.putExtra("nextjobid", String.valueOf(count));
        intent.putExtra("nextjobid", selposi_ticketid);
        crossclick = true;
        // HandyObject.putPrams(getActivity(), AppConstants.JOBRUNNING_TOTALTIME, "66");
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
        App.appInstance.startTimer();
    }

    public void onClickSubmit() {
        //  dialog.dismiss();

        if (binding.checkboxyes.isChecked() == false && binding.checkboxno.isChecked() == false && binding.checkboxmaybe.isChecked() == false) {
            HandyObject.showAlert(getActivity(), getString(R.string.selectjobtype));
        }

//        else if (binding.etSupplyamount.getText().toString().length() == 0) {
//            binding.etSupplyamount.requestFocus();
//            binding.etSupplyamount.requestFocus();
//            HandyObject.showAlert(getActivity(), getString(R.string.supplyamountreq));
//        }
        else if (binding.checkboxyes.isChecked() == true && binding.checkboxno.isChecked() == false && binding.checkboxmaybe.isChecked() == false) {
            String captainAware = "";
            String notbilled = "";
            String notbilled_desc = "";
            jobstatus = "yes";
            description = get_needtoknow;
            if (binding.etLaborperform.getText().toString().length() == 0) {
                HandyObject.showAlert(getActivity(), getString(R.string.fieldempty_techlabor));
                binding.etLaborperform.requestFocus();
            } else if (binding.checkboxNoowner.isChecked() == false && binding.checkboxYesowner.isChecked() == false) {
                HandyObject.showAlert(getActivity(), "Please select Owner Aware");

            } else if (binding.checkboxYesbillable.isChecked() == false && binding.checkboxNobillable.isChecked() == false) {
                HandyObject.showAlert(getActivity(), getString(R.string.notbillable));

            } else if (binding.checkboxNoowner.isChecked() || binding.checkboxYesowner.isChecked()) {
                if (binding.checkboxNoowner.isChecked()) {
                    captainAware = "no";
                } else if (binding.checkboxYesowner.isChecked()) {
                    captainAware = "yes";
                }
                JobStatusApi(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), selposi_ticketid, jobstatus, captainAware, binding.etLaborperform.getText().toString(),
                        binding.etSupplyamount.getText().toString(), notbilled, notbilled_desc, returnimmediate, alreadyScheduled, reason, description, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID));

            } else if (binding.checkboxYesbillable.isChecked() || binding.checkboxNobillable.isChecked()) {
                if (binding.checkboxYesbillable.isChecked()) {
                    notbilled = et_notbilled.getText().toString();
                    notbilled_desc = et_description.getText().toString();
                } else if (binding.checkboxNobillable.isChecked()) {
                    notbilled = "";
                    notbilled_desc = "";
                }
                JobStatusApi(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), selposi_ticketid, jobstatus, captainAware, binding.etLaborperform.getText().toString(),
                        binding.etSupplyamount.getText().toString(), notbilled, notbilled_desc, returnimmediate, alreadyScheduled, reason, description, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID));
            } else {
                JobStatusApi(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), selposi_ticketid, jobstatus, captainAware, binding.etLaborperform.getText().toString(),
                        binding.etSupplyamount.getText().toString(), notbilled, notbilled_desc, returnimmediate, alreadyScheduled, reason, description, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID));
            }
        } else if (binding.checkboxyes.isChecked() == false && binding.checkboxno.isChecked() == false && binding.checkboxmaybe.isChecked() == true) {
            String captainAware = "";
            String notbilled = "";
            String notbilled_desc = "";
            jobstatus = "May be";
            description = get_needtoknow;
            if (binding.etLaborperform.getText().toString().length() == 0) {
                binding.etLaborperform.requestFocus();
                HandyObject.showAlert(getActivity(), getString(R.string.fieldempty_techlabor));

            } else if (binding.checkboxNoowner.isChecked() == false && binding.checkboxYesowner.isChecked() == false) {
                HandyObject.showAlert(getActivity(), "Please select Owner Aware");

            } else if (binding.checkboxYesbillable.isChecked() == false && binding.checkboxNobillable.isChecked() == false) {
                HandyObject.showAlert(getActivity(), getString(R.string.notbillable));

            } else if (binding.checkboxNoowner.isChecked() || binding.checkboxYesowner.isChecked()) {
                if (binding.checkboxNoowner.isChecked()) {
                    captainAware = "no";
                } else if (binding.checkboxYesowner.isChecked()) {
                    captainAware = "yes";
                }
                JobStatusApi(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), selposi_ticketid, jobstatus, captainAware, binding.etLaborperform.getText().toString(),
                        binding.etSupplyamount.getText().toString(), notbilled, notbilled_desc, returnimmediate, alreadyScheduled, reason, description, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID));

            } else if (binding.checkboxYesbillable.isChecked() || binding.checkboxNobillable.isChecked()) {
                if (binding.checkboxYesbillable.isChecked()) {
                    notbilled = et_notbilled.getText().toString();
                    notbilled_desc = et_description.getText().toString();
                } else if (binding.checkboxNobillable.isChecked()) {
                    notbilled = "";
                    notbilled_desc = "";
                }
                JobStatusApi(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), selposi_ticketid, jobstatus, captainAware, binding.etLaborperform.getText().toString(),
                        binding.etSupplyamount.getText().toString(), notbilled, notbilled_desc, returnimmediate, alreadyScheduled, reason, description, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID));
            } else {
                JobStatusApi(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), selposi_ticketid, jobstatus, captainAware, binding.etLaborperform.getText().toString(),
                        binding.etSupplyamount.getText().toString(), notbilled, notbilled_desc, returnimmediate, alreadyScheduled, reason, description, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID));
            }
        } else if (binding.checkboxyes.isChecked() == false && binding.checkboxno.isChecked() == true && binding.checkboxmaybe.isChecked() == false) {
            String captainAware = "";
            String notbilled = "";
            String notbilled_desc = "";
            jobstatus = "no";
            if (binding.etLaborperform.getText().toString().length() == 0) {
                binding.etLaborperform.requestFocus();
                HandyObject.showAlert(getActivity(), getString(R.string.fieldempty_techlabor));

            } else if (binding.checkboxNoowner.isChecked() == false && binding.checkboxYesowner.isChecked() == false) {
                HandyObject.showAlert(getActivity(), "Please select Owner Aware");

            } else if (binding.checkboxYesbillable.isChecked() == false && binding.checkboxNobillable.isChecked() == false) {
                HandyObject.showAlert(getActivity(), getString(R.string.notbillable));

            } else if (binding.checkboxNoowner.isChecked() || binding.checkboxYesowner.isChecked()) {
                if (binding.checkboxNoowner.isChecked()) {
                    captainAware = "no";
                } else if (binding.checkboxYesowner.isChecked()) {
                    captainAware = "yes";
                }
                JobStatusApi(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), selposi_ticketid, jobstatus, captainAware, binding.etLaborperform.getText().toString(),
                        binding.etSupplyamount.getText().toString(), notbilled, notbilled_desc, returnimmediate, alreadyScheduled, reason, description, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID));

            } else if (binding.checkboxYesbillable.isChecked() || binding.checkboxNobillable.isChecked()) {
                if (binding.checkboxYesbillable.isChecked()) {
                    notbilled = et_notbilled.getText().toString();
                    notbilled_desc = et_description.getText().toString();
                } else if (binding.checkboxNobillable.isChecked()) {
                    notbilled = "";
                    notbilled_desc = "";
                }
                JobStatusApi(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), selposi_ticketid, jobstatus, captainAware, binding.etLaborperform.getText().toString(),
                        binding.etSupplyamount.getText().toString(), notbilled, notbilled_desc, returnimmediate, alreadyScheduled, reason, description, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID));
            } else {
                JobStatusApi(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), selposi_ticketid, jobstatus, captainAware, binding.etLaborperform.getText().toString(),
                        binding.etSupplyamount.getText().toString(), notbilled, notbilled_desc, returnimmediate, alreadyScheduled, reason, description, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID));
            }
        }
    }

                        /*private void displayPopupWtngCutomer(final View anchorview, final PopupWindow popnono, final PopupWindow popwtngcstmr, final String jobstatus) {
                            final PopupWindow popup = new PopupWindow(getActivity());
                            View layout = getLayoutInflater().inflate(R.layout.popupwd_wtngcstmr, null);
                            popup.setContentView(layout);
                            popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                            popup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                            ImageView cross = (ImageView) layout.findViewById(R.id.cross);
                            final AppCompatCheckBox cbx_installation = (AppCompatCheckBox) layout.findViewById(R.id.cbx_installation);
                            final AppCompatCheckBox cbx_otherreason = (AppCompatCheckBox) layout.findViewById(R.id.cbx_otherreason);

                            cbx_installation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {
                                        cbx_otherreason.setChecked(false);
                                        popnono.dismiss();
                                        popup.dismiss();
                                        popwtngcstmr.dismiss();
                                        reason = "Waiting for customer";
                                        returnimmediate = "no";
                                        description = "";
                                    }
                                }
                            });

                            cbx_otherreason.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {
                                        cbx_installation.setChecked(false);
                                        displayPopupWtngcutomerReason(anchorview, getString(R.string.otherreason), popnono, popwtngcstmr, popup, jobstatus);
                                    }
                                }
                            });
                            cross.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popup.dismiss();
                                }
                            });
                            popup.setOutsideTouchable(true);
                            popup.setFocusable(true);
                            popup.showAsDropDown(anchorview);
                        }*/

    private void displayPopupWtngcutomerReason(View anchorview, String text, final PopupWindow popupnono, final PopupWindow poplast,
                                               final String jobstatus) {
        final PopupWindow popup = new PopupWindow(getActivity());
        View layout = getLayoutInflater().inflate(R.layout.popupwd_wtngcstmreason, null);
        popup.setContentView(layout);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageView cross = (ImageView) layout.findViewById(R.id.cross);
        TextView toptext = (TextView) layout.findViewById(R.id.toptext);
        TextView submit = (TextView) layout.findViewById(R.id.submit);
        final EditText et_description = (EditText) layout.findViewById(R.id.et_description);
        toptext.setText(text);

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* popupnono.dismiss();
                popwtngcstmr.dismiss();
                poplast.dismiss();
                popup.dismiss();*/
                if (et_description.getText().toString().length() == 0) {
                    et_description.requestFocus();
                    //  popup.dismiss();
                    HandyObject.showAlert(getActivity(), getString(R.string.fieldempty));
                } else {
                    popupnono.dismiss();
                    //  popwtngcstmr.dismiss();
                    poplast.dismiss();
                    popup.dismiss();
                    returnimmediate = "no";
                    reason = "other reason";
                    description = et_description.getText().toString();
                }
            }
        });
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        popup.showAsDropDown(anchorview);
    }

    private void displayPopupExceptionPolicy(View anchorview, String text, final PopupWindow popnono, final PopupWindow popwtngcstmr, final String jobstatus) {
        final PopupWindow popup = new PopupWindow(getActivity());
        View layout = getLayoutInflater().inflate(R.layout.popupwd_wtngcstmreason, null);
        popup.setContentView(layout);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageView cross = (ImageView) layout.findViewById(R.id.cross);
        TextView toptext = (TextView) layout.findViewById(R.id.toptext);
        TextView submit = (TextView) layout.findViewById(R.id.submit);
        final EditText et_description = (EditText) layout.findViewById(R.id.et_description);
        toptext.setText(text);
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* popnono.dismiss();
                popwtngcstmr.dismiss();
                popup.dismiss();*/

              /*  popnono.dismiss();
                popwtngcstmr.dismiss();
                popup.dismiss();*/

                if (et_description.getText().toString().length() == 0) {
                    et_description.requestFocus();
                    //  popup.dismiss();
                    HandyObject.showAlert(getActivity(), getString(R.string.fieldempty));
                } else {
                    popnono.dismiss();
                    popwtngcstmr.dismiss();
                    popup.dismiss();
                    reason = "Exception to policy";
                    returnimmediate = "no";
                    description = et_description.getText().toString();
                }
            }
        });
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        popup.showAsDropDown(anchorview);
    }

    private void JobStatusApi(String teqid, String jobid, String job_completed, String captainaware, String notes, String supplyAmount, String nbillablehrs,
                              String nbillablehrsDiscription, String returnimmid, String already_scheduled, String reason, String description, String sessionid) {


        String endtimeNew = arralistJobTime.get(timerindex_prev).getParsedate() + " " + arralistJobTime.get(timerindex_prev).getHrminutes();
        String hrsAdjustedNew = String.valueOf(HoursAdjusted(timerindex_prev));


      //  HandyObject.showAlert(context,endtimeNew+"---"+hrsAdjustedNew);


        String edit_capname="";

       // HandyObject.showAlert(getActivity(),allValues);
        String start_T = allValues.split("--")[0];
        String end_T = allValues.split("--")[1];
        String hours = allValues.split("--")[2];
        String hours_adjusted = allValues.split("--")[3];
        String labour_code = allValues.split("--")[4];

        String edit_boatname = editvalues.split("-")[0];
        String edit_hullid = editvalues.split("-")[1];
        if(editvalues.split("-")[2] .equalsIgnoreCase("captainblank")) {
            edit_capname = "";
        } else {
            edit_capname = editvalues.split("-")[2];
        }

        int count_lcjobcomp = HandyObject.getIntPrams(getActivity(), AppConstants.LCJOBCOMPLETION_COUNT);

        JobStatusSkeleton ske = new JobStatusSkeleton();
        ske.setBoat_name(edit_boatname);
        ske.setHull_id(edit_hullid);
        ske.setCaptain_name(edit_capname);
        ske.setTech_id(teqid);
        ske.setJob_id(jobid);
        ske.setJob_completed(job_completed);
        ske.setCaptain_aware(captainaware);
        ske.setNotes(notes);
        if(supplyAmount.equalsIgnoreCase("")) {
            Log.e("Zeerooo","Zeerooo");
            ske.setSupply_amount("0");
        } else {
            ske.setSupply_amount(supplyAmount);
        }
        ske.setNon_billable_hours(nbillablehrs);
        ske.setNon_billable_hours_description(nbillablehrsDiscription);
        ske.setReturn_immediately(returnimmid);
        ske.setAlready_scheduled(already_scheduled);
        ske.setReason(reason);
        ske.setDescription(description);
        ske.setStart_time(start_T);
      //  ske.setEnd_time(end_T);
        ske.setEnd_time(endtimeNew);
        ske.setHours(hours);
        ske.setHours_adjusted(hours_adjusted);

        ske.setHours_adjusted_end(hrsAdjustedNew);

        ske.setLabour_code(labour_code);
        ske.setCount(String.valueOf(count_lcjobcomp));

        ArrayList<JobStatusSkeleton> arrayList = new ArrayList<>();
        arrayList.add(ske);
        Gson gson = new Gson();
        String alldata = gson.toJson(arrayList);
        Log.e("ALLLDATAA",alldata);
        final String insertedTime = HandyObject.ParseDateTimeForNotes(new Date());
        insertToDB(arrayList, insertedTime);
        if (HandyObject.checkInternetConnection(getActivity())) {
            HandyObject.showProgressDialog(getActivity());
            HandyObject.getApiManagerMain().JobStatusData(alldata, sessionid)
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                String jsonResponse = response.body().string();
                                Log.e("responseJobStatus", jsonResponse);
                                JSONObject jsonObject = new JSONObject(jsonResponse);
                                if (jsonObject.getString("status").toLowerCase().equals("success")) {
                                    HandyObject.showAlert(getActivity(), jsonObject.getString("message"));
                                    dialog.dismiss();
                                    //popupWindow.dismiss();
                                    //    App.appInstance.stopTimer();
                                    Intent intent = new Intent("updateJob");
                                   // count++;
                                    //intent.putExtra("nextjobid", String.valueOf(count));
                                    intent.putExtra("nextjobid", coming_jobid);
                                    HandyObject.putPrams(getActivity(), AppConstants.JOBRUNNING_TOTALTIME, "66");
                                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jobj = jsonArray.getJSONObject(i);

                                        //Delete related row from database
                                        database.delete(ParseOpenHelper.TABLE_JOBSTATUS, ParseOpenHelper.JOBSTATUSJOBID + " =? AND " + ParseOpenHelper.JOBSTATUSCREATEDAT + " = ?",
                                                new String[]{jobj.getString("job_id"), insertedTime});
                                    }
                                } else {
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
                        }
                    });
        } else {
            Intent intent = new Intent("updateJob");
            // count++;
            // intent.putExtra("nextjobid", String.valueOf(count));
            intent.putExtra("nextjobid", coming_jobid);
            HandyObject.putPrams(getActivity(), AppConstants.JOBRUNNING_TOTALTIME, "66");
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

            HandyObject.showAlert(getActivity(), getString(R.string.fetchdata_whenonline));
            HandyObject.stopProgressDialog();
            dialog.dismiss();
        }
    }

    private void insertToDB(ArrayList<JobStatusSkeleton> arrayList, String insertedAt) {
        database = ParseOpenHelper.getInstance(getActivity()).getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ParseOpenHelper.JOBSTATUSTECHID, arrayList.get(0).getTech_id());
        cv.put(ParseOpenHelper.JOBSTATUSJOBID, arrayList.get(0).getJob_id());
        cv.put(ParseOpenHelper.JOBSTATUSCOMPLETED, arrayList.get(0).getJob_completed());
        cv.put(ParseOpenHelper.JOBSTATUSCAPTAINAWARE, arrayList.get(0).getCaptain_aware());
        cv.put(ParseOpenHelper.JOBSTATUSNOTES, arrayList.get(0).getNotes());
        cv.put(ParseOpenHelper.JOBSTATUSSUPPLAYAMT, arrayList.get(0).getSupply_amount());
        cv.put(ParseOpenHelper.JOBSTATUSNBILLABLEHR, arrayList.get(0).getNon_billable_hours());
        cv.put(ParseOpenHelper.JOBSTATUSNBILLABLEHRDESC, arrayList.get(0).getNon_billable_hours_description());
        cv.put(ParseOpenHelper.JOBSTATUSRETURNIMMED, arrayList.get(0).getReturn_immediately());
        cv.put(ParseOpenHelper.JOBSTATUSALREADYSCHEDULED, arrayList.get(0).getAlready_scheduled());
        cv.put(ParseOpenHelper.JOBSTATUSREASON, arrayList.get(0).getReason());
        cv.put(ParseOpenHelper.JOBSTATUSDESCRIPTION, arrayList.get(0).getDescription());
        cv.put(ParseOpenHelper.JOBSTATUSSTARTTIME, arrayList.get(0).getStart_time());
        cv.put(ParseOpenHelper.JOBSTATUSENDTTIME, arrayList.get(0).getEnd_time());
        cv.put(ParseOpenHelper.JOBSTATUSHOURS, arrayList.get(0).getHours());

        cv.put(ParseOpenHelper.JOBSTATUSHOURSADJUSTED, arrayList.get(0).getHours_adjusted());
        cv.put(ParseOpenHelper.JOBSTATUSHOURSADJUSTEDEND, arrayList.get(0).getHours_adjusted_end());

        cv.put(ParseOpenHelper.JOBSTATUSLABOURCODE, arrayList.get(0).getLabour_code());
        cv.put(ParseOpenHelper.JOBSTATUSCREATEDAT, insertedAt);
        cv.put(ParseOpenHelper.JOBSTATUSBOATNAME, arrayList.get(0).getBoat_name());
        cv.put(ParseOpenHelper.JOBSTATUSHULLID, arrayList.get(0).getHull_id());
        cv.put(ParseOpenHelper.JOBSTATUSCAPTIONNAME, arrayList.get(0).getCaptain_name());
        cv.put(ParseOpenHelper.JOBSTATUSCOUNT, arrayList.get(0).getCount());
        long idd = database.insert(ParseOpenHelper.TABLE_JOBSTATUS, null, cv);
    }

}
