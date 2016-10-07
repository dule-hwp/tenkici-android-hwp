package tankgame.android;

/**
 * Created by dusan_cvetkovic on 11/13/15.
 */
public interface JoystickMovedListener {
    void OnMoved(double angle, double radius, double hypo);

    void OnReleased();
}
