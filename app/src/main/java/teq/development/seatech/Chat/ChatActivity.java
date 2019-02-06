package teq.development.seatech.Chat;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.net.URISyntaxException;

import teq.development.seatech.App;
import teq.development.seatech.R;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.databinding.ChatactivityBinding;

public class ChatActivity extends AppCompatActivity {
   // public static Socket mSocket;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChatactivityBinding binding = DataBindingUtil.setContentView(this, R.layout.chatactivity);
        binding.setChatactivity(this);
        initViews(binding);
    }

    private void initViews(ChatactivityBinding binding) {

        if (getIntent().getStringExtra("type") == null) {
            //HandyObject.showAlert(ChatActivity.this, "null");
            setLeftView(new ChatLeftFragment());
            setRightView(new ChatRightFragment());
        } else if (getIntent().getExtras().getString("type").equalsIgnoreCase("chat")) {
           /* Intent intent = new Intent("ToChatLeftView");
            intent.putExtra("jobidtoleft", getIntent().getExtras().getString("jobid"));
            LocalBroadcastManager.getInstance(ChatActivity.this).sendBroadcast(intent);*/
            ChatLeftFragment ch =  new ChatLeftFragment();
            Bundle bundle = new Bundle();
            bundle.putString("jobidd",getIntent().getExtras().getString("jobid"));
            ch.setArguments(bundle);
            setLeftView(ch);
            setRightView(new ChatRightFragment());
        }
    }


    private void setLeftView(Fragment fragment) {
        FragmentManager fmleft = getSupportFragmentManager();
        fmleft.beginTransaction().replace(R.id.containerLeft, fragment).commit();
    }

    private void setRightView(Fragment fragment) {
        FragmentManager fmright = getSupportFragmentManager();
        fmright.beginTransaction().replace(R.id.containerRight, fragment).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mSocket.disconnect();
        // mSocket.off("receive chat message'", onNewMessage);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_lefttoright, R.anim.activity_righttoleft);
    }

    public void OnClickBack() {
        onBackPressed();
    }

    public void OnClickAddchat() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("composemsg");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        DialogFragment newFragment = ComposeDialog.newInstance(4);
        newFragment.show(ft, "composemsg");
    }
}
