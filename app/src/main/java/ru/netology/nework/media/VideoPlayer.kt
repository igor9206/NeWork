package ru.netology.nework.media

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer

class VideoPlayer(private val context: Context) {

    private var player: ExoPlayer? = null

    private fun initializePlayer() {
        player = ExoPlayer.Builder(context)
            .build()
//            .also { exoPlayer ->
//                viewBinding.videoView.player = exoPlayer
//            }
    }
}