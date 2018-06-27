package semesterproject.mobileprogramming.com.trackme;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Iterator;


public class MapsActivity extends FragmentActivity  implements OnMapReadyCallback,
        NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private CameraUpdate currentCameraPosition=null;
    private DatabaseHelper dbHelper;
    private NotificationCompat.Builder nBuilder;
    private NotificationManager nManager;
    private float speed;
    private int colorPolyline=Color.BLUE;

    private int batteryLevel;
    private BroadcastReceiver batInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            if(batteryLevel<18){
                String str="Battery level "+batteryLevel+". Record is stopped";
                notifyMessage(str);
                stopRecording();
            }

        }
    };

    private BroadcastReceiver locationUpdatedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            speed=intent.getFloatExtra("speed",0);
            updateUI();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            enableGPSDialog();
        }
        startService(new Intent(getBaseContext(), TrackMeBackground.class));

        nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        addNotification();

        dbHelper=new DatabaseHelper(this);

        registerReceiver(this.batInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        LocalBroadcastManager.getInstance(this).registerReceiver(locationUpdatedReceiver,
                        new IntentFilter("Location Updated"));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationInfo locationInfo = dbHelper.getLastLocInfo();
                if(locationInfo!=null){
                    Snackbar.make(view, "Latitude:"+locationInfo.getLatitude()
                            +" Longitude:"+locationInfo.getLongitude()
                            +" Altitude:"+locationInfo.getAltitude(),Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this
                , android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            FragmentManager fragmentManager = getFragmentManager();
            SettingsDialog settingsDialog = new SettingsDialog();
            settingsDialog.show(fragmentManager,"GPS frequency");
        } else if (id == R.id.nav_reset_database) {
            dbHelper.resetData();
        } else if (id == R.id.closeApplication) {
            stopRecording();
            finish();
            System.exit(0);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateUI() {
        mMap.clear();

        LocationInfo locationInfo = dbHelper.getLastLocInfo();
        LatLng currentCoors = new LatLng(locationInfo.getLatitude(), locationInfo.getLongitude());
        mMap.addMarker(new MarkerOptions().position(currentCoors).title("You are here, Speed: "+speed));
        if(currentCameraPosition==null){
            currentCameraPosition = CameraUpdateFactory.newLatLngZoom(currentCoors, 15);
            mMap.animateCamera(currentCameraPosition);
        }
        else{
            currentCameraPosition = CameraUpdateFactory.newLatLng(currentCoors);
            mMap.animateCamera(currentCameraPosition);
        }

        ArrayList <LocationInfo> locInfo;
        PolylineOptions lines=new PolylineOptions();
        if((locInfo=dbHelper.getAllRecords()).size()>0){
            Iterator <LocationInfo> itr = locInfo.iterator();
            LatLng latLng;
            int color;
            while(itr.hasNext()){
                locationInfo = itr.next();
                latLng = new LatLng(locationInfo.getLatitude(),locationInfo.getLongitude());
                switch (locationInfo.getActivityType()){
                    case "Driving": color=Color.RED;    break;
                    case "Cycling": color=Color.GREEN;  break;
                    case "On foot": color=Color.BLACK;  break;
                    case "Running": color=Color.DKGRAY; break;
                    case "Still"  : color=Color.CYAN;   break;
                    case "Tilting": color=Color.YELLOW; break;
                    case "Walking": color=Color.GRAY;   break;
                    default: color=Color.BLUE; break;
                }
                if(color!=colorPolyline){
                    lines.width(5)
                            .color(colorPolyline)
                            .geodesic(true)
                            .visible(true);
                    mMap.addPolyline(lines);
                    lines=new PolylineOptions();
                    colorPolyline=color;
                }
                lines.add(latLng);

            }
            lines.width(4)
                    .color(colorPolyline)
                    .geodesic(true)
                    .visible(true);
            mMap.addPolyline(lines);
        }

    }

    protected void onDestroy() {
        unregisterReceiver(batInfoReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationUpdatedReceiver);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationUpdatedReceiver);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        // This registers mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(locationUpdatedReceiver,
                        new IntentFilter("Location Updated"));
    }

    public void enableGPSDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Enable it?")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public void stopRecording(){
        stopService(new Intent(getBaseContext(), TrackMeBackground.class));
        nManager.cancel(0);
        Toast.makeText(getApplicationContext(),"Service stopped", Toast.LENGTH_LONG).show();
    }

    private void addNotification() {
        nBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("TrackMe on background")
                .setContentText("Running")
                .setOngoing(true);

        Intent notificationIntent = new Intent(this, MapsActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        nBuilder.setContentIntent(contentIntent);

        // Add as notification
        nManager.notify(0, nBuilder.build());
    }

    public void notifyMessage(String message){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setSmallIcon( R.mipmap.ic_launcher );
        builder.setContentTitle( getString( R.string.app_name ) );
        builder.setContentText( message);
        nManager.notify(1, builder.build());
    }

}
