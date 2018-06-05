package com.vadevelopment.RedAppetite.login;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vadevelopment.RedAppetite.R;
import com.vadevelopment.RedAppetite.dashboard.DashboardActivity;

/**
 * Created by vibrantappz on 5/10/2017.
 */

public class InsideLoginFragment extends Fragment implements View.OnClickListener {
    private TextView bottomtext, forget, register;
    private Button btn_login, btn_register, btn_frgtpwd;
    private Dialog openDialog;
    private DashboardActivity mdashboard;
    private LinearLayout ll_main;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mdashboard = (DashboardActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frgm_login, container, false);
        getActivity().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        bottomtext = (TextView) view.findViewById(R.id.textBottom);
        btn_frgtpwd = (Button) view.findViewById(R.id.btn_frgtpwd);
        btn_login = (Button) view.findViewById(R.id.btn_login);
        btn_register = (Button) view.findViewById(R.id.btn_register);
        ll_main = (LinearLayout) view.findViewById(R.id.ll_main);
        com.vadevelopment.RedAppetite.HandyObjects.SetButton_TEXGYREHEROES_BOLD(getActivity(),btn_frgtpwd);
        com.vadevelopment.RedAppetite.HandyObjects.SetButton_TEXGYREHEROES_BOLD(getActivity(),btn_login);
        com.vadevelopment.RedAppetite.HandyObjects.SetButton_TEXGYREHEROES_BOLD(getActivity(),btn_register);
        slideToAbove();

        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        btn_frgtpwd.setOnClickListener(this);

        bottomtext.setText("After registration you can\n*add review\n*get the newsletter\n (events,news and updates about the redlight and the app),\n*save your favorites.");
        mdashboard.footer.setVisibility(View.GONE);
        mdashboard.centertextsearch.setVisibility(View.VISIBLE);
        mdashboard.centertextsearch.setText("Login");
        mdashboard.textsearchimage.setVisibility(View.GONE);
        mdashboard.mapButton.setVisibility(View.GONE);
        mdashboard.setting.setVisibility(View.VISIBLE);
        mdashboard.setting.setText("Cancel");
        mdashboard.mapicon.setVisibility(View.GONE);
        mdashboard.settingicon.setVisibility(View.GONE);
        mdashboard.setting.setBackgroundResource(R.drawable.backbtn);
        mdashboard.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                showDialog();
                break;
            case R.id.btn_frgtpwd:
                frgtpwdDialog();
                break;
            case R.id.btn_register:
               /* RegisterFragment af = new RegisterFragment();
                Bundle bundle = new Bundle();
                bundle.putString("from", "login");
                af.setArguments(bundle);
                mdashboard.displayWithoutViewFragment(af);*/
                break;
        }
    }

    private void showDialog() {
        openDialog = new Dialog(getActivity());
        openDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        openDialog.setCanceledOnTouchOutside(false);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int width = dm.widthPixels;
        openDialog.show();
        Window window = openDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        window.setAttributes(wlp);

        openDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        openDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        View vview = inflater.inflate(R.layout.verifyemail_dialog, null, false);
        openDialog.setContentView(vview);
        LinearLayout approx_lay = (LinearLayout) openDialog.findViewById(R.id.approx_lay);
        /*Double d = new Double(2.99);
        int hei = d.intValue();*/
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width - 60, (height / 3) + 60);
        approx_lay.setLayoutParams(params);
        TextView text_ok = (TextView) vview.findViewById(R.id.text_ok);
        TextView text_contact = (TextView) vview.findViewById(R.id.text_contact);

        text_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog.hide();
            }
        });

        text_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog.hide();
                mdashboard.displayWithoutViewFragment(new com.vadevelopment.RedAppetite.contact.ContactFragment());
            }
        });
        openDialog.show();
    }

    private void frgtpwdDialog() {
        openDialog = new Dialog(getActivity());
        openDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        openDialog.setCanceledOnTouchOutside(false);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int width = dm.widthPixels;
        openDialog.show();
        Window window = openDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        window.setAttributes(wlp);


        openDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        openDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        View vview = inflater.inflate(R.layout.forgotpasswordalert, null, false);
        openDialog.setContentView(vview);
        LinearLayout approx_lay = (LinearLayout) openDialog.findViewById(R.id.approx_lay);
      /*  Double d = new Double(2.99);
        int hei = d.intValue();*/
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width - 60, (height / 3) + 20);
        approx_lay.setLayoutParams(params);
        TextView text_ok = (TextView) vview.findViewById(R.id.text_ok);
        TextView text_cancel = (TextView) vview.findViewById(R.id.text_cancel);

        text_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog.hide();
                Intent i = new Intent(getActivity(), DashboardActivity.class);
                i.putExtra("fromskip","nop");
                startActivity(i);
                getActivity().finish();
            }
        });

        text_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog.dismiss();
            }
        });
        openDialog.show();
    }

    private void slideToAbove() {
        Animation slide = null;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        slide.setDuration(1600);
        slide.setFillAfter(true);
        slide.setFillBefore(true);
        ll_main.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ll_main.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}

