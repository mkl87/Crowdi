package eu.applabs.crowdsensingtv.gui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import eu.applabs.crowdsensingtv.service.RecommendationService;
import eu.applabs.crowdsensingtv.service.UpnpService;

public class BootCompletedReceiver extends BroadcastReceiver {

    private static final String sClassName = BootCompletedReceiver.class.getSimpleName();
    private static final long sInitialDelay = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(sClassName, "BootCompletedReceiver called");

        if (intent.getAction().endsWith(Intent.ACTION_BOOT_COMPLETED)) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent recommendationIntent = new Intent(context, RecommendationService.class);
            PendingIntent alarmIntent = PendingIntent.getService(context, 0, recommendationIntent, 0);

            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    sInitialDelay,
                    AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                    alarmIntent);

            Intent upnpIntent = new Intent(context, UpnpService.class);
            context.startService(upnpIntent);
        }
    }
}
