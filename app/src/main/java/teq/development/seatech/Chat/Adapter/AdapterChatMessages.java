package teq.development.seatech.Chat.Adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import teq.development.seatech.App;
import teq.development.seatech.Chat.ChatActivity;
import teq.development.seatech.Chat.Skeleton.ChatMessagesSkeleton;
import teq.development.seatech.Dashboard.DashBoardActivity;
import teq.development.seatech.R;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;

public class AdapterChatMessages extends RecyclerView.Adapter<AdapterChatMessages.ViewHolder> {

    ArrayList<ChatMessagesSkeleton> arrayList;
    ArrayList<String> msgids;
    Context context;
    private static final int CHAT_RIGHT = 1;
    private static final int CHAT_LEFT = 2;

    public AdapterChatMessages(Context context, ArrayList<ChatMessagesSkeleton> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
       /* ChatActivity.mSocket.off("delivered ids", deliverdstatuschange);
        ChatActivity.mSocket.off("acknowledge ids", acknowledgestatuschange);
        ChatActivity.mSocket.on("delivered ids", deliverdstatuschange);
        ChatActivity.mSocket.on("acknowledge ids", acknowledgestatuschange);*/
        App.appInstance.getSocket().off("delivered ids", deliverdstatuschange);
        App.appInstance.getSocket().off("acknowledge ids", acknowledgestatuschange);
        App.appInstance.getSocket().on("delivered ids", deliverdstatuschange);
        App.appInstance.getSocket().on("acknowledge ids", acknowledgestatuschange);
    }

