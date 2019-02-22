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
            case SyncSubmitLaborPerf.TAG:
                return new SyncSubmitLaborPerf();
            case SyncNeedEstimate.TAG:
                return new SyncNeedEstimate();
            case SyncLCChange.TAG:
                return new SyncLCChange();
            case SyncJobStatus.TAG:
                return new SyncJobStatus();
            case SyncUploadImages.TAG:
                return new SyncUploadImages();
            case SyncAddPart.TAG:
                return new SyncAddPart();
            case SyncJobStart.TAG:
                return new SyncJobStart();
            default:
                return null;
        }
    }
}
