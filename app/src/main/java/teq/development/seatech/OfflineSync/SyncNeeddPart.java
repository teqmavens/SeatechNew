package teq.development.seatech.OfflineSync;

import android.support.annotation.NonNull;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

public class SyncNeeddPart extends Job {

    public static final String TAG = "needpartjob_tag";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Log.e("backgroNEEDPART","backgroNEEDPART");
        return Result.SUCCESS;
    }

    public static void scheduleJob() {
        new JobRequest.Builder(SyncNeeddPart.TAG)
                .setBackoffCriteria(5L, JobRequest.BackoffPolicy.EXPONENTIAL)
                .setExact(10L)
                .build()
                .schedule();
    }
}
