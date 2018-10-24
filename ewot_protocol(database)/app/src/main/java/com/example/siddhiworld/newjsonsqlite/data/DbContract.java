package com.example.siddhiworld.newjsonsqlite.data;

import android.provider.BaseColumns;

public class DbContract {
    public static final class EwotEntry implements BaseColumns {

        public static final String TABLE_NAME="protocol";
        public static final String COLUMN_NAME="name";
        public static final String COLUMN_DESCRIPTION="description";
        public static final String COLUMN_TOTALDURATION="totalduration";
        public static final String COLUMN_NUMBEROFSTAGES="numberofstages";
        public static final String COLUMN_suitablePersonality="suitablePersonality";


        public static final String TABLE_NAME1="stages";
        public static final String COLUMN_NAME1="name";
        public static final String COLUMN_POSITIVEO2="positiveO2";
        public static final String COLUMN_NEGATIVEO2="negativeO2";
        public static final String COLUMN_ALTITUDE="altitude";
        public static final String COLUMN_TOTALDURATION1="totalDuration";
        public static final String COLUMN_METHOD="method";
        public static final String COLUMN_DESCRIPTION1="description";

    }
}
