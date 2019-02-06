package teq.development.seatech.Chat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.github.nkzawa.emitter.Emitter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.App;
import teq.development.seatech.Chat.Adapter.AdapterChatMessages;
import teq.development.seatech.Chat.Adapter.AdapterOppEmpList;
import teq.development.seatech.Chat.Skeleton.AllEmployeeSkeleton;
import teq.development.seatech.Chat.Skeleton.ChatMessagesSkeleton;
import teq.development.seatech.R;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.database.ParseOpenHelper;
import teq.development.seatech.databinding.FrgmChatrightBinding;

public class ChatRightFragment extends Fragment {

    FrgmChatrightBinding binding;
    AdapterChatMessages adapter;
    AdapterOppEmpList adapterOppEmpList;
    ArrayList<AllEmployeeSkeleton> arrayListEmp;
    ArrayList<ChatMessagesSkeleton> arrayList;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    UUID uniqueKey;
    String job_chatid;
    Gson gson;
    int pageCount = 0;
    int pageCountTotal = 0;
    boolean isloading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgm_chatright, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        binding = DataBindingUtil.bind(rootView);
        binding.setFrgmchatright(this);
        initViews();
        return rootView;
    }

    private void initViews() {
        gson = new Gson();
        sqLiteDatabase = ParseOpenHelper.getInstance(getActivity()).getWritableDatabase();
        arrayList = new ArrayList<>();
        adapter = new AdapterChatMessages(getActivity(), arrayList);
        binding.chatRecycler.setAdapter(adapter);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(SwithChatReciever,
                new IntentFilter("switchChat"));
        final LinearLayoutManager lLManagerUrgentJobs = new LinearLayoutManager(getActivity());
        lLManagerUrgentJobs.setOrientation(LinearLayoutManager.VERTICAL);
        binding.chatRecycler.setLayoutManager(lLManagerUrgentJobs);
        binding.chatRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int firstVisibleItemPosition = lLManagerUrgentJobs.findFirstVisibleItemPosition();
                int lastvisposi = lLManagerUrgentJobs.findLastVisibleItemPosition();
                int visibleItemCount = lLManagerUrgentJobs.getChildCount();
                int totalItemCount = lLManagerUrgentJobs.getItemCount();
              /*  Log.e("firstVisiItemPosition", String.valueOf(firstVisibleItemPosition));
                Log.e("lastvisposi", String.valueOf(lastvisposi));
                Log.e("visibleItemCount", String.valueOf(visibleItemCount));
                Log.e("totalItemCount", String.valueOf(totalItemCount));
                Log.e("dx", String.valueOf(dx));
                Log.e("dy", String.valueOf(dy));
                Log.e("COUNTTTTOP", String.valueOf(pageCount));*/

                if (String.valueOf(dy).contains("-") && firstVisibleItemPosition == 0 && totalItemCount >= 20) {
                    if (!isloading && pageCount < pageCountTotal) {
                        LoadChatMessage(job_chatid, String.valueOf(pageCount));
                    }
                }
            }
        });
        OpponentEmployeeList();
        App.appInstance.getSocket().on("receive chat message", onNewMessage);
        App.appInstance.getSocket().on("last urgent unacknowledged message", onAllAcknowledge);
        App.appInstance.getSocket().on("unacknowledged message count", AfterAcknowledgeTrigger);
        App.appInstance.getSocket().connect();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jobj = new JSONObject();
                    jobj.put("customId", Integer.parseInt(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQPARENT_ID)));
                    jobj.put("device_id", HandyObject.getPrams(getActivity(), AppConstants.DEVICE_TOKEN));
                    App.appInstance.getSocket().emit("storeClientInfo", jobj);
                } catch (Exception e) {
                }

            }
        }, 1000);
    }

    private Emitter.Listener onAllAcknowledge = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e("LastAcknowledge", "STop Alarm");
                    //  JSONObject data = (JSONObject) args[0];
                    // App.appInstance.stopTimer();
                    HandyObject.stopAlarm(getActivity());
                }
            }, 10);
        }
    };

    private Emitter.Listener AfterAcknowledgeTrigger = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e("unacknow msg count", "unacknow msg count");
                    JSONObject data = (JSONObject) args[0];
                    try {
                        Log.e("unacknow msg countRES", String.valueOf(args[0]));
                        // HandyObject.showAlert(getActivity(), data.toString());
                        Intent intent = new Intent("ToChatLeftUpdateCount");
                        intent.putExtra("jobid_forcount", data.getString("job_id"));
                        intent.putExtra("customer_name_forcount", data.getString("customer_name"));
                        intent.putExtra("customer_type_forcount", data.getString("customer_type"));
                        intent.putExtra("boat_make_year_forcount", data.getString("boat_make_year"));
                        intent.putExtra("boat_name_forcount", data.getString("boat_name"));
                        intent.putExtra("count_forcount", data.getString("count"));
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                    } catch (Exception e) {
                    }

                }
            }, 10);
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e("Messageeeeee", "dfdfdf");
                    JSONObject data = (JSONObject) args[0];
                    try {
                        StringBuilder sb = new StringBuilder();
                        StringBuilder sbid = new StringBuilder();
                        Log.e("Messageeeeee", String.valueOf(args[0]));
                        if (job_chatid.equalsIgnoreCase(data.getString("job_id"))) {
                            ChatMessagesSkeleton ske = new ChatMessagesSkeleton();
                            ske.setChatjobid(data.getString("job_id"));
                            ske.setMessage(data.getString("message"));
                            ske.setCreated(data.getString("created_at"));
                            ske.setSenderid(data.getString("sender_id"));
                            ske.setSendername(data.getString("sender_name"));
                            JSONArray jarry = data.getJSONArray("receiver");
                            for (int i = 0; i < jarry.length(); i++) {
                                sb.append(jarry.getJSONObject(i).getString("receiver_name") + ",");
                                sbid.append(jarry.getJSONObject(i).getString("receiver_id") + ",");
                            }
                            sb.setLength(sb.length() - 1);
                            sbid.setLength(sbid.length() - 1);
                            ske.setReceiverid(sbid.toString());
                            ske.setReceivername(sb.toString());
                            ske.setAcknowledge("0");
                            ske.setDelivered("0");
                            ske.setExist("yes");
                            ske.setId(data.getString("id"));
                            ske.setUrgent(data.getString("urgent"));
                            ske.setUUID("abc");
                            arrayList.add(ske);
                            adapter.notifyDataSetChanged();
                            binding.chatRecycler.scrollToPosition(adapter.getItemCount() - 1);
                            sendDeliverStatus(data.getString("id"), data.getString("sender_id"));

                           /* String restdata = gson.toJson(ske);
                            ContentValues cv = new ContentValues();
                            cv.put(ParseOpenHelper.CHAT_TEQID, HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQPARENT_ID));
                            cv.put(ParseOpenHelper.CHAT_JOBID, data.getString("job_id"));
                            cv.put(ParseOpenHelper.CHAT_REST, restdata);
                            long id = sqLiteDatabase.insert(ParseOpenHelper.TABLE_CHATMSGS, null, cv);*/
                        }

                       /* Intent intent = new Intent("ToChatLeftUpdateCount");
                        intent.putExtra("jobid_forcount", data.getString("job_id"));
                        intent.putExtra("customer_name_forcount", data.getString("customer_name"));
                        intent.putExtra("customer_type_forcount", data.getString("customer_type"));
                        intent.putExtra("boat_make_year_forcount", data.getString("boat_make_year"));
                        intent.putExtra("boat_name_forcount", data.getString("boat_name"));
                        intent.putExtra("count_forcount", data.getString("count"));
                        HandyObject.showAlert(getActivity(),data.getString("count"));
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);*/
                    } catch (Exception e) {
                    }
                }
            }, 10);
        }
    };

    private BroadcastReceiver SwithChatReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            binding.chatBg.setVisibility(View.VISIBLE);
            binding.topBg.setVisibility(View.VISIBLE);
            job_chatid = intent.getStringExtra("jobid");
            String cname = intent.getStringExtra("customer_name");
            String ctype = intent.getStringExtra("customer_type");
            String boatyear = intent.getStringExtra("boat_year");
            String boatname = intent.getStringExtra("boat_name");
            //sadma to h mujhe bhi

            binding.ticketidvalue.setText("#" + job_chatid);
            binding.customernamevalue.setText(cname);
            binding.customertypevalue.setText(ctype);
            binding.boatmakeyearvalue.setText(boatyear);
            binding.boatnamevalue.setText(boatname);
            new databaseFetch().execute();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    pageCount = 0;
                    pageCountTotal = 0;
                    Log.e("SWITCHHHHHH", "SWITHHHH");
                    //if (!isloading) {
                    LoadChatMessage(job_chatid, String.valueOf(pageCount));
                    // }
                }
            }, 10);

        }
    };

    public class databaseFetch extends AsyncTask<ArrayList<ChatMessagesSkeleton>, Void, ArrayList<ChatMessagesSkeleton>> {


        @Override
        protected ArrayList<ChatMessagesSkeleton> doInBackground(ArrayList<ChatMessagesSkeleton>... arrayLists) {
            arrayList.clear();
            cursor = sqLiteDatabase.query(ParseOpenHelper.TABLE_CHATMSGS, null, ParseOpenHelper.CHAT_JOBID + "=?", new String[]{job_chatid}, null, null, null);
            cursor.moveToFirst();
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    while (!cursor.isAfterLast()) {
                        Type type = new TypeToken<ChatMessagesSkeleton>() {
                        }.getType();

                        String getSke = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.CHAT_REST));
                        ChatMessagesSkeleton ske = gson.fromJson(getSke, type);
                        arrayList.add(0, ske);
                        cursor.moveToNext();
                    }
                    cursor.close();
                } else {
                    //binding.rcyviewJobs.setAdapter(adapterjobs);
                    //  HandyObject.showAlert(getActivity(), "cursor not greater than zero");
                }

            } else {
                // HandyObject.showAlert(getActivity(), "cursor null");
            }
            return arrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<ChatMessagesSkeleton> chatMessagesSkeletons) {
            super.onPostExecute(chatMessagesSkeletons);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    adapter.notifyDataSetChanged();
                    binding.chatRecycler.scrollToPosition(adapter.getItemCount() - 1);
                }
            }


        }
    }

    public void OnClickSendMsg() {
        if (binding.etMessage.getText().toString().length() == 0) {
            HandyObject.showAlert(getActivity(), getString(R.string.cannotsendblankmsg));
        } else if (binding.empspinner.getSelectedItemPosition() == 0) {
            HandyObject.showAlert(getActivity(), getString(R.string.selectreciever));
        } else {
            try {
                uniqueKey = UUID.randomUUID();
                Log.e("UNiqueid", String.valueOf(uniqueKey));
                String receiverId = arrayListEmp.get(binding.empspinner.getSelectedItemPosition()).getEmployeeId();
                String receiverName = arrayListEmp.get(binding.empspinner.getSelectedItemPosition()).getEmployeename();
                //  String senderid = HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID);
                String senderid = HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQPARENT_ID);
                String sendername = HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_FIRSTNAME)+" "+HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_LASTNAME);
                String urgent = "";
                if (binding.cbneedurgent.isChecked()) {
                    urgent = "1";
                } else {
                    urgent = "0";
                }

                JSONObject jobj = new JSONObject();
                JSONObject jobj_receiver = new JSONObject();
                jobj_receiver.put("receiver_id", Integer.parseInt(receiverId));
                jobj_receiver.put("receiver_name", receiverName);
                JSONArray jarry_receiver = new JSONArray();
                jarry_receiver.put(jobj_receiver);
                jobj.put("job_id", job_chatid);
                jobj.put("sender_id", Integer.parseInt(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQPARENT_ID)));
                jobj.put("sender_name", sendername);
                jobj.put("receiver", jarry_receiver);
                jobj.put("urgent", urgent);
                jobj.put("token", String.valueOf(uniqueKey));
                jobj.put("message", binding.etMessage.getText().toString());
                //  ChatActivity.mSocket.emit("send chat message", jobj);
                App.appInstance.getSocket().emit("send chat message", jobj);

                // OnMessageSendNodeReceive();

                // ChatActivity.mSocket.on("message id", onReceiveMsgID);
                App.appInstance.getSocket().on("message id", onReceiveMsgID);


                ChatMessagesSkeleton ske = new ChatMessagesSkeleton();
                ske.setChatjobid(job_chatid);
                ske.setMessage(binding.etMessage.getText().toString());
                ske.setCreated("20:20");
                ske.setSenderid(senderid);
                ske.setSendername(sendername);
                ske.setReceiverid(receiverId);
                ske.setReceivername(receiverName);
                ske.setUrgent(urgent);
                ske.setAcknowledge("0");
                ske.setDelivered("0");
                ske.setId("0");
                ske.setExist("no");
                ske.setUUID(String.valueOf(uniqueKey));
                arrayList.add(ske);
                adapter.notifyDataSetChanged();
                binding.chatRecycler.scrollToPosition(adapter.getItemCount() - 1);
                binding.etMessage.setText("");
            } catch (Exception e) {
            }
        }
    }

    private Emitter.Listener onReceiveMsgID = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        for (int i = 0; i < arrayList.size(); i++) {
                            if (arrayList.get(i).getUUID().equalsIgnoreCase(uniqueKey.toString())) {
                                arrayList.get(i).setId(data.getString("id"));
                                arrayList.get(i).setUUID(data.getString("token"));
                                arrayList.get(i).setCreated(data.getString("created_at"));
                                Log.e("massssssgg", arrayList.get(i).getMessage() + "----" + arrayList.get(i).getSendername());
                                adapter.notifyDataSetChanged();
/*
                                ChatMessagesSkeleton skeleton = new ChatMessagesSkeleton();
                                skeleton.setMessage(arrayList.get(i).getMessage());
                                skeleton.setCreated(data.getString("created_at"));
                                skeleton.setSenderid(arrayList.get(i).getSenderid());
                                skeleton.setSendername(arrayList.get(i).getSendername());
                                skeleton.setReceiverid(arrayList.get(i).getReceiverid());
                                skeleton.setReceivername(arrayList.get(i).getReceivername());
                                skeleton.setUrgent(arrayList.get(i).getUrgent());
                                skeleton.setDelivered("0");
                                skeleton.setAcknowledge("0");
                                skeleton.setUUID(data.getString("token"));
                                skeleton.setId(data.getString("id"));

                                String restdata = gson.toJson(skeleton);
                                ContentValues cv = new ContentValues();
                                cv.put(ParseOpenHelper.CHAT_TEQID, HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID));
                                cv.put(ParseOpenHelper.CHAT_JOBID, job_chatid);
                                cv.put(ParseOpenHelper.CHAT_REST, restdata);
                                long id = sqLiteDatabase.insert(ParseOpenHelper.TABLE_CHATMSGS, null, cv);*/

                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }, 10);
        }
    };

    private void sendDeliverStatus(String msgid, String senderid) {
        try {
            Log.e("DELEIVER FROM CHAT", "dsdfsdf");
            JSONObject jobj_delv = new JSONObject();
            jobj_delv.put("id", Integer.parseInt(msgid));
            jobj_delv.put("sender_id", senderid);
            jobj_delv.put("amit", "SINGHGHHHHH");
            JSONArray jarry_delv = new JSONArray();
            jarry_delv.put(jobj_delv);
            // ChatActivity.mSocket.emit("delivered", jarry_delv);
            App.appInstance.getSocket().emit("delivered1", jarry_delv);
        } catch (Exception e) {
        }
    }

    private void OpponentEmployeeList() {
        HandyObject.getApiManagerMain().AllEmployeeList(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQPARENT_ID), HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String jsonResponse = response.body().string();
                            Log.e("responseEmpList", jsonResponse);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            arrayListEmp = new ArrayList<>();
                            AllEmployeeSkeleton skee = new AllEmployeeSkeleton();
                            skee.setEmployeeId("111111");
                            skee.setEmployeename("Select Receiver");
                            arrayListEmp.add(skee);
                            if (jsonObject.getString("status").toLowerCase().equals("success")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jobj = jsonArray.getJSONObject(i);
                                    AllEmployeeSkeleton ske = new AllEmployeeSkeleton();
                                    ske.setEmployeeId(jobj.getString("id"));
                                    ske.setEmployeename(jobj.getString("name"));
                                    arrayListEmp.add(ske);
                                }
                                if (((Activity) getActivity()) != null) {
                                    adapterOppEmpList = new AdapterOppEmpList(getActivity(), arrayListEmp);
                                    binding.empspinner.setAdapter(adapterOppEmpList);
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
    }

    private void LoadChatMessage(final String jobid, final String count) {
        Log.e("COUNTTT", count);
        isloading = true;
        HandyObject.showProgressDialog(getActivity());
        HandyObject.getApiManagerMain().LoadChatMessages(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQPARENT_ID), jobid, count, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String jsonResponse = response.body().string();
                            Log.e("RespLoadAllmsg", jsonResponse);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            if (pageCount == 0) {
                                arrayList.clear();
                                sqLiteDatabase.delete(ParseOpenHelper.TABLE_CHATMSGS, ParseOpenHelper.CHAT_JOBID + "=?", new String[]{jobid});
                            }

                            JSONArray jarry_delv = new JSONArray();
                            if (jsonObject.getString("status").toLowerCase().equals("success")) {
                                pageCountTotal = Integer.parseInt(jsonObject.getString("pagecount"));
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jobj = jsonArray.getJSONObject(i);
                                    ChatMessagesSkeleton ske = new ChatMessagesSkeleton();
                                    ske.setChatjobid(jobj.getString("job_id"));
                                    ske.setMessage(jobj.getString("message"));
                                    ske.setCreated(jobj.getString("created"));
                                    ske.setSenderid(jobj.getString("sender_id"));
                                    ske.setSendername(jobj.getString("sender_name"));
                                    ske.setReceiverid(jobj.getString("receiver_id"));
                                    ske.setReceivername(jobj.getString("receiver_name"));
                                    ske.setUrgent(jobj.getString("urgent"));
                                    ske.setDelivered(jobj.getString("delivered"));
                                    ske.setAcknowledge(jobj.getString("acknowledge"));
                                    ske.setExist(jobj.getString("exist"));
                                    ske.setUUID("abc");
                                    ske.setId(jobj.getString("id"));
                                    arrayList.add(0, ske);
                                    //   if(jobj.getString("sender_id").equalsIgnoreCase("")) {
                                    if (jobj.getString("exist").equalsIgnoreCase("yes")) {
                                        if (jobj.getString("delivered").equalsIgnoreCase("0")) {
                                            JSONObject jobj_delv = new JSONObject();
                                            jobj_delv.put("id", jobj.getString("id"));
                                            jobj_delv.put("sender_id", HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQPARENT_ID));
                                            jarry_delv.put(jobj_delv);
                                        }
                                    }
                                    //   }

                                    String restdata = gson.toJson(ske);

                                    ContentValues cv = new ContentValues();
                                    cv.put(ParseOpenHelper.CHAT_TEQID, HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQPARENT_ID));
                                    cv.put(ParseOpenHelper.CHAT_JOBID, jobid);
                                    cv.put(ParseOpenHelper.CHAT_REST, restdata);
                                    long id = sqLiteDatabase.insert(ParseOpenHelper.TABLE_CHATMSGS, null, cv);
                                }

                                HandyObject.stopProgressDialog();
                                adapter.notifyDataSetChanged();
                                if (pageCount == 0) {
                                    binding.chatRecycler.scrollToPosition(adapter.getItemCount() - 1);
                                }
                                if (pageCount > 0) {
                                    binding.chatRecycler.scrollToPosition(20);
                                }
                                isloading = false;
                                pageCount++;
                                App.appInstance.getSocket().emit("delivered1", jarry_delv);
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        App.appInstance.getSocket().off("receive chat message", onNewMessage);
        App.appInstance.getSocket().off("last urgent unacknowledged message", onAllAcknowledge);
        App.appInstance.getSocket().off("unacknowledged message count", AfterAcknowledgeTrigger);
        App.appInstance.getSocket().off("message id", onReceiveMsgID);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(SwithChatReciever);
    }
}