package semesterproject.mobileprogramming.com.trackme;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;


public class ActivityRecognitionService extends IntentService {

    public ActivityRecognitionService() {
        super("ActivityRecognizedService");
    }

    public ActivityRecognitionService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities( result.getProbableActivities() );
        }
    }

    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
        for( DetectedActivity activity : probableActivities ) {
            switch( activity.getType() ) {
                case DetectedActivity.IN_VEHICLE: {
                    if( activity.getConfidence() >= 80 ) {
                        sendMessage(1);
                    }
                    break;
                }
                case DetectedActivity.ON_BICYCLE: {
                    if( activity.getConfidence() >= 75 ) {
                        sendMessage(2);
                    }
                    break;
                }
                case DetectedActivity.ON_FOOT: {
                    if( activity.getConfidence() >= 60 ) {
                        sendMessage(3);
                    }
                    break;
                }
                case DetectedActivity.RUNNING: {
                    if( activity.getConfidence() >= 70 ) {
                        sendMessage(4);
                    }
                    break;
                }
                case DetectedActivity.STILL: {
                    if( activity.getConfidence() >= 70 ) {
                        sendMessage(5);
                    }
                    break;
                }
                case DetectedActivity.TILTING: {
                    if( activity.getConfidence() >= 70 ) {
                        sendMessage(6);
                    }
                    break;
                }
                case DetectedActivity.WALKING: {
                    if( activity.getConfidence() >= 70 ) {
                        sendMessage(7);
                    }
                    break;
                }
                case DetectedActivity.UNKNOWN: {
                    break;
                }
            }
        }
    }


    private void sendMessage(int value) {
        Intent intent = new Intent("Activity Updated");
        intent.putExtra("activityType",value);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}