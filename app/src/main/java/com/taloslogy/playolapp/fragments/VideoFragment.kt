package com.taloslogy.playolapp.fragments

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import com.taloslogy.playolapp.R
import com.taloslogy.playolapp.utils.Decryptor
import kotlinx.android.synthetic.main.fragment_video.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.concurrent.thread

class VideoFragment : Fragment(), MediaPlayer.OnPreparedListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val position = arguments?.let { VideoFragmentArgs.fromBundle(it).lessonNumber }
        val name = arguments?.let { VideoFragmentArgs.fromBundle(it).lessonName }
        val subject = arguments?.let { VideoFragmentArgs.fromBundle(it).subject }
        lesson_number.text = (position!! + 1).toString()
        lesson_name.text = name!!

        thread { decryptVideo() }
    }

    private fun decryptVideo() {
        val keyFile = File("/storage/5E71-DBAD/key.talos")
        val decryptedVal = Decryptor().decryptKey(keyFile)
        val decryptKeys = String(decryptedVal!!, Charsets.UTF_8)
        val passphrase = decryptKeys.split('\n').first().trim()
        val iv = decryptKeys.split('\n').last()

        val pass1 = "wrmffxYB"
        val pass2 = "GPVSIrun"
        val iv1 = "d=Z~"
        val iv2 = "fGe+"

        val pChunk = passphrase.chunked(8)
        val ivChunk = iv.chunked(4)

        val pp = pass1 + pChunk.first() + pass2 + pChunk.last()
        val ivv = iv1 + ivChunk.first() + iv2 + ivChunk.last()

        val file = File("/storage/5E71-DBAD/science-11-t1-06-130kgt.mp4.talos")
        val decrypted = Decryptor().decryptVideoFile(pp, ivv, file)
        val path = getDataSource(decrypted)

        activity?.runOnUiThread { playVideo(path) }
    }

    private fun playVideo(path: String?) {
        try {
            videoView?.setOnPreparedListener { onPrepared(it) }
            val controller = MediaController(activity)
            controller.setAnchorView(videoView)
            videoView?.setMediaController(controller)
            videoView?.setVideoPath(path)
            videoView?.requestFocus()
            videoView?.start()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onPrepared(player: MediaPlayer?) {
        videoView?.setBackgroundColor(Color.TRANSPARENT)
    }

    @Throws(IOException::class)
    private fun getDataSource(array: ByteArray?): String? {
        val temp = File.createTempFile("test", ".mp4")
        temp.deleteOnExit()
        val out = FileOutputStream(temp)
        out.write(array)
        out.flush()
        out.close()
        return temp.absolutePath
    }

}