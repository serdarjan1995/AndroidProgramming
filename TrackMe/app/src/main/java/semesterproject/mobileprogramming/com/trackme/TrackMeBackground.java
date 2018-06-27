package semesterproject.mobileprogramming.com.trackme;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;


public class TrackMeBackground extends Service implements LocationListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private DatabaseHelper dbHelper;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private LocationManager locationManager;
    private SharedPreferences sharedPref;
    private Context context;
    private String activityType="Unknown";
    private float speed;

    private BroadcastReceiver activityUpdatedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int value=intent.getIntExtra("activityType",5);
            switch (value){
                case 1 : activityType="Driving"; break;
                case 2 : activityType="Cycling"; break;
                case 3 : activityType="On foot"; break;
                case 4 : activityType="Running"; break;
                case 5 : activityType="Still";   break;
                case 6 : activityType="Tilting"; break;
                case 7 : activityType="Walking"; break;
                default: break;
            }
            notifyMessage("Activity type: "+activityType);
        }
    };

    public TrackMeBackground() {
    }

    public void onCreate(){
        super.onCreate();

        dbHelper=new DatabaseHelper(this);
        sharedPref = getSharedPreferences(getString(R.string.sharedName), Context.MODE_PRIVATE);
        if(sharedPref.contains("gpsFreq")){
            UPDATE_INTERVAL=1000*Integer.parseInt(sharedPref.getString("gpsFreq","1"));
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        context=getApplicationContext();
        LocalBroadcastManager.getInstance(this).registerReceiver(activityUpdatedReceiver,
                new IntentFilter("Activity Updated"));
        connectApiClient();
    }


    private void connectApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
    }

    public int onStartCommand(Intent intent, int flags, int startId){
        return Service.START_STICKY;
    }

    public void onDestroy(){
        super.onDestroy();
        Toast.makeText(getApplicationContext(),"Service destroyed", Toast.LENGTH_LONG).show();
        // only stop if it's connected, otherwise we crash
        if (googleApiClient != null) {

            // Disconnecting the client invalidates it.
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(activityUpdatedReceiver);
            googleApiClient.disconnect();
        }
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.cancelAll();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onLocationChanged(Location location) {
        dbHelper.addGeolocation(location.getAltitude(),location.getLongitude(),location.getLatitude(),
                DateFormat.getDateTimeInstance().format(new Date()),activityType);
        speed=location.getSpeed();
        sendMessage();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
        Intent intent = new Intent( this, ActivityRecognitionService.class );
        PendingIntent pendingIntent = PendingIntent.getService( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates( googleApiClient, 5000, pendingIntent );
    }

    protected void startLocationUpdates() {
        // Create the location request
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED ) {
            Toast.makeText(context,"Location access denied!", Toast.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Disconnected.", Toast.LENGTH_SHORT).show();
    }

    private void sendMessage() {
        Intent intent = new Intent("Location Updated");
        intent.putExtra("speed",speed);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void notifyMessage(String message){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setSmallIcon( R.mipmap.ic_launcher );
        builder.setContentTitle( getString( R.string.app_name ) + "| Activity detected" );
        builder.setContentText( message +"\nSpeed:"+speed+" m/s");
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(1, builder.build());
    }
}
