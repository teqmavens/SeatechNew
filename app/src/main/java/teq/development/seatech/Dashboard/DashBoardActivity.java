package teq.development.seatech.Dashboard;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.LoginActivity;
import teq.development.seatech.Profile.ChangePwdFragment;
import teq.development.seatech.Profile.MyProfileFragment;
import teq.development.seatech.R;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;

public class DashBoardActivity extends AppCompatActivity implements View.OnClickListener{

    Toolbar toolbar;
    DrawerLayout mDrawerLayout;
    public ActionBarDrawerToggle mDrawerToggle;
    public Boolean mABoolean = false;
    public ImageView menu_icon,sick_icon;
    SimpleDraweeView userimage;
    TextView username;
   // ImageView navigation_icon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this,R.layout.activity_dashboard);
        replaceFragmentWithoutBack(new DashBoardFragment());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        menu_icon = (ImageView) findViewById(R.id.menu_icon);
        sick_icon = (ImageView) findViewById(R.id.sick_icon);
        userimage = (SimpleDraweeView) findViewById(R.id.userimage);
        username = (TextView) findViewById(R.id.username);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.navigation_icon);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        userimage.setImageURI(HandyObject.getPrams(this,AppConstants.LOGINTEQ_IMAGE));
        username.setText(HandyObject.getPrams(this,AppConstants.LOGINTEQ_USERNAME));
        displayLeft(new LeftDrawer());
        menu_icon.setOnClickListener(this);
        sick_icon.setOnClickListener(this);

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.navigation_icon,
                R.string.app_name,
                R.string.detailpage
        ) {
            public void onDrawerClosed(View view) {
                if (mABoolean == false) {
                    toolbar.setNavigationIcon(R.drawable.navigation_icon);
                    invalidateOptionsMenu();
                } else {
                    toolbar.setNavigationIcon(R.drawable.navigation_icon);
                    invalidateOptionsMenu();
                }
            }

            public void onDrawerOpened(View drawerView) {
                if (mABoolean == false) {
                    toolbar.setNavigationIcon(R.drawable.cross);
                    invalidateOptionsMenu();
                } else {
                    toolbar.setNavigationIcon(R.drawable.cross);
                    invalidateOptionsMenu();
                }

            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public void displayLeft(Fragment mFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.leftDrawer, mFragment)
                .commit();
    }

    public void replaceFragmentWithoutBack(Fragment mFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, mFragment)
                .commit();
    }

    public void replaceFragment(Fragment mFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, mFragment).addToBackStack(null)
                .commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
       // inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
//         boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//         menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.menu_icon:
               // Toast.makeText(this, "click", Toast.LENGTH_SHORT).show();
                displyPopup(menu_icon);
                break;
            case R.id.sick_icon:
                displaypopupLeave(sick_icon);
                break;
        }
    }

    private void displaypopupLeave(View anchorview) {
        final PopupWindow popup = new PopupWindow(this);
        View layout = getLayoutInflater().inflate(R.layout.popup_leave, null);
        popup.setContentView(layout);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageView cross = (ImageView) layout.findViewById(R.id.cross);
        Button submit = (Button) layout.findViewById(R.id.submit);
        final CheckBox cbx_vacation = (CheckBox) layout.findViewById(R.id.cbx_vacation);
        final CheckBox cbx_sick = (CheckBox) layout.findViewById(R.id.cbx_sick);
        cbx_vacation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    cbx_sick.setChecked(false);
                }

            }
        });

        cbx_sick.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    cbx_vacation.setChecked(false);
                }
            }
        });

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        popup.showAsDropDown(anchorview);
    }

    private void displyPopup(View view){
        PopupMenu popup = new PopupMenu(this,view);
        popup.getMenuInflater().inflate(R.menu.popupmenu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getTitle().toString().equalsIgnoreCase("My Profile")){
                    replaceFragment(new MyProfileFragment());
                } else if(item.getTitle().toString().equalsIgnoreCase("Change Password")) {
                    replaceFragment(new ChangePwdFragment());
                } else {
                    if (HandyObject.checkInternetConnection(DashBoardActivity.this)) {
                        LogoutTask();
                    } else {
                        HandyObject.showAlert(DashBoardActivity.this, getString(R.string.check_internet_connection));
                    }
                }
                return true;
            }
        });
        popup.show();
    }

    private void LogoutTask(){
        HandyObject.showProgressDialog(this);
        HandyObject.getApiManagerType().logout(HandyObject.getPrams(this, AppConstants.LOGINTEQ_ID),HandyObject.getPrams(this,AppConstants.LOGIN_SESSIONID))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String jsonResponse = response.body().string();
                            Log.e("response",jsonResponse);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            if (jsonObject.getString("status").toLowerCase().equals("success")){
                                HandyObject.showAlert(DashBoardActivity.this, jsonObject.getString("message"));
                                Intent intent_reg = new Intent(DashBoardActivity.this, LoginActivity.class);
                                startActivity(intent_reg);
                                finish();
                                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                                HandyObject.clearpref(DashBoardActivity.this);
                            } else {
                                HandyObject.showAlert(DashBoardActivity.this, jsonObject.getString("message"));
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
