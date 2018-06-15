package teq.development.seatech.JobDetail;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import teq.development.seatech.R;
import teq.development.seatech.databinding.DialogJobstatusBinding;

public class JobStatusDialog extends DialogFragment {

    Dialog dialog;

    static JobStatusDialog newInstance(int num) {
        JobStatusDialog f = new JobStatusDialog();
        Bundle args = new Bundle();
        args.putInt("num", num);
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
        DialogJobstatusBinding binding = DataBindingUtil.bind(rootView);
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
                    displayPopupwindow(binding.checkboxyes, getString(R.string.doesowner_needtoknow));
                }
            }
        });

        binding.checkboxno.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.checkboxyes.setChecked(false);
                    binding.checkboxmaybe.setChecked(false);
                    displayPopupwindowNo(binding.checkboxno);
                }
            }
        });

        binding.checkboxmaybe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.checkboxyes.setChecked(false);
                    binding.checkboxno.setChecked(false);
                    displayPopupwindow(binding.checkboxmaybe, getString(R.string.whodecide));
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
        params.width = width - 360;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        ;
       /* params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;*/
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    private void displayPopupwindow(View anchorview, String text) {
        final PopupWindow popup = new PopupWindow(getActivity());
        View layout = getLayoutInflater().inflate(R.layout.popupwd_jcyes, null);
        popup.setContentView(layout);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageView cross = (ImageView) layout.findViewById(R.id.cross);
        TextView toptext = (TextView) layout.findViewById(R.id.toptext);
        Button submit = (Button) layout.findViewById(R.id.submit);
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
                popup.dismiss();
            }
        });
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        popup.showAsDropDown(anchorview);
    }

    private void displayPopupwindowNo(final View anchorview) {
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
                    displayPopupwindowNoYes(anchorview, getActivity(),popup);
                }
            }
        });

        checkboxno.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkboxyes.setChecked(false);
                    displayPopupwindowNoNo(anchorview, getActivity(),popup);
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

    private void displayPopupwindowNoYes(View anchorview, Context context,final PopupWindow pop) {
        final PopupWindow popup = new PopupWindow(context);
        View layout = getLayoutInflater().inflate(R.layout.popupwd_jcnoyes, null);
        popup.setContentView(layout);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageView cross = (ImageView) layout.findViewById(R.id.cross);
       /* AppCompatCheckBox cbx_wtngcstmr = (AppCompatCheckBox) layout.findViewById(R.id.cbx_wtngcstmr);

        cbx_wtngcstmr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    displayPopupwindowNoYes(buttonView);
                }
            }
        });*/
        final AppCompatCheckBox checkboxyes = (AppCompatCheckBox) layout.findViewById(R.id.checkboxyes);
        final AppCompatCheckBox checkboxno = (AppCompatCheckBox) layout.findViewById(R.id.checkboxno);

        checkboxyes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkboxno.setChecked(false);
                    popup.dismiss();
                    pop.dismiss();
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

    private void displayPopupwindowNoNo(final View anchorview, Context context,final PopupWindow popupnono) {
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
                    displayPopupWtngCutomer(anchorview,popupnono,popup);
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
                    displayPopupExceptionPolicy(anchorview, getString(R.string.exceptiontoplcy),popupnono,popup);
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
    }

    public void onClickSubmit(){
        dialog.dismiss();
    }

    private void displayPopupWtngCutomer(final View anchorview,final PopupWindow popnono,final PopupWindow popwtngcstmr) {
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
                }
            }
        });

        cbx_otherreason.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbx_installation.setChecked(false);
                    displayPopupWtngcutomerReason(anchorview, getString(R.string.otherreason),popnono,popwtngcstmr,popup);
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

    private void displayPopupWtngcutomerReason(View anchorview, String text,final PopupWindow popupnono,final PopupWindow popwtngcstmr,final PopupWindow poplast) {
        final PopupWindow popup = new PopupWindow(getActivity());
        View layout = getLayoutInflater().inflate(R.layout.popupwd_wtngcstmreason, null);
        popup.setContentView(layout);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageView cross = (ImageView) layout.findViewById(R.id.cross);
        TextView toptext = (TextView) layout.findViewById(R.id.toptext);
        TextView submit = (TextView) layout.findViewById(R.id.submit);
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
                popupnono.dismiss();
                popwtngcstmr.dismiss();
                poplast.dismiss();
                popup.dismiss();
            }
        });
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        popup.showAsDropDown(anchorview);
    }
    private void displayPopupExceptionPolicy(View anchorview, String text,final PopupWindow popnono,final PopupWindow popwtngcstmr) {
        final PopupWindow popup = new PopupWindow(getActivity());
        View layout = getLayoutInflater().inflate(R.layout.popupwd_wtngcstmreason, null);
        popup.setContentView(layout);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageView cross = (ImageView) layout.findViewById(R.id.cross);
        TextView toptext = (TextView) layout.findViewById(R.id.toptext);
        TextView submit = (TextView) layout.findViewById(R.id.submit);
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
                popnono.dismiss();
                popwtngcstmr.dismiss();
                popup.dismiss();
            }
        });
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        popup.showAsDropDown(anchorview);
    }

}
