package teq.development.seatech.Dashboard.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.logging.Handler;

import teq.development.seatech.App;
import teq.development.seatech.Chat.ChatActivity;
import teq.development.seatech.Dashboard.DashBoardActivity;
import teq.development.seatech.Dashboard.DashBoardFragment;
import teq.development.seatech.Dashboard.Skeleton.UrgentMsgSkeleton;
import teq.development.seatech.R;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.database.ParseOpenHelper;
import teq.development.seatech.databinding.RowDashurgentmsgBinding;

public class AdapterDashbrdUrgentMsg extends RecyclerView.Adapter<AdapterDashbrdUrgentMsg.ViewHolder> {

    Context context;
    Fragment fragment;
    ArrayList<UrgentMsgSkeleton> arrayList;
    RowDashurgentmsgBinding binding;
    SQLiteDatabase database;

    public AdapterDashbrdUrgentMsg(Context context, ArrayList<UrgentMsgSkeleton> arrayList, Fragment fragment) {
        this.context = context;
        this.fragment = fragment;
        this.arrayList = arrayList;
        database = ParseOpenHelper.getInstance(context).getWritableDatabase();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = DataBindingUtil.inflate(inflater, R.layout.row_dashurgentmsg, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.bind(arrayList.get(position));

        binding.cltop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (HandyObject.checkInternetConnection(context)) {
                    try {
                        final UrgentMsgSkeleton ske = arrayList.get(holder.getAdapterPosition());
                        if (ske.getAcknowledge().equalsIgnoreCase("1")) {
                        } else {
                            if (checkTablecount() == 1) {
                                HandyObject.stopAlarm(context);
                            }
                            JSONObject jobj = new JSONObject();
                            jobj.put("id", ske.getMessageid());
                            jobj.put("user_id", HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID));
                            JSONObject jobj_receiver = new JSONObject();
                            jobj_receiver.put("receiver_id", ske.getReceiverid());
                            jobj_receiver.put("receiver_name", ske.getReceiver());
                            JSONArray jarry_receiver = new JSONArray();
                            jarry_receiver.put(jobj_receiver);
                            jobj.put("receiver", jarry_receiver);
                            // DashBoardActivity.mSocket.emit("acknowledge", jobj);
                            App.appInstance.getSocket().emit("acknowledge", jobj);

                            arrayList.remove(ske);
                            database.delete(ParseOpenHelper.TABLE_URGENTMSG, ParseOpenHelper.URGENT_JOBID + " =? AND " + ParseOpenHelper.URGENT_MESSAGEID + " = ?",
                                    new String[]{ske.getJobticketid(), ske.getMessageid()});
                            notifyDataSetChanged();


                            new android.os.Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent resultIntent = new Intent(context, ChatActivity.class);
                                    resultIntent.putExtra("type", "chat");
                                    resultIntent.putExtra("jobid", ske.getJobticketid());
                                    ((Activity) context).startActivity(resultIntent);
                                }
                            }, 800);
                        }
                    } catch (Exception e) {
                    }

                } else {
                    HandyObject.showAlert(context, context.getString(R.string.check_internet_connection));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RowDashurgentmsgBinding mbinding;

        public ViewHolder(RowDashurgentmsgBinding binding) {
            super(binding.getRoot());
            mbinding = binding;
        }

        public void bind(UrgentMsgSkeleton urgentMsgSkeleton) {
            mbinding.setRowdashurgentmsg(urgentMsgSkeleton);
            mbinding.executePendingBindings();
        }
    }

    private int checkTablecount() {
        Cursor cursor = database.query(ParseOpenHelper.TABLE_URGENTMSG, null, ParseOpenHelper.URGENT_TECHID + "=?", new String[]{HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID)}, null, null, null);
        return cursor.getCount();
    }
}
