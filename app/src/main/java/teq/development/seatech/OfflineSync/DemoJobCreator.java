package teq.development.seatech.OfflineSync;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

public class DemoJobCreator implements JobCreator {


    @Override
    @Nullable
    public Job create(@NonNull String tag) {
        switch (tag) {
            case DemoSyncJob.TAG:
                return new DemoSyncJob();
            case SyncNeeddPart.TAG:
                return new SyncNeeddPart();
            default:
                return null;
        }
    }
}
