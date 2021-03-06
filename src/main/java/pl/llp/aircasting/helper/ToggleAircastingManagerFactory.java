package pl.llp.aircasting.helper;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;
import com.google.inject.Inject;
import pl.llp.aircasting.model.CurrentSessionSensorManager;
import pl.llp.aircasting.model.CurrentSessionManager;

/**
 * Created by radek on 13/07/17.
 */
public class ToggleAircastingManagerFactory {
    @Inject CurrentSessionManager currentSessionManager;
    @Inject SettingsHelper settingsHelper;
    @Inject LocationHelper locationHelper;
    @Inject CurrentSessionSensorManager currentSessionSensorManager;
    @Inject Context context;
    @Inject DashboardChartManager dashboardChartManager;

    public ToggleAircastingManager getAircastingHelper(Activity activity, AppCompatDelegate delegate) {
        return new ToggleAircastingManager(activity,
                currentSessionManager,
                settingsHelper,
                currentSessionSensorManager,
                locationHelper,
                delegate,
                context,
                dashboardChartManager);
    }
}
