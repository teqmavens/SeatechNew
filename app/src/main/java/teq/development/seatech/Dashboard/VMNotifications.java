package teq.development.seatech.Dashboard;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Observable;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.App;
import teq.development.seatech.Chat.Adapter.AdapterChatMessages;
import teq.development.seatech.Chat.ChatActivity;
import teq.development.seatech.Chat.Skeleton.ChatMessagesSkeleton;
import teq.development.seatech.Dashboard.Skeleton.NotificationAdapter;
import teq.development.seatech.Dashboard.Skeleton.NotificationSkeleton;
import teq.development.seatech.Dashboard.Skeleton.PartsSkeleton;
import teq.development.seatech.Dashboard.Skeleton.ReadNotificationSkeleton;
import teq.development.seatech.LoginActivity;
import teq.development.seatech.R;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.database.ParseOpenHelper;
import teq.development.seatech.databinding.ActivityNotificationsBinding;

public class VMNotifications extends Observable {

    Context context;
    ActivityNotificationsBinding binding;
    NotificationAdapter adapter;
    SQLiteDatabase database;
    Cursor cursor;
    ArrayList<NotificationSkeleton.NotificationData> arrayList;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public VMNotifications(Context context, ActivityNotificationsBinding binding) {
        this.context = context;
        this.binding = binding;
        binding.setViewmodelnotif(this);
        initRecyclerView();
        initRxWithData();
    }

    private void initRecyclerView() {
        binding.recyclerview.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        binding.recyclerview.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();
        database = ParseOpenHelper.getInstance(context).getWritableDatabase();
        new DatabaseFetch().execute();
    }

    private void initRxWithData() {
        //   try {
            /*Disposable disposable = HandyObject.getApiManagerMainRx().NotificationsList(HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID), HandyObject.getPrams(context, AppConstants.LOGIN_SESSIONID)).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<NotificationSkeleton>() {
                        @Override
                        public void accept(NotificationSkeleton notificationSkeleton) throws Exception {
                            String status = notificationSkeleton.status;

                            if (status.equalsIgnoreCase("success")) {
                                ArrayList<NotificationSkeleton.NotificationData> arrayList = notificationSkeleton.data;
                                if (arrayList.size() == 0) {
                                    binding.recyclerview.setVisibility(View.INVISIBLE);
                                    binding.rlnodata.setVisibility(View.VISIBLE);
                                 //   HandyObject.showAlert(context, "blankdata");
                                } else {
                                    binding.recyclerview.setVisibility(View.VISIBLE);
                                    binding.rlnodata.setVisibility(View.INVISIBLE);
                                    adapter = new NotificationAdapter(context, arrayList, VMNotifications.this);
                                    binding.recyclerview.setAdapter(adapter);
                                }

                            } else {
                                HandyObject.showAlert(context, status);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    });
            compositeDisposable.add(disposable);*/
       /* } catch (Exception e) {
        }*/


        // HandyObject.showProgressDialog(context);
        HandyObject.getApiManagerMain().NotificationsList(HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID), HandyObject.getPrams(context, AppConstants.LOGIN_SESSIONID))
                .enqueue(new Callback<NotificationSkeleton>() {
                    @Override
                    public void onResponse(Call<NotificationSkeleton> call, Response<NotificationSkeleton> response) {
                        NotificationSkeleton notificationSkeleton = response.body();
                        String status = notificationSkeleton.status;

                        if (status.equalsIgnoreCase("success")) {
                            arrayList.clear();
                            arrayList = notificationSkeleton.data;
                            database.delete(ParseOpenHelper.TABLE_NOTIFICATIONS, ParseOpenHelper.NOTIFICATION_TECHID + "=?", new String[]{HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID)});
                            if (arrayList.size() == 0) {
                                binding.recyclerview.setVisibility(View.INVISIBLE);
                                binding.rlnodata.setVisibility(View.VISIBLE);
                            } else {
                                binding.recyclerview.setVisibility(View.VISIBLE);
                                binding.rlnodata.setVisibility(View.INVISIBLE);
                                adapter = new NotificationAdapter(context, arrayList, VMNotifications.this);
                                binding.recyclerview.setAdapter(adapter);
                                insertIntoDB(arrayList);
                            }
                        } else {
                            HandyObject.showAlert(context, status);
                            if(status.equalsIgnoreCase("logout")){
                                HandyObject.clearpref(context);
                                HandyObject.deleteAllDatabase(context);
                                App.appInstance.stopTimer();
                                Intent intent_reg = new Intent(context, LoginActivity.class);
                                ((Activity) context).startActivity(intent_reg);
                                ((Activity) context).finish();
                                ((Activity) context).overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<NotificationSkeleton> call, Throwable t) {
                        Log.e("responseError", t.getMessage());
                        //HandyObject.stopProgressDialog();
                    }
                });
    }

