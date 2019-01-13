package cubex.mahesh.yoursaloon;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import cubex.mahesh.yoursaloon.pojos.BusinessGuestPojo;
import cubex.mahesh.yoursaloon.pojos.BusinessWomenPojo;
import cubex.mahesh.yoursaloon.pojos.SaloonPojo;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    String selected_type = "";

    SeekBar sBar;

    TextView tv_max;

    Spinner sp_cities;

    String button_clicked = "";

    int range= 0;

    int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        sBar = (SeekBar) findViewById(R.id.sBar);
        tv_max = findViewById(R.id.max);
        sp_cities = findViewById(R.id.sp_cities);

        sBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_max.setText(progress + "");

                range = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        selected_type = getIntent().getStringExtra("selected_type");


        if (android.os.Build.VERSION.SDK_INT > 21) {
            getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.gradient1));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Button nearBy = (Button) findViewById(R.id.nearBy);
        nearBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                button_clicked = "Near By";

                count = 0;

                if (tv_max.equals(0)) {
                    Toast.makeText(MapsActivity.this,
                            "Please Select Range on Seek Bar", Toast.LENGTH_SHORT).show();
                } else {
                    mMap.clear();

                    loadData();
                }
            }
        });


        Button cities = findViewById(R.id.cities);
        cities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                button_clicked = "Cities";

                if (sp_cities.getSelectedItem().toString().equals("Select City")) {
                    Toast.makeText(MapsActivity.this,
                            "Please Select City", Toast.LENGTH_SHORT).show();
                } else {

                    mMap.clear();

                    loadData();
                }


            }
        });


    } // onCreate( )


    double cur_lati = 0;
    double cur_long = 0;
    Location current_location;

    @SuppressLint("MissingPermission")
    void loadCurrentLocation() {

        LocationManager lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        @SuppressLint("MissingPermission") Location l = lManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        cur_lati = l.getLatitude();
        cur_long = l.getLongitude();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        lManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                1000, 1, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                         cur_lati = location.getLatitude();
                         cur_long = location.getLongitude();

                        current_location = new Location("curent_place");
                        current_location.setLatitude(cur_lati);
                        current_location.setLongitude(cur_long);

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
     }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    } // onMapReady( )


    void  loadData( ){

        if(mMap!=null) {
            switch (selected_type) {
                case "ic_makeup":
                    for (SaloonPojo pojo : DashboardActivity.saloons_list) {
                        if (pojo.isMakeup()) {
                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 15));
                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent("android.intent.action.VIEW");
                                                Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                intent.setData(data);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });

                            }
                        }
                    }

                    for (BusinessWomenPojo pojo : DashboardActivity.bwomens_list) {
                        if (pojo.isMakeup()) {
                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 15));
                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent(getApplicationContext(),Customer_Chat.class);
                                               // Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                              //  intent.setData(data);
                                                String bwname = pojo.bwname;
                                                intent.putExtra("namebw",bwname);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }
                        }
                    }


                    for (BusinessGuestPojo pojo : DashboardActivity.bguests_list) {
                        if (pojo.isMakeup()) {
                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(loc_obj));
                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 15));
                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent("android.intent.action.VIEW");
                                                Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                intent.setData(data);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }
                        }
                    }


                    break;
                case "ic_hairstyle":
                    for (SaloonPojo pojo : DashboardActivity.saloons_list) {
                        if (pojo.isHairstyle()) {

                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(loc_obj));
                                }
                                // mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc_obj, 17));
                                //  mMap.animateCamera(CameraUpdateFactory.zoomTo(15f));
                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent("android.intent.action.VIEW");
                                                Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                intent.setData(data);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }
                        }
                    }

                    for (BusinessWomenPojo pojo : DashboardActivity.bwomens_list) {
                        if (pojo.isHairstyle()) {
                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 15));
                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent(getApplicationContext(),Customer_Chat.class);
                                                // Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                //  intent.setData(data);
                                                String bwname = pojo.bwname;
                                                intent.putExtra("namebw",bwname);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }
                        }
                    }

                    for (BusinessGuestPojo pojo : DashboardActivity.bguests_list) {
                        if (pojo.isHairstyle()) {
                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 15));
                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent("android.intent.action.VIEW");
                                                Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                intent.setData(data);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }
                        }
                    }

                    break;
                case "ic_facial":  //bodycare
                    for (SaloonPojo pojo : DashboardActivity.saloons_list) {
                        if (pojo.isBodycare()) {

                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 10));
                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent("android.intent.action.VIEW");
                                                Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                intent.setData(data);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }

                        }
                    }

                    for (BusinessWomenPojo pojo : DashboardActivity.bwomens_list) {
                        if (pojo.isBodycare()) {
                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 15));
                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent(getApplicationContext(),Customer_Chat.class);
                                                // Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                //  intent.setData(data);
                                                String bwname = pojo.bwname;
                                                intent.putExtra("namebw",bwname);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }
                        }
                    }

                    for (BusinessGuestPojo pojo : DashboardActivity.bguests_list) {
                        if (pojo.isBodycare()) {
                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 15));
                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent("android.intent.action.VIEW");
                                                Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                intent.setData(data);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }
                        }
                    }

                    break;
                case "ic_hair_treatment":

                    for (SaloonPojo pojo : DashboardActivity.saloons_list) {
                        if (pojo.isHairtreatment()) {

                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 10));
                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent("android.intent.action.VIEW");
                                                Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                intent.setData(data);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }

                        }
                    }

                    for (BusinessWomenPojo pojo : DashboardActivity.bwomens_list) {
                        if (pojo.isHairtreatment()) {
                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 15));
                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent(getApplicationContext(),Customer_Chat.class);
                                                // Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                //  intent.setData(data);
                                                String bwname = pojo.bwname;
                                                intent.putExtra("namebw",bwname);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }
                        }
                    }


                    for (BusinessGuestPojo pojo : DashboardActivity.bguests_list) {
                        if (pojo.isHairtreatment()) {
                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 15));
                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent("android.intent.action.VIEW");
                                                Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                intent.setData(data);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }
                        }
                    }


                    break;
                case "ic_henna":

                    for (SaloonPojo pojo : DashboardActivity.saloons_list) {
                        if (pojo.isHennadesign()) {

                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 10));
                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent("android.intent.action.VIEW");
                                                Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                intent.setData(data);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }

                        }
                    }

                    for (BusinessWomenPojo pojo : DashboardActivity.bwomens_list) {
                        if (pojo.isHennadesign()) {
                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 15));
                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent(getApplicationContext(),Customer_Chat.class);
                                                // Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                //  intent.setData(data);
                                                String bwname = pojo.bwname;
                                                intent.putExtra("namebw",bwname);
                                                startActivity(intent);
                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }
                        }
                    }

                    for (BusinessGuestPojo pojo : DashboardActivity.bguests_list) {
                        if (pojo.isHennadesign()) {
                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 15));
                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent("android.intent.action.VIEW");
                                                Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                intent.setData(data);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }
                        }
                    }


                    break;
                case "ic_bath":

                    for (SaloonPojo pojo : DashboardActivity.saloons_list) {
                        if (pojo.isWesternbath()) {

                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 10));
                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent("android.intent.action.VIEW");
                                                Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                intent.setData(data);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }

                        }
                    }

                    for (BusinessWomenPojo pojo : DashboardActivity.bwomens_list) {
                        if (pojo.isWesternbath()) {
                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 15));
                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent(getApplicationContext(),Customer_Chat.class);
                                                // Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                //  intent.setData(data);
                                                String bwname = pojo.bwname;
                                                intent.putExtra("namebw",bwname);
                                                startActivity(intent);
                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }
                        }
                    }

                    for (BusinessGuestPojo pojo : DashboardActivity.bguests_list) {
                        if (pojo.isHennadesign()) {
                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 15));
                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent("android.intent.action.VIEW");
                                                Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                intent.setData(data);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }
                        }
                    }


                    break;
                case "ic_haircut":

                    for (SaloonPojo pojo : DashboardActivity.saloons_list) {
                        if (pojo.isHaircut()) {

                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 10));
                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent("android.intent.action.VIEW");
                                                Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                intent.setData(data);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }

                        }
                    }


                    for (BusinessWomenPojo pojo : DashboardActivity.bwomens_list) {
                        if (pojo.isHaircut()) {
                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 15));

                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent(getApplicationContext(),Customer_Chat.class);
                                                // Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                //  intent.setData(data);
                                                String bwname = pojo.bwname;
                                                intent.putExtra("namebw",bwname);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }
                        }
                    }


                    for (BusinessGuestPojo pojo : DashboardActivity.bguests_list) {
                        if (pojo.isHaircut()) {
                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 15));

                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent("android.intent.action.VIEW");
                                                Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                intent.setData(data);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }
                        }
                    }

                    break;
                case "ic_wax":

                    for (SaloonPojo pojo : DashboardActivity.saloons_list) {
                        if (pojo.isWax()) {

                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 10));
                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent("android.intent.action.VIEW");
                                                Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                intent.setData(data);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }

                        }
                    }

                    for (BusinessWomenPojo pojo : DashboardActivity.bwomens_list) {
                        if (pojo.isWax()) {
                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 15));

                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent(getApplicationContext(),Customer_Chat.class);
                                                // Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                //  intent.setData(data);
                                                String bwname = pojo.bwname;
                                                intent.putExtra("namebw",bwname);
                                                startActivity(intent);
                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }
                        }
                    }

                    for (BusinessGuestPojo pojo : DashboardActivity.bguests_list) {
                        if (pojo.isHaircut()) {
                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 15));
                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent("android.intent.action.VIEW");
                                                Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                intent.setData(data);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }
                        }
                    }

                    break;
                case "ic_eyebrows":

                    for (SaloonPojo pojo : DashboardActivity.saloons_list) {
                        if (pojo.isEyebrows()) {

                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 10));
                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent("android.intent.action.VIEW");
                                                Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                intent.setData(data);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }

                        }
                    }

                    for (BusinessWomenPojo pojo : DashboardActivity.bwomens_list) {
                        if (pojo.isEyebrows()) {
                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 15));
                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent(getApplicationContext(),Customer_Chat.class);
                                                // Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                //  intent.setData(data);
                                                String bwname = pojo.bwname;
                                                intent.putExtra("namebw",bwname);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }
                        }
                    }


                    for (BusinessGuestPojo pojo : DashboardActivity.bguests_list) {
                        if (pojo.isHaircut()) {
                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 15));

                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent("android.intent.action.VIEW");
                                                Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                intent.setData(data);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }
                        }
                    }


                    break;
                case "ic_massage":

                    for (SaloonPojo pojo : DashboardActivity.saloons_list) {
                        if (pojo.isMassage()) {

                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 10));

                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent("android.intent.action.VIEW");
                                                Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                intent.setData(data);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }

                        }
                    }


                    for (BusinessWomenPojo pojo : DashboardActivity.bwomens_list) {
                        if (pojo.isMassage()) {
                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 15));

                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent(getApplicationContext(),Customer_Chat.class);
                                                // Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                //  intent.setData(data);
                                                String bwname = pojo.bwname;
                                                intent.putExtra("namebw",bwname);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }
                        }
                    }

                    for (BusinessGuestPojo pojo : DashboardActivity.bguests_list) {
                        if (pojo.isHaircut()) {
                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 15));

                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent("android.intent.action.VIEW");
                                                Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                intent.setData(data);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }
                        }
                    }


                    break;
                case "ic_hair_protien":

                    for (SaloonPojo pojo : DashboardActivity.saloons_list) {
                        if (pojo.isHairprotein()) {

                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 10));

                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent("android.intent.action.VIEW");
                                                Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                intent.setData(data);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }

                        }
                    }

                    for (BusinessWomenPojo pojo : DashboardActivity.bwomens_list) {
                        if (pojo.isHairprotein()) {
                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 15));

                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent(getApplicationContext(),Customer_Chat.class);
                                                // Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                //  intent.setData(data);
                                                String bwname = pojo.bwname;
                                                intent.putExtra("namebw",bwname);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }
                        }
                    }

                    for (BusinessGuestPojo pojo : DashboardActivity.bguests_list) {
                        if (pojo.isHaircut()) {
                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 15));

                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent("android.intent.action.VIEW");
                                                Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                intent.setData(data);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }
                        }
                    }

                    break;
                case "ic_photograpgy":

                    for (SaloonPojo pojo : DashboardActivity.saloons_list) {
                        if (pojo.isPhotography()) {

                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 10));

                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent("android.intent.action.VIEW");
                                                Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                intent.setData(data);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }

                        }
                    }

                    for (BusinessWomenPojo pojo : DashboardActivity.bwomens_list) {
                        if (pojo.isPhotography()) {
                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 15));

                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent(getApplicationContext(),Customer_Chat.class);
                                                // Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                //  intent.setData(data);
                                                String bwname = pojo.bwname;
                                                intent.putExtra("namebw",bwname);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }
                        }
                    }

                    for (BusinessGuestPojo pojo : DashboardActivity.bguests_list) {
                        if (pojo.isHaircut()) {
                            String loc = pojo.getLocation();
                            if (loc.contains(",")) {
                                String[] values = loc.split(",");
                                double lati = Double.parseDouble(values[0]);
                                double longi = Double.parseDouble(values[1]);
                                LatLng loc_obj = new LatLng(lati, longi);
                                if(button_clicked == "Cities" && pojo.getCity().equals(sp_cities.getSelectedItem().toString())) {
                                   mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(loc_obj));
                                }else if(button_clicked == "Near By" && distanceBetweenLocations(lati,longi)){
                                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(loc_obj));

                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_obj));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        loc_obj, 15));

                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage("Email : " + pojo.getEmail() + "\n" + "Contact No : " + pojo.getPhoneno());
                                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_DIAL);
                                                i.setData(Uri.parse("tel:" + pojo.getPhoneno()));
                                                startActivity(i);

                                            }
                                        });
                                        builder.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent("android.intent.action.VIEW");
                                                Uri data = Uri.parse("sms:" + pojo.getPhoneno());
                                                intent.setData(data);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("Email", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_SEND);
                                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{pojo.getEmail()});
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Message from Salon App");
                                                i.putExtra(Intent.EXTRA_TEXT, "");
                                                i.setType("message/rfc822");
                                                startActivity(i);

                                            }
                                        });
                                        builder.show();

                                        return false;
                                    }
                                });
                            }
                        }
                    }

                    break;
            }
        }else{

            Toast.makeText(MapsActivity.this,"Something went wrong with map..",
                    Toast.LENGTH_LONG).show();
        }
    } // loadData( )




    boolean distanceBetweenLocations(double lati,double longi){

        if(current_location!=null) {

            Location loc2 = new Location("location_salon");
            loc2.setLatitude(lati);
            loc2.setLongitude(longi);

            float distance = current_location.distanceTo(loc2);

            Log.i("msg", distance + "");

            if (distance < range) {
                return true;
            } else {
                return false;
            }
        }else {
            try {
                if(count==0) {
                    count = 1;
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                    builder.setTitle("Message");
                    builder.setMessage("Unable to detect the device Current Location, Please select the choose your current location");
                    builder.setPositiveButton("Change Location", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                            Intent i = null;
                            try {
                                i = builder.build(MapsActivity.this);
                            } catch (GooglePlayServicesRepairableException e) {
                                e.printStackTrace();
                            } catch (GooglePlayServicesNotAvailableException e) {
                                e.printStackTrace();
                            }
                            startActivityForResult(i, 150);

                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            System.exit(0);

                        }
                    });
                    builder.show();
                }

            }catch (Exception e){
                e.printStackTrace();
            }


        }

        return  false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 150 && resultCode == RESULT_OK) {
            Place selectedPlace = PlacePicker.getPlace(data, this);

            double lati = selectedPlace.getLatLng().latitude;
            double longi = selectedPlace.getLatLng().latitude;

            String selectedCountry = selectedPlace.getAddress().toString();

            if(selectedCountry.contains("Saudi Arabia")){

                current_location = new Location("curent_place");
                current_location.setLatitude(lati);
                current_location.setLongitude(longi);

            }else{


                AlertDialog.Builder  builder = new AlertDialog.Builder(MapsActivity.this);
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
                            Intent i = builder.build(MapsActivity.this);
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
} // MainActivity
