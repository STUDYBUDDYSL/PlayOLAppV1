package com.taloslogy.playolapp.views

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.Navigation
import com.taloslogy.playolapp.R
import com.taloslogy.playolapp.utils.FileUtils
import com.taloslogy.playolapp.utils.StringUtils
import kotlinx.android.synthetic.main.fragment_revision_year.*
import kotlinx.android.synthetic.main.stage_btn.view.*
import org.json.JSONObject
import java.io.File
import kotlin.concurrent.thread

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
class RevisionYearFragment : Fragment() {

    private val fUtils = FileUtils()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_revision_year, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val height = resources.displayMetrics.heightPixels

        val iParams = LinearLayout.LayoutParams(
            getPxFromDp((getLogoHeight(height)*1.2).toFloat()),
            getPxFromDp(getLogoHeight(height))
        )
        iParams.setMargins(0,20,0,0)
        iParams.gravity = Gravity.CENTER
        revLogo.layoutParams = iParams

        val files = fUtils.getFilesFromPath(
            "${StringUtils.getCoursePath}/${StringUtils.getRevisionName}")
        thread { generateTable(files) }

    }

    private fun generateTable(files: List<File>) {

        val json = fUtils.readFileText(StringUtils.getJsonFileName, requireActivity())
        val jsonObject = JSONObject(json)

        val width = resources.displayMetrics.widthPixels

        val lParams = LinearLayout.LayoutParams(
            getPxFromDp(getStageWidth(width)),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        lParams.setMargins(0,10,0,20)
        lParams.gravity = Gravity.END

        for (x in files) {
            val btnS = LayoutInflater.from(activity).inflate(R.layout.stage_btn, null)
            btnS.stage_btn.text = jsonObject.getString(x.name)
            btnS.layoutParams = lParams

            btnS.setOnClickListener {
                val action = RevisionYearFragmentDirections.selectYear(x.name)
                Navigation.findNavController(it).navigate(action)
            }

            // Add the completed row to table
            requireActivity().runOnUiThread {
                revYears.addView(btnS)
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

    private fun getLogoHeight(height: Int) : Float {
        return when {
            height < 1000 -> (height * 0.18).toFloat()
            height < 1800 -> (height * 0.13).toFloat()
            else -> (height * 0.08).toFloat()
        }
    }

    private fun getStageWidth(width: Int) : Float {
        return when {
            width < 650 -> (width * 0.6).toFloat()
            width < 950 ->(width * 0.45).toFloat()
            else -> (width * 0.3).toFloat()
        }
    }
}