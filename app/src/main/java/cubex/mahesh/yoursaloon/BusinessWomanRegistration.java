package cubex.mahesh.yoursaloon;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import cubex.mahesh.yoursaloon.api.ApiClient;
import cubex.mahesh.yoursaloon.api.ApiInterface;
import cubex.mahesh.yoursaloon.api.Credentials;
import cubex.mahesh.yoursaloon.api.OTPResponse;
import cubex.mahesh.yoursaloon.pojos.SaloonLocation;
import de.hdodenhof.circleimageview.CircleImageView;
import me.philio.pinentry.PinEntryView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusinessWomanRegistration extends AppCompatActivity {

    TextView sr;
    CircleImageView cview;

    EditText email, pass, phno,  location, refno,bwname;

    Spinner city;

    Button next;

    private FirebaseAuth mAuth;

    SaloonLocation mSaloonLocation;
    boolean profile_pic_avaiable = false;
    ProgressDialog mProgressDialog;

    String fcm_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_business_women_registration);


        FirebaseInstanceId.getInstance().getInstanceId().
                addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {

                        fcm_id = task.getResult().getToken();

                        //child_ref.child("fcm_id").setValue(token);
                    }
                });


        mAuth = FirebaseAuth.getInstance();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Sending OTP");

        Typeface tf = Typeface.createFromAsset
                (getAssets(), "B93.ttf");

        sr = findViewById(R.id.sr);
        sr.setTypeface(tf);

        cview = findViewById(R.id.ciview);

        cview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder ad =
                        new AlertDialog.Builder(BusinessWomanRegistration.this);
                ad.setTitle("Your Salon");
                ad.setMessage("Please select Image source.");
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == AlertDialog.BUTTON_POSITIVE) {
                            Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
                            startActivityForResult(i, 123);
                        } else if (which == AlertDialog.BUTTON_NEGATIVE) {

                            Intent i = new Intent();
                            i.setAction(Intent.ACTION_GET_CONTENT);
                            i.setType("image/*");
                            startActivityForResult(i, 124);

                        } else if (which == AlertDialog.BUTTON_NEUTRAL) {
                            dialog.cancel();
                        }
                    }
                };
                ad.setPositiveButton("Camera", listener);
                ad.setNegativeButton("Gallery", listener);
                ad.setNeutralButton("Cancel", listener);
                ad.show();
            }
        });

        Typeface tf1 = Typeface.createFromAsset
                (getAssets(), "calibri.ttf");

        email = findViewById(R.id.email);
        email.setTypeface(tf1);

        pass = findViewById(R.id.pass);
        pass.setTypeface(tf1);

        phno = findViewById(R.id.phno);
        phno.setTypeface(tf1);

        city = findViewById(R.id.city);
     //   city.setTypeface(tf1);


        location = findViewById(R.id.location);
        location.setTypeface(tf1);
        location.setEnabled(false);

        Button location_picker = findViewById(R.id.location_picker);
        location_picker.setOnClickListener((v) -> {

            try {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                Intent i = builder.build(BusinessWomanRegistration.this);
                startActivityForResult(i, 150);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        next = findViewById(R.id.next);
        next.setTypeface(tf1);

        refno = findViewById(R.id.ref_no);
        refno.setTypeface(tf1);

     /*   LocationManager lManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            Location l = lManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            lManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    1000, 1, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {

                            double lati = location.getLatitude();
                            double longi = location.getLongitude();

                            BusinessWomanRegistration.this.location.setText(lati + "," + longi);
                            mSaloonLocation = new SaloonLocation(lati, longi);
                            lManager.removeUpdates(this);
                        }

                        @Override
                        public void onStatusChanged(String s, int i, Bundle bundle) {

                        }

                        @Override
                        public void onProviderEnabled(String s) {

                        }

                        @Override
                        public void onProviderDisabled(String s) {

                        }
                    });
        }*/

    }

    public void next(View v) {

//    startActivity(new Intent(this,
//                    BusinessWomanRegistration1.class));
        if (validadtions()) {
            String randomNumber = String.format("%04d", new Random().nextInt(10000));
            sendOTP(phno.getText().toString(), randomNumber);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123 && resultCode == RESULT_OK) {
            Bitmap bmp = (Bitmap) data.getExtras().get("data");
            cview.setImageBitmap(bmp);


            try {
                FileOutputStream fos = openFileOutput("business_women_profile_pic.png",
                        Context.MODE_PRIVATE);
                bmp.compress(Bitmap.CompressFormat.PNG,
                        100, fos);
                fos.flush();
                fos.close();

                profile_pic_avaiable = true;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if (requestCode == 124 && resultCode == RESULT_OK) {
            try {

                Uri u = data.getData();
                cview.setImageURI(u);
                Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), u);

                FileOutputStream fos = openFileOutput("business_women_profile_pic.png",
                        Context.MODE_PRIVATE);
                bmp.compress(Bitmap.CompressFormat.PNG,
                        100, fos);
                fos.flush();
                fos.close();
                profile_pic_avaiable = true;

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if (requestCode == 150 && resultCode == RESULT_OK) {
            Place selectedPlace = PlacePicker.getPlace(data, this);

            double lati = selectedPlace.getLatLng().latitude;
            double longi = selectedPlace.getLatLng().latitude;

           //  location.setText(lati + "," + longi);

            String selectedCountry = selectedPlace.getAddress().toString();

            if(selectedCountry.contains("Saudi Arabia")){

                location.setText(lati + "," + longi);

            }else{


                AlertDialog.Builder  builder = new AlertDialog.Builder(BusinessWomanRegistration.this);
                builder.setMessage("Your Address is : "+selectedPlace.getAddress()+", You are not belongs to Saudi Arabia, This app is only for Saudi Arabia People.");
                builder.setTitle("Message");
                builder.setCancelable(false);
                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        System.exit(0);

                        dialog.dismiss();

                    }
                });
                builder.setNegativeButton("Change Location", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                        try {
                            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                            Intent i = builder.build(BusinessWomanRegistration.this);
                            startActivityForResult(i, 150);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                });
                builder.show();


            }


        }

    }


    public boolean validadtions() {
        boolean isValid = true;
        if (!isValidEmail(email.getText().toString().trim())) {
            email.setError("Invalid Email");
            isValid = false;
        }
        if (phno.getText().toString().length() < 10) {
            phno.setError("Enter 10 digit mobile number");
            isValid = false;
        }
        if (phno.getText().toString().length() < 6) {
            email.setError("Password must be minimum 6 characters");
            isValid = false;
        }
        if (city.getSelectedItem().toString().equals("Select City")) {
           // city.setError("City Name must be minimum 3 characters");
            Toast.makeText(getApplicationContext(), "Please select City", Toast.LENGTH_SHORT).show();

            isValid = false;
        }
        if (refno.getText().toString().length() < 5) {
            refno.setError("Enter 5 digit reference number");
            isValid = false;
        }
        if (!profile_pic_avaiable) {
            Toast.makeText(getApplicationContext(), "Upload a profile picture", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        return isValid;
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public void sendOTP(String s, String randomNumber) {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        mProgressDialog.show();
        Call<OTPResponse> call = apiService.sendOTP(Credentials.MOBILE, Credentials.API_PASSWORD, s, Credentials.SENDER, "Your Saloon OTP is "+randomNumber, Credentials.APPLICATION_TYPE, Credentials.LANGUAGE, Credentials.RETURN_JSON);
        call.enqueue(new Callback<OTPResponse>() {
            @Override
            public void onResponse(Call<OTPResponse> call, Response<OTPResponse> response) {
                Response<OTPResponse> response2 = response;
                mProgressDialog.dismiss();
                if (response.body().getResponseStatus().equalsIgnoreCase("success")) {
                    showOTPDialog(randomNumber);
                } else {
                    Toast.makeText(getApplicationContext(), response.body().getError().getMessageEn(), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<OTPResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                mProgressDialog.dismiss();
            }
        });

    }


    public void showOTPDialog(String otp) {
        Toast.makeText(getApplicationContext(), otp, Toast.LENGTH_LONG).show();
        Dialog d = new Dialog(BusinessWomanRegistration.this);
        d.setContentView(R.layout.activity_otp_);
        d.setCancelable(false);


        PinEntryView mEdtPin = (PinEntryView) d.findViewById(R.id.inputOtp);
        Button mCancel = (Button) d.findViewById(R.id.btn_cacel);
        Button mSubmit = (Button) d.findViewById(R.id.btn_verify_otp);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEdtPin.getText().toString().equalsIgnoreCase(otp)) {
                    d.dismiss();
                    startSignUp();
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid OTP", Toast.LENGTH_LONG).show();
                }
            }
        });
        d.show();
    }

    private void startSignUp() {
        mProgressDialog.setMessage("Signing In for first Time");
        mProgressDialog.show();
        bwname = findViewById(R.id.bwname);
        String Bwname = bwname.getText().toString();
        mAuth.createUserWithEmailAndPassword(
                email.getText().toString(),
                pass.getText().toString()).addOnCompleteListener((task) -> {
            mProgressDialog.dismiss();
            if (task.isSuccessful()) {

                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                FirebaseDatabase dBase = FirebaseDatabase.getInstance();
                DatabaseReference ref = dBase.getReference("/business_women");
                DatabaseReference child_ref = ref.child("/" + uid);
                child_ref.child("type").setValue("business_women");
                child_ref.child("email").setValue(email.getText().toString());
                child_ref.child("password").setValue(pass.getText().toString());
                child_ref.child("phoneno").setValue(phno.getText().toString());
                child_ref.child("city").setValue(city.getSelectedItem().toString());
                child_ref.child("location").setValue(location.getText().toString());
                child_ref.child("ref_no").setValue(refno.getText().toString());
                child_ref.child("accepted").setValue(false);
                child_ref.child("fcm_id").setValue(fcm_id);
                child_ref.child("Name").setValue(Bwname);

               /* FirebaseInstanceId.getInstance().getInstanceId().
                        addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {

                                String token = task.getResult().getToken();

                                child_ref.child("fcm_id").setValue(token);
                            }
                        });
*/


                startActivity(new Intent(this,
                        BusinessWomanRegistration1.class));


            } else {

                Toast.makeText(BusinessWomanRegistration.this,
                        "Failed to Register, May be Email id is already exist!",
                        Toast.LENGTH_LONG).show();

            }
        });
    }
}
