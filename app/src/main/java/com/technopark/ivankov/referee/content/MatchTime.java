package com.technopark.ivankov.referee.content;

import android.os.Vibrator;
import android.widget.Chronometer;

public class MatchTime {
        final private int vibrateTime = 500;
        private static Chronometer chronometer;
        private static Chronometer additionalChronometer;
        private Vibrator vibrator;
        private long timeWhenStopped = 0L;
        private long timeWhenAdditionalStopped = 0L;
        private boolean additional = false;
        private boolean stopped = true;
}
