package eu.applabs.crowdsensingapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import eu.applabs.crowdsensingapp.data.Constants;

public class MainActivity3 extends Activity implements View.OnClickListener {

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        init();
        Button b = (Button) findViewById(R.id.button);
        b.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button:
                sendNotification("Messung starten", "Herzfrequenzmessung starten");
                break;
        }
    }

    private void init() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                    }
                })
                .addApi(Wearable.API)
                .build();
    }

    private void sendNotification(String title, String content) {
        if(mGoogleApiClient.isConnected()) {
            PutDataMapRequest dataMapRequest = PutDataMapRequest.create(Constants.NOTIFICATION_PATH);
            dataMapRequest.getDataMap().putDouble(Constants.NOTIFICATION_TIMESTAMP, System.currentTimeMillis());
            dataMapRequest.getDataMap().putString(Constants.NOTIFICATION_TITLE, title);
            dataMapRequest.getDataMap().putString(Constants.NOTIFICATION_CONTENT, content);
            PutDataRequest putDataRequest = dataMapRequest.asPutDataRequest();
            Wearable.DataApi.putDataItem(mGoogleApiClient, putDataRequest);
        }
    }
}
