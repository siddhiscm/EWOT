package app.json.com.myjson.db;

import android.provider.BaseColumns;

public class DbContract {
    public static final class ProtocolEntry implements BaseColumns {

        public static final String TABLE_NAME="protocol";
        public static final String COLUMN_PHASE="Phase";
        public static final String COLUMN_ALTITUDE="Altitude";
        public static final String COLUMN_ACTIVITY="Activity";
        public static final String COLUMN_TIME="Time";

    }
}
