package cubex.mahesh.yoursaloon;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
//import cubex.mahesh.yoursaloon.Adapter.Bwc_Adapter;

public class BusinessWoman_Chat extends AppCompatActivity {

    private RecyclerView mRecyclerView;
//    private Bwc_Adapter mBwc_Adapter;
//    private List<Bwc_client> mBwc_client;
   // String impname;
    //private ArrayList<Object> listFriendID;
    //private String BWname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_woman__chat);

//        FirebaseDatabase.getInstance().getReference().child("Business_woman").child(FirebaseAuth.getInstance()
//                .getCurrentUser().getUid()).child("").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                //dataSnapshot.getValue();
//                ccname = dataSnapshot.getValue().toString();
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        mRecyclerView = findViewById(R.id.bwc_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

//        ListFriend dataListFriend = null;
//        if (dataListFriend == null) {
//            dataListFriend = FriendDB.getInstance(getApplicationContext()).getListFriend();
//            if (dataListFriend.getListFriend().size() > 0) {
//                listFriendID = new ArrayList<>();
//                for (Friend friend : dataListFriend.getListFriend()) {
//                    listFriendID.add(friend.id);
//                }
//               // detectFriendOnline.start();
//            }
//        }

//        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance()
//                .getCurrentUser().getUid()).child("Cname").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                //dataSnapshot.getValue();
//                BWname = dataSnapshot.getValue().toString();
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

//        mBwc_client = new ArrayList<>();
//        readClients();


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

    //    private void getAllFriendInfo(final int index) {
//        if (index == listFriendID.size()) {
//            //save list friend
//            adapter.notifyDataSetChanged();
//            dialogFindAllFriend.dismiss();
//            mSwipeRefreshLayout.setRefreshing(false);
//            detectFriendOnline.start();
//        } else {
//            try {
//                final String id = listFriendID.get(index);
//                FirebaseDatabase.getInstance().getReference().child("chatRooms").child(namelo)addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.getValue() != null) {
//                            Friend user = new Friend();
//                            HashMap mapUserInfo = (HashMap) dataSnapshot.getValue();
////                            user.name = (String) mapUserInfo.get("name");
////                            user.email = (String) mapUserInfo.get("email");
////                            user.avata = (String) mapUserInfo.get("avata");
//                            user.id = id;
//                            user.idRoom = id.compareTo(StaticConfig.UID) > 0 ? (StaticConfig.UID + id).hashCode() + "" : "" + (id + StaticConfig.UID).hashCode();
//                            dataListFriend.getListFriend().add(user);
//                            FriendDB.getInstance(getContext()).addFriend(user);
//                        }
//                        getAllFriendInfo(index + 1);
//
//                        dialogFindAllFriend.dismiss();
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//            } catch (Exception e) {
//
//            }
//        }
//    }
//}

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        DatabaseReference friendRef =  FirebaseDatabase.getInstance().getReference().child("chatRooms").child(BWname);
////        FirebaseRecyclerAdapter<>
//        FirebaseRecyclerAdapter<User, UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(
//                User.class,
//                R.layout.bwc_card_view,
//                UserViewHolder.class,
//                friendRef
//        );
//    }







//    private void readClients(){
//
//        FirebaseDatabase.getInstance().getReference().child("business_women").
//                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//
//                impname = dataSnapshot.getValue().toString();
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });





//        FirebaseDatabase.getInstance().getReference().child("chatRooms").child(impname).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                mBwc_client.clear();
//                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//
//                    Bwc_client cBwc_client = snapshot.getValue(Bwc_client.class);
//                    mBwc_client.add(cBwc_client);
//
//                }
//
//                mBwc_Adapter = new Bwc_Adapter(getApplicationContext(), mBwc_client);
//                mRecyclerView.setAdapter(mBwc_Adapter);
//
//            }
//
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });




    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        }
        finish();
    }




}

// class User{
//    private String message;
//    private String sender, receiver;
//
//     public String getMessage() {
//         return message;
//     }
//
//     public void setMessage(String message) {
//         this.message = message;
//     }
//
//     public String getSender() {
//         return sender;
//     }
//
//     public void setSender(String sender) {
//         this.sender = sender;
//     }
//
//     public String getReceiver() {
//         return receiver;
//     }
//
//     public void setReceiver(String receiver) {
//         this.receiver = receiver;
//     }
// }
