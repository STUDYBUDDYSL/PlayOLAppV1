package com.taloslogy.playolapp.views

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.taloslogy.playolapp.R
import com.taloslogy.playolapp.utils.ByteMagic
import com.taloslogy.playolapp.utils.Decryptor
import com.taloslogy.playolapp.utils.FileUtils
import com.taloslogy.playolapp.utils.StringUtils
import com.taloslogy.playolapp.utils.storage.PrefHelper
import kotlinx.android.synthetic.main.fragment_video.*
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import kotlin.concurrent.thread

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
class VideoFragment : Fragment() {

    private var isPrepared = false
    private var isFullScreen = false
    private var isPaused = false

    private val fileUtils: FileUtils = FileUtils()
    private lateinit var playFile: File
    private lateinit var exoPlayer: SimpleExoPlayer

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

        if(resources.configuration.orientation  == Configuration.ORIENTATION_PORTRAIT){
            val height = resources.displayMetrics.heightPixels

            val margin = (height * 0.03).toInt()

            val params = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                getPxFromDp(getVideoHeight(height))
            )
            params.topToBottom = subject_content.id
            params.bottomToTop = media_controls.id
            video_content.layoutParams = params

            setMargins(video_content, 0, margin, 0, margin)
        }

        thread { if(!isPrepared) decryptVideo(files, path, jsonObject) }

        play_pause_btn.setOnClickListener {
            if(isPrepared){
                if(exoPlayer.isPlaying) {
                    exoPlayer.pause()
                }
                else {
                    exoPlayer.play()
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
                    exoPlayer.release()
                    videoView?.player = null
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
                    exoPlayer.release()
                    videoView?.player = null
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

        val prefs = PrefHelper.getInstance(requireActivity())
        val decryptor = Decryptor(prefs)

        val keyFile = File("${StringUtils.getCoursePath}/${StringUtils.getKeyFileName}")
        val decryptedVal = decryptor.decryptKey(keyFile)
        val decryptKeys = String(decryptedVal!!, Charsets.UTF_8)
        val passphrase = decryptKeys.split('\n').first().trim()
        val iv = decryptKeys.split('\n').last()

        val pass1 = ByteMagic(prefs.key1Pref.get()!!).str
        val iv1 = ByteMagic(prefs.iv1Pref.get()!!).str
        val pass2 = ByteMagic(prefs.key2Pref.get()!!).str
        val iv2 = ByteMagic(prefs.iv2Pref.get()!!).str

        val pChunk = passphrase.chunked(8)
        val ivChunk = iv.chunked(4)

        val pp = pass1 + pChunk.first() + pass2 + pChunk.last()
        val ivv = iv1 + ivChunk.first() + iv2 + ivChunk.last()

        val file = File("$videoPath/$name")
        val decrypted = decryptor.decryptVideoFile(pp, ivv, file)
        getDataSource(decrypted)

        activity?.runOnUiThread {
            playVideo()
            decrypt_loader?.visibility = View.GONE
        }
    }

    private fun playVideo() {
        try {
            exoPlayer = SimpleExoPlayer.Builder(requireActivity()).build()
            videoView.player = exoPlayer
            val mediaItem = MediaItem.fromUri(playFile.absolutePath)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.addListener(object : Player.EventListener {
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        if(!isPrepared){
                            isPrepared = true
                            videoView?.setBackgroundColor(Color.TRANSPARENT)
                            exoPlayer.play()
                            play_pause_btn?.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_pause)
                            setMargins(play_pause_btn!!, 5, 4, 5, 4)
                        }
                        else {
                            setButtonControls()
                        }
                    }
                    if(playbackState == Player.STATE_ENDED){
                        play_pause_btn.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_play_icon)
                        setMargins(play_pause_btn, 7, 4, 3, 4)
                    }

                }
            })
            exoPlayer.seekTo(0, 0)
            exoPlayer.prepare()
            videoView?.requestFocus()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun getDataSource(array: ByteArray?) {
        try{
            if(activity?.cacheDir?.exists() == false){
                activity?.cacheDir?.mkdir()
            }
            playFile = File.createTempFile("test", ".mp4")
            val out = FileOutputStream(playFile)
            out.write(array)
            out.flush()
            out.close()
        }
        catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
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

            val params = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.FILL_PARENT,
                ConstraintLayout.LayoutParams.FILL_PARENT
            )
            video_content.layoutParams = params
        }
        else {
            top_image.visibility = View.VISIBLE
            subject_content.visibility = View.VISIBLE
            media_controls.visibility = View.VISIBLE

            val height = resources.displayMetrics.heightPixels

            val margin = (height * 0.03).toInt()

            val params = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                getPxFromDp(getVideoHeight(height))
            )

            params.topToBottom = subject_content.id
            params.bottomToTop = media_controls.id

            video_content.layoutParams = params
            setMargins(video_content, 0, margin, 0, margin)
            setButtonControls()
        }

    }

    private fun setButtonControls() {
        if(this::exoPlayer.isInitialized){
            if(exoPlayer.isPlaying) {
                play_pause_btn.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_pause)
                setMargins(play_pause_btn, 5, 4, 5, 4)
            }
            else {
                play_pause_btn.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_play_icon)
                setMargins(play_pause_btn, 7, 4, 3, 4)
            }
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
        videoView?.onPause()
        if(this::exoPlayer.isInitialized)
            exoPlayer.release()
        if(this::playFile.isInitialized)
            playFile.delete()
        activity?.cacheDir?.deleteRecursively()
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        isPaused = true
        videoView?.onPause()
        if(this::exoPlayer.isInitialized)
            exoPlayer.release()
        if(this::playFile.isInitialized)
            playFile.delete()
        activity?.cacheDir?.deleteRecursively()
    }

    override fun onResume() {
        super.onResume()
        if(isPaused){
            isPaused = false
            isPrepared = false
            play_pause_btn.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_play_icon)
            setMargins(play_pause_btn, 7, 4, 3, 4)
            videoView?.onResume()

            position = arguments?.let { VideoFragmentArgs.fromBundle(it).lessonNumber }
            val path = arguments?.let { VideoFragmentArgs.fromBundle(it).subject }

            val files = fileUtils.getFilesFromPath(path!!, onlyFolders = false)
            val json = fileUtils.readFileText(StringUtils.getJsonFileName, requireActivity())
            val jsonObject = JSONObject(json)

            thread { decryptVideo(files, path, jsonObject) }
        }
    }

    private fun getVideoHeight(height: Int) : Float {
        return when {
            height < 1000 -> {
                (height * 0.375).toFloat()
            }
            height < 1800 -> {
                (height * 0.25).toFloat()
            }
            else -> {
                (height * 0.15).toFloat()
            }
        }
    }

}