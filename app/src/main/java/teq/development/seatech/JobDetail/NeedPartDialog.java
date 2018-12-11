package teq.development.seatech.JobDetail;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.LinearLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.App;
import teq.development.seatech.JobDetail.Adapter.AdapterManufacturerSpinner;
import teq.development.seatech.JobDetail.Adapter.AdapterPartUpldImages;
import teq.development.seatech.JobDetail.Skeleton.AddPartSkeleton;
import teq.development.seatech.LoginActivity;
import teq.development.seatech.ManufacturerSkeleton;
import teq.development.seatech.R;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.Utils.UserPicture;
import teq.development.seatech.database.Action;
import teq.development.seatech.database.CustomGallery;
import teq.development.seatech.database.ParseOpenHelper;
import teq.development.seatech.databinding.DialogNeedpartBinding;

import static teq.development.seatech.database.ParseOpenHelper.*;

public class NeedPartDialog extends DialogFragment {

    Dialog dialog;
    ArrayList<ManufacturerSkeleton> manufacturerArrayList;
    AdapterManufacturerSpinner adaptermanuf;
    AdapterPartUpldImages adapterUploadImages;
    ArrayList<String> imagesarray;
    DialogNeedpartBinding binding;
    private static final int PICK_IMAGE_FROM_GALLARY = 1000;
    private static final int REQUEST_IMAGE_CAPTURE = 5484;
    MediaType MEDIA_TYPE_FORM = MediaType.parse("image/png");
    File imageFile;
    Calendar myCalendarStartDate;
    DatePickerDialog.OnDateSetListener dateStart;
    String[] all_path = null;
    public static String jobid;
    SQLiteDatabase database;
    Gson gson;
    int partcount = 1;

    static NeedPartDialog newInstance(String id) {
        NeedPartDialog f = new NeedPartDialog();
        Bundle args = new Bundle();
        jobid = id;
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
        binding = DataBindingUtil.bind(rootView);
        binding.setDialogneedpart(this);
        initView(binding);
        return rootView;
    }

    private void initView(final DialogNeedpartBinding binding) {
        manufacturerArrayList = new ArrayList<>();
        imagesarray = new ArrayList<>();
        myCalendarStartDate = Calendar.getInstance();
        binding.cbxneedNormal.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(getActivity(), R.drawable.small_calendar), null);
        LinearLayoutManager lLManagerDashNotes = new LinearLayoutManager(getActivity());
        lLManagerDashNotes.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.relimagesRecyclerView.setLayoutManager(lLManagerDashNotes);

        new FetchManuFacture().execute();
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

