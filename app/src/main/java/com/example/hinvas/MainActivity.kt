package com.example.hinvas

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.work.*
import com.google.android.material.navigation.NavigationBarView
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private val fireStore = FireStore()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startWork()

        setupWithNavController(
            findViewById<NavigationBarView>(R.id.bottom_navigation),
            findNavController(R.id.nav_host_fragment)
        )
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        setNavigationBarViewHeight(findViewById<NavigationBarView>(R.id.bottom_navigation).height)
    }


    // 共有から送られてきたURLを取得
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (intent?.action == Intent.ACTION_VIEW) {
            val uriStr = intent.data.toString()
            //UriからAccessTokenの抽出
            val accessToken = uriStr.substring(uriStr.indexOf('=') + 1, uriStr.indexOf('&'))
            Log.d("TwitchAPI", "アクセストークンを取得")
            //データベースでの保存
            fireStore.addAccessToken(accessToken, "Twitch")
        }

        // 共有から送られてきたURLを取得
        if (intent?.action == Intent.ACTION_SEND) {
            val activityIntent = Intent(this, StreamerAddActivity::class.java)
            activityIntent.putExtra("url", intent.getStringExtra(Intent.EXTRA_TEXT).toString())
            Log.d("main", "activity")
            startActivity(activityIntent)
        }
    }


    /* Workerを起動 */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun startWork() {

        val manager = WorkManager.getInstance(this)
        if (isWorkerEnqueued(manager)) {
            Log.d(DataCheckWorker.WORK_TAG, "裏画面処理を行っています")
            return
        }

        // 定期的に動画・配信を取得する処理のスタート時間を設定
        fireStore.addUpdatedTime()

        //制約（起動条件）を設定
        val constraints: Constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        //WorkRequestの作成
        // PeriodicWorkRequestを使って20分毎に定期実行
        val request = PeriodicWorkRequest.Builder(
            DataCheckWorker::class.java, 15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag(DataCheckWorker.WORK_TAG)
            .build()

        //キューに登録（スケジューリング）
        manager.enqueue(request)
        Log.d("Worker", "裏画面処理を設定")
    }

    /* Workerを停止 */
    private fun stopWork() {
        val manager = WorkManager.getInstance(this)
        manager.cancelAllWorkByTag(DataCheckWorker.WORK_TAG)
        Log.d("Worker", "裏画面処理を停止")
    }

    /* ---------------------------------------------
        Workerの状態を取得する
        引数：workManager = WorKManagerのインスタンス
            tag = タグ
        戻値：true = 稼働中
            false = 非稼働
    ----------------------------------------------- */
    private fun isWorkerEnqueued(workManager: WorkManager): Boolean {
        val future: ListenableFuture<List<WorkInfo>> =
            workManager.getWorkInfosByTag(DataCheckWorker.WORK_TAG)
        try {
            for (workInfo in future.get()) {
                //workInfoからstateを取得して状態をチェック
                if (workInfo.state != WorkInfo.State.CANCELLED) {
                    //CANCELLED以外だったら稼働中とみなす
                    return true
                }
            }
        } catch (e: ExecutionException) {
            e.printStackTrace()
            return false
        } catch (e: InterruptedException) {
            e.printStackTrace()
            return false
        }
        return false
    }

}
