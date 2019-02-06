package teq.development.seatech.Timesheet;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.App;
import teq.development.seatech.LoginActivity;
import teq.development.seatech.R;
import teq.development.seatech.Timesheet.Adapter.AdapterTSMonth;
import teq.development.seatech.Timesheet.Skeleton.TSMonthSkeleton;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;

public class TSMonthChildFragment extends Fragment {

    RecyclerView recyclerView;
    AdapterTSMonth adapterTSMonth;
    ArrayList<TSMonthSkeleton> arrayList;
    Calendar calendar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgm_tsmonthchild, container, false);
        //https://drive.google.com/file/d/15Df7HTXyir5s694W0i6HpYxrwJw89v79/view?usp=drivesdk
        initViews(rootView);
        return rootView;
    }

    private void initViews(View rootView) {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        LinearLayoutManager lLManagerImages = new LinearLayoutManager(getActivity());
        lLManagerImages.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(lLManagerImages);
        calendar = Calendar.getInstance();
        if (getArguments() != null) {
            TSMonthApi(getArguments().getString("status"), getArguments().getString("fromdate"), getArguments().getString("todate"));
            TimeSheetFragment.FromMonthdate_forStatus = getArguments().getString("fromdate");
            TimeSheetFragment.ToMonthdate_forStatus = getArguments().getString("todate");
            //   yyyy-MM-dd
            TimeSheetFragment.binding.displaydate.setText("(" + HandyObject.parseDateToMDYNew(getArguments().getString("fromdate")) + ") - " + "(" + HandyObject.parseDateToMDYNew(getArguments().getString("todate")) + ")");
        }
    }

    private void TSMonthApi(final String status, final String from, String to) {
        // HandyObject.showProgressDialog(getActivity());
       // HandyObject.showAlert(getActivity(),from+"  "+to);
        if (HandyObject.checkInternetConnection(getActivity())) {
            HandyObject.getApiManagerMain().TSMonthData(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), from, to, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID))
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                String jsonResponse = response.body().string();
                                Log.e("responseTSMonth", jsonResponse);
                                JSONObject jsonObject = new JSONObject(jsonResponse);
                                arrayList = new ArrayList<>();
                                if (jsonObject.getString("status").toLowerCase().equals("success")) {
                                    //   HandyObject.showAlert(getActivity(), jsonObject.getString("message"));
                                    // JSONObject jobjdata = jsonObject.getJSONObject("data");
                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jobjinside = jsonArray.getJSONObject(i);
                                        TSMonthSkeleton ske = new TSMonthSkeleton();
                                        if (status.equalsIgnoreCase("All")) {
                                            ske.setEnd_date(jobjinside.getString("end_date"));
                                            ske.setStatus(jobjinside.getString("status"));
                                            arrayList.add(ske);
                                        } else if (jobjinside.getString("status").equalsIgnoreCase(status)) {
                                            ske.setEnd_date(jobjinside.getString("end_date"));
                                            ske.setStatus(jobjinside.getString("status"));
                                            arrayList.add(ske);
                                        }
                                    }
                                    adapterTSMonth = new AdapterTSMonth(getActivity(), arrayList, TSMonthChildFragment.this);
                                    recyclerView.setAdapter(adapterTSMonth);
                                } else {
                                    arrayList.clear();
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
                                //  HandyObject.stopProgressDialog();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.e("responseError", t.getMessage());
                            // HandyObject.stopProgressDialog();
                        }
                    });
        } else {
            HandyObject.showAlert(getActivity(), getString(R.string.check_internet_connection));
        }
    }

    public void onClickViewDetail(final int position) {
        Intent intent = new Intent("frommonthview");
        intent.putExtra("sel_enddate", arrayList.get(position).getEnd_date());
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("onDestyMonth", "onDestyMonth");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("onDestyViewMonth", "onDestyViewMonth");
    }
}