        binding.cbxneedDeadline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.checkboxNeedNormal.setChecked(false);
                    // binding.cbxneedNormal.setText("");
                    binding.cbxneedNormal.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.checkboxNeedNormal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.cbxneedDeadline.setChecked(false);
                    binding.cbxneedNormal.setVisibility(View.GONE);
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
                    binding.advancerepl.setVisibility(View.VISIBLE);
                    binding.cbxadvancereplYes.setVisibility(View.VISIBLE);
                    binding.cbxadvancereplNo.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.cbxdoesmanfdeemNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.cbxdoesmanfdeemYes.setChecked(false);
                    binding.cbxdoesmanfdeemMaybe.setChecked(false);
                    binding.advancerepl.setVisibility(View.GONE);
                    binding.cbxadvancereplYes.setVisibility(View.GONE);
                    binding.cbxadvancereplNo.setVisibility(View.GONE);
                    binding.partsoldbySeatecch.setVisibility(View.GONE);
                    binding.cbxpartsoldbyYes.setVisibility(View.GONE);
                    binding.cbxpartsoldbyNo.setVisibility(View.GONE);
                    binding.cbxpartsoldbyNotsure.setVisibility(View.GONE);
                    binding.needloaner.setVisibility(View.GONE);
                    binding.cbxneedloanerYes.setVisibility(View.GONE);
                    binding.cbxneedloanerNo.setVisibility(View.GONE);
                }
            }
        });
        binding.cbxdoesmanfdeemMaybe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.cbxdoesmanfdeemYes.setChecked(false);
                    binding.cbxdoesmanfdeemNo.setChecked(false);
                    binding.advancerepl.setVisibility(View.VISIBLE);
                    binding.cbxadvancereplYes.setVisibility(View.VISIBLE);
                    binding.cbxadvancereplNo.setVisibility(View.VISIBLE);
                }
            }
        });


        binding.cbxadvancereplYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.cbxadvancereplNo.setChecked(false);

                    binding.partsoldbySeatecch.setVisibility(View.VISIBLE);
                    binding.cbxpartsoldbyYes.setVisibility(View.VISIBLE);
                    binding.cbxpartsoldbyNo.setVisibility(View.VISIBLE);
                    binding.cbxpartsoldbyNotsure.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.cbxadvancereplNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.cbxadvancereplYes.setChecked(false);

                    binding.partsoldbySeatecch.setVisibility(View.GONE);
                    binding.cbxpartsoldbyYes.setVisibility(View.GONE);
                    binding.cbxpartsoldbyNo.setVisibility(View.GONE);
                    binding.cbxpartsoldbyNotsure.setVisibility(View.GONE);
                    binding.needloaner.setVisibility(View.GONE);
                    binding.cbxneedloanerYes.setVisibility(View.GONE);
                    binding.cbxneedloanerNo.setVisibility(View.GONE);
                }
            }
        });

        binding.cbxpartsoldbyYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.cbxpartsoldbyNo.setChecked(false);
                    binding.cbxpartsoldbyNotsure.setChecked(false);

                    binding.needloaner.setVisibility(View.VISIBLE);
                    binding.cbxneedloanerYes.setVisibility(View.VISIBLE);
                    binding.cbxneedloanerNo.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.cbxpartsoldbyNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.cbxpartsoldbyYes.setChecked(false);
                    binding.cbxpartsoldbyNotsure.setChecked(false);
                    binding.needloaner.setVisibility(View.GONE);
                    binding.cbxneedloanerYes.setVisibility(View.GONE);
                    binding.cbxneedloanerNo.setVisibility(View.GONE);
                }
            }
        });

        binding.cbxpartsoldbyNotsure.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.cbxpartsoldbyYes.setChecked(false);
                    binding.cbxpartsoldbyNo.setChecked(false);
                    binding.needloaner.setVisibility(View.VISIBLE);
                    binding.cbxneedloanerYes.setVisibility(View.VISIBLE);
                    binding.cbxneedloanerNo.setVisibility(View.VISIBLE);
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

        binding.cbPartrepair.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.cbPartsale.setChecked(false);
                    binding.selectmanuf.setVisibility(View.VISIBLE);
                    binding.spinnerSelectmanuf.setVisibility(View.VISIBLE);
                    binding.manufspinnerarrow.setVisibility(View.VISIBLE);
                    binding.detailcardview.setVisibility(View.GONE);
                    adaptermanuf = new AdapterManufacturerSpinner(getActivity(), manufacturerArrayList);
                    binding.spinnerSelectmanuf.setAdapter(adaptermanuf);
                    binding.partno.setVisibility(View.VISIBLE);
                    binding.etPartno.setVisibility(View.VISIBLE);

                    binding.quantityNeeded.setVisibility(View.GONE);
                    binding.etQuantityNeeded.setVisibility(View.GONE);

                    binding.serialno.setVisibility(View.VISIBLE);
                    binding.etSerialno.setVisibility(View.VISIBLE);

                    binding.failuredesc.setVisibility(View.VISIBLE);
                    binding.etFailuredesc.setVisibility(View.VISIBLE);

                    binding.techsupportname.setVisibility(View.VISIBLE);
                    binding.etTechsupportname.setVisibility(View.VISIBLE);

                    binding.rmaorcase.setVisibility(View.VISIBLE);
                    binding.etRmaorcase.setVisibility(View.VISIBLE);
                    binding.savebtn.setText(getString(R.string.sendRepairReq));
                }
            }
        });

        binding.spinnerSelectmanuf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (manufacturerArrayList != null) {
                    if (position == 0) {
                        binding.detailcardview.setVisibility(View.GONE);
                    } else {
                        binding.detailcardview.setVisibility(View.VISIBLE);
                        binding.manufnamevalue.setText(manufacturerArrayList.get(position).getName());
                        binding.manufphonevalue.setText(manufacturerArrayList.get(position).getPhone());
                        binding.rmareqvalue.setText(manufacturerArrayList.get(position).getRmaRequired());
                        binding.manufcommentvalue.setText(manufacturerArrayList.get(position).getComment());

                        if (binding.cbPartsale.isChecked()) {
                            binding.doesmanfdeem.setVisibility(View.GONE);
                            binding.cbxdoesmanfdeemYes.setVisibility(View.GONE);
                            binding.cbxdoesmanfdeemNo.setVisibility(View.GONE);
                            binding.cbxdoesmanfdeemMaybe.setVisibility(View.GONE);

                            binding.advancerepl.setVisibility(View.GONE);
                            binding.cbxadvancereplYes.setVisibility(View.GONE);
                            binding.cbxadvancereplNo.setVisibility(View.GONE);

                            binding.partsoldbySeatecch.setVisibility(View.GONE);
                            binding.cbxpartsoldbyYes.setVisibility(View.GONE);
                            binding.cbxpartsoldbyNo.setVisibility(View.GONE);
                            binding.cbxpartsoldbyNotsure.setVisibility(View.GONE);

                            binding.needloaner.setVisibility(View.GONE);
                            binding.cbxneedloanerYes.setVisibility(View.GONE);
                            binding.cbxneedloanerNo.setVisibility(View.GONE);

                            binding.doesmanfdeem.setVisibility(View.GONE);
                            binding.cbxdoesmanfdeemYes.setVisibility(View.GONE);
                            binding.cbxdoesmanfdeemNo.setVisibility(View.GONE);
                            binding.cbxdoesmanfdeemMaybe.setVisibility(View.GONE);
                        } else if (binding.cbPartrepair.isChecked()) {
                            binding.cbxdoesmanfdeemYes.setChecked(false);
                            binding.cbxdoesmanfdeemNo.setChecked(false);
                            binding.cbxdoesmanfdeemMaybe.setChecked(false);

                            binding.cbxadvancereplYes.setChecked(false);
                            binding.cbxadvancereplNo.setChecked(false);

                            binding.cbxpartsoldbyYes.setChecked(false);
                            binding.cbxpartsoldbyNo.setChecked(false);
                            binding.cbxpartsoldbyNotsure.setChecked(false);

                            binding.cbxneedloanerYes.setChecked(false);
                            binding.cbxneedloanerNo.setChecked(false);

                            binding.cbxdoesmanfdeemYes.setChecked(false);
                            binding.cbxdoesmanfdeemNo.setChecked(false);
                            binding.cbxdoesmanfdeemMaybe.setChecked(false);


                            if (binding.rmareqvalue.getText().toString().toLowerCase().equalsIgnoreCase("no")) {
                                binding.etRmaorcase.setText("Not Required");
                                binding.etRmaorcase.setEnabled(false);
                                binding.doesmanfdeem.setVisibility(View.GONE);
                                binding.cbxdoesmanfdeemYes.setVisibility(View.GONE);
                                binding.cbxdoesmanfdeemNo.setVisibility(View.GONE);
                                binding.cbxdoesmanfdeemMaybe.setVisibility(View.GONE);

                                binding.advancerepl.setVisibility(View.GONE);
                                binding.cbxadvancereplYes.setVisibility(View.GONE);
                                binding.cbxadvancereplNo.setVisibility(View.GONE);

                                binding.partsoldbySeatecch.setVisibility(View.GONE);
                                binding.cbxpartsoldbyYes.setVisibility(View.GONE);
                                binding.cbxpartsoldbyNo.setVisibility(View.GONE);
                                binding.cbxpartsoldbyNotsure.setVisibility(View.GONE);

                                binding.needloaner.setVisibility(View.GONE);
                                binding.cbxneedloanerYes.setVisibility(View.GONE);
                                binding.cbxneedloanerNo.setVisibility(View.GONE);
                            } else {
                                binding.etRmaorcase.setText("");
                                binding.etRmaorcase.setEnabled(true);
                                binding.doesmanfdeem.setVisibility(View.VISIBLE);
                                binding.cbxdoesmanfdeemYes.setVisibility(View.VISIBLE);
                                binding.cbxdoesmanfdeemNo.setVisibility(View.VISIBLE);
                                binding.cbxdoesmanfdeemMaybe.setVisibility(View.VISIBLE);
                            }
                        }
                    }


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.cbPartsale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.cbPartrepair.setChecked(false);
                    binding.selectmanuf.setVisibility(View.VISIBLE);
                    binding.spinnerSelectmanuf.setVisibility(View.VISIBLE);
                    binding.manufspinnerarrow.setVisibility(View.VISIBLE);
                    binding.detailcardview.setVisibility(View.GONE);
                    adaptermanuf = new AdapterManufacturerSpinner(getActivity(), manufacturerArrayList);
                    binding.spinnerSelectmanuf.setAdapter(adaptermanuf);
                    binding.serialno.setVisibility(View.GONE);
                    binding.etSerialno.setVisibility(View.GONE);

                    binding.failuredesc.setVisibility(View.GONE);
                    binding.etFailuredesc.setVisibility(View.GONE);

                    binding.techsupportname.setVisibility(View.GONE);
                    binding.etTechsupportname.setVisibility(View.GONE);

                    binding.rmaorcase.setVisibility(View.GONE);
                    binding.etRmaorcase.setVisibility(View.GONE);

                    binding.partno.setVisibility(View.VISIBLE);
                    binding.etPartno.setVisibility(View.VISIBLE);

                    binding.quantityNeeded.setVisibility(View.VISIBLE);
                    binding.etQuantityNeeded.setVisibility(View.VISIBLE);

                    binding.serialno.setVisibility(View.GONE);
                    binding.etSerialno.setVisibility(View.GONE);

                    binding.failuredesc.setVisibility(View.GONE);
                    binding.etFailuredesc.setVisibility(View.GONE);

                    binding.techsupportname.setVisibility(View.GONE);
                    binding.etTechsupportname.setVisibility(View.GONE);

                    binding.rmaorcase.setVisibility(View.GONE);
                    binding.etRmaorcase.setVisibility(View.GONE);

                    binding.doesmanfdeem.setVisibility(View.GONE);
                    binding.cbxdoesmanfdeemYes.setVisibility(View.GONE);
                    binding.cbxdoesmanfdeemNo.setVisibility(View.GONE);
                    binding.cbxdoesmanfdeemMaybe.setVisibility(View.GONE);

                    binding.advancerepl.setVisibility(View.GONE);
                    binding.cbxadvancereplYes.setVisibility(View.GONE);
                    binding.cbxadvancereplNo.setVisibility(View.GONE);

                    binding.partsoldbySeatecch.setVisibility(View.GONE);
                    binding.cbxpartsoldbyYes.setVisibility(View.GONE);
                    binding.cbxpartsoldbyNo.setVisibility(View.GONE);
                    binding.cbxpartsoldbyNotsure.setVisibility(View.GONE);

                    binding.needloaner.setVisibility(View.GONE);
                    binding.cbxneedloanerYes.setVisibility(View.GONE);
                    binding.cbxneedloanerNo.setVisibility(View.GONE);
                    binding.savebtn.setText(getString(R.string.sendPartReq));
                }
            }
        });

        dateStart = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendarStartDate.set(Calendar.YEAR, year);
                myCalendarStartDate.set(Calendar.MONTH, monthOfYear);
                myCalendarStartDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                if (view.isShown()) {
                    long selectedMilli = myCalendarStartDate.getTimeInMillis();
                    Date datePickerDate = new Date(selectedMilli);
                    // binding.cbxneedDeadline.setChecked(false);
                    binding.cbxneedNormal.setText(HandyObject.getDateFromPickerNew(myCalendarStartDate.getTime()));
                }
            }
        };
    }

    public void OnClickSelectDate() {
        new DatePickerDialog(getActivity(), dateStart, myCalendarStartDate
                .get(Calendar.YEAR), myCalendarStartDate.get(Calendar.MONTH),
                myCalendarStartDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void OnClickCancel() {
        dialog.dismiss();
    }

    public void OnClickSave() {
        String NeedUrgent = "";
        String howfast_Date = "";
        String price_approval_required = "";
        String quantity_needed = "";
        String deemthisWarranty = "";
        String advance_replacement = "";
        String partsold_byseatech = "";
        String need_loaner = "";

        if (binding.checkboxYesurgent.isChecked() == false && binding.checkboxNourgent.isChecked() == false) {
            HandyObject.showAlert(getActivity(), getString(R.string.selectneedurgent));
        } else if (binding.etSupplyamount.getText().toString().length() == 0) {
            HandyObject.showAlert(getActivity(), getString(R.string.partdesc_empty));
        } else if (binding.cbxneedDeadline.isChecked() == false && binding.checkboxNeedNormal.isChecked() == false) {
            HandyObject.showAlert(getActivity(), getString(R.string.howfastneedit));
        } else if (binding.cbxneedDeadline.isChecked() == true && binding.checkboxNeedNormal.isChecked() == false && binding.cbxneedNormal.getText().toString().length() == 0) {
            HandyObject.showAlert(getActivity(), getString(R.string.deadlinedateReq));
        } else if (binding.cbxappoveYes.isChecked() == false && binding.cbxappoveNo.isChecked() == false) {
            HandyObject.showAlert(getActivity(), getString(R.string.approveprice));
        } else if (binding.cbPartrepair.isChecked() == false && binding.cbPartsale.isChecked() == false) {
            HandyObject.showAlert(getActivity(), getString(R.string.partforrepairorsale));
        } else if (binding.spinnerSelectmanuf.getSelectedItemPosition() == 0) {
            HandyObject.showAlert(getActivity(), getString(R.string.pleaseselectmanuf));
        } else if (binding.cbPartrepair.isChecked() == false && binding.cbPartsale.isChecked() == true) {
            /*if (binding.etPartno.getText().toString().length() == 0) {
                HandyObject.showAlert(getActivity(), getString(R.string.partnoreq));
            } else */if (binding.etQuantityNeeded.getText().toString().length() == 0) {
                HandyObject.showAlert(getActivity(), getString(R.string.quantityneededblank));
            } else {
                if (binding.checkboxYesurgent.isChecked() == true && binding.checkboxNourgent.isChecked() == false) {
                    NeedUrgent = "1";
                } else if (binding.checkboxYesurgent.isChecked() == false && binding.checkboxNourgent.isChecked() == true) {
                    NeedUrgent = "0";
                }
                if (binding.cbxappoveYes.isChecked() == true && binding.cbxappoveNo.isChecked() == false) {
                    price_approval_required = "1";
                } else if (binding.cbxappoveYes.isChecked() == false && binding.cbxappoveNo.isChecked() == true) {
                    price_approval_required = "0";
                }
                if (binding.checkboxNeedNormal.isChecked() == false && binding.cbxneedDeadline.isChecked() == true && binding.cbxneedNormal.getText().toString().length() > 0) {
                    howfast_Date = HandyObject.parseDateToYMDNew(binding.cbxneedNormal.getText().toString());
                } else if (binding.checkboxNeedNormal.isChecked() == true && binding.cbxneedDeadline.isChecked() == false) {
                    howfast_Date = "0000-00-00";
                }

                AddPartToSrver(NeedUrgent, binding.etSupplyamount.getText().toString(), howfast_Date, price_approval_required, binding.etQuantityNeeded.getText().toString(), manufacturerArrayList.get(binding.spinnerSelectmanuf.getSelectedItemPosition()).getId(), binding.etPartno.getText().toString(), binding.etQuantityNeeded.getText().toString(), "", "", "", "", "", "", "", "", binding.etAdditnotes.getText().toString());
            }
        } else if (binding.cbPartrepair.isChecked() == true && binding.cbPartsale.isChecked() == false) {

         if (manufacturerArrayList.get(binding.spinnerSelectmanuf.getSelectedItemPosition()).getNeedProduct().toLowerCase().equalsIgnoreCase("yes")) {
                if (binding.etTechsupportname.getText().toString().length() == 0) {
                    //  HandyObject.showAlert(getActivity(), getString(R.string.Techsupportnameblank));
                    ProductYesDialog();
                } else if (binding.etRmaorcase.getText().toString().length() == 0) {
                    //  HandyObject.showAlert(getActivity(), getString(R.string.rmaorcasereq));
                    ProductYesDialog();
                } else if (binding.doesmanfdeem.getVisibility() == View.VISIBLE && binding.cbxdoesmanfdeemYes.isChecked() == false &&
                        binding.cbxdoesmanfdeemMaybe.isChecked() == false && binding.cbxdoesmanfdeemNo.isChecked() == false) {
                    ProductYesDialog();
                } else if (binding.advancerepl.getVisibility() == View.VISIBLE && binding.cbxadvancereplYes.isChecked() == false &&
                        binding.cbxadvancereplNo.isChecked() == false) {
                    ProductYesDialog();
                } else if (binding.partsoldbySeatecch.getVisibility() == View.VISIBLE && binding.cbxpartsoldbyYes.isChecked() == false &&
                        binding.cbxpartsoldbyNo.isChecked() == false && binding.cbxpartsoldbyNotsure.isChecked() == false) {
                    ProductYesDialog();
                } else if (binding.needloaner.getVisibility() == View.VISIBLE && binding.cbxneedloanerYes.isChecked() == false &&
                        binding.cbxneedloanerNo.isChecked() == false) {
                    ProductYesDialog();
                } else {
                    if (binding.checkboxYesurgent.isChecked() == true && binding.checkboxNourgent.isChecked() == false) {
                        NeedUrgent = "1";
                    } else if (binding.checkboxYesurgent.isChecked() == false && binding.checkboxNourgent.isChecked() == true) {
                        NeedUrgent = "0";
                    }
                    if (binding.cbxappoveYes.isChecked() == true && binding.cbxappoveNo.isChecked() == false) {
                        price_approval_required = "1";
                    } else if (binding.cbxappoveYes.isChecked() == false && binding.cbxappoveNo.isChecked() == true) {
                        price_approval_required = "0";
                    }
                    if (binding.checkboxNeedNormal.isChecked() == false && binding.cbxneedDeadline.isChecked() == true && binding.cbxneedNormal.getText().toString().length() > 0) {
                        howfast_Date = HandyObject.parseDateToYMDNew(binding.cbxneedNormal.getText().toString());
                    } else if (binding.checkboxNeedNormal.isChecked() == true && binding.cbxneedDeadline.isChecked() == false) {
                        howfast_Date = "0000-00-00";
                    }


                    if (binding.cbxdoesmanfdeemYes.isChecked() == true && binding.cbxdoesmanfdeemNo.isChecked() == false && binding.cbxdoesmanfdeemMaybe.isChecked() == false) {
                        deemthisWarranty = "1";
                    } else if (binding.cbxdoesmanfdeemYes.isChecked() == false && binding.cbxdoesmanfdeemNo.isChecked() == false && binding.cbxdoesmanfdeemMaybe.isChecked() == true) {
                        deemthisWarranty = "1";
                    } else if (binding.cbxdoesmanfdeemYes.isChecked() == false && binding.cbxdoesmanfdeemNo.isChecked() == true && binding.cbxdoesmanfdeemMaybe.isChecked() == false) {
                        deemthisWarranty = "0";
                    }

                    if (binding.cbxadvancereplYes.isChecked() == true && binding.cbxadvancereplNo.isChecked() == false) {
                        advance_replacement = "1";
                    } else if (binding.cbxadvancereplYes.isChecked() == false && binding.cbxadvancereplNo.isChecked() == true) {
                        advance_replacement = "0";
                    }

                    if (binding.cbxpartsoldbyYes.isChecked() == true && binding.cbxpartsoldbyNo.isChecked() == false && binding.cbxpartsoldbyNotsure.isChecked() == false) {
                        partsold_byseatech = "1";
                    } else if (binding.cbxpartsoldbyYes.isChecked() == false && binding.cbxpartsoldbyNo.isChecked() == true && binding.cbxpartsoldbyNotsure.isChecked() == false) {
                        partsold_byseatech = "0";
                    } else if (binding.cbxpartsoldbyYes.isChecked() == false && binding.cbxpartsoldbyNo.isChecked() == false && binding.cbxpartsoldbyNotsure.isChecked() == true) {
                        partsold_byseatech = "1";
                    }

                    if (binding.cbxneedloanerYes.isChecked() == true && binding.cbxneedloanerNo.isChecked() == false) {
                        need_loaner = "1";
                    } else if (binding.cbxneedloanerYes.isChecked() == false && binding.cbxneedloanerNo.isChecked() == true) {
                        need_loaner = "0";
                    }

                    AddPartToSrver(NeedUrgent, binding.etSupplyamount.getText().toString(), howfast_Date, price_approval_required, "0", manufacturerArrayList.get(binding.spinnerSelectmanuf.getSelectedItemPosition()).getId(), binding.etPartno.getText().toString(), binding.etQuantityNeeded.getText().toString(), binding.etSerialno.getText().toString(), binding.etFailuredesc.getText().toString(), binding.etTechsupportname.getText().toString(), binding.etRmaorcase.getText().toString(), deemthisWarranty, advance_replacement, partsold_byseatech, need_loaner, binding.etAdditnotes.getText().toString());
                }
            } else {
                if (binding.checkboxYesurgent.isChecked() == true && binding.checkboxNourgent.isChecked() == false) {
                    NeedUrgent = "1";
                } else if (binding.checkboxYesurgent.isChecked() == false && binding.checkboxNourgent.isChecked() == true) {
                    NeedUrgent = "0";
                }
                if (binding.cbxappoveYes.isChecked() == true && binding.cbxappoveNo.isChecked() == false) {
                    price_approval_required = "1";
                } else if (binding.cbxappoveYes.isChecked() == false && binding.cbxappoveNo.isChecked() == true) {
                    price_approval_required = "0";
                }
                if (binding.checkboxNeedNormal.isChecked() == false && binding.cbxneedDeadline.isChecked() == true && binding.cbxneedNormal.getText().toString().length() > 0) {
                    howfast_Date = HandyObject.parseDateToYMDNew(binding.cbxneedNormal.getText().toString());
                } else if (binding.checkboxNeedNormal.isChecked() == true && binding.cbxneedDeadline.isChecked() == false) {
                    howfast_Date = "0000-00-00";
                }


                if (binding.cbxdoesmanfdeemYes.isChecked() == true && binding.cbxdoesmanfdeemNo.isChecked() == false && binding.cbxdoesmanfdeemMaybe.isChecked() == false) {
                    deemthisWarranty = "1";
                } else if (binding.cbxdoesmanfdeemYes.isChecked() == false && binding.cbxdoesmanfdeemNo.isChecked() == false && binding.cbxdoesmanfdeemMaybe.isChecked() == true) {
                    deemthisWarranty = "1";
                } else if (binding.cbxdoesmanfdeemYes.isChecked() == false && binding.cbxdoesmanfdeemNo.isChecked() == true && binding.cbxdoesmanfdeemMaybe.isChecked() == false) {
                    deemthisWarranty = "0";
                }

                if (binding.cbxadvancereplYes.isChecked() == true && binding.cbxadvancereplNo.isChecked() == false) {
                    advance_replacement = "1";
                } else if (binding.cbxadvancereplYes.isChecked() == false && binding.cbxadvancereplNo.isChecked() == true) {
                    advance_replacement = "0";
                }

                if (binding.cbxpartsoldbyYes.isChecked() == true && binding.cbxpartsoldbyNo.isChecked() == false && binding.cbxpartsoldbyNotsure.isChecked() == false) {
                    partsold_byseatech = "1";
                } else if (binding.cbxpartsoldbyYes.isChecked() == false && binding.cbxpartsoldbyNo.isChecked() == true && binding.cbxpartsoldbyNotsure.isChecked() == false) {
                    partsold_byseatech = "0";
                } else if (binding.cbxpartsoldbyYes.isChecked() == false && binding.cbxpartsoldbyNo.isChecked() == false && binding.cbxpartsoldbyNotsure.isChecked() == true) {
                    partsold_byseatech = "1";
                }

                if (binding.cbxneedloanerYes.isChecked() == true && binding.cbxneedloanerNo.isChecked() == false) {
                    need_loaner = "1";
                } else if (binding.cbxneedloanerYes.isChecked() == false && binding.cbxneedloanerNo.isChecked() == true) {
                    need_loaner = "0";
                }

                AddPartToSrver(NeedUrgent, binding.etSupplyamount.getText().toString(), howfast_Date, price_approval_required, "0", manufacturerArrayList.get(binding.spinnerSelectmanuf.getSelectedItemPosition()).getId(), binding.etPartno.getText().toString(), binding.etQuantityNeeded.getText().toString(), binding.etSerialno.getText().toString(), binding.etFailuredesc.getText().toString(), binding.etTechsupportname.getText().toString(), binding.etRmaorcase.getText().toString(), deemthisWarranty, advance_replacement, partsold_byseatech, need_loaner, binding.etAdditnotes.getText().toString());
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int width = dm.widthPixels;
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = width - 140;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
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

    public void onClickCross() {
        dialog.dismiss();
    }

    public void OnClickUploadimage() {
        displayMediaPickerDialog();
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

    private void displayMediaPickerDialog() {
        final Display display = getActivity().getWindowManager().getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();
        final Dialog mediaDialog = new Dialog(getActivity());
        mediaDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = mediaDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        mediaDialog.setContentView(R.layout.dialog_media_picker);
        LinearLayout approx_lay = (LinearLayout) mediaDialog.findViewById(R.id.approx_lay);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w - 60, (h / 3) - 140);
        approx_lay.setLayoutParams(params);

        TextView options_camera = (TextView) mediaDialog.findViewById(R.id.options_camera);
        options_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaDialog.dismiss();
                dispatchTakePictureIntent();
            }
        });
        TextView options_gallery = (TextView) mediaDialog.findViewById(R.id.options_gallery);
        options_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaDialog.dismiss();
                Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
                startActivityForResult(i, 200);
            }
        });
        TextView options_cancel = (TextView) mediaDialog.findViewById(R.id.options_cancel);
        options_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaDialog.dismiss();
            }
        });
        mediaDialog.show();
    }


    private void NeedAnotherProdDialog() {
        final Display display = getActivity().getWindowManager().getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();
        final Dialog NeedAnotherProdDialog = new Dialog(getActivity());
        NeedAnotherProdDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = NeedAnotherProdDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        NeedAnotherProdDialog.setContentView(R.layout.dialog_needanother);
        LinearLayout approx_lay = (LinearLayout) NeedAnotherProdDialog.findViewById(R.id.approx_lay);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w - 280, (h / 3) - 100);
        approx_lay.setLayoutParams(params);

        TextView no = (TextView) NeedAnotherProdDialog.findViewById(R.id.no);
        TextView yes = (TextView) NeedAnotherProdDialog.findViewById(R.id.yes);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NeedAnotherProdDialog.dismiss();
                //dispatchTakePictureIntent();
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NeedAnotherProdDialog.dismiss();
                Intent intent = new Intent("needpartreciever");
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            }
        });

        NeedAnotherProdDialog.show();
    }


    private void ProductYesDialog() {
        final Display display = getActivity().getWindowManager().getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();
        final Dialog productyesDialog = new Dialog(getActivity());
        productyesDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = productyesDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        productyesDialog.setContentView(R.layout.dialog_productyes);
        LinearLayout approx_lay = (LinearLayout) productyesDialog.findViewById(R.id.approx_lay);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w - 280, (h / 3) - 60);
        approx_lay.setLayoutParams(params);

        TextView ok = (TextView) productyesDialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productyesDialog.dismiss();
                if (binding.etTechsupportname.getText().toString().length() == 0) {
                    HandyObject.showAlert(getActivity(), getString(R.string.Techsupportnameblank));
                } else if (binding.etRmaorcase.getText().toString().length() == 0) {
                    HandyObject.showAlert(getActivity(), getString(R.string.rmaorcasereq));
                } else if (binding.doesmanfdeem.getVisibility() == View.VISIBLE && binding.cbxdoesmanfdeemYes.isChecked() == false &&
                        binding.cbxdoesmanfdeemMaybe.isChecked() == false && binding.cbxdoesmanfdeemNo.isChecked() == false) {
                    HandyObject.showAlert(getActivity(), getString(R.string.doesmanfdeem));
                } else if (binding.advancerepl.getVisibility() == View.VISIBLE && binding.cbxadvancereplYes.isChecked() == false &&
                        binding.cbxadvancereplNo.isChecked() == false) {
                    HandyObject.showAlert(getActivity(), getString(R.string.advancerepl));
                } else if (binding.partsoldbySeatecch.getVisibility() == View.VISIBLE && binding.cbxpartsoldbyYes.isChecked() == false &&
                        binding.cbxpartsoldbyNo.isChecked() == false && binding.cbxpartsoldbyNotsure.isChecked() == false) {
                    HandyObject.showAlert(getActivity(), getString(R.string.partsoldby_seatecch));
                } else if (binding.needloaner.getVisibility() == View.VISIBLE && binding.cbxneedloanerYes.isChecked() == false &&
                        binding.cbxneedloanerNo.isChecked() == false) {
                    HandyObject.showAlert(getActivity(), getString(R.string.needloaner));
                }
                //dispatchTakePictureIntent();
            }
        });
        TextView cantwait = (TextView) productyesDialog.findViewById(R.id.cantwait);
        cantwait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productyesDialog.dismiss();

                String NeedUrgent = "";
                String howfast_Date = "";
                String price_approval_required = "";
                String quantity_needed = "";
                String deemthisWarranty = "";
                String advance_replacement = "";
                String partsold_byseatech = "";
                String need_loaner = "";
                if (binding.checkboxYesurgent.isChecked() == true && binding.checkboxNourgent.isChecked() == false) {
                    NeedUrgent = "1";
                } else if (binding.checkboxYesurgent.isChecked() == false && binding.checkboxNourgent.isChecked() == true) {
                    NeedUrgent = "0";
                }
                if (binding.cbxappoveYes.isChecked() == true && binding.cbxappoveNo.isChecked() == false) {
                    price_approval_required = "1";
                } else if (binding.cbxappoveYes.isChecked() == false && binding.cbxappoveNo.isChecked() == true) {
                    price_approval_required = "0";
                }
                if (binding.checkboxNeedNormal.isChecked() == false && binding.cbxneedDeadline.isChecked() == true && binding.cbxneedNormal.getText().toString().length() > 0) {
                    howfast_Date = HandyObject.parseDateToYMDNew(binding.cbxneedNormal.getText().toString());
                } else if (binding.checkboxNeedNormal.isChecked() == true && binding.cbxneedDeadline.isChecked() == false) {
                    howfast_Date = "0000-00-00";
                }


                if (binding.cbxdoesmanfdeemYes.isChecked() == true && binding.cbxdoesmanfdeemNo.isChecked() == false && binding.cbxdoesmanfdeemMaybe.isChecked() == false) {
                    deemthisWarranty = "1";
                } else if (binding.cbxdoesmanfdeemYes.isChecked() == false && binding.cbxdoesmanfdeemNo.isChecked() == false && binding.cbxdoesmanfdeemMaybe.isChecked() == true) {
                    deemthisWarranty = "1";
                } else if (binding.cbxdoesmanfdeemYes.isChecked() == false && binding.cbxdoesmanfdeemNo.isChecked() == true && binding.cbxdoesmanfdeemMaybe.isChecked() == false) {
                    deemthisWarranty = "0";
                }

                if (binding.cbxadvancereplYes.isChecked() == true && binding.cbxadvancereplNo.isChecked() == false) {
                    advance_replacement = "1";
                } else if (binding.cbxadvancereplYes.isChecked() == false && binding.cbxadvancereplNo.isChecked() == true) {
                    advance_replacement = "0";
                }

                if (binding.cbxpartsoldbyYes.isChecked() == true && binding.cbxpartsoldbyNo.isChecked() == false && binding.cbxpartsoldbyNotsure.isChecked() == false) {
                    partsold_byseatech = "1";
                } else if (binding.cbxpartsoldbyYes.isChecked() == false && binding.cbxpartsoldbyNo.isChecked() == true && binding.cbxpartsoldbyNotsure.isChecked() == false) {
                    partsold_byseatech = "0";
                } else if (binding.cbxpartsoldbyYes.isChecked() == false && binding.cbxpartsoldbyNo.isChecked() == false && binding.cbxpartsoldbyNotsure.isChecked() == true) {
                    partsold_byseatech = "1";
                }

                if (binding.cbxneedloanerYes.isChecked() == true && binding.cbxneedloanerNo.isChecked() == false) {
                    need_loaner = "1";
                } else if (binding.cbxneedloanerYes.isChecked() == false && binding.cbxneedloanerNo.isChecked() == true) {
                    need_loaner = "0";
                }

                AddPartToSrver(NeedUrgent, binding.etSupplyamount.getText().toString(), howfast_Date, price_approval_required, "0", manufacturerArrayList.get(binding.spinnerSelectmanuf.getSelectedItemPosition()).getId(), binding.etPartno.getText().toString(), binding.etQuantityNeeded.getText().toString(), binding.etSerialno.getText().toString(), binding.etFailuredesc.getText().toString(), binding.etTechsupportname.getText().toString(), binding.etRmaorcase.getText().toString(), deemthisWarranty, advance_replacement, partsold_byseatech, need_loaner, binding.etAdditnotes.getText().toString());
            }
        });

        productyesDialog.show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            if (photoFile != null) {
                Uri fileUri = FileProvider.getUriForFile(getActivity(),
                        "com.example.android.fileprovider", photoFile);

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                    takePictureIntent.setClipData(ClipData.newRawUri("", fileUri));
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PNG_SEATECH" + timeStamp + "_";
        String dir = "";
        File file = null;
        try {
            dir = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/Seatech/";
            file = new File(dir);
            file.mkdir();
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageFile = new File(file, imageFileName + ".png");

        return imageFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            all_path = data.getStringArrayExtra("all_path");
            ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();
            imagesarray.clear();
            for (String string : all_path) {
                CustomGallery item = new CustomGallery();
                item.sdcardPath = string;
                dataT.add(item);
                imagesarray.add(string);
            }
            adapterUploadImages = new AdapterPartUpldImages(getActivity(), imagesarray);
            binding.relimagesRecyclerView.setAdapter(adapterUploadImages);

        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                Log.e("imageUri below:", "" + Uri.fromFile(imageFile));
                imagesarray.clear();
                try {
                    all_path = new String[]{imageFile.getAbsolutePath()};
                    imagesarray.add(imageFile.getAbsolutePath());
                    adapterUploadImages = new AdapterPartUpldImages(getActivity(), imagesarray);
                    binding.relimagesRecyclerView.setAdapter(adapterUploadImages);
                } catch (Exception e) {
                }

            } else {
                Uri fileUri = FileProvider.getUriForFile(getActivity(),
                        "com.example.android.fileprovider", imageFile);
                imagesarray.clear();
                Log.i("imageUri:", "" + fileUri);
                all_path = new String[]{imageFile.getAbsolutePath()};
                imagesarray.add(imageFile.getAbsolutePath());
                adapterUploadImages = new AdapterPartUpldImages(getActivity(), imagesarray);
                binding.relimagesRecyclerView.setAdapter(adapterUploadImages);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class FetchManuFacture extends AsyncTask<ArrayList<ManufacturerSkeleton>, Void, ArrayList<ManufacturerSkeleton>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ManufacturerSkeleton ske = new ManufacturerSkeleton();
            ske.setName("Select Manufacturer");
            ske.setNeedProduct("");
            ske.setRmaRequired("");
            ske.setComment("");
            ske.setId("");
            ske.setPhone("");
            manufacturerArrayList.add(ske);
        }

        @Override
        protected ArrayList<ManufacturerSkeleton> doInBackground(ArrayList<ManufacturerSkeleton>... arrayLists) {
            SQLiteDatabase database = getInstance(getActivity()).getWritableDatabase();
            Gson gson = new Gson();
            Cursor cursor = database.query(ParseOpenHelper.TABLENAME_MANUFACTURER, null, null, null, null, null, null);
            cursor.moveToFirst();
            ArrayList<ManufacturerSkeleton> arralistManufacture = new ArrayList<>();

            while (!cursor.isAfterLast()) {
                Type typeske = new TypeToken<ArrayList<ManufacturerSkeleton>>() {
                }.getType();
                String getSke = cursor.getString(cursor.getColumnIndex(ALLMANUFACTURER));
                ArrayList<ManufacturerSkeleton> arrayListDash = gson.fromJson(getSke, typeske);
                arralistManufacture.addAll(arrayListDash);
                cursor.moveToNext();
            }
            cursor.close();
            return arralistManufacture;
        }

        @Override
        protected void onPostExecute(ArrayList<ManufacturerSkeleton> manufacturerSkeletons) {
            super.onPostExecute(manufacturerSkeletons);
            // manufacturerArrayList = manufacturerSkeletons;
            manufacturerArrayList.addAll(manufacturerSkeletons);
          /*  ManufacturerSkeleton ske = new ManufacturerSkeleton();
            ske.setName("Select Amnuffff");
            ske.setNeedProduct("");
            ske.setRmaRequired("");
            ske.setComment("");
            ske.setId("");
            ske.setPhone("");
            manufacturerArrayList.add(ske);*/
        }
    }

    private void AddPartToSrver(String urgent, String part_description, String how_fast_needed, String price_approval_required, String partforrepair, String manufacturer_id, String part_no, String quantity_needed, String serial_no, String failure_description, String tech_support_name, String rma_or_case_from_mfg_support,
                                String mfg_deem_this_warranty, String advance_replacement, String part_sold_by_seatech, String need_loner, String notes) {
        int count_needpart = HandyObject.getIntPrams(getActivity(), AppConstants.NEEDPART_COUNT);
        count_needpart++;
        HandyObject.putIntPrams(getActivity(), AppConstants.NEEDPART_COUNT, count_needpart);
        AddPartSkeleton ske = new AddPartSkeleton();
        ske.setCount(String.valueOf(count_needpart));
        ske.setTech_id(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID));
        ske.setJob_id(jobid);
        ske.setUrgent(urgent);
        ske.setPart_description(part_description);
        ske.setHow_fast_needed(how_fast_needed);
        ske.setPrice_approval_required(price_approval_required);
        ske.setPart_for_repair(partforrepair);
        ske.setManufacturer_id(manufacturer_id);
        ske.setPart_no(part_no);
        ske.setQuantity_needed(quantity_needed);
        ske.setSerial_no(serial_no);
        ske.setFailure_description(failure_description);
        ske.setTech_support_name(tech_support_name);
        ske.setRma_or_case_from_mfg_support(rma_or_case_from_mfg_support);
        ske.setMfg_deem_this_warranty(mfg_deem_this_warranty);
        ske.setAdvance_replacement(advance_replacement);
        ske.setPart_sold_by_seatech(part_sold_by_seatech);
        ske.setNeed_loner(need_loner);
        ske.setNotes(notes);
        ArrayList<AddPartSkeleton> arrayList = new ArrayList<>();
        arrayList.add(ske);

        gson = new Gson();
        String part_request = gson.toJson(arrayList);
        final String insertedTime = HandyObject.ParseDateTimeForNotes(new Date());
        insertIntoDB(insertedTime, arrayList, all_path);

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM)
                .addFormDataPart("part_request", part_request);
       /* builder.setType(MultipartBody.FORM)
                .addFormDataPart("tech_id", HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID))
                .addFormDataPart("job_id", jobid)
                .addFormDataPart("urgent", urgent)
                .addFormDataPart("part_description", part_description)
                .addFormDataPart("how_fast_needed", how_fast_needed)
                .addFormDataPart("price_approval_required", price_approval_required)
                .addFormDataPart("part_for_repair", partforrepair)
                .addFormDataPart("manufacturer_id", manufacturer_id)
                .addFormDataPart("part_no", part_no)
                .addFormDataPart("quantity_needed", quantity_needed)
                .addFormDataPart("serial_no", serial_no)
                .addFormDataPart("failure_description", failure_description)
                .addFormDataPart("tech_support_name", tech_support_name)
                .addFormDataPart("rma_or_case_from_mfg_support", rma_or_case_from_mfg_support)
                .addFormDataPart("mfg_deem_this_warranty", mfg_deem_this_warranty)
                .addFormDataPart("advance_replacement", advance_replacement)
                .addFormDataPart("part_sold_by_seatech", part_sold_by_seatech)
                .addFormDataPart("need_loner", need_loner)
                .addFormDataPart("notes", notes);*/

        if (all_path == null || all_path.length < 1) {
        } else if (all_path.length > 0) {
            ArrayList<File> imagesData = getImagesData();
            // co.showLoading();
            for (int i = 0; i < imagesData.size(); i++) {
                builder.setType(MultipartBody.FORM)
                        .addFormDataPart("img_" + count_needpart + "[" + i + "]", "image" + i + ".png",
                                RequestBody.create(MEDIA_TYPE_FORM, imagesData.get(i)))
                        .build();
            }
        }
        if (HandyObject.checkInternetConnection(getActivity())) {
            MultipartBody requestBody = builder.build();
            HandyObject.showProgressDialog(getActivity());
            HandyObject.getApiManagerMain().NeedPartData(requestBody, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID)).enqueue(
                    new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                String jsonResponse = response.body().string();
                                Log.e("needpart", jsonResponse);
                                JSONObject jsonObject = new JSONObject(jsonResponse);
                                if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                                    HandyObject.showAlert(getActivity(), jsonObject.getString("message"));

                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jobjInside = jsonArray.getJSONObject(i);
                                        String jobid = jobjInside.getString("job_id");
                                        database.delete(ParseOpenHelper.TABLE_ADDPART, ParseOpenHelper.ADDPARTJOBID + " =? AND " + ParseOpenHelper.ADDPARTCREATEDAT + " = ?",
                                                new String[]{jobid, insertedTime});
                                    }

                                    NeedAnotherProdDialog();
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
                                dialog.dismiss();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } finally {
                                //  commonObjects.showHideProgressBar(getActivity());
                                HandyObject.stopProgressDialog();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.e("responseError", t.getMessage());
                            // commonObjects.showHideProgressBar(getActivity());
                            HandyObject.stopProgressDialog();
                        }
                    }
            );
        } else {
            HandyObject.showAlert(getActivity(), getString(R.string.fetchdata_whenonline));
            HandyObject.stopProgressDialog();
            dialog.dismiss();
        }
    }

    private ArrayList<File> getImagesData() {
        ArrayList<File> imagesList = new ArrayList<>();
        for (String string : all_path) {
            File file = new File(string);
            Long s1 = file.length() / 1024;
            //  File f = compressImage(file);
            //   Long s2 = f.length() / 1024;

            Log.d("fileSize :- ", "s1 = " + s1.toString() + "\n" +
                    "s2 = " + s1.toString());
            imagesList.add(file);
        }
        return imagesList;
    }

    private void insertIntoDB(String time, ArrayList<AddPartSkeleton> arrayList, String[] images) {
        database = ParseOpenHelper.getInstance(getActivity()).getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ParseOpenHelper.ADDPARTCOUNT, arrayList.get(0).getCount());
        cv.put(ParseOpenHelper.ADDPARTTECHID, arrayList.get(0).getTech_id());
        cv.put(ParseOpenHelper.ADDPARTJOBID, arrayList.get(0).getJob_id());
        cv.put(ParseOpenHelper.ADDPARTURGENT, arrayList.get(0).getUrgent());
        cv.put(ParseOpenHelper.ADDPARTDESCRIPTION, arrayList.get(0).getPart_description());
        cv.put(ParseOpenHelper.ADDPARTHOWFASTNEEDED, arrayList.get(0).getHow_fast_needed());
        cv.put(ParseOpenHelper.ADDPARTPRICEAPPROVALREQUIRED, arrayList.get(0).getPrice_approval_required());
        cv.put(ParseOpenHelper.ADDPARTFORREPAIR, arrayList.get(0).getPart_for_repair());
        cv.put(ParseOpenHelper.ADDPARTMANUFACTID, arrayList.get(0).getManufacturer_id());
        cv.put(ParseOpenHelper.ADDPARTNO, arrayList.get(0).getPart_no());
        cv.put(ParseOpenHelper.ADDPARTQUANTITYNEEDED, arrayList.get(0).getQuantity_needed());
        cv.put(ParseOpenHelper.ADDPARTSERIALNO, arrayList.get(0).getSerial_no());
        cv.put(ParseOpenHelper.ADDPARTFAILUREDESC, arrayList.get(0).getFailure_description());
        cv.put(ParseOpenHelper.ADDPARTTECHSUPPORTNAME, arrayList.get(0).getTech_support_name());
        cv.put(ParseOpenHelper.ADDPARTSETRMAORCASE, arrayList.get(0).getRma_or_case_from_mfg_support());
        cv.put(ParseOpenHelper.ADDPARTMFGDEEMTHISWARRANTY, arrayList.get(0).getMfg_deem_this_warranty());
        cv.put(ParseOpenHelper.ADDPARTADVANCEREPLACEMENT, arrayList.get(0).getAdvance_replacement());
        cv.put(ParseOpenHelper.ADDPARTSOLDBYSEATECH, arrayList.get(0).getPart_sold_by_seatech());
        cv.put(ParseOpenHelper.ADDPARTNEEDLOANER, arrayList.get(0).getNeed_loner());
        cv.put(ParseOpenHelper.ADDPARTNOTES, arrayList.get(0).getNotes());
        cv.put(ParseOpenHelper.ADDPARTCREATEDAT, time);
        if (images == null || images.length < 1) {
            cv.put(ParseOpenHelper.ADDPARTUPLOADEDIMAGES, "");
        } else {
            ArrayList<File> imagesData = getImagesData();
            cv.put(ParseOpenHelper.ADDPARTUPLOADEDIMAGES, gson.toJson(imagesData));

        }
        long idd = database.insert(ParseOpenHelper.TABLE_ADDPART, null, cv);

    }
}
