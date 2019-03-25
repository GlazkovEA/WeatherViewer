package homounikumus1.com.data2.model.weather;

import io.realm.RealmObject;

public class Notify extends RealmObject {
    private boolean isShow;

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
}
