package eu.applabs.crowdsensingtv.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;

import eu.applabs.crowdsensinglibrary.ILibraryResultListener;
import eu.applabs.crowdsensinglibrary.Library;
import eu.applabs.crowdsensinglibrary.data.Command;
import eu.applabs.crowdsensinglibrary.data.Poll;
import eu.applabs.crowdsensingtv.R;

public class MainActivity extends Activity implements ILibraryResultListener, View.OnClickListener {

    public static final String BASE_URL = "https://www.applabs.eu/";

    private MainActivity mActivity = null;

    private LinearLayout mLinearLayout = null;
    private List<Command> mCommandList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = this;
        mLinearLayout = (LinearLayout) findViewById(R.id.id_MainActivity_LL_Commands);

        Library library = new Library();
        library.registerListener(this);
        library.loadCommands(BASE_URL + "start.txt");

        startPeriodicNotification();
    }

    @Override
    public void onLibraryResult(final ExecutionStatus status, final Poll poll) {

    }

    @Override
    public void onLibraryResult(final ExecutionStatus status, final List<Command> list) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(status == ExecutionStatus.Success) {
                    mCommandList = list;

                    for(int i = 0; i < mCommandList.size(); ++i) {
                        Command command = mCommandList.get(i);

                        Button b = new Button(getApplicationContext());
                        b.setText(command.getInfo());
                        b.setId(command.getId());
                        b.setOnClickListener(mActivity);

                        if(i == 0) {
                            b.requestFocus();
                        }

                        mLinearLayout.addView(b);
                    }
                } else {
                    Toast.makeText(mActivity, "Es konnte keine Verbindung zum Server" +
                            " aufgebaut werden...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(mCommandList != null) {
            for(Command command : mCommandList) {
                if(command.getId() == v.getId()) {
                    Intent intent = new Intent(this, SinglePollActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString(SinglePollActivity.EXTRA_URL, BASE_URL + command.getCommand());
                    intent.putExtras(extras);

                    startActivity(intent);
                }
            }
        }
    }

    private void startPeriodicNotification() {
        BootupActivity ba = new BootupActivity();
        ba.onReceive(this, new Intent().setAction(Intent.ACTION_BOOT_COMPLETED));
    }
}