    @NonNull
    @Override
    public AdapterChatMessages.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (viewType == CHAT_RIGHT) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chatright, parent, false);
            return new ViewHolder(v);
        } else if (viewType == CHAT_LEFT) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_left, parent, false);
            return new ViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterChatMessages.ViewHolder holder, int position) {
        Log.e("sizeeeeeeeeeeeeeee", String.valueOf(arrayList.size()));
        holder.time.setText(arrayList.get(position).getCreated());
        holder.mTextView.setText(arrayList.get(position).getMessage());
        // holder.to.setText(arrayList.get(position).getReceivername());
        holder.to.setText("@ " + arrayList.get(position).getReceivername());

        if (arrayList.get(position).getSenderid().equalsIgnoreCase(HandyObject.getPrams(context, AppConstants.LOGINTEQPARENT_ID))) {
            //  holder.tick.setVisibility(View.VISIBLE);
            holder.tick.setVisibility(View.GONE);
            if (arrayList.get(position).getDelivered().equalsIgnoreCase("1")) {
                //  holder.tick.setImageResource(R.drawable.doubletick);
                holder.mTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(context, R.drawable.doubletick), null);
            } else {
                holder.mTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(context, R.drawable.singletick), null);
                ///  holder.tick.setImageResource(R.drawable.singletick);
            }
            holder.from.setText("you");
            if (arrayList.get(position).getUrgent().equalsIgnoreCase("1")) {
                holder.llmessage.setBackground(ContextCompat.getDrawable(context, R.drawable.blue));
            } else {
                holder.llmessage.setBackground(ContextCompat.getDrawable(context, R.drawable.blue));
            }
        } else {
            holder.mTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            //  holder.tick.setVisibility(View.INVISIBLE);
            holder.tick.setVisibility(View.GONE);
            holder.from.setText(arrayList.get(position).getSendername());


          /*  if (arrayList.get(position).getUrgent().equalsIgnoreCase("1")) {
                if (Integer.parseInt(arrayList.get(position).getAcknowledge()) == 0) {
                    holder.llmessage.setBackground(ContextCompat.getDrawable(context, R.drawable.chatacknowle_withugent));
                } else {
                    holder.llmessage.setBackground(ContextCompat.getDrawable(context, R.drawable.chatnormal_bg));
                }
            } else if (Integer.parseInt(arrayList.get(position).getAcknowledge()) > 0) {
                if (arrayList.get(position).getUrgent().equalsIgnoreCase("1")) {
                    holder.llmessage.setBackground(ContextCompat.getDrawable(context, R.drawable.chatnormal_bg));
                } else {
                    holder.llmessage.setBackground(ContextCompat.getDrawable(context, R.drawable.chatnormal_bg));
                }
            } else {
                holder.llmessage.setBackground(ContextCompat.getDrawable(context, R.drawable.chatacknowledge_only));
            }*/
            if (arrayList.get(position).getExist().equalsIgnoreCase("yes")) {
                if (arrayList.get(position).getUrgent().equalsIgnoreCase("1")) {
                    if (Integer.parseInt(arrayList.get(position).getAcknowledge()) == 0) {
                        holder.llmessage.setBackground(ContextCompat.getDrawable(context, R.drawable.dark_red));
                    } else {
                        holder.llmessage.setBackground(ContextCompat.getDrawable(context, R.drawable.light));
                    }
                } else if (Integer.parseInt(arrayList.get(position).getAcknowledge()) > 0) {
                    if (arrayList.get(position).getUrgent().equalsIgnoreCase("1")) {
                        holder.llmessage.setBackground(ContextCompat.getDrawable(context, R.drawable.light));
                    } else {
                        holder.llmessage.setBackground(ContextCompat.getDrawable(context, R.drawable.light));
                    }
                } else {
                    holder.llmessage.setBackground(ContextCompat.getDrawable(context, R.drawable.dark));
                }
            } else {
                holder.llmessage.setBackground(ContextCompat.getDrawable(context, R.drawable.light));
            }

        }


        holder.llmessage.setTag(arrayList.get(position));
        holder.llmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HandyObject.checkInternetConnection(context)) {
                    ChatMessagesSkeleton ske = (ChatMessagesSkeleton) v.getTag();
                    if (ske.getSenderid().equalsIgnoreCase(HandyObject.getPrams(context, AppConstants.LOGINTEQPARENT_ID))) {
                    } else {
                        if (ske.getReceiverid().contains(",")) {
                            String[] arry = ske.getReceiverid().split(",");
                            // JSONArray jarry_receiver;
                            JSONArray jarry_receiver = new JSONArray();
                            String checkid = "";
                            for (int i = 0; i < arry.length; i++) {
                                try {
                                    JSONObject jobj_receiver = new JSONObject();
                                    jobj_receiver.put("receiver_id", arry[i]);
                                    jarry_receiver.put(jobj_receiver);
                                    if (arry[i].equalsIgnoreCase(HandyObject.getPrams(context, AppConstants.LOGINTEQPARENT_ID))) {
                                        checkid = arry[i];
                                    }
                                } catch (Exception e) {
                                }

                            }
                            if (checkid.equalsIgnoreCase(HandyObject.getPrams(context, AppConstants.LOGINTEQPARENT_ID))) {
                                if (Integer.parseInt(ske.getAcknowledge()) == 0) {
                                    try {
                                        JSONObject jobj = new JSONObject();
                                        jobj.put("id", ske.getId());
                                        jobj.put("job_id", ske.getChatjobid());
                                        jobj.put("urgent", ske.getUrgent());
                                        jobj.put("user_id", HandyObject.getPrams(context, AppConstants.LOGINTEQPARENT_ID));
                                        jobj.put("receiver", jarry_receiver);
                                        //ChatActivity.mSocket.emit("acknowledge", jobj);
                                        App.appInstance.getSocket().emit("acknowledge", jobj);
                                        ske.setAcknowledge("1");
                                        notifyDataSetChanged();
                                    } catch (Exception e) {
                                    }
                                }
                            }

                        }
                        if (HandyObject.getPrams(context, AppConstants.LOGINTEQPARENT_ID).equalsIgnoreCase(ske.getReceiverid())) {
                            if (Integer.parseInt(ske.getAcknowledge()) > 0) {
                            } else if (Integer.parseInt(ske.getAcknowledge()) == 0) {
                                try {
                                    JSONObject jobj = new JSONObject();
                                    jobj.put("id", ske.getId());
                                    jobj.put("job_id", ske.getChatjobid());
                                    jobj.put("urgent", ske.getUrgent());
                                    jobj.put("user_id", HandyObject.getPrams(context, AppConstants.LOGINTEQPARENT_ID));
                                    JSONObject jobj_receiver = new JSONObject();
                                    jobj_receiver.put("receiver_id", ske.getReceiverid());
                                    jobj_receiver.put("receiver_name", ske.getReceivername());
                                    JSONArray jarry_receiver = new JSONArray();
                                    jarry_receiver.put(jobj_receiver);
                                    jobj.put("receiver", jarry_receiver);
                                    //ChatActivity.mSocket.emit("acknowledge", jobj);
                                    App.appInstance.getSocket().emit("acknowledge", jobj);
                                    ske.setAcknowledge("1");
                                    notifyDataSetChanged();
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                } else {
                    HandyObject.showAlert(context, context.getString(R.string.check_internet_connection));
                }

            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (arrayList.get(position).getSenderid().equalsIgnoreCase(HandyObject.getPrams(context, AppConstants.LOGINTEQPARENT_ID)))
            return CHAT_RIGHT;
        else {
            return CHAT_LEFT;
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView, time, to, from;
        public RelativeLayout llmessage;
        private ImageView tick;

        public ViewHolder(View itemView) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.time);
            mTextView = (TextView) itemView.findViewById(R.id.tvMessage);
            to = (TextView) itemView.findViewById(R.id.to);
            from = (TextView) itemView.findViewById(R.id.from);
            llmessage = (RelativeLayout) itemView.findViewById(R.id.llmessage);
            tick = (ImageView) itemView.findViewById(R.id.tick);
        }
    }

    private Emitter.Listener deliverdstatuschange = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        //    {"message_ids":"636,12,34"}
                        String msgid = data.getString("message_ids");
                        Log.e("MesgIDssssss", msgid);
                        if (msgid.contains(",")) {
                            String[] abc = msgid.split(",");
                            for (int k = 0; k < abc.length; k++) {
                                for (int i = 0; i < arrayList.size(); i++) {
                                    if (arrayList.get(i).getId().equalsIgnoreCase(abc[k])) {
                                        arrayList.get(i).setDelivered("1");
                                    }
                                }
                                notifyDataSetChanged();
                            }
                        } else {
                            for (int i = 0; i < arrayList.size(); i++) {
                                if (arrayList.get(i).getId().equalsIgnoreCase(msgid)) {
                                    arrayList.get(i).setDelivered("1");
                                    notifyDataSetChanged();
                                }
                            }
                        }

                    } catch (Exception e) {
                    }
                }
            }, 10);

        }
    };

    private Emitter.Listener acknowledgestatuschange = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        String msgid = data.getString("id");
                        for (int i = 0; i < arrayList.size(); i++) {
                            if (arrayList.get(i).getAcknowledge().equalsIgnoreCase(msgid)) {
                                arrayList.get(i).setAcknowledge("1");
                                notifyDataSetChanged();
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }, 10);
        }
    };

}
