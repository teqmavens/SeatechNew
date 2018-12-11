package teq.development.seatech.Timesheet;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.content.res.AppCompatResources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import teq.development.seatech.R;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.databinding.FrgmTimesheetBinding;

public class TimeSheetFragment extends Fragment {

    public static FrgmTimesheetBinding binding;
    boolean statusactive;
    DatePickerDialog.OnDateSetListener dateFrom, dateTo;
    Calendar myCalendarFrom, myCalendarTo, calendarweek;
    String fromMonth_SelDate, SelectedMonth_Date;
    public static String FromMonthdate_forStatus, ToMonthdate_forStatus;
    MonthPickerDialog.Builder builder;
    int previousindex;
    boolean optionavailable, clickonsearch;
    int count = 0;
    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgm_timesheet, container, false);
        binding = DataBindingUtil.bind(rootView);
        binding.setFrgmtimesheet(this);
        context = getActivity();
        initViews();
        return rootView;
    }

    private void initViews() {
        calendarweek = Calendar.getInstance(Locale.UK);
        SelectedMonth_Date = HandyObject.ParseDateJobTime(calendarweek.getTime());
        //  prevnextMonth_date = HandyObject.ParseDateJobTime(calendarweek.getTime());
        FromMonthdate_forStatus = HandyObject.ParseDateJobTime(HandyObject.CurrentMonthFirstDate(HandyObject.ParseDateJobTime(calendarweek.getTime())));
        ToMonthdate_forStatus = HandyObject.ParseDateJobTime(HandyObject.CurrentMonthLastDate(HandyObject.ParseDateJobTime(calendarweek.getTime())));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(fromMonthreciever,
                new IntentFilter("frommonthview"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(clickbackreciever,
                new IntentFilter("clickback_fromchild"));
        binding.etfrom.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(getActivity(), R.drawable.small_calendar), null);
        binding.etto.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(getActivity(), R.drawable.small_calendar), null);

        binding.previousweek.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(getActivity(), R.drawable.leftarrow), null, null, null);
        binding.nextweek.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(getActivity(), R.drawable.rightarrow), null);
        binding.previousmonth.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(getActivity(), R.drawable.leftarrow), null, null, null);
        binding.nextmonth.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(getActivity(), R.drawable.rightarrow), null);

        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.status_array,
                android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerStatus.setAdapter(statusAdapter);

        ArrayAdapter<CharSequence> weekAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.week_array1st,
                android.R.layout.simple_spinner_item);
        weekAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerWeekMonth.setAdapter(weekAdapter);


        binding.spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sel = parent.getItemAtPosition(position).toString();
                if (statusactive == true) {
                    TSMonthChildFragment fgm = new TSMonthChildFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("status", binding.spinnerStatus.getSelectedItem().toString());
                    bundle.putString("fromdate", FromMonthdate_forStatus);
                    bundle.putString("todate", ToMonthdate_forStatus);
                    // HandyObject.showAlert(getActivity(), binding.spinnerStatus.getSelectedItem().toString());
                    fgm.setArguments(bundle);
                    replaceChildFragment(fgm);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.spinnerWeekMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sel = parent.getItemAtPosition(position).toString();
                if (sel.equalsIgnoreCase("Current Month")) {
                    //   binding.statuskey.setVisibility(View.VISIBLE);

                    if (optionavailable == false) {
                        optionavailable = false;
                        binding.spinnerStatus.setVisibility(View.VISIBLE);
                        binding.etfrom.setVisibility(View.VISIBLE);
                        binding.etto.setVisibility(View.VISIBLE);
                        binding.btnclear.setVisibility(View.VISIBLE);
                        binding.btnsearch.setVisibility(View.VISIBLE);
                        //  binding.calendar.setVisibility(View.VISIBLE);
                        //  binding.or2.setVisibility(View.VISIBLE);

                        binding.previousweek.setVisibility(View.GONE);
                        binding.nextweek.setVisibility(View.GONE);
                        binding.previousmonth.setVisibility(View.VISIBLE);
                        binding.nextmonth.setVisibility(View.VISIBLE);

                        statusactive = true;
                        TSMonthChildFragment fgm = new TSMonthChildFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("status", binding.spinnerStatus.getSelectedItem().toString());
                        bundle.putString("fromdate", HandyObject.ParseDateJobTime(HandyObject.CurrentMonthFirstDate(HandyObject.ParseDateJobTime(calendarweek.getTime()))));
                        bundle.putString("todate", HandyObject.ParseDateJobTime(HandyObject.CurrentMonthLastDate(HandyObject.ParseDateJobTime(calendarweek.getTime()))));
                        //   HandyObject.showAlert(getActivity(), binding.spinnerStatus.getSelectedItem().toString());
                        fgm.setArguments(bundle);
                        replaceChildFragment(fgm);
                        binding.etfrom.setText("");
                        binding.etto.setText("");
                        binding.displaydate.setText("(" + HandyObject.ParseDateJobTime(calendarweek.getTime()) + ")");
                    }
                } else if (sel.equalsIgnoreCase("Current Week")) {
                    //  binding.statuskey.setVisibility(View.GONE);

                    if (optionavailable == false) {
                        optionavailable = false;
                        binding.spinnerStatus.setVisibility(View.GONE);
                        binding.etfrom.setVisibility(View.VISIBLE);
                        binding.etto.setVisibility(View.VISIBLE);
                        binding.btnclear.setVisibility(View.VISIBLE);
                        binding.btnsearch.setVisibility(View.VISIBLE);
                        // binding.calendar.setVisibility(View.GONE);
                        //    binding.or2.setVisibility(View.GONE);

                        binding.previousweek.setVisibility(View.VISIBLE);
                        binding.nextweek.setVisibility(View.VISIBLE);
                        binding.previousmonth.setVisibility(View.GONE);
                        binding.nextmonth.setVisibility(View.GONE);

                        TSWeekChildFragment fgm = new TSWeekChildFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("weekstartdate", HandyObject.getCurrentWeek_FirstDate(getActivity()));
                        binding.displaydate.setText("(" + HandyObject.getCurrentWeek_FirstDateNew(getActivity()).split(",")[0] + ") - " + "(" + HandyObject.getCurrentWeek_FirstDateNew(getActivity()).split(",")[1] + ")");
                        binding.displaydate.setText("(" + HandyObject.getCurrentWeek_FirstDateNew(getActivity()).split(",")[0] + ") - " + "(" + HandyObject.getCurrentWeek_FirstDateNew(getActivity()).split(",")[1] + ")");
                        fgm.setArguments(bundle);
                        replaceChildFragment(fgm);
                        binding.etfrom.setText("");
                        binding.etto.setText("");
                        //fromMonth_SelDate = HandyObject.ParseDateJobTime(calendarweek.getTime());
                        fromMonth_SelDate = HandyObject.ParseDateJobTimeNew(calendarweek.getTime());
                    }
                } else if (sel.equalsIgnoreCase("Select Month")) {
                    binding.spinnerWeekMonth.setSelection(previousindex);
                    builder.setActivatedMonth(Calendar.JULY)
                            .setMinYear(1990)
                            .setActivatedYear(2018)
                            .setMaxYear(2030)
                            .setMinMonth(Calendar.JANUARY)
                            .setTitle("Select Month/Year")
                            .setMonthRange(Calendar.JANUARY, Calendar.DECEMBER)
                            .setOnMonthChangedListener(new MonthPickerDialog.OnMonthChangedListener() {
                                @Override
                                public void onMonthChanged(int selectedMonth) {
                                    //HandyObject.showAlert(getActivity(), String.valueOf(selectedMonth));
                                }
                            }).setOnYearChangedListener(new MonthPickerDialog.OnYearChangedListener() {
                        @Override
                        public void onYearChanged(int year) {
                            //HandyObject.showAlert(getActivity(), String.valueOf(year));
                        }
                    }).build().show();
                }
                previousindex = position;
                Log.e("safsfs", "dasdasd");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        myCalendarFrom = Calendar.getInstance();
        dateFrom = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendarFrom.set(Calendar.YEAR, year);
                myCalendarFrom.set(Calendar.MONTH, monthOfYear);
                myCalendarFrom.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                if (view.isShown()) {
                    long selectedMilli = myCalendarFrom.getTimeInMillis();
                    Date datePickerDate = new Date(selectedMilli);
                    if (datePickerDate.after(new Date())) {
                        HandyObject.showAlert(getActivity(), "Can't Select Future date");
                    } else {
                     //   binding.etfrom.setText(HandyObject.getDateFromPicker(myCalendarFrom.getTime()));
                        binding.etfrom.setText(HandyObject.getDateFromPickerNew(myCalendarFrom.getTime()));
                    }
                }
            }
        };

        myCalendarTo = Calendar.getInstance();
        dateTo = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendarTo.set(Calendar.YEAR, year);
                myCalendarTo.set(Calendar.MONTH, monthOfYear);
                myCalendarTo.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                if (view.isShown()) {
                    //updateLabelFrom();
                    long selectedMilli = myCalendarFrom.getTimeInMillis();
                    Date datePickerDate = new Date(selectedMilli);
                    if (datePickerDate.after(new Date())) {
                        HandyObject.showAlert(getActivity(), "Can't Select Future date");
                    } else {
                       /* binding.etto.setText(HandyObject.getDateFromPicker(myCalendarTo.getTime()));*/
                        binding.etto.setText(HandyObject.getDateFromPickerNew(myCalendarTo.getTime()));
                    }
                }
            }
        };

        builder = new MonthPickerDialog.Builder(getActivity(), new MonthPickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(int selectedMonth, int selectedYear) {
                binding.spinnerWeekMonth.setSelection(previousindex);
                binding.spinnerStatus.setVisibility(View.GONE);
                binding.spinnerWeekMonth.setVisibility(View.INVISIBLE);
                binding.rlselected.setVisibility(View.VISIBLE);
                binding.rlselectedText.setText("Selected Month");
                //      HandyObject.showAlert(getActivity(), String.valueOf(selectedYear) + "-" + String.valueOf(selectedMonth) + "-" + "01");

                TSMonthChildFragment fgm = new TSMonthChildFragment();
                Bundle bundle = new Bundle();
                bundle.putString("status", binding.spinnerStatus.getSelectedItem().toString());
                bundle.putString("fromdate", HandyObject.ParseDateJobTime(HandyObject.CurrentMonthFirstDate(String.valueOf(selectedYear) + "-" + String.valueOf(selectedMonth) + "-" + "01")));
                bundle.putString("todate", HandyObject.ParseDateJobTime(HandyObject.CurrentMonthLastDate(String.valueOf(selectedYear) + "-" + String.valueOf(selectedMonth) + "-" + "01")));
                SelectedMonth_Date = String.valueOf(selectedYear) + "-" + String.valueOf(selectedMonth) + "-" + "01";
                //HandyObject.showAlert(getActivity(), binding.spinnerStatus.getSelectedItem().toString());
                fgm.setArguments(bundle);
                replaceChildFragment(fgm);
                binding.etfrom.setText("");
                binding.etto.setText("");
            }
        }, 2018, 8);
    }

    private void callHandler() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                optionavailable = false;
                // clickonsearch = true;
            }
        }, 1000);
    }

    private BroadcastReceiver fromMonthreciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // binding.lastoffRecrdnotes.setText(intent.getStringExtra("lastofftherecord"));
            binding.spinnerStatus.setVisibility(View.GONE);
            binding.etfrom.setVisibility(View.VISIBLE);
            binding.etto.setVisibility(View.VISIBLE);
            binding.btnclear.setVisibility(View.VISIBLE);
            binding.btnsearch.setVisibility(View.VISIBLE);
            //  binding.calendar.setVisibility(View.GONE);
            //   binding.or2.setVisibility(View.GONE);

            binding.previousweek.setVisibility(View.VISIBLE);
            binding.nextweek.setVisibility(View.VISIBLE);
            binding.previousmonth.setVisibility(View.GONE);
            binding.nextmonth.setVisibility(View.GONE);

            binding.spinnerWeekMonth.setVisibility(View.INVISIBLE);
            binding.rlselected.setVisibility(View.VISIBLE);
            binding.rlselectedText.setText("Selected Week");
            fromMonth_SelDate = intent.getStringExtra("sel_enddate");

           /* binding.displaydate.setText("(" + HandyObject.getSelectedWeek_FirstDateslash(getActivity(), fromMonth_SelDate).split(",")[0] + ") - " + "(" + HandyObject.getSelectedWeek_FirstDateslash(getActivity(), fromMonth_SelDate).split(",")[1] + ")");*/
            binding.displaydate.setText("(" + HandyObject.getSelectedWeek_FirstDateslashNew(getActivity(), fromMonth_SelDate).split(",")[0] + ") - " + "(" + HandyObject.getSelectedWeek_FirstDateslashNew(getActivity(), fromMonth_SelDate).split(",")[1] + ")");

            TSWeekChildFragment fgm = new TSWeekChildFragment();
            Bundle bundle = new Bundle();
            bundle.putString("weekstartdate", HandyObject.getSelectedWeek_FirstDateslash(getActivity(), fromMonth_SelDate));
            fgm.setArguments(bundle);
            replaceChildFragment(fgm);
        }
    };


    private BroadcastReceiver clickbackreciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            OnClickClear();
        }
    };

    private void replaceChildFragment(Fragment mFragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.childcontainer, mFragment).addToBackStack(null)
                .commit();
    }


    public void OnClickClear() {
        binding.spinnerWeekMonth.setVisibility(View.VISIBLE);
        binding.rlselected.setVisibility(View.GONE);
        ArrayAdapter<CharSequence> weekAdapter = ArrayAdapter.createFromResource(context, R.array.week_array1st,
                android.R.layout.simple_spinner_item);
        weekAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerWeekMonth.setAdapter(weekAdapter);
       // binding.displaydate.setText("(" + HandyObject.ParseDateJobTime(calendarweek.getTime()) + ")");
        binding.displaydate.setText("(" + HandyObject.ParseDateJobTimeNew(calendarweek.getTime()) + ")");
    }

    public void OnClickSearch() {
        clickonsearch = true;
        if (binding.etfrom.getText().length() == 0 || binding.etto.getText().length() == 0) {
            HandyObject.showAlert(getActivity(), getString(R.string.selectbothdates));
        } else {
            binding.spinnerWeekMonth.setVisibility(View.INVISIBLE);
            binding.rlselected.setVisibility(View.VISIBLE);
            binding.rlselectedText.setText("Selected Month");
            binding.spinnerStatus.setVisibility(View.GONE);
            //  binding.calendar.setVisibility(View.GONE);
            //  binding.or2.setVisibility(View.GONE);

            binding.previousweek.setVisibility(View.GONE);
            binding.nextweek.setVisibility(View.GONE);
            binding.previousmonth.setVisibility(View.GONE);
            binding.nextmonth.setVisibility(View.GONE);
           /* binding.displaydate.setText("(" + HandyObject.parseDateToYMD(binding.etfrom.getText().toString()) + ") - " + "(" + HandyObject.parseDateToYMD(binding.etto.getText().toString()) + ")");*/
            binding.displaydate.setText("(" + HandyObject.parseDateToYMDNew(binding.etfrom.getText().toString()) + ") - " + "(" + HandyObject.parseDateToYMDNew(binding.etto.getText().toString()) + ")");
            TSWeekChildFragment fgm = new TSWeekChildFragment();
            Bundle bundle = new Bundle();
            bundle.putString("weekstartdate", HandyObject.parseDateToYMDNew(binding.etfrom.getText().toString()) + "," + HandyObject.parseDateToYMDNew(binding.etto.getText().toString()));
            fgm.setArguments(bundle);
            replaceChildFragment(fgm);
            binding.etfrom.setText("");
            binding.etto.setText("");
        }
    }

    public void OnClickEtFrom() {
        new DatePickerDialog(getActivity(), dateFrom, myCalendarFrom
                .get(Calendar.YEAR), myCalendarFrom.get(Calendar.MONTH),
                myCalendarFrom.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void OnClickEtTo() {
        new DatePickerDialog(getActivity(), dateTo, myCalendarTo
                .get(Calendar.YEAR), myCalendarTo.get(Calendar.MONTH),
                myCalendarTo.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void OnClickPreviousWeek() {
        TSWeekChildFragment fgm = new TSWeekChildFragment();
        Bundle bundle = new Bundle();
        bundle.putString("weekstartdate", HandyObject.getSelectedPreviousWeek_FirstDateSlash(getActivity(), fromMonth_SelDate));
        fgm.setArguments(bundle);
        replaceChildFragment(fgm);
    }

    public void OnClickNextWeek() {
        if (HandyObject.weekcount == 0) {
            HandyObject.showAlert(getActivity(), "Can't access further Week Data");
        } else {
            TSWeekChildFragment fgm = new TSWeekChildFragment();
            Bundle bundle = new Bundle();
            bundle.putString("weekstartdate", HandyObject.getSelectedPreviousWeek_FirstDateSlash(getActivity(), fromMonth_SelDate));
            fgm.setArguments(bundle);
            replaceChildFragment(fgm);
        }
    }

    public void OnClickCalendar() {
        builder.setActivatedMonth(Calendar.JULY)
                .setMinYear(1990)
                .setActivatedYear(2018)
                .setMaxYear(2030)
                .setMinMonth(Calendar.JANUARY)
                .setTitle("Select Month/Year")
                .setMonthRange(Calendar.JANUARY, Calendar.DECEMBER)
                .setOnMonthChangedListener(new MonthPickerDialog.OnMonthChangedListener() {
                    @Override
                    public void onMonthChanged(int selectedMonth) {
                        //HandyObject.showAlert(getActivity(), String.valueOf(selectedMonth));
                    }
                }).setOnYearChangedListener(new MonthPickerDialog.OnYearChangedListener() {
            @Override
            public void onYearChanged(int year) {
                //HandyObject.showAlert(getActivity(), String.valueOf(year));
            }
        }).build().show();
    }

    public void OnClickPreviousMonth() {
        TSMonthChildFragment fgm = new TSMonthChildFragment();
        Bundle bundle = new Bundle();
        bundle.putString("status", binding.spinnerStatus.getSelectedItem().toString());
        //   bundle.putString("fromdate", HandyObject.ParseDateJobTime(HandyObject.PreviousMonthFirstDate(HandyObject.ParseDateJobTime(calendarweek.getTime()))));
        //  bundle.putString("todate", HandyObject.ParseDateJobTime(HandyObject.PreviousMonthLastDate(HandyObject.ParseDateJobTime(calendarweek.getTime()))));
        bundle.putString("fromdate", HandyObject.ParseDateJobTime(HandyObject.PreviousMonthFirstDate(SelectedMonth_Date)));
        bundle.putString("todate", HandyObject.ParseDateJobTime(HandyObject.PreviousMonthLastDate(SelectedMonth_Date)));
        fgm.setArguments(bundle);
        replaceChildFragment(fgm);
        binding.etfrom.setText("");
        binding.etto.setText("");
    }

    public void OnClickNextMonth() {
        if (HandyObject.monthcount == 0) {
            HandyObject.showAlert(getActivity(), "Can't access further Month Data");
        } else {
            TSMonthChildFragment fgm = new TSMonthChildFragment();
            Bundle bundle = new Bundle();
            bundle.putString("status", binding.spinnerStatus.getSelectedItem().toString());
            bundle.putString("fromdate", HandyObject.ParseDateJobTime(HandyObject.NextMonthFirstDate(SelectedMonth_Date)));
            bundle.putString("todate", HandyObject.ParseDateJobTime(HandyObject.NextMonthLastDate(SelectedMonth_Date)));
            fgm.setArguments(bundle);
            replaceChildFragment(fgm);
            binding.etfrom.setText("");
            binding.etto.setText("");
        }
    }

    public void OnClickRlSelected() {
        HandyObject.showAlert(getActivity(), "Press clear button to active this.");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(fromMonthreciever);
        Log.e("onDestyViewMain","onDestyViewMain");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("onDestyMain","onDestyMain");
    }

    /*public void OnClickBack() {
        getActivity().getSupportFragmentManager().popBackStack();
    }*/
}
