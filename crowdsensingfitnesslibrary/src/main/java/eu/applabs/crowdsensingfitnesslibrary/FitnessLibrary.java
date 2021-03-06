package eu.applabs.crowdsensingfitnesslibrary;

import android.app.Activity;
import android.content.Intent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import eu.applabs.crowdsensingfitnesslibrary.data.ActivityBucket;
import eu.applabs.crowdsensingfitnesslibrary.data.Person;
import eu.applabs.crowdsensingfitnesslibrary.data.StepBucket;
import eu.applabs.crowdsensingfitnesslibrary.portal.Portal;
import eu.applabs.crowdsensingfitnesslibrary.portal.apple.ApplePortal;
import eu.applabs.crowdsensingfitnesslibrary.portal.fake.FakePortal;
import eu.applabs.crowdsensingfitnesslibrary.portal.google.GooglePortal;
import eu.applabs.crowdsensingfitnesslibrary.portal.microsoft.MicrosoftPortal;
import eu.applabs.crowdsensingfitnesslibrary.settings.SettingsManager;

public class FitnessLibrary implements Portal.IPortalListener{

    public interface IFitnessLibraryListener {
        enum ExecutionStatus {
            Undefined,
            Success,
            Error
        }

        void onPersonReceived(ExecutionStatus status, int requestId, Person person);
        void onStepsReceived(ExecutionStatus status, int requestId, List<StepBucket> list);
        void onActivitiesReceived(ExecutionStatus status, int requestId, List<ActivityBucket> list);

        void onPortalConnectionStateChanged();
    }

    private static final String sClassName = FitnessLibrary.class.getSimpleName();

    private static FitnessLibrary mInstance = null;
    private boolean mInitialized = false;

    private Activity mActivity = null;
    private SettingsManager mSettingsManager = null;
    private List<Portal> mPortalList = null;
    private List<IFitnessLibraryListener> mIFitnessLibraryListenerList = null;

    private FitnessLibrary() { }

    public static FitnessLibrary getInstance() {
        if(FitnessLibrary.mInstance == null) {
            FitnessLibrary.mInstance = new FitnessLibrary();
            FitnessLibrary.mInstance.mPortalList = new ArrayList<>();
            FitnessLibrary.mInstance.mIFitnessLibraryListenerList = new ArrayList<>();
        }

        return FitnessLibrary.mInstance;
    }

    public void init(Activity activity) {
        if(!mInitialized) {
            mActivity = activity;
            mSettingsManager = new SettingsManager(mActivity);

            mPortalList.add(new GooglePortal());
            mPortalList.add(new ApplePortal());
            mPortalList.add(new MicrosoftPortal());
            mPortalList.add(new FakePortal());

            List<Portal.PortalType> list = mSettingsManager.getConnectedServices();

            for(Portal.PortalType type : list) {
                connect(type);
            }
        }
    }

    public void connect(Portal.PortalType type) {
        connect(type, mActivity);
    }

    public boolean isConnected(Portal.PortalType type) {
        Portal portal = findPortal(type);

        if(portal != null) {
            return portal.isConnected();
        }

        return false;
    }

    public List<Portal.PortalType> getConnectedPortals() {
        List<Portal.PortalType> list = new ArrayList<>();

        if(isConnected(Portal.PortalType.Google)) {
            list.add(Portal.PortalType.Google);
        }

        if(isConnected(Portal.PortalType.Apple)) {
            list.add(Portal.PortalType.Apple);
        }

        if(isConnected(Portal.PortalType.Microsoft)) {
            list.add(Portal.PortalType.Microsoft);
        }

        if(isConnected(Portal.PortalType.Fake)) {
            list.add(Portal.PortalType.Fake);
        }

        return list;
    }

    public void connect(Portal.PortalType type, Activity activity) {
        Portal portal = findPortal(type);

        if(portal != null) {
            portal.registerListener(this);
            portal.login(activity);
        }
    }

    public void disconnect(Portal.PortalType type) {
        Portal portal = findPortal(type);

        if(portal != null) {
            portal.unregisterListener(this);
            portal.logout();
        }
    }

    public void checkActivityResult(int requestCode, int resultCode, Intent data) {
        for(Portal portal : mPortalList) {
            if(portal.checkActivityResult(requestCode, resultCode, data)) {
                return;
            }
        }
    }

    public void registerListener(IFitnessLibraryListener listener) {
        mIFitnessLibraryListenerList.add(listener);
    }

    public void unregisterListener(IFitnessLibraryListener listener) {
        mIFitnessLibraryListenerList.remove(listener);
    }

    public void getPerson(Portal.PortalType type, int requestId) {
        Portal portal = findPortal(type);

        if(portal != null) {
            portal.getPerson(requestId);
        }
    }

    public void getSteps(Portal.PortalType type,
                         long startTime,
                         long endTime,
                         TimeUnit rangeUnit,
                         int duration,
                         TimeUnit durationUnit,
                         int requestId) {
        Portal portal = findPortal(type);

        if(portal != null) {
            portal.getSteps(startTime, endTime, rangeUnit, duration, durationUnit, requestId);
        }
    }

    public void getActivities(Portal.PortalType type,
                                 long startTime,
                                 long endTime,
                                 TimeUnit rangeUnit,
                                 int duration,
                                 TimeUnit durationUnit,
                                 int requestId) {
        Portal portal = findPortal(type);

        if(portal != null) {
            portal.getActivities(startTime, endTime, rangeUnit, duration, durationUnit, requestId);
        }
    }

    private Portal findPortal(Portal.PortalType type) {
        for(Portal portal : mPortalList) {
            if(portal.getPortalType() == type) {
                return portal;
            }
        }

        return null;
    }

    @Override
    public void onPersonReceived(IFitnessLibraryListener.ExecutionStatus status, int requestId, Person person) {
        for(IFitnessLibraryListener listener : mIFitnessLibraryListenerList) {
            listener.onPersonReceived(status, requestId, person);
        }
    }

    @Override
    public void onStepsReceived(IFitnessLibraryListener.ExecutionStatus status, int requestId, List<StepBucket> list) {
        for(IFitnessLibraryListener listener : mIFitnessLibraryListenerList) {
            listener.onStepsReceived(status, requestId, list);
        }
    }

    @Override
    public void onActivitiesReceived(IFitnessLibraryListener.ExecutionStatus status, int requestId, List<ActivityBucket> list) {
        for(IFitnessLibraryListener listener : mIFitnessLibraryListenerList) {
            listener.onActivitiesReceived(status, requestId, list);
        }
    }

    @Override
    public void onPortalConnectionStateChanged() {
        for(IFitnessLibraryListener listener : mIFitnessLibraryListenerList) {
            listener.onPortalConnectionStateChanged();
        }
    }
}
