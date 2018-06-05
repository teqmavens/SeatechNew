package teq.development.seatech;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import teq.development.seatech.Dashboard.DashBoardActivity;
import teq.development.seatech.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_login);
        binding.setLoginactivity(this);
    }

    public void onClickLogin(){
        Intent intent_reg = new Intent(LoginActivity.this, DashBoardActivity.class);
        startActivity(intent_reg);
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
    }
}
