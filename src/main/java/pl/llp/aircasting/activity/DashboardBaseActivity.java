package pl.llp.aircasting.activity;

import android.os.AsyncTask;
import pl.llp.aircasting.Intents;
import pl.llp.aircasting.R;
import pl.llp.aircasting.helper.LocationHelper;
import pl.llp.aircasting.helper.SettingsHelper;
import pl.llp.aircasting.helper.ToggleAircastingManager;
import pl.llp.aircasting.helper.ToggleAircastingManagerFactory;
import pl.llp.aircasting.model.CurrentSessionManager;
import pl.llp.aircasting.model.CurrentSessionSensorManager;
import pl.llp.aircasting.model.Session;
import pl.llp.aircasting.receiver.SyncBroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import pl.llp.aircasting.storage.UnfinishedSessionChecker;

import java.util.concurrent.TimeUnit;

/**
 * A common superclass for activities that want to display left/right
 * navigation arrows
 */
public abstract class DashboardBaseActivity extends RoboActivityWithProgress {
    @Inject Context context;
    @Inject EventBus eventBus;
    @Inject CurrentSessionManager currentSessionManager;
    @Inject CurrentSessionSensorManager currentSessionSensorManager;
    @Inject LocationHelper locationHelper;
    @Inject SettingsHelper settingsHelper;
    @Inject UnfinishedSessionChecker checker;
    @Inject ApplicationState state;
    @Inject ToggleAircastingManagerFactory aircastingHelperFactory;
    @Inject SyncBroadcastReceiver syncBroadcastReceiver;
    SyncBroadcastReceiver registeredReceiver;

    private ToggleAircastingManager toggleAircastingManager;
    private boolean initialized = false;

    @Override
    protected void onResume() {
        super.onResume();

        initialize();

        if (currentSessionSensorManager.anySensorConnected()) {
            Intents.startSensors(context);
        }

        registerReceiver(syncBroadcastReceiver, SyncBroadcastReceiver.INTENT_FILTER);
        registeredReceiver = syncBroadcastReceiver;

        eventBus.register(this);
        checkForUnfinishedSessions();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (currentSessionSensorManager.anySensorConnected()) {
            Intents.stopSensors(context);
        }

        locationHelper.stop();

        if (registeredReceiver != null) {
            unregisterReceiver(syncBroadcastReceiver);
            registeredReceiver = null;
        }
        eventBus.unregister(this);
    }

    private void initialize() {
        toggleAircastingManager = aircastingHelperFactory.getAircastingHelper(this, getDelegate());

        if (!initialized) {
            initialized = true;
        }
    }

    public synchronized void toggleAirCasting() {
        toggleAircastingManager.toggleAirCasting();
        getDelegate().invalidateOptionsMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Intents.SAVE_DIALOG:
                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                break;
            case Intents.EDIT_SESSION:
                if (resultCode == R.id.save_button) {
                    Session session = Intents.editSessionResult(data);
                    currentSessionManager.updateSession(session);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void checkForUnfinishedSessions() {
        if (shouldCheckForUnfinishedSessions()) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    checker.finishIfNeeded(DashboardBaseActivity.this);
                    return null;
                }
            }.execute();
        }
    }

    private boolean shouldCheckForUnfinishedSessions() {
        if (currentSessionManager.isSessionRecording()) {
            return false;
        }

        if (state.saving().isSaving()) {
            return false;
        }

        return true;
    }
}
