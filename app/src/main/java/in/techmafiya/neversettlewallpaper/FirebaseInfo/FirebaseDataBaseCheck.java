package in.techmafiya.neversettlewallpaper.FirebaseInfo;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by saiso on 03-02-2017.
 */

public class FirebaseDataBaseCheck {

    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }
}