    private void insertIntoDB(ArrayList<NotificationSkeleton.NotificationData> arrayList) {
        Gson gson = new Gson();
        String notiarray = gson.toJson(arrayList);
        ContentValues cv = new ContentValues();
        cv.put(ParseOpenHelper.NOTIFICATION_TECHID, HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID));
        cv.put(ParseOpenHelper.NOTIFICATION_REST, notiarray);
        long idd = database.insert(ParseOpenHelper.TABLE_NOTIFICATIONS, null, cv);
    }

    public void reset() {
        compositeDisposable.clear();
        compositeDisposable = null;
        context = null;
    }

    public void RowClick(NotificationSkeleton.NotificationData ske) {
        if (HandyObject.checkInternetConnection(context)) {
            changeReadStatus(ske.notification_id);
        } else {
            Toast.makeText(context, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
        }
    }

    private void changeReadStatus(String notiid) {
        HandyObject.getApiManagerMain().NotificationsRead(notiid, HandyObject.getPrams(context, AppConstants.LOGIN_SESSIONID))
                .enqueue(new Callback<ReadNotificationSkeleton>() {
                    @Override
                    public void onResponse(Call<ReadNotificationSkeleton> call, Response<ReadNotificationSkeleton> response) {
                        ReadNotificationSkeleton notificationSkeleton = response.body();
                        String status = notificationSkeleton.status;
                        if (status.equalsIgnoreCase("success")) {
                            HandyObject.showAlert(context, "Notification read successfully");
                            initRxWithData();
                        } else {
                            HandyObject.showAlert(context, status);
                        }
                    }

                    @Override
                    public void onFailure(Call<ReadNotificationSkeleton> call, Throwable t) {
                        Log.e("responseError", t.getMessage());
                        //HandyObject.stopProgressDialog();
                    }
                });
        /*Disposable disposable = HandyObject.getApiManagerMainRx().NotificationsRead(notiid, HandyObject.getPrams(context, AppConstants.LOGIN_SESSIONID)).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<ReadNotificationSkeleton>() {
                    @Override
                    public void accept(ReadNotificationSkeleton notificationSkeleton) throws Exception {
                        String status = notificationSkeleton.status;
                        if (status.equalsIgnoreCase("success")) {
                            HandyObject.showAlert(context, "Notification read successfully");
                            initRxWithData();
                        } else {
                            HandyObject.showAlert(context, status);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(disposable);*/
    }

    public void OnClickBack() {
        ((Activity) context).onBackPressed();
        //  ((Activity) context).finish();
    }

    private class DatabaseFetch extends AsyncTask<ArrayList<NotificationSkeleton.NotificationData>, Void, ArrayList<NotificationSkeleton.NotificationData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            arrayList.clear();
        }

        @Override
        protected ArrayList<NotificationSkeleton.NotificationData> doInBackground(ArrayList<NotificationSkeleton.NotificationData>... arrayLists) {
            Gson gson = new Gson();
            cursor = database.query(ParseOpenHelper.TABLE_NOTIFICATIONS, null, ParseOpenHelper.NOTIFICATION_TECHID + "=?", new String[]{HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID)}, null, null, null);
            cursor.moveToFirst();
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    while (!cursor.isAfterLast()) {
                        Type typenoti = new TypeToken<ArrayList<NotificationSkeleton.NotificationData>>() {
                        }.getType();
                        String notirest = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.NOTIFICATION_REST));
                        ArrayList<NotificationSkeleton.NotificationData> arrayListNOTI = gson.fromJson(notirest, typenoti);
                        arrayList.addAll(arrayListNOTI);
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
        protected void onPostExecute(ArrayList<NotificationSkeleton.NotificationData> arrayList) {
            super.onPostExecute(arrayList);
         //   HandyObject.showAlert(context, String.valueOf(cursor.getCount()));
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    binding.recyclerview.setVisibility(View.VISIBLE);
                    binding.rlnodata.setVisibility(View.INVISIBLE);
                    adapter = new NotificationAdapter(context, arrayList, VMNotifications.this);
                    binding.recyclerview.setAdapter(adapter);
                } else {
                    binding.recyclerview.setVisibility(View.INVISIBLE);
                    binding.rlnodata.setVisibility(View.VISIBLE);
                }
            }
        }
    }


}
