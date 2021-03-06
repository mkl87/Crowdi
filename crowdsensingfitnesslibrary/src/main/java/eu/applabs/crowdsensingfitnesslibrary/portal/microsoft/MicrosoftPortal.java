package eu.applabs.crowdsensingfitnesslibrary.portal.microsoft;

import android.app.Activity;
import android.content.Intent;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import eu.applabs.crowdsensingfitnesslibrary.FitnessLibrary;
import eu.applabs.crowdsensingfitnesslibrary.data.ActivityBucket;
import eu.applabs.crowdsensingfitnesslibrary.data.Person;
import eu.applabs.crowdsensingfitnesslibrary.data.StepBucket;
import eu.applabs.crowdsensingfitnesslibrary.portal.Portal;

public class MicrosoftPortal extends Portal {
    @Override
    public PortalType getPortalType() {
        return PortalType.Microsoft;
    }

    @Override
    public void login(Activity activity) {

    }

    @Override
    public void logout() {

    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public boolean checkActivityResult(int requestCode, int resultCode, Intent data) {
        return false;
    }

    @Override
    public void getPerson(int requestId) {
        notifyPersonReceived(FitnessLibrary.IFitnessLibraryListener.ExecutionStatus.Error, requestId, new Person());
    }

    @Override
    public void getSteps(long startTime, long endTime, TimeUnit rangeUnit, int duration, TimeUnit durationUnit, int requestId) {
        notifyStepsReceived(FitnessLibrary.IFitnessLibraryListener.ExecutionStatus.Error, requestId, new ArrayList<StepBucket>());
    }

    @Override
    public void getActivities(long startTime, long endTime, TimeUnit rangeUnit, int duration, TimeUnit durationUnit, int requestId) {
        notifyActivitiesReceived(FitnessLibrary.IFitnessLibraryListener.ExecutionStatus.Error, requestId, new ArrayList<ActivityBucket>());
    }
}
