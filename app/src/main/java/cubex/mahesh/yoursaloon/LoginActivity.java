package cubex.mahesh.yoursaloon;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import cubex.mahesh.yoursaloon.pojos.ApprovalRequestPojo;

public class LoginActivity extends AppCompatActivity {

    TextView li, fp;
    EditText email, pwd;
    CheckBox cb1;
    Button login, caft;
    private FirebaseAuth mAuth;

    ProgressDialog mProgressDialog;
    SharedPreferences mSharedpref;


    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    String AccountType, AccountID, CustomerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mSharedpref = getSharedPreferences("saloon_prfs", Context.MODE_PRIVATE);
        Typeface tf = Typeface.createFromAsset
                (getAssets(), "B93.ttf");

        li = findViewById(R.id.li);
        li.setTypeface(tf);

        Typeface tf1 = Typeface.createFromAsset
                (getAssets(), "calibri.ttf");

        mAuth = FirebaseAuth.getInstance();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Logging In");
        email = findViewById(R.id.email);
        email.setTypeface(tf1);

        pwd = findViewById(R.id.password);
        pwd.setTypeface(tf1);

        cb1 = findViewById(R.id.cb1);
        cb1.setTypeface(tf1);

        fp = findViewById(R.id.fp);
        fp.setTypeface(tf1);

        login = findViewById(R.id.login);
        login.setTypeface(tf1);

        caft = findViewById(R.id.caft);
        caft.setTypeface(tf1);

        mAuth = FirebaseAuth.getInstance();


        cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


            }
        });

        login.setOnClickListener((v) -> {
            mProgressDialog.show();

            mAuth.signInWithEmailAndPassword(email.getText().toString(),
                    pwd.getText().toString()).addOnCompleteListener((task) -> {
                mProgressDialog.dismiss();
                if (task.isSuccessful()) {
                    mProgressDialog.setMessage("Checking Approval Status");
                    String type = mSharedpref.getString("user_type", "none");
                    String id = mAuth.getCurrentUser().getUid();

                    AccountType = type;
                    AccountID = id;
                    if (type.equalsIgnoreCase("saloon")) {
                        mProgressDialog.show();
                        mFirebaseDatabase.getReference("saloons").child(id).child("accepted").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                mProgressDialog.dismiss();

                                boolean status = (boolean) dataSnapshot.getValue();
                                AlertDialog.Builder ad =
                                        new AlertDialog.Builder(LoginActivity.this);
                                ad.setTitle("Your Salon");

                                if (!status) {
                                    ad.setMessage("Your Account Approval is Pending.");
                                    ad.setPositiveButton("Upload Receipt", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //  dialogInterface.dismiss();
                                            captureReceipt();
                                        }
                                    });
                                    ad.setNegativeButton("Pay Now", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });
                                    ad.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            dialogInterface.dismiss();

                                        }
                                    });
                                } else {
                                    ad.setMessage("Account Approved.");
                                    ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                          onBackPressed();
                                        }
                                    });
                                }
                                ad.show();
                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                mProgressDialog.dismiss();
                            }
                        });
                    } else if (type.equalsIgnoreCase("customer")) {
                        startActivity(new Intent(LoginActivity.this,
                                DashboardActivity.class));
                    } else if (type.equalsIgnoreCase("business_women")) {
                        mProgressDialog.show();
                        mFirebaseDatabase.getReference("business_women").child(id).child("accepted").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                mProgressDialog.dismiss();
                                boolean status = (boolean) dataSnapshot.getValue();
                                AlertDialog.Builder ad =
                                        new AlertDialog.Builder(LoginActivity.this);
                                ad.setTitle("Your Salon");

                                if (!status) {
                                    ad.setMessage("Your Account Approval is Pending.");
                                    ad.setPositiveButton("Upload Receipt", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //  dialogInterface.dismiss();
                                            captureReceipt();
                                        }
                                    });
                                    ad.setNegativeButton("Pay Now", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });
                                } else {
                                    ad.setMessage("Account Approved.");
                                    ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            onBackPressed();
                                        }
                                    });
                                    Intent intent = new Intent(getApplicationContext(),BusinessWoman_Chat.class);
                                    startActivity(intent);
                                }
                                ad.show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                mProgressDialog.dismiss();
                            }
                        });
                    } else if (type.equalsIgnoreCase("business_guest")) {
                        mProgressDialog.show();
                        mFirebaseDatabase.getReference("business_guest").child(id).child("accepted").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                mProgressDialog.dismiss();
                                boolean status = (boolean) dataSnapshot.getValue();
                                AlertDialog.Builder ad =
                                        new AlertDialog.Builder(LoginActivity.this);
                                ad.setTitle("Your Salon");
                                if (!status) {
                                    ad.setMessage("Your Account Approval is Pending.");
                                    ad.setPositiveButton("Upload Receipt", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //  dialogInterface.dismiss();
                                            captureReceipt();
                                        }
                                    });
                                    ad.setNegativeButton("Pay Now", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });
                                } else {
                                    ad.setMessage("Account Approved.");
                                    ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            onBackPressed();
                                        }
                                    });
                                }
                                ad.show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                mProgressDialog.dismiss();
                            }
                        });
                    }
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Please provide valid credentials",
                            Toast.LENGTH_LONG).show();
                }

            });
        });

        caft.setOnClickListener((v) -> {

            SharedPreferences spf =
                    getSharedPreferences("saloon_prfs", Context.MODE_PRIVATE);
            String type = spf.getString("user_type", "");

            if (type.equals("saloon")) {
                startActivity(new Intent(LoginActivity.this,
                        SalonRegistration.class));
            } else if (type.equals("business_women")) {
                startActivity(new Intent(LoginActivity.this,
                        BusinessWomanRegistration.class));
            } else if (type.equals("business_guest")) {
                startActivity(new Intent(LoginActivity.this,
                        BusinessGuestRegistration.class));
            } else if (type.equals("customer")) {
                startActivity(new Intent(LoginActivity.this,
                        CustomerRegistration.class));
            }
        });


    }

    public void captureReceipt() {
        AlertDialog.Builder ad =
                new AlertDialog.Builder(LoginActivity.this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123 && resultCode == RESULT_OK) {
            Bitmap bmp = (Bitmap) data.getExtras().get("data");
            //cview.setImageBitmap(bmp);


            try {
                FileOutputStream fos = openFileOutput("receipt.png",
                        Context.MODE_PRIVATE);
                bmp.compress(Bitmap.CompressFormat.PNG,
                        100, fos);
                fos.flush();
                fos.close();

                //    profile_pic_avaiable = true;

                showPreview(requestCode, bmp, null);

                //   uploadReceipt();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if (requestCode == 124 && resultCode == RESULT_OK) {
            try {

                Uri u = data.getData();
                //   cview.setImageURI(u);
                Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), u);

                FileOutputStream fos = openFileOutput("receipt.png",
                        Context.MODE_PRIVATE);
                bmp.compress(Bitmap.CompressFormat.PNG,
                        100, fos);
                fos.flush();
                fos.close();
                //  profile_pic_avaiable = true;

                showPreview(requestCode, null, u);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }


    void uploadReceipt() {
        mProgressDialog.setMessage("Uploading Payment Receipt");
        mProgressDialog.show();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference ref = storage.getReference("Receipts/" + AccountType + "/" + AccountID);
        try {
            FileInputStream fis = openFileInput("receipt.png");

          //  UploadTask mUpload = ref.putStream(fid)
                    ref.putStream(fis).
                    addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mProgressDialog.dismiss();


                           ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                  String  url = task.getResult().toString();
                                    FirebaseDatabase dBase = FirebaseDatabase.getInstance();
                                    DatabaseReference ref = dBase.getReference("/PaymentRequests");
                                    ApprovalRequestPojo mPojo = new ApprovalRequestPojo(AccountID, AccountType, url);
                                    ref.child(AccountID).setValue(mPojo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Request sent to Admin for Approval", Toast.LENGTH_SHORT).show();

                                            } else {
                                                Toast.makeText(getApplicationContext(), "Something went wrong, Please retry...", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                                }
                            });


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Something went wrong, Please retry...", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showPreview(int requestCode, Bitmap bmp, Uri u) {
        Dialog d = new Dialog(LoginActivity.this);
        d.setContentView(R.layout.dialog_receipt);

        ImageView image = d.findViewById(R.id.img_preview);
        Button upload, cancel;
        d.setCancelable(true);

        upload = d.findViewById(R.id.upload);
        cancel = d.findViewById(R.id.cancel);

        switch (requestCode) {
            case 123:
                image.setImageBitmap(bmp);
                break;

            case 124:
                image.setImageURI(u);
                break;
        }

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadReceipt();
                d.cancel();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
            }
        });

        d.show();
    }
}
