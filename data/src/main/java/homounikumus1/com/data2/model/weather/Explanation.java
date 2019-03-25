package homounikumus1.com.data2.model.weather;

import io.realm.RealmObject;

public class Explanation extends RealmObject {
    private boolean explanation;

    public boolean isExplanation() {
        return explanation;
    }

    public void setExplanation(boolean explanation) {
        this.explanation = explanation;
    }
}
