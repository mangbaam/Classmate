package mangbaam.classmate.ui.more

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.fragment_more.*
import mangbaam.classmate.Constants.Companion.TAG
import mangbaam.classmate.PreferenceHelper
import mangbaam.classmate.R
import mangbaam.classmate.dao.AlarmDao
import mangbaam.classmate.dao.ScheduleDao
import mangbaam.classmate.database.DB_keys.Companion.ALARM_MINUTE
import mangbaam.classmate.database.DB_keys.Companion.ALARM_ON
import mangbaam.classmate.database.getAlarmDB
import mangbaam.classmate.database.getScheduleDB
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
    private val alarms = mutableListOf<AlarmModel>()
    private val schedules = arrayListOf<ScheduleModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "SettingFragment - onCreateView() called")
        mBinding = FragmentMoreBinding.inflate(inflater)

        scheduleDao = getScheduleDB(requireContext()).scheduleDao()
        alarmDao = getAlarmDB(requireContext()).alarmDao()

        schedules.addAll(scheduleDao.getAll())
        alarms.addAll(alarmDao.getAll())

        initViews()
        initLinks()

        return binding.root
    }

    private fun initViews() {
        initValues() // SharedPreference 값을 가져와 초기화
        val context = requireContext()
        if (PreferenceHelper.getBoolean(context, ALARM_ON).not()) {
            binding.editButton.isEnabled = false
        }
        // '알람 켜기' 스위치가 false -> 다른 스위치들 설정 불가
        binding.alarmOnSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            Log.d(TAG, "알람켜기 스위치 - $isChecked")
            if (isChecked) {
                binding.editButton.isEnabled = isChecked
                // TODO 모든 알림 켜기
                activateAllAlarms(context, alarmDao.getAll())
                alarms.forEach {
                    val model = it.copy()
                    model.onOff = true
                    alarmDao.update(model)
                }
            } else {
                binding.editButton.isEnabled = isChecked
                // TODO 모든 알림 끄기
                removeAllAlarms(context, alarmDao.getAll())
                alarms.forEach {
                    val model = it.copy()
                    model.onOff = false
                    alarmDao.update(model)
                }
            }
            PreferenceHelper.setBoolean(context, ALARM_ON, isChecked)
        }

        binding.editButton.setOnClickListener {
            Log.d(TAG, "Setting 모드 진입")
            showSettingDialog(context)
        }

        /* 라이선스 더 보기 클릭 */
        binding.loadMoreTextView.setOnClickListener {
            if (binding.licenseContainer.isGone) {
                binding.loadMoreTextView.text = "간략히"
            } else {
                binding.loadMoreTextView.text = "더 보기"
            }
            binding.licenseContainer.isGone = binding.licenseContainer.isGone.not()
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
            setTitle("수업 몇 분 전에 알려드릴까요?")
            setMessage("다이얼을 돌려 설정하세요")
            setView(dialogView.root)
            setPositiveButton(R.string.ok) { _, _ ->
                val value = (dialogView.picker.value + 1) * 10
                if (originValue != value) {
                    binding.minuteTextView.text = value.toString()
                    PreferenceHelper.setInt(context, ALARM_MINUTE, value)
                    changeAlarmTime()
                    Log.d(TAG, "Setting 완료")
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
        Log.d(TAG, "${alarms.size}개의 알림 시간이 변경되었습니다")
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