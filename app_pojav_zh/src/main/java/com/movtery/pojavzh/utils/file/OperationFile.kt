package com.movtery.pojavzh.utils.file;

import static net.kdt.pojavlaunch.Tools.runOnUiThread;

import android.content.Context;

import com.movtery.pojavzh.ui.dialog.ProgressDialog;
import com.movtery.pojavzh.utils.ZHTools;

import net.kdt.pojavlaunch.PojavApplication;
import net.kdt.pojavlaunch.R;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

public class OperationFile {
    private final Context context;
    private final Runnable runnable;
    private final OperationFileFunction operationFileFunction;
    private Future<?> currentTask;
    private Timer timer;

    public OperationFile(Context context, Runnable runnable, OperationFileFunction operationFileFunction) {
        this.context = context;
        this.runnable = runnable;
        this.operationFileFunction = operationFileFunction;
    }

    public void operationFile(List<File> selectedFiles) {
        runOnUiThread(() -> {
            ProgressDialog dialog = new ProgressDialog(context, () -> {
                cancelTask();
                return true;
            });
            dialog.updateText(context.getString(R.string.zh_file_operation_file, "0B", "0B", 0));

            AtomicLong totalFileSize = new AtomicLong(0);
            AtomicLong fileSize = new AtomicLong(0);
            AtomicLong fileCount = new AtomicLong(0);

            currentTask = PojavApplication.sExecutorService.submit(() -> {
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(() -> dialog.updateText(context.getString(
                                R.string.zh_file_operation_file,
                                ZHTools.formatFileSize(fileSize.get()),
                                ZHTools.formatFileSize(totalFileSize.get()),
                                fileCount.get())));
                    }
                }, 0, 80);

                runOnUiThread(dialog::show);

                List<File> preDeleteFiles = new ArrayList<>();
                selectedFiles.forEach(selectedFile -> {
                    if (currentTask.isCancelled()) {
                        return;
                    }

                    fileSize.addAndGet(FileUtils.sizeOf(selectedFile));

                    if (selectedFile.isDirectory()) {
                        Collection<File> allFiles = FileUtils.listFiles(selectedFile, null, true);
                        allFiles.forEach(file1 -> {
                            if (currentTask.isCancelled()) {
                                return;
                            }
                            fileCount.addAndGet(1);
                            preDeleteFiles.add(file1);
                        });
                    }

                    fileCount.addAndGet(1);
                    preDeleteFiles.add(selectedFile);
                });
                totalFileSize.set(fileSize.get());

                preDeleteFiles.forEach(file -> {
                    if (currentTask.isCancelled()) {
                        return;
                    }
                    fileSize.addAndGet(-FileUtils.sizeOf(file));
                    fileCount.getAndDecrement();
                    operationFileFunction.operationFile(file);
                });
                runOnUiThread(dialog::dismiss);
                timer.cancel();
                finish();
            });
        });
    }

    private void cancelTask() {
        if (currentTask != null && !currentTask.isDone()) {
            currentTask.cancel(true);
            if (timer != null) {
                timer.cancel();
            }
            finish();
        }
    }

    private void finish() {
        if (runnable != null) {
            PojavApplication.sExecutorService.execute(runnable);
        }
    }

    public interface OperationFileFunction {
        void operationFile(File file);
    }
}
