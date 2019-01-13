package cubex.mahesh.yoursaloon;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import cubex.mahesh.yoursaloon.pojos.UserPojo;

public class Customer_Chat extends AppCompatActivity {

    DatabaseReference DBref = FirebaseDatabase.getInstance().getReference().child("chatRooms");
    public Button sedn;
    public EditText messview;
    String ccname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer__chat);
        Bundle bundle = getIntent().getExtras();
        String namelo = bundle.getString("namebw");
        sedn = findViewById(R.id.send);
        messview = findViewById(R.id.messview);

        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).child("Cname").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //dataSnapshot.getValue();
                ccname = dataSnapshot.getValue().toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        sedn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Message = messview.getText().toString();
                HashMap<String,String> Message0 = new HashMap<>();
                Message0.put("SENDER",ccname);
                Message0.put("MESSAGE",Message);
                Message0.put("RECEIVER",namelo);
                DBref.child(namelo).child(ccname).push().setValue(Message0);


            }
        });













    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.logout,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);

        int id = item.getItemId();

        if(id == R.id.log0ut ){

            FirebaseAuth.getInstance().signOut();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            }
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
