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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.App;
import teq.development.seatech.JobDetail.Skeleton.JobStatusSkeleton;
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
    static int count = 0;
    public static boolean crossclick;
    public static String allValues, editvalues;
    public String jobstatus;
    private SQLiteDatabase database;
    public String get_needtoknow, returnimmediate, alreadyScheduled, reason, description = "";
    boolean popupsubmit;

    static JobStatusDialog newInstance(String jobid, int selecposition, String values, String evalues) {
        JobStatusDialog f = new JobStatusDialog();
        Bundle args = new Bundle();
        coming_jobid = jobid;
        count = selecposition;
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
                    PopupNotBillable(binding.checkboxYesbillable);
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
        intent.putExtra("nextjobid", String.valueOf(count));
        crossclick = true;
        // HandyObject.putPrams(getActivity(), AppConstants.JOBRUNNING_TOTALTIME, "66");
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
        App.appInstance.startTimer();
    }

    public void onClickSubmit() {
        //  dialog.dismiss();

        if (binding.checkboxyes.isChecked() == false && binding.checkboxno.isChecked() == false && binding.checkboxmaybe.isChecked() == false) {
            HandyObject.showAlert(getActivity(), getString(R.string.selectjobtype));
        } else if (binding.etSupplyamount.getText().toString().length() == 0) {
            binding.etSupplyamount.requestFocus();
            HandyObject.showAlert(getActivity(), getString(R.string.supplyamountreq));
        } else if (binding.checkboxyes.isChecked() == true && binding.checkboxno.isChecked() == false && binding.checkboxmaybe.isChecked() == false) {
            String captainAware = "";
            String notbilled = "";
            String notbilled_desc = "";
            jobstatus = "yes";
            description = get_needtoknow;
            if (binding.etLaborperform.getText().toString().length() == 0) {
                HandyObject.showAlert(getActivity(), getString(R.string.fieldempty));

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
                JobStatusApi(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), coming_jobid, jobstatus, captainAware, binding.etLaborperform.getText().toString(),
                        binding.etSupplyamount.getText().toString(), notbilled, notbilled_desc, returnimmediate, alreadyScheduled, reason, description, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID));

            } else if (binding.checkboxYesbillable.isChecked() || binding.checkboxNobillable.isChecked()) {
                if (binding.checkboxYesbillable.isChecked()) {
                    notbilled = et_notbilled.getText().toString();
                    notbilled_desc = et_description.getText().toString();
                } else if (binding.checkboxNobillable.isChecked()) {
                    notbilled = "";
                    notbilled_desc = "";
                }
                JobStatusApi(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), coming_jobid, jobstatus, captainAware, binding.etLaborperform.getText().toString(),
                        binding.etSupplyamount.getText().toString(), notbilled, notbilled_desc, returnimmediate, alreadyScheduled, reason, description, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID));
            } else {
                JobStatusApi(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), coming_jobid, jobstatus, captainAware, binding.etLaborperform.getText().toString(),
                        binding.etSupplyamount.getText().toString(), notbilled, notbilled_desc, returnimmediate, alreadyScheduled, reason, description, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID));
            }
        } else if (binding.checkboxyes.isChecked() == false && binding.checkboxno.isChecked() == false && binding.checkboxmaybe.isChecked() == true) {
            String captainAware = "";
            String notbilled = "";
            String notbilled_desc = "";
            jobstatus = "May be";
            description = get_needtoknow;
            if (binding.etLaborperform.getText().toString().length() == 0) {
                HandyObject.showAlert(getActivity(), getString(R.string.fieldempty));

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
                JobStatusApi(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), coming_jobid, jobstatus, captainAware, binding.etLaborperform.getText().toString(),
                        binding.etSupplyamount.getText().toString(), notbilled, notbilled_desc, returnimmediate, alreadyScheduled, reason, description, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID));

            } else if (binding.checkboxYesbillable.isChecked() || binding.checkboxNobillable.isChecked()) {
                if (binding.checkboxYesbillable.isChecked()) {
                    notbilled = et_notbilled.getText().toString();
                    notbilled_desc = et_description.getText().toString();
                } else if (binding.checkboxNobillable.isChecked()) {
                    notbilled = "";
                    notbilled_desc = "";
                }
                JobStatusApi(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), coming_jobid, jobstatus, captainAware, binding.etLaborperform.getText().toString(),
                        binding.etSupplyamount.getText().toString(), notbilled, notbilled_desc, returnimmediate, alreadyScheduled, reason, description, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID));
            } else {
                JobStatusApi(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), coming_jobid, jobstatus, captainAware, binding.etLaborperform.getText().toString(),
                        binding.etSupplyamount.getText().toString(), notbilled, notbilled_desc, returnimmediate, alreadyScheduled, reason, description, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID));
            }
        } else if (binding.checkboxyes.isChecked() == false && binding.checkboxno.isChecked() == true && binding.checkboxmaybe.isChecked() == false) {
            String captainAware = "";
            String notbilled = "";
            String notbilled_desc = "";
            jobstatus = "no";
            if (binding.etLaborperform.getText().toString().length() == 0) {
                HandyObject.showAlert(getActivity(), getString(R.string.fieldempty));

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
                JobStatusApi(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), coming_jobid, jobstatus, captainAware, binding.etLaborperform.getText().toString(),
                        binding.etSupplyamount.getText().toString(), notbilled, notbilled_desc, returnimmediate, alreadyScheduled, reason, description, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID));

            } else if (binding.checkboxYesbillable.isChecked() || binding.checkboxNobillable.isChecked()) {
                if (binding.checkboxYesbillable.isChecked()) {
                    notbilled = et_notbilled.getText().toString();
                    notbilled_desc = et_description.getText().toString();
                } else if (binding.checkboxNobillable.isChecked()) {
                    notbilled = "";
                    notbilled_desc = "";
                }
                JobStatusApi(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), coming_jobid, jobstatus, captainAware, binding.etLaborperform.getText().toString(),
                        binding.etSupplyamount.getText().toString(), notbilled, notbilled_desc, returnimmediate, alreadyScheduled, reason, description, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID));
            } else {
                JobStatusApi(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), coming_jobid, jobstatus, captainAware, binding.etLaborperform.getText().toString(),
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

        String start_T = allValues.split("--")[0];
        String end_T = allValues.split("--")[1];
        String hours = allValues.split("--")[2];
        String hours_adjusted = allValues.split("--")[3];
        String labour_code = allValues.split("--")[4];

        String edit_boatname = editvalues.split("-")[0];
        String edit_hullid = editvalues.split("-")[1];
        String edit_capname = editvalues.split("-")[2];

        JobStatusSkeleton ske = new JobStatusSkeleton();
        ske.setBoat_name(edit_boatname);
        ske.setHull_id(edit_hullid);
        ske.setCaptain_name(edit_capname);
        ske.setTech_id(teqid);
        ske.setJob_id(jobid);
        ske.setJob_completed(job_completed);
        ske.setCaptain_aware(captainaware);
        ske.setNotes(notes);
        ske.setSupply_amount(supplyAmount);
        ske.setNon_billable_hours(nbillablehrs);
        ske.setNon_billable_hours_description(nbillablehrsDiscription);
        ske.setReturn_immediately(returnimmid);
        ske.setAlready_scheduled(already_scheduled);
        ske.setReason(reason);
        ske.setDescription(description);
        ske.setStart_time(start_T);
        ske.setEnd_time(end_T);
        ske.setHours(hours);
        ske.setHours_adjusted(hours_adjusted);
        ske.setLabour_code(labour_code);

        ArrayList<JobStatusSkeleton> arrayList = new ArrayList<>();
        arrayList.add(ske);
        Gson gson = new Gson();
        String alldata = gson.toJson(arrayList);
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
                                    count++;
                                    intent.putExtra("nextjobid", String.valueOf(count));
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
            count++;
            intent.putExtra("nextjobid", String.valueOf(count));
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
        cv.put(ParseOpenHelper.JOBSTATUSLABOURCODE, arrayList.get(0).getLabour_code());
        cv.put(ParseOpenHelper.JOBSTATUSCREATEDAT, insertedAt);
        cv.put(ParseOpenHelper.JOBSTATUSBOATNAME, arrayList.get(0).getBoat_name());
        cv.put(ParseOpenHelper.JOBSTATUSHULLID, arrayList.get(0).getHull_id());
        cv.put(ParseOpenHelper.JOBSTATUSCAPTIONNAME, arrayList.get(0).getCaptain_name());
        long idd = database.insert(ParseOpenHelper.TABLE_JOBSTATUS, null, cv);
    }

}
