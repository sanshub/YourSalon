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
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import cubex.mahesh.yoursaloon.api.ApiClient;
import cubex.mahesh.yoursaloon.api.ApiInterface;
import cubex.mahesh.yoursaloon.api.Credentials;
import cubex.mahesh.yoursaloon.api.OTPResponse;
import cubex.mahesh.yoursaloon.pojos.UserPojo;
import de.hdodenhof.circleimageview.CircleImageView;
import me.philio.pinentry.PinEntryView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CustomerRegistration extends AppCompatActivity {

    TextView sr;
    CircleImageView cview;

    EditText email, pass, phno, city, Cnameet;

    Button next;

    private FirebaseAuth mAuth;
    boolean profile_pic_avaiable = false;

    ProgressDialog mProgressDialog;

    String fcm_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_customer_registration);

        mAuth = FirebaseAuth.getInstance();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Sending OTP");

        FirebaseInstanceId.getInstance().getInstanceId().
                addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {

                        fcm_id = task.getResult().getToken();

                        //child_ref.child("fcm_id").setValue(token);
                    }
                });


        Typeface tf = Typeface.createFromAsset
                (getAssets(), "B93.ttf");

        sr = findViewById(R.id.sr);
        sr.setTypeface(tf);

        cview = findViewById(R.id.ciview);
        cview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder ad =
                        new AlertDialog.Builder(CustomerRegistration.this);
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

        email = findViewById(R.id.email);
        email.setTypeface(tf);

        pass = findViewById(R.id.pass);
        pass.setTypeface(tf);

        phno = findViewById(R.id.phno);
        phno.setTypeface(tf);

        city = findViewById(R.id.city);
        city.setTypeface(tf);

        next = findViewById(R.id.next);
        next.setTypeface(tf);

        Cnameet = findViewById(R.id.Cnameet);
        Cnameet.setTypeface(tf);

    }

    public void submit(View v) {
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
                FileOutputStream fos = openFileOutput("customer_profile_pic.png",
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

                FileOutputStream fos = openFileOutput("customer_profile_pic.png",
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

    }

    void uploadProfilePic() {
        mProgressDialog.setMessage("Uploading Profile Pic");
        mProgressDialog.show();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference ref = storage.getReference("/users/" + uid);
        try {
            FileInputStream fis = openFileInput("customer_profile_pic.png");;

            UploadTask uploadTask = ref.child("customer_profile_pic.png").
                    putStream(fis);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            }).
                    addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mProgressDialog.dismiss();

                            taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                      String url =     task.getResult().toString();
                                    UserPojo mUser = new UserPojo(Cnameet.getText().toString().trim(),
                                            pass.getText().toString().trim(),
                                            email.getText().toString(),
                                            phno.getText().toString().trim(),
                                            city.getText().toString(),
                                            fcm_id, url, "customer");
                                    uploadIntoDB(mUser, uid);

//                                    Intent uintent = new Intent(getApplicationContext(),Customer_Chat.class);
//                                    uintent.putExtra("Cnamo",Cnameet.getText().toString().trim());
//                                    Customer_Chat mcustomer_chat = new Customer_Chat();
//                                    mcustomer_chat.Cnaame = Cnameet.getText().toString().trim();
                                }
                            });




                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Failed to upload profile picture", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void uploadIntoDB(UserPojo mUser, String uid) {
        mProgressDialog.setMessage("Setting up your account");
        mProgressDialog.show();
        FirebaseDatabase dBase = FirebaseDatabase.getInstance();
        DatabaseReference ref = dBase.getReference("/users");


        ref.child(uid).setValue(mUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mProgressDialog.dismiss();
                if (task.isSuccessful()) {
                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                    Toast.makeText(getApplicationContext(), "Welcome", Toast.LENGTH_LONG).show();

                } else {

                }
            }
        });
       /* DatabaseReference child_ref = ref.child("/"+uid);
        child_ref.child("reg_type").setValue("customer");
        child_ref.child("email").setValue(email.getText().toString());
        child_ref.child("password").setValue(pass.getText().toString());
        child_ref.child("phoneno").setValue(phno.getText().toString());
        child_ref.child("city").setValue(city.getText().toString());*/
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
        if (city.getText().toString().length() < 3) {
            city.setError("City Name must be minimum 3 characters");
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


    public void showOTPDialog(String otp) {
        Toast.makeText(getApplicationContext(), otp, Toast.LENGTH_LONG).show();
        Dialog d = new Dialog(CustomerRegistration.this);
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
        mAuth.createUserWithEmailAndPassword(
                email.getText().toString(),
                pass.getText().toString()).addOnCompleteListener((task) -> {
            mProgressDialog.dismiss();
            if (task.isSuccessful()) {
                uploadProfilePic();
            } else {
                Toast.makeText(CustomerRegistration.this,
                        "Failed to Register, May be Email id is already exist!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

}
