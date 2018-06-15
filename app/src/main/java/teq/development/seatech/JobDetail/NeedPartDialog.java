package teq.development.seatech.JobDetail;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import teq.development.seatech.R;
import teq.development.seatech.databinding.DialogNeedpartBinding;

public class NeedPartDialog extends DialogFragment {

    Dialog dialog;

    static NeedPartDialog newInstance(int num) {
        NeedPartDialog f = new NeedPartDialog();
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
        View rootView = inflater.inflate(R.layout.dialog_needpart, container, false);
        DialogNeedpartBinding binding = DataBindingUtil.bind(rootView);
        binding.setDialogneedpart(this);
        initView(binding);
        return rootView;
    }

    private void initView(final DialogNeedpartBinding binding) {
        binding.cbxadvancereplYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    popupAdvanceRep(buttonView);
                    binding.cbxadvancereplNo.setChecked(false);
                }
            }
        });

        binding.cbxadvancereplNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                binding.cbxadvancereplYes.setChecked(false);
            }
        });

        binding.checkboxYesurgent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.checkboxNourgent.setChecked(false);
                }
            }
        });

        binding.checkboxNourgent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.checkboxYesurgent.setChecked(false);
                }
            }
        });

        binding.cbxneedNormal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.cbxneedDeadline.setChecked(false);
                }
            }
        });

        binding.cbxneedDeadline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.cbxneedNormal.setChecked(false);
                }
            }
        });

        binding.cbxappoveYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.cbxappoveNo.setChecked(false);
                }
            }
        });

        binding.cbxappoveNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.cbxappoveYes.setChecked(false);
                }
            }
        });


        binding.cbxdoesmanfdeemYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.cbxdoesmanfdeemNo.setChecked(false);
                    binding.cbxdoesmanfdeemMaybe.setChecked(false);
                }
            }
        });

        binding.cbxdoesmanfdeemNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.cbxdoesmanfdeemYes.setChecked(false);
                    binding.cbxdoesmanfdeemMaybe.setChecked(false);
                }
            }
        });
        binding.cbxdoesmanfdeemMaybe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.cbxdoesmanfdeemYes.setChecked(false);
                    binding.cbxdoesmanfdeemNo.setChecked(false);
                }
            }
        });


        binding.cbxadvancereplYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.cbxadvancereplNo.setChecked(false);
                }
            }
        });

        binding.cbxadvancereplNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.cbxadvancereplYes.setChecked(false);
                }
            }
        });

        binding.cbxneedloanerYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.cbxneedloanerNo.setChecked(false);
                }
            }
        });

        binding.cbxneedloanerNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.cbxneedloanerYes.setChecked(false);
                }
            }
        });


        binding.cbxpartsoldbyYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.cbxpartsoldbyNo.setChecked(false);
                    binding.cbxpartsoldbyNotsure.setChecked(false);
                }
            }
        });

        binding.cbxpartsoldbyNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.cbxpartsoldbyYes.setChecked(false);
                    binding.cbxpartsoldbyNotsure.setChecked(false);
                }
            }
        });

        binding.cbxpartsoldbyNotsure.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.cbxpartsoldbyYes.setChecked(false);
                    binding.cbxpartsoldbyNo.setChecked(false);
                }
            }
        });


        binding.cbxneedquoteYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.cbxneedquoteNo.setChecked(false);
                }
            }
        });

        binding.cbxneedquoteNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.cbxneedquoteYes.setChecked(false);
                }
            }
        });

        List<String> list = new ArrayList<String>();
        list.add("Manufacture 1");
        list.add("Manufacture 2");
        list.add("Manufacture 3");
        list.add("Manufacture 4");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerSelectmanuf.setAdapter(dataAdapter);
    }

    public void OnClickCancel() {
        dialog.dismiss();
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
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    public void onClickCross() {
        dialog.dismiss();
    }

    private void popupAdvanceRep(View anchorview) {
        final PopupWindow popup = new PopupWindow(getActivity());
        View layout = getLayoutInflater().inflate(R.layout.popup_advancerepl, null);
        popup.setContentView(layout);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageView cross = (ImageView) layout.findViewById(R.id.cross);
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
}
