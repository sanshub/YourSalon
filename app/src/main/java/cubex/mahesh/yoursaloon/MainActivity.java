package cubex.mahesh.yoursaloon;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);


        int cam_per_status = ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA);

        int storg_per_status = ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int coarse_loc_status = ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int fine_loc_status = ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE);


        if (cam_per_status == PackageManager.PERMISSION_GRANTED
                && storg_per_status == PackageManager.PERMISSION_GRANTED
                && coarse_loc_status == PackageManager.PERMISSION_GRANTED
                && fine_loc_status == PackageManager.PERMISSION_GRANTED) {
            startHandler();
        } else {
            requestPermissions();
        }
    }


    void startHandler() {
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(MainActivity.this,
                        LoginOptionsActivity.class));

            }
        }, 3000);
    }


    void requestPermissions() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION},
                123);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            startHandler();
        } else {

            AlertDialog.Builder ad = new AlertDialog.Builder(this);
            ad.setTitle("Message");
            ad.setMessage("You can't continue  the application with out permission grant ..");
            ad.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            ad.show();
        }
    }

    @Override
    protected void onStart() {

        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            Toast.makeText(getApplicationContext(),"Already signed in",Toast.LENGTH_SHORT).show();

        }


    }
}
