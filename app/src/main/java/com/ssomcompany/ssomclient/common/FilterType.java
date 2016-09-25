package com.ssomcompany.ssomclient.common;

import android.content.Context;

import com.ssomcompany.ssomclient.BaseApplication;
import com.ssomcompany.ssomclient.R;

public enum FilterType {
    twentyEarly("20") {
        @Override
        public String getTitle() {
            return context.getString(R.string.write_age_20_early);
        }

        @Override
        public String getValue() {
            return this.value;
        }
    },
    twentyMiddle("25") {
        @Override
        public String getTitle() {
            return context.getString(R.string.write_age_20_middle);
        }

        @Override
        public String getValue() {
            return this.value;
        }
    },
    twentyLate("29") {
        @Override
        public String getTitle() {
            return context.getString(R.string.write_age_20_late);
        }

        @Override
        public String getValue() {
            return this.value;
        }
    },
    thirtyOver("30") {
        @Override
        public String getTitle() {
            return context.getString(R.string.write_age_30_all);
        }

        @Override
        public String getValue() {
            return this.value;
        }
    },
    onePerson("1") {
        @Override
        public String getTitle() {
            return context.getString(R.string.filter_people_1);
        }

        @Override
        public String getValue() {
            return this.value;
        }
    },
    twoPeople("2") {
        @Override
        public String getTitle() {
            return context.getString(R.string.filter_people_2);
        }

        @Override
        public String getValue() {
            return this.value;
        }
    },
    threePeople("3") {
        @Override
        public String getTitle() {
            return context.getString(R.string.filter_people_3);
        }

        @Override
        public String getValue() {
            return this.value;
        }
    },
    fourPeople("4") {
        @Override
        public String getTitle() {
            return context.getString(R.string.filter_people_4_n_over);
        }

        @Override
        public String getValue() {
            return this.value;
        }
    };
    public String value;
    private static Context context = BaseApplication.getInstance().getApplicationContext();

    public abstract String getTitle();
    public abstract String getValue();

    FilterType(String value) {
        this.value = value;
    }
}
