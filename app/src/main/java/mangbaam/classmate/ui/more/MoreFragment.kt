package mangbaam.classmate.ui.more

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_more.*
import mangbaam.classmate.Constants.Companion.TAG
import mangbaam.classmate.PreferenceHelper
import mangbaam.classmate.R
import mangbaam.classmate.dao.AlarmDao
import mangbaam.classmate.dao.LectureDao
import mangbaam.classmate.dao.ScheduleDao
import mangbaam.classmate.dao.TodoDao
import mangbaam.classmate.database.*
import mangbaam.classmate.database.DB_keys.Companion.ALARM_MINUTE
import mangbaam.classmate.database.DB_keys.Companion.ALARM_ON
import mangbaam.classmate.databinding.DialogMinuteSettingBinding
import mangbaam.classmate.databinding.FragmentMoreBinding
import mangbaam.classmate.model.AlarmModel
import mangbaam.classmate.model.ScheduleModel
import mangbaam.classmate.notification.NotificationHelper.Companion.activateAllAlarms
import mangbaam.classmate.notification.NotificationHelper.Companion.removeAllAlarms

class MoreFragment : Fragment() {
    private var mBinding: FragmentMoreBinding? = null
    private val binding get() = mBinding!!
    private lateinit var scheduleDao: ScheduleDao
    private lateinit var alarmDao: AlarmDao
    private lateinit var tableDao: LectureDao
    private lateinit var todoDao: TodoDao
    private val alarms = mutableListOf<AlarmModel>()
    private val schedules = arrayListOf<ScheduleModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        scheduleDao = getScheduleDB(context).scheduleDao()
        alarmDao = getAlarmDB(context).alarmDao()
        tableDao = getTableDB(context).tableDao()
        todoDao = getTodoDB(context).todoDao()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "SettingFragment - onCreateView() called")
        mBinding = FragmentMoreBinding.inflate(inflater)

        schedules.addAll(scheduleDao.getAll())
        alarms.addAll(alarmDao.getAll())

        initViews()
        initLinks()

        return binding.root
    }

    private fun initViews() {
        initValues() // SharedPreference ?????? ????????? ?????????
        val context = requireContext()
        if (PreferenceHelper.getBoolean(context, ALARM_ON).not()) {
            binding.editButton.isEnabled = false
        }

        with(binding) {
            // '?????? ??????' ???????????? false -> ?????? ???????????? ?????? ??????
            alarmOnSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                Log.d(TAG, "???????????? ????????? - $isChecked")
                if (isChecked) {
                    binding.editButton.isEnabled = isChecked
                    activateAllAlarms(context, alarmDao.getAll())
                    alarms.forEach {
                        val model = it.copy()
                        model.onOff = true
                        alarmDao.update(model)
                    }
                } else {
                    binding.editButton.isEnabled = isChecked
                    removeAllAlarms(context, alarmDao.getAll())
                    alarms.forEach {
                        val model = it.copy()
                        model.onOff = false
                        alarmDao.update(model)
                    }
                }
                PreferenceHelper.setBoolean(context, ALARM_ON, isChecked)
            }

            editButton.setOnClickListener {
                Log.d(TAG, "Setting ?????? ??????")
                showSettingDialog(context)
            }

            /* ???????????? ??? ?????? ?????? */
            // mintimetable
            loadMoreTextView.setOnClickListener {
                if (licenseInformation1.isGone) {
                    loadMoreTextView.text = "?????????"
                } else {
                    loadMoreTextView.text = "??? ??????"
                }
                licenseInformation1.isGone = licenseInformation1.isGone.not()
            }
            // nice-spinner
            loadMoreTextView2.setOnClickListener {
                if (licenseInformation2.isGone) {
                    loadMoreTextView2.text = "?????????"
                } else {
                    loadMoreTextView2.text = "??? ??????"
                }
                licenseInformation2.isGone = licenseInformation2.isGone.not()
            }
            // SwipeRevealLayout
            loadMoreTextView3.setOnClickListener {
                if (licenseInformation3.isGone) {
                    loadMoreTextView3.text = "?????????"
                } else {
                    loadMoreTextView3.text = "??? ??????"
                }
                licenseInformation3.isGone = licenseInformation3.isGone.not()
            }

            /* ????????? ?????? ????????? ?????? */
            clearAllDataButton.setOnClickListener {
                val toast = Toast.makeText(requireContext(), "?????? ????????? ???????????????", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.BOTTOM, 0, 500)
                toast.show()
            }
            clearAllDataButton.setOnLongClickListener {
                showDeleteAllDataDialog()
            }
        }
    }

    private fun initValues() {
        val context = requireContext()
        binding.alarmOnSwitch.isChecked = PreferenceHelper.getBoolean(context, ALARM_ON)
        binding.minuteTextView.text = PreferenceHelper.getInt(context, ALARM_MINUTE).toString()
    }

    private fun initLinks() {
        binding.emailAddress.setOnClickListener {
            sendEmail()
        }
        binding.blogAddress.setOnClickListener {
            floatWebView(BLOG_ADDRESS)
        }
        binding.githubAddress.setOnClickListener {
            floatWebView(GITHUB_ADDRESS)
        }
    }

    private fun sendEmail() {
        val emailIntent = Intent(Intent.ACTION_SEND)
        try {
            emailIntent.apply {
                putExtra(Intent.EXTRA_EMAIL, arrayOf(EMAIL_ADDRESS))
                type = "text/html"
                setPackage("com.google.android.gm")
                context?.startActivity(emailIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun floatWebView(link: String) {
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(webIntent)
    }

    private fun showSettingDialog(context: Context) {
        val minutesArray = resources.getStringArray(R.array.setting_minutes)
        val dialogView = DialogMinuteSettingBinding.inflate(layoutInflater)
        val picker = dialogView.picker
        val originValue = minuteTextView.text.toString().toInt()
        with(picker) {
            minValue = 0
            maxValue = minutesArray.size - 1
            displayedValues = minutesArray
            wrapSelectorWheel = false
            value = (PreferenceHelper.getInt(context, ALARM_MINUTE)?.div(10)?.minus(1)) ?: 2
        }
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context).apply {
            setTitle("?????? ??? ??? ?????? ???????????????????")
            setMessage("???????????? ?????? ???????????????")
            setView(dialogView.root)
            setPositiveButton(R.string.ok) { _, _ ->
                val value = (dialogView.picker.value + 1) * 10
                if (originValue != value) {
                    binding.minuteTextView.text = value.toString()
                    PreferenceHelper.setInt(context, ALARM_MINUTE, value)
                    changeAlarmTime()
                    Log.d(TAG, "Setting ??????")
                }
            }
            setNegativeButton(R.string.cancel) { _, _ -> }
        }
        dialogBuilder.create().show()
    }

    private fun changeAlarmTime() {
        val context = requireContext()
        val alarms = alarmDao.getAll()
        removeAllAlarms(context, alarms)
        activateAllAlarms(context, alarms)
        Log.d(TAG, "${alarms.size}?????? ?????? ????????? ?????????????????????")
    }

    private fun showDeleteAllDataDialog(): Boolean {
        val listener = DialogInterface.OnClickListener { _, which ->
            if (which == DialogInterface.BUTTON_POSITIVE) {
                alarmDao.clear()
                scheduleDao.clear()
                tableDao.clear()
                todoDao.clear()

                Snackbar.make(
                    this.clearAllDataButton,
                    "???????????? ????????????????????????.",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
        AlertDialog.Builder(requireContext())
            .setTitle("???????????? ?????????????????????????")
            .setIcon(R.drawable.ic_warning)
            .setMessage("????????? ????????? ????????? ????????? ????????? ?????? ???????????? ????????? ??? ????????????.")
            .setPositiveButton("????????? ??????", listener)
            .create()
            .show()
        return true
    }

    override fun onResume() {
        super.onResume()
        initValues()
    }

    override fun onDestroyView() {
        Log.d(TAG, "SettingFragment - onDestroyView() called")
        super.onDestroyView()
        mBinding = null
    }

    override fun onDestroy() {
        Log.d(TAG, "SettingFragment - onDestroy() called")
        super.onDestroy()
    }

    companion object {
        const val EMAIL_ADDRESS: String = "pmb0836+classmate@gmail.com"
        const val BLOG_ADDRESS: String = "https://latte-is-horse.tistory.com"
        const val GITHUB_ADDRESS: String = "https://github.com/mangbaam/Classmate"
    }
}