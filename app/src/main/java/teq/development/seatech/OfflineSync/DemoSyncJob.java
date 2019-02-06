package teq.development.seatech.OfflineSync;

import android.support.annotation.NonNull;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

public class DemoSyncJob extends Job {

    public static final String TAG = "job_demo_tag";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Log.e("background","backkkkkkkk");
        return Result.SUCCESS;
    }

    public static void scheduleJob() {
        new JobRequest.Builder(DemoSyncJob.TAG)
                .setBackoffCriteria(5L, JobRequest.BackoffPolicy.EXPONENTIAL)
                .setExact(10L)
                .build()
                .schedule();
    }

}
