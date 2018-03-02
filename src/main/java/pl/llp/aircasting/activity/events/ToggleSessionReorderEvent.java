package pl.llp.aircasting.activity.events;

/**
 * Created by radek on 14/11/17.
 */
public class ToggleSessionReorderEvent {
    private boolean sessionsCleared;

    public ToggleSessionReorderEvent(boolean sessionsCleared) {
        this.sessionsCleared = sessionsCleared;
    }

    public boolean areSessionsCleared() {
        return sessionsCleared;
    }
}
