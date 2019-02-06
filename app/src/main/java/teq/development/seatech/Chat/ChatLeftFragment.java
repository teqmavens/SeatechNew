package teq.development.seatech.Chat;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.github.nkzawa.emitter.Emitter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.App;
import teq.development.seatech.Chat.Adapter.AdapterChatJobList;
import teq.development.seatech.Chat.Skeleton.ChatJobListSkeleton;
import teq.development.seatech.JobDetail.UploadImageDialog;
import teq.development.seatech.LoginActivity;
import teq.development.seatech.ManufacturerSkeleton;
import teq.development.seatech.R;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.database.ParseOpenHelper;
import teq.development.seatech.databinding.FrgmChatleftBinding;

public class ChatLeftFragment extends Fragment {

    AdapterChatJobList adapter;
    public static FrgmChatleftBinding binding;
    ArrayList<ChatJobListSkeleton> arrayList;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    ArrayList<String> jobIds;
    String fromlocal, jobidlocal, clickedJobID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgm_chatleft, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        binding = DataBindingUtil.bind(rootView);
        binding.setFrgmchatleft(this);
        initViews(binding);
        return rootView;
    }

    private void initViews(FrgmChatleftBinding binding) {
        sqLiteDatabase = ParseOpenHelper.getInstance(getActivity()).getWritableDatabase();
        arrayList = new ArrayList<>();
        jobIds = new ArrayList<>();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(FromComposeReciever,
                new IntentFilter("ToChatLeftView"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(FromMsgToUpdateCount,
                new IntentFilter("ToChatLeftUpdateCount"));
        LinearLayoutManager lLManagerPickupJobs = new LinearLayoutManager(getActivity());
        lLManagerPickupJobs.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerview.setLayoutManager(lLManagerPickupJobs);
        binding.etSearch.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(getActivity(), R.drawable.searchleftchat), null);
        // App.appInstance.getSocket().on("job specific message", onNewLeftJobMessage);
        App.appInstance.getSocket().on("update left message count", onNewLeftJobMessage);
        App.appInstance.getSocket().connect();

        new databaseFetch().execute();
        if (getArguments() != null) {
            GetChatJobList("fromcomposer", getArguments().getString("jobidd"));
            fromlocal = "fromcomposer";
            jobidlocal = getArguments().getString("jobidd");
        } else {
            fromlocal = "normal";
            jobidlocal = "1111";
            GetChatJobList("normal", "1111");
        }

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapter.getFilter().filter(s.toString());
                } catch (Exception e) {
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private BroadcastReceiver FromComposeReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String job_id = intent.getStringExtra("jobidtoleft");
            fromlocal = "fromcomposer";
            jobidlocal = job_id;
            GetChatJobList("fromcomposer", job_id);
        }
    };

    private BroadcastReceiver FromMsgToUpdateCount = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String job_id = intent.getStringExtra("jobid_forcount");
            String customer_name = intent.getStringExtra("customer_name_forcount");
            String customer_type = intent.getStringExtra("customer_type_forcount");
            String boat_make_year = intent.getStringExtra("boat_make_year_forcount");
            String boat_name = intent.getStringExtra("boat_name_forcount");
            String count = intent.getStringExtra("count_forcount");

            if (jobIds.contains(job_id)) {
                int position = jobIds.indexOf(job_id);
                arrayList.get(position).setNewmsg(count);
                adapter.notifyDataSetChanged();
                ContentValues cv = new ContentValues();
                cv.put(ParseOpenHelper.CHATPARENT_NEWMSG, count);
                sqLiteDatabase.update(ParseOpenHelper.TABLE_CHATPARENTLEFT, cv, ParseOpenHelper.CHATPARENT_TEQID + " =? AND " + ParseOpenHelper.CHATPARENT_JOBID + " = ?",
                        new String[]{HandyObject.getPrams(getContext(), AppConstants.LOGINTEQPARENT_ID), job_id});
            } else {
                ChatJobListSkeleton ske = new ChatJobListSkeleton();
                ske.setJobid(job_id);
                ske.setCustomer_name(customer_name);
                ske.setCustomer_type(customer_type);
                ske.setBoat_make_year(boat_make_year);
                ske.setBoat_name(boat_name);
                ske.setNewmsg(count);
                arrayList.add(ske);
                jobIds.add(job_id);
                adapter.notifyDataSetChanged();
                ContentValues cv = new ContentValues();
                cv.put(ParseOpenHelper.CHATPARENT_TEQID, HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQPARENT_ID));
                cv.put(ParseOpenHelper.CHATPARENT_JOBID, job_id);
                cv.put(ParseOpenHelper.CHATPARENT_CUSNAME, customer_name);
                cv.put(ParseOpenHelper.CHATPARENT_CUSTYPE, customer_type);
                cv.put(ParseOpenHelper.CHATPARENT_BMY, boat_make_year);
                cv.put(ParseOpenHelper.CHATPARENT_BNAME, boat_name);
                cv.put(ParseOpenHelper.CHATPARENT_NEWMSG, count);
                long id = sqLiteDatabase.insert(ParseOpenHelper.TABLE_CHATPARENTLEFT, null, cv);
            }
            // }
        }
    };

    public void OnClickCompose() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("composemsg");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment newFragment = ComposeDialog.newInstance(4);
        newFragment.show(ft, "composemsg");
    }

    private Emitter.Listener onNewLeftJobMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e("LeftJOBID Update", "LeftJOBID");
                    try {
                        // JSONArray data = (JSONArray) args[0];
                        //  for (int k = 0; k < data.length(); k++) {
                        // JSONObject jobj_leftupdate = data.getJSONObject(k);
                        JSONObject jobj_leftupdate = (JSONObject) args[0];
                        //   if(jobj_leftupdate.getString("job_id").equalsIgnoreCase(clickedJobID)){} else {
                        if (jobIds.contains(jobj_leftupdate.getString("job_id"))) {
                            int position = jobIds.indexOf(jobj_leftupdate.getString("job_id"));
                            arrayList.get(position).setNewmsg(jobj_leftupdate.getString("count"));
                            adapter.notifyDataSetChanged();
                            ContentValues cv = new ContentValues();
                            cv.put(ParseOpenHelper.CHATPARENT_NEWMSG, jobj_leftupdate.getString("count"));
                            sqLiteDatabase.update(ParseOpenHelper.TABLE_CHATPARENTLEFT, cv, ParseOpenHelper.CHATPARENT_TEQID + " =? AND " + ParseOpenHelper.CHATPARENT_JOBID + " = ?",
                                    new String[]{HandyObject.getPrams(getContext(), AppConstants.LOGINTEQPARENT_ID), jobj_leftupdate.getString("job_id")});
                        } else {
                            ChatJobListSkeleton ske = new ChatJobListSkeleton();
                            ske.setJobid(jobj_leftupdate.getString("job_id"));
                            ske.setCustomer_name(jobj_leftupdate.getString("customer_name"));
                            ske.setCustomer_type(jobj_leftupdate.getString("customer_type"));
                            ske.setBoat_make_year(jobj_leftupdate.getString("boat_make_year"));
                            ske.setBoat_name(jobj_leftupdate.getString("boat_name"));
                            ske.setNewmsg(jobj_leftupdate.getString("count"));
                            arrayList.add(ske);
                            jobIds.add(jobj_leftupdate.getString("job_id"));
                            adapter.notifyDataSetChanged();
                            ContentValues cv = new ContentValues();
                            cv.put(ParseOpenHelper.CHATPARENT_TEQID, HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQPARENT_ID));
                            cv.put(ParseOpenHelper.CHATPARENT_JOBID, jobj_leftupdate.getString("job_id"));
                            cv.put(ParseOpenHelper.CHATPARENT_CUSNAME, jobj_leftupdate.getString("customer_name"));
                            cv.put(ParseOpenHelper.CHATPARENT_CUSTYPE, jobj_leftupdate.getString("customer_type"));
                            cv.put(ParseOpenHelper.CHATPARENT_BMY, jobj_leftupdate.getString("boat_make_year"));
                            cv.put(ParseOpenHelper.CHATPARENT_BNAME, jobj_leftupdate.getString("boat_name"));
                            cv.put(ParseOpenHelper.CHATPARENT_NEWMSG, jobj_leftupdate.getString("count"));
                            long id = sqLiteDatabase.insert(ParseOpenHelper.TABLE_CHATPARENTLEFT, null, cv);
                        }
                        //}
                        //  }
                    } catch (Exception e) {
                    }
                }
            }, 10);
        }
    };

    private void GetChatJobList(final String from, final String jobid) {

        if (HandyObject.checkInternetConnection(getActivity())) {
            HandyObject.getApiManagerMain().LeftChatJobList(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQPARENT_ID), HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID))
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                String jsonResponse = response.body().string();
                                Log.e("responseChatLeft", jsonResponse);
                                JSONObject jsonObject = new JSONObject(jsonResponse);
                                arrayList.clear();
                                jobIds.clear();

                                sqLiteDatabase.delete(ParseOpenHelper.TABLE_CHATPARENTLEFT, ParseOpenHelper.CHATPARENT_TEQID + "=?", new String[]{HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQPARENT_ID)});
                                if (jsonObject.getString("status").toLowerCase().equals("success")) {
                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                    if (jsonArray.length() == 0) {
                                        ChatLeftFragment.binding.recyclerview.setVisibility(View.INVISIBLE);
                                        ChatLeftFragment.binding.nodatafound.setVisibility(View.VISIBLE);
                                    } else {
                                        ChatLeftFragment.binding.recyclerview.setVisibility(View.VISIBLE);
                                        ChatLeftFragment.binding.nodatafound.setVisibility(View.INVISIBLE);
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jobj = jsonArray.getJSONObject(i);
                                            ChatJobListSkeleton ske = new ChatJobListSkeleton();
                                            ske.setJobid(jobj.getString("job_id"));
                                            ske.setCustomer_name(jobj.getString("customer_name"));
                                            ske.setCustomer_type(jobj.getString("customer_type"));
                                            ske.setBoat_make_year(jobj.getString("boat_make_year"));
                                            ske.setBoat_name(jobj.getString("boat_name"));
                                            ske.setNewmsg(jobj.getString("message_count"));
                                            arrayList.add(ske);
                                            jobIds.add(jobj.getString("job_id"));

                                            ContentValues cv = new ContentValues();
                                            cv.put(ParseOpenHelper.CHATPARENT_TEQID, HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQPARENT_ID));
                                            cv.put(ParseOpenHelper.CHATPARENT_JOBID, jobj.getString("job_id"));
                                            cv.put(ParseOpenHelper.CHATPARENT_CUSNAME, jobj.getString("customer_name"));
                                            cv.put(ParseOpenHelper.CHATPARENT_CUSTYPE, jobj.getString("customer_type"));
                                            cv.put(ParseOpenHelper.CHATPARENT_BMY, jobj.getString("boat_make_year"));
                                            cv.put(ParseOpenHelper.CHATPARENT_BNAME, jobj.getString("boat_name"));
                                            cv.put(ParseOpenHelper.CHATPARENT_NEWMSG, jobj.getString("message_count"));
                                            long id = sqLiteDatabase.insert(ParseOpenHelper.TABLE_CHATPARENTLEFT, null, cv);
                                        }

                                        if (from.equalsIgnoreCase("fromcomposer")) {
                                            int posi = jobIds.indexOf(jobid);
                                            Intent intentnew = new Intent("switchChat");
                                            intentnew.putExtra("jobid", jobid);
                                            intentnew.putExtra("customer_name", arrayList.get(posi).getCustomer_name());
                                            intentnew.putExtra("customer_type", arrayList.get(posi).getCustomer_type());
                                            intentnew.putExtra("boat_year", arrayList.get(posi).getBoat_make_year());
                                            intentnew.putExtra("boat_name", arrayList.get(posi).getBoat_name());
                                            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intentnew);
                                            adapter = new AdapterChatJobList(getActivity(), arrayList, ChatLeftFragment.this, posi);
                                            binding.recyclerview.setAdapter(adapter);
                                        } else {
                                            adapter = new AdapterChatJobList(getActivity(), arrayList, ChatLeftFragment.this, -1);
                                            binding.recyclerview.setAdapter(adapter);
                                        }
                                    }

                                } else {
                                    HandyObject.showAlert(getActivity(), jsonObject.getString("message"));
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
            HandyObject.showAlert(getActivity(), getString(R.string.check_internet_connection));
        }

    }

    public void onClickItem(ChatJobListSkeleton chatJobListSkeleton) {
       /* clickedJobID = chatJobListSkeleton.getJobid();
        if (jobIds.contains(chatJobListSkeleton.getJobid())) {
            int position = jobIds.indexOf(chatJobListSkeleton.getJobid());
            arrayList.get(position).setNewmsg("0");
            adapter.notifyDataSetChanged();
            ContentValues cv = new ContentValues();
            cv.put(ParseOpenHelper.CHATPARENT_NEWMSG, "0");
            sqLiteDatabase.update(ParseOpenHelper.TABLE_CHATPARENTLEFT, cv, ParseOpenHelper.CHATPARENT_TEQID + " =? AND " + ParseOpenHelper.CHATPARENT_JOBID + " = ?",
                    new String[]{HandyObject.getPrams(getContext(), AppConstants.LOGINTEQPARENT_ID), chatJobListSkeleton.getJobid()});
        }*/
        Intent intent = new Intent("switchChat");
        intent.putExtra("jobid", chatJobListSkeleton.getJobid());
        intent.putExtra("customer_name", chatJobListSkeleton.getCustomer_name());
        intent.putExtra("customer_type", chatJobListSkeleton.getCustomer_type());
        intent.putExtra("boat_year", chatJobListSkeleton.getBoat_make_year());
        intent.putExtra("boat_name", chatJobListSkeleton.getBoat_name());
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        App.appInstance.getSocket().off("update left message count", onNewLeftJobMessage);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(FromComposeReciever);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(FromMsgToUpdateCount);
    }

    public class databaseFetch extends AsyncTask<ArrayList<ChatJobListSkeleton>, Void, ArrayList<ChatJobListSkeleton>> {

        @Override
        protected ArrayList<ChatJobListSkeleton> doInBackground(ArrayList<ChatJobListSkeleton>... arrayLists) {
            arrayList.clear();
            jobIds.clear();
            cursor = sqLiteDatabase.query(ParseOpenHelper.TABLE_CHATPARENTLEFT, null, ParseOpenHelper.CHATPARENT_TEQID + "=?", new String[]{HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQPARENT_ID)}, null, null, null);
            cursor.moveToFirst();
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    while (!cursor.isAfterLast()) {
                        ChatJobListSkeleton ske = new ChatJobListSkeleton();
                        ske.setJobid(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.CHATPARENT_JOBID)));
                        ske.setCustomer_name(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.CHATPARENT_CUSNAME)));
                        ske.setCustomer_type(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.CHATPARENT_CUSTYPE)));
                        ske.setBoat_make_year(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.CHATPARENT_BMY)));
                        ske.setBoat_name(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.CHATPARENT_BNAME)));
                        ske.setNewmsg(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.CHATPARENT_NEWMSG)));
                        arrayList.add(ske);
                        jobIds.add(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.CHATPARENT_JOBID)));
                        cursor.moveToNext();
                    }
                    cursor.close();
                } else {
                }
            } else {
            }
            return arrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<ChatJobListSkeleton> chatJobListSkeletons) {
            super.onPostExecute(chatJobListSkeletons);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                   /* if (fromlocal.equalsIgnoreCase("fromcomposer")) {
                        Intent intentnew = new Intent("switchChat");
                        intentnew.putExtra("jobid", jobidlocal);
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intentnew);
                        int posi = jobIds.indexOf(jobidlocal);
                        adapter = new AdapterChatJobList(getActivity(), chatJobListSkeletons, ChatLeftFragment.this, posi);
                        binding.recyclerview.setAdapter(adapter);
                    } else {*/
                    adapter = new AdapterChatJobList(getActivity(), chatJobListSkeletons, ChatLeftFragment.this, -1);
                    binding.recyclerview.setAdapter(adapter);
                    //  }
                } else {
                }
            }
        }
    }
}
