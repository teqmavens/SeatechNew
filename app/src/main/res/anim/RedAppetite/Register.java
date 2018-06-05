package com.vadevelopment.RedAppetite;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class Register extends AppCompatActivity {
TextView textnewline;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        textnewline=(TextView)findViewById(R.id.textnewline);
       // textnewline.setText("By creating an account,you agree to\n  <u> the Terms of Use</u> \n <u> and Privacy Policy </u>\n from RedAppetite");

    }
}
