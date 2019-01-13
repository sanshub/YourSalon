package cubex.mahesh.yoursaloon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class LoginOptionsActivity extends AppCompatActivity {
    Button salon, cust, bwoman, bguest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login_options);

        salon = findViewById(R.id.sln_btn);
        cust = findViewById(R.id.cust_btn);
        bwoman = findViewById(R.id.bwmn_btn);
        bguest = findViewById(R.id.bns_bgst);

        Typeface tf = Typeface.createFromAsset
                (getAssets(), "calibri.ttf");
        salon.setTypeface(tf);
        cust.setTypeface(tf);
        bwoman.setTypeface(tf);
        bguest.setTypeface(tf);
    }

    public void salon(View v) {
        SharedPreferences spf =
                getSharedPreferences("saloon_prfs", Context.MODE_PRIVATE);
        SharedPreferences.Editor spe = spf.edit();
        switch (v.getId()) {
            case R.id.sln_btn:

                spe.putString("user_type", "saloon");

                startActivity(new Intent(this,
                        LoginActivity.class));

                break;

            case R.id.cust_btn:
                spe.putString("user_type", "customer");

                startActivity(new Intent(this,
                        LoginActivity.class));

                break;
            case R.id.bwmn_btn:
                spe.putString("user_type", "business_women");

                startActivity(new Intent(this,
                        LoginActivity.class));

                break;
            case R.id.bns_bgst:
                spe.putString("user_type", "business_guest");

                startActivity(new Intent(this,
                        LoginActivity.class));

                break;

        }
        spe.commit();
    }
}
