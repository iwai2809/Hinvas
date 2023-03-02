package com.example.hinvas

import android.content.Context
import android.os.Build
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters

class DataCheckWorker(@NonNull context: Context, @NonNull params: WorkerParameters) :
    Worker(context, params) {

    private val fireStore = FireStore()

    companion object {
        //定数
//        private const val LOG_TAG = "worker_DataCheck"
        const val WORK_TAG = "DataCheckWorkerTAG"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @NonNull
    override fun doWork(): Result {

//        クォータ量制限から定期的に動画・配信を取得する処理をコメントアウト
//        fireStore.getUpdatedMovies()        

        return Result.success()
    }
}