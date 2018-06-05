package com.vadevelopment.RedAppetite.login;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vadevelopment.RedAppetite.R;

/**
 * Created by Vibrant Android on 08-03-2017.
 */

public class ContainerActivity extends AppCompatActivity {
    Bundle extras;
    public TextView cancelText;
    public TextView centertext;
    public TextView next;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.containerloginregister);
        relativeLayout = (RelativeLayout) findViewById(R.id.cancelSkipLayout);
        cancelText = (TextView) findViewById(R.id.cancelText);
        centertext = (TextView) findViewById(R.id.centertext);
        next = (TextView) findViewById(R.id.skipText);

        relativeLayout.setVisibility(View.VISIBLE);
        try {
            extras = getIntent().getExtras();
            if (extras.getBoolean("Login")) {
                if (extras.getString("fromdashboard").equalsIgnoreCase("yup")) {
                    LoginFragment lf = new LoginFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("fromdashboard", "yup");
                    if (extras.getString("forwhich").equalsIgnoreCase("review")) {
                        bundle.putString("forwhich", "review");
                    } else if (extras.getString("forwhich").equalsIgnoreCase("fvrt")) {
                        bundle.putString("forwhich", "fvrt");
                    } else if (extras.getString("forwhich").equalsIgnoreCase("normal")) {
                        bundle.putString("forwhich", "normal");
                    }
                    lf.setArguments(bundle);
                    displayWithoutViewFragment(lf);
                } else {
                    LoginFragment lf = new LoginFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("fromdashboard", "nop");
                    bundle.putString("forwhich", "normal");
                    lf.setArguments(bundle);
                    displayWithoutViewFragment(lf);
                }
            } else if (!extras.getBoolean("Login")) {
                if (extras.getString("from").equalsIgnoreCase("settings")) {
                    com.vadevelopment.RedAppetite.login.RegisterFragment af = new com.vadevelopment.RedAppetite.login.RegisterFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("from", "settings");
                    af.setArguments(bundle);
                    displayWithoutViewFragment(af);
                } else if (extras.getString("from").equalsIgnoreCase("main")) {
                    com.vadevelopment.RedAppetite.login.RegisterFragment af = new com.vadevelopment.RedAppetite.login.RegisterFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("from", "main");
                    af.setArguments(bundle);
                    displayWithoutViewFragment(af);
                }
            }
        } catch (NullPointerException e) {
            System.out.println("Exception is");
        }
    }

    public void displayWithoutViewFragment(Fragment mFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame, mFragment)
                .commit();
    }

    public void displayWithViewFragment(Fragment mFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame, mFragment).addToBackStack(null)
                .commit();
    }
}