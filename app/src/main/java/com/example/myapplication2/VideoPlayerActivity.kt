package com.example.myapplication2

import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class VideoPlayerActivity : AppCompatActivity() {

    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val videoUrl = intent.getStringExtra("videoUrl")
        val title = intent.getStringExtra("title")
        val channelName = intent.getStringExtra("channelName")
        val likes = intent.getIntExtra("likes", 0)
        val description = intent.getStringExtra("description")

        val titleTextView = findViewById<TextView>(R.id.titleTextView)
        val channelNameTextView = findViewById<TextView>(R.id.channelNameTextView)
        val likesTextView = findViewById<TextView>(R.id.likesTextView)
        val descriptionTextView = findViewById<TextView>(R.id.descriptionTextView)

        titleTextView.text = title
        channelNameTextView.text = "Channel: ${channelName}"
        likesTextView.text = "Likes: ${likes}"
        descriptionTextView.text = description

        if (videoUrl != null) {
            initializePlayer(videoUrl)
        }
    }

    private fun initializePlayer(videoUrl: String) {
        player = ExoPlayer.Builder(this).build()
        playerView = findViewById(R.id.playerView)
        playerView.player = player

        val mediaItem = MediaItem.Builder()
            .setUri(Uri.parse(videoUrl))
            .build()

        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    override fun onPause() {
        super.onPause()
        player.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}
