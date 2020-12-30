package com.taloslogy.playolapp.fragments

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.taloslogy.playolapp.BuildConfig
import com.taloslogy.playolapp.R
import com.taloslogy.playolapp.utils.ByteMagic
import com.taloslogy.playolapp.utils.Decryptor
import com.taloslogy.playolapp.utils.FileUtils
import com.taloslogy.playolapp.utils.StringUtils
import kotlinx.android.synthetic.main.fragment_video.*
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.concurrent.thread

class VideoFragment : Fragment(), MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
MediaPlayer.OnErrorListener {

    private var isPrepared = false
    private var isFullScreen = false
    private var isPaused = false

    private val fileUtils: FileUtils = FileUtils()
    private lateinit var playFile: File

    private var position: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Do custom work here
                    if(isFullScreen) {
                        isFullScreen = false
                        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    }
                    else{
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            }
            )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        position = arguments?.let { VideoFragmentArgs.fromBundle(it).lessonNumber }
        val path = arguments?.let { VideoFragmentArgs.fromBundle(it).subject }

        val files = fileUtils.getFilesFromPath(path!!, onlyFolders = false)
        val json = fileUtils.readFileText(StringUtils.getJsonFileName, requireActivity())
        val jsonObject = JSONObject(json)

        thread { if(!isPrepared) decryptVideo(files, path, jsonObject) }

        play_pause_btn.setOnClickListener {
            if(isPrepared){
                if(videoView.isPlaying) {
                    play_pause_btn.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_play_icon)
                    setMargins(play_pause_btn, 7, 4, 3, 4)
                    videoView.pause()
                }
                else {
                    play_pause_btn.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_pause)
                    setMargins(play_pause_btn, 5, 4, 5, 4)
                    videoView.start()
                }
            }
        }

        fullscreen_btn.setOnClickListener {
            if(isPrepared){
                isFullScreen = true
                enterFullScreen()
            }
        }

        back_btn.setOnClickListener {
            if(isPrepared){
                if(position!! != 0){
                    isPrepared = false
                    play_pause_btn.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_play_icon)
                    setMargins(play_pause_btn, 7, 4, 3, 4)
                    videoView?.stopPlayback()
                    videoView?.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                    playFile.delete()
                    position = position!! - 1
                    thread { decryptVideo(files, path, jsonObject) }
                }
                else{
                    Toast.makeText(activity,
                        requireActivity().resources.getText(R.string.first_video_toast),
                        Toast.LENGTH_SHORT).show()
                }
            }
        }

        next_btn.setOnClickListener {
            if(isPrepared){
                if(position!! != files.size-1){
                    isPrepared = false
                    play_pause_btn.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_play_icon)
                    setMargins(play_pause_btn, 7, 4, 3, 4)
                    videoView?.stopPlayback()
                    videoView?.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                    playFile.delete()
                    position = position!! + 1
                    thread { decryptVideo(files, path, jsonObject) }
                }
                else{
                    Toast.makeText(activity,
                        requireActivity().resources.getText(R.string.last_video_toast),
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun enterFullScreen() {
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

    }

    private fun decryptVideo(files: List<File>, videoPath: String, fileNames: JSONObject) {

        activity?.runOnUiThread {
            videoView?.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            decrypt_loader?.visibility = View.VISIBLE
        }

        val name = files[position!!].name

        activity?.runOnUiThread {
            lesson_number.text = (position!! + 1).toString()
            lesson_name.text = if (fileNames.has(name.dropLast(10)))
                if(fileNames.getString(name.dropLast(10)).isNotEmpty())
                    fileNames.getString(name.dropLast(10))
                else name.dropLast(10)
            else name.dropLast(10)
        }

        val keyFile = File("${StringUtils.getCoursePath}/${StringUtils.getKeyFileName}")
        val decryptedVal = Decryptor().decryptKey(keyFile)
        val decryptKeys = String(decryptedVal!!, Charsets.UTF_8)
        val passphrase = decryptKeys.split('\n').first().trim()
        val iv = decryptKeys.split('\n').last()

        val pass1 = ByteMagic(resources.getString(R.string.ssk1)).str
        val iv1 = ByteMagic(BuildConfig.SSIV2).str
        val pass2 = ByteMagic(BuildConfig.SSK2).str
        val iv2 = ByteMagic(resources.getString(R.string.ssiv1)).str

        val pChunk = passphrase.chunked(8)
        val ivChunk = iv.chunked(4)

        val pp = pass1 + pChunk.first() + pass2 + pChunk.last()
        val ivv = iv1 + ivChunk.first() + iv2 + ivChunk.last()

        val file = File("$videoPath/$name")
        val decrypted = Decryptor().decryptVideoFile(pp, ivv, file)
        getDataSource(decrypted)

        activity?.runOnUiThread {
            playVideo()
            decrypt_loader?.visibility = View.GONE
        }
    }

    private fun playVideo() {
        try {
            videoView?.setOnPreparedListener { onPrepared(it) }
            videoView?.setOnCompletionListener { onCompletion(it) }
            videoView?.setOnErrorListener { mp, i, j -> onError(mp, i,j) }
            val controller = MediaController(activity)
            controller.setAnchorView(videoView)
            videoView?.setMediaController(controller)
            videoView?.setVideoPath(playFile.absolutePath)
            videoView?.requestFocus()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onPrepared(player: MediaPlayer?) {
        isPrepared = true
        videoView?.setBackgroundColor(Color.TRANSPARENT)
        player?.start()
        play_pause_btn?.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_pause)
        setMargins(play_pause_btn!!, 5, 4, 5, 4)
    }

    override fun onCompletion(player: MediaPlayer?) {
        play_pause_btn.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_play_icon)
        setMargins(play_pause_btn, 7, 4, 3, 4)
    }

    override fun onError(player: MediaPlayer?, p1: Int, p2: Int): Boolean {
        return true
    }

    @Throws(IOException::class)
    private fun getDataSource(array: ByteArray?) {
        playFile = File.createTempFile("test", ".mp4")
        val out = FileOutputStream(playFile)
        out.write(array)
        out.flush()
        out.close()
    }

    private fun setMargins(
        view: View,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        if (view.layoutParams is MarginLayoutParams) {
            val p = view.layoutParams as MarginLayoutParams
            p.setMargins(left, top, right, bottom)
            view.requestLayout()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            top_image.visibility = View.GONE
            subject_content.visibility = View.GONE
            media_controls.visibility = View.GONE

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT
            )
            video_content.layoutParams = params
        }
        else {
            top_image.visibility = View.VISIBLE
            subject_content.visibility = View.VISIBLE
            media_controls.visibility = View.VISIBLE

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                getPxFromDp(450f)
            )
            video_content.layoutParams = params
            setMargins(video_content, 0, 40, 0, 40)
            setButtonControls()
        }

    }

    private fun setButtonControls() {
        if(videoView.isPlaying) {
            play_pause_btn.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_pause)
            setMargins(play_pause_btn, 5, 4, 5, 4)
        }
        else {
            play_pause_btn.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_play_icon)
            setMargins(play_pause_btn, 7, 4, 3, 4)
        }
    }

    private fun getPxFromDp(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        ).toInt()
    }

    override fun onDestroy() {
        videoView?.stopPlayback()
        playFile.delete()
        super.onDestroy()
    }

    override fun onPause() {
        isPaused = true
        videoView?.stopPlayback()
        playFile.delete()
        super.onPause()
    }

    override fun onResume() {
        if(isPaused){
            isPaused = false
            isPrepared = false
            play_pause_btn.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_play_icon)
            setMargins(play_pause_btn, 7, 4, 3, 4)
            videoView?.stopPlayback()
            videoView?.setBackgroundColor(resources.getColor(R.color.colorPrimary))

            position = arguments?.let { VideoFragmentArgs.fromBundle(it).lessonNumber }
            val path = arguments?.let { VideoFragmentArgs.fromBundle(it).subject }

            val files = fileUtils.getFilesFromPath(path!!, onlyFolders = false)
            val json = fileUtils.readFileText(StringUtils.getJsonFileName, requireActivity())
            val jsonObject = JSONObject(json)

            thread { decryptVideo(files, path, jsonObject) }
        }
        super.onResume()
    }

}