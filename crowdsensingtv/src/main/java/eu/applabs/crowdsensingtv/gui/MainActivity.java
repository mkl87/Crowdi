package eu.applabs.crowdsensingtv.gui;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;

import java.util.List;

import eu.applabs.crowdsensingfitnesslibrary.FitnessLibrary;
import eu.applabs.crowdsensinglibrary.Library;
import eu.applabs.crowdsensinglibrary.data.Command;
import eu.applabs.crowdsensinglibrary.data.Poll;
import eu.applabs.crowdsensingtv.R;
import eu.applabs.crowdsensingtv.data.Action;
import eu.applabs.crowdsensingtv.presenter.ActionPresenter;
import eu.applabs.crowdsensingtv.presenter.CommandPresenter;

public class MainActivity extends Activity implements
        OnItemViewClickedListener,
        Library.ILibraryResultListener {

    private static final String sClassName = MainActivity.class.getSimpleName();

    public static final String BASE_URL = "http://as.applabs.eu:8080/FancyModule/";
    public static final String START_URL = "http://as.applabs.eu:8080/FancyModule/start";

    private FragmentManager mFragmentManager = null;
    private BrowseFragment mBrowseFragment = null;
    private ArrayObjectAdapter mArrayObjectAdapter = null;
    private List<Command> mCommandList = null;

    private Library mLibrary = null;
    private FitnessLibrary mFitnessLibrary = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tvmain);

        mFragmentManager = getFragmentManager();
        mBrowseFragment = (BrowseFragment) mFragmentManager.findFragmentById(R.id.id_frag_TVMainActivity);

        mBrowseFragment.setHeadersState(BrowseFragment.HEADERS_ENABLED);
        mBrowseFragment.setBadgeDrawable(getDrawable(R.drawable.browse_logo));
        mBrowseFragment.setOnItemViewClickedListener(this);

        BackgroundManager backgroundManager = BackgroundManager.getInstance(this);
        backgroundManager.attach(this.getWindow());
        backgroundManager.setDrawable(getResources().getDrawable(R.drawable.background, null));

        mArrayObjectAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        mBrowseFragment.setAdapter(mArrayObjectAdapter);

        mLibrary = Library.getInstance();
        mLibrary.init(this);

        mFitnessLibrary = FitnessLibrary.getInstance();
        mFitnessLibrary.init(this);

        updateUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFitnessLibrary.checkActivityResult(requestCode, resultCode, data);
    }

    private void startPeriodicNotification() {
        BootCompletedReceiver ba = new BootCompletedReceiver();
        ba.onReceive(this, new Intent().setAction(Intent.ACTION_BOOT_COMPLETED));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mLibrary != null && mLibrary.accountAvailable()) {
            mLibrary.registerListener(this);
            mLibrary.loadCommands(START_URL, sClassName);

            startPeriodicNotification();
        } else {
            updateUI();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(mLibrary != null) {
            mCommandList = null;
            mLibrary.unregisterListener(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        if(item instanceof Action) {
            Action action = (Action) item;

            Intent intent = action.getIntent();

            if(intent != null) {
                startActivity(intent);
            }
        } else if (item instanceof Command) {
            Command command = (Command) item;

            Intent intent = new Intent(this, SinglePollActivity.class);
            Bundle extras = new Bundle();
            extras.putString(SinglePollActivity.EXTRA_URL, BASE_URL + command.getCommand());
            intent.putExtras(extras);

            startActivity(intent);
        }
    }

    public void updateUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mArrayObjectAdapter.clear();

                // Commands
                ArrayObjectAdapter accountAdapter = new ArrayObjectAdapter(new CommandPresenter());

                if(mCommandList != null) {
                    for(Command command : mCommandList) {
                        accountAdapter.add(command);
                    }
                }

                HeaderItem accountHeader = new HeaderItem(getString(R.string.MainActivity_Header_Polls));
                mArrayObjectAdapter.add(new ListRow(accountHeader, accountAdapter));

                // Settings
                ArrayObjectAdapter settingsAdapter = new ArrayObjectAdapter(new ActionPresenter());

                Action action = new Action(getApplicationContext());
                action.setTitle(getString(R.string.MainActivity_Action_ManageAccounts));
                action.setIcon(getApplicationContext().getDrawable(R.drawable.settings_manageaccounts));
                action.setIntent(new Intent(getApplicationContext(), ManageAccountsActivity.class));

                settingsAdapter.add(action);

                HeaderItem settingsHeader = new HeaderItem(getString(R.string.MainActivity_Header_Settings));
                mArrayObjectAdapter.add(new ListRow(settingsHeader, settingsAdapter));
            }
        });
    }

    @Override
    public void onLibraryResult(ExecutionStatus status, Poll poll, String className) {
        if(className.compareTo(sClassName) == 0) {

        }
    }

    @Override
    public void onLibraryResult(ExecutionStatus status, List<Command> list, String className) {
        if(className.compareTo(sClassName) == 0) {
            if (status == ExecutionStatus.Success) {
                mCommandList = list;

                updateUI();
            }
        }
    }
}
