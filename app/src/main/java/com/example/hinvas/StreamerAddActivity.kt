package com.example.hinvas

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class StreamerAddActivity : AppCompatActivity() {

    private val fireStore = FireStore()
    private val youtubeApi = YoutubeApi()

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_streamer_add)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // 動画投稿者・配信登録画面に移動した時に、直前の登録者をIDをすべて取得する
        val subscribersList = fireStore.getSubscribersDoc() // DBからlist[Subscriber1(),Subscriber2()..]の形式で格納
        val subscriberIdList = ArrayList<String>() // subscriberIdだけを格納する配列を用意する
        for (subscriber in subscribersList) {
            subscriberIdList.add(subscriber.subscriberId)
        }

        //テキスト入力
        val  addStreamerText: EditText = findViewById(R.id.add_text_streamer)

        // 共有から送られてきたURLをeditTextにセット
        val shareChannelId = intent.getStringExtra("url").toString()
        if(shareChannelId != "null") addStreamerText.text.append(shareChannelId)

        // 追加ボタン押下時トースト表示
        val addButton: Button = findViewById(R.id.add_streamer_btn)
        addButton.setOnClickListener {

            val inputSubscriberUrl = addStreamerText.text.toString() // EditTextに入力されているID
            var inputSubscriberId = "" // URLから抽出したチャンネルIDを格納する変数
            var isExistId = false // DBにinputSubscriberIDと同じIDが存在しているかどうかの判定

            // youtubeのチャンネルURLの正規表現
            // 共通事項
            // 最初の【^(http(s)?://(www.)?)?youtube.com/】の関して：https://www.youtube.com/, http://www.youtube.com/, https://youtube.com/, http://youtube.com/, youtube.com/ の5パターンのみを検知する
            // デフォルトのチャンネルURLの場合はchannel/,カスタムURLの場合は@/, c/, users/の3パターンのみを検知する
            // 最後の[-._0-9a-zA-Z]{3,30}$に関して：youtubeのchannelIDは英数字（A～Z、a～z、0～9）とアンダースコア（_）、ハイフン（-）、ピリオド（.）で構成されており、3~30字の文字数制限に対応したidを検知する
            val youtubeDefaultChannelUrlRegex = Regex("^(http(s)?://(www.)?)?youtube.com/channel/[-._0-9a-zA-Z]{3,30}$")
            val youtubeCustomChannelURlRegex = Regex("^(http(s)?://(www.)?)?youtube.com/(@|c/|user/)[-._0-9a-zA-Z]{3,30}$")

            // デフォルトのURLかカスタムURLの場合はそれぞれ異なるの処理を行い、そうでない形式が入力された場合は一連の処理を終了する
            if (youtubeDefaultChannelUrlRegex.matches(inputSubscriberUrl)) { // デフォルトのチャンネルURLのときの処理
                val splitterRegex = Regex("^(http(s)?://(www.)?)?youtube.com/channel/") // splitメソッドの引数
                inputSubscriberId = inputSubscriberUrl.split(splitterRegex)[1] // チャンネルIDだけ抽出

            } else if (youtubeCustomChannelURlRegex.matches(inputSubscriberUrl)) { // カスタムチャンネルURLのときの処理
                val data = scope.launch {
                    inputSubscriberId = youtubeApi.customUrlConverter(inputSubscriberUrl) // カスタムURLを変換し、チャンネルIDを抽出
                }
                runBlocking {
                    data.join()
                }

            } else { // 想定されるURLの形式で入力されない場合
                Toast.makeText(this, "正しいチャンネルURLを入力してください", Toast.LENGTH_SHORT).show()
                addStreamerText.text.clear()
                return@setOnClickListener
            }

            // すでにDBに登録しているかをチェック
            for (subscriberId in subscriberIdList) {
                if (subscriberId == inputSubscriberId) {
                    isExistId = true
                }
            }

            // 入力されたチャンネルがDBに登録されていなければ登録処理を行う
            if(!isExistId) {
                fireStore.addYoutubeChannel(inputSubscriberId) // DBに登録する
                Toast.makeText(this, "追加されました", Toast.LENGTH_SHORT).show()
                subscriberIdList.add(inputSubscriberId)
                addStreamerText.text.clear()
            } else {
                Toast.makeText(this, "入力されたチャンネルはすでに登録されています", Toast.LENGTH_SHORT).show()
                addStreamerText.text.clear()
            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var returnVal = true

        if (item.itemId == android.R.id.home) {
            finish()
        } else {
            returnVal = super.onOptionsItemSelected(item)
        }
        return returnVal
    }
}