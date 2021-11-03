package mangbaam.classmate.ui.more

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mangbaam.classmate.Constants.Companion.TAG
import mangbaam.classmate.PreferenceHelper
import mangbaam.classmate.database.DB_keys.Companion.ALARM_BEFORE_10
import mangbaam.classmate.database.DB_keys.Companion.ALARM_BEFORE_30
import mangbaam.classmate.database.DB_keys.Companion.ALARM_ON
import mangbaam.classmate.databinding.FragmentMoreBinding

class MoreFragment : Fragment() {
    private var mBinding: FragmentMoreBinding? = null
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "SettingFragment - onCreateView() called")
        mBinding = FragmentMoreBinding.inflate(inflater)

        initSwitchLayout()
        initLinks()

        return binding.root
    }

    private fun initSwitchLayout() {
        // SharePreference에서 값을 가져와 초기화
        initSwitchValues()
        val context = requireContext()
        if (PreferenceHelper.getBoolean(context, ALARM_ON).not()) {
            binding.before10Switch.isEnabled = false
            binding.before30Switch.isEnabled = false
        }
        // '알람 켜기' 스위치가 false라면 다른 스위치들 설정 불가
        binding.alarmOnSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.before10Switch.isEnabled = true
                binding.before30Switch.isEnabled = true
            } else {
                binding.before10Switch.isEnabled = false
                binding.before30Switch.isEnabled = false
            }
            PreferenceHelper.setBoolean(context, ALARM_ON, isChecked)
            Log.d(TAG, "알람켜기 스위치 - ${PreferenceHelper.getBoolean(context, ALARM_ON)}")
        }
        binding.before10Switch.setOnCheckedChangeListener { buttonView, isChecked ->
            PreferenceHelper.setBoolean(context, ALARM_BEFORE_10, isChecked)
            Log.d(TAG, "10분 스위치 - ${PreferenceHelper.getBoolean(context, ALARM_BEFORE_10)}")
        }
        binding.before30Switch.setOnCheckedChangeListener { buttonView, isChecked ->
            PreferenceHelper.setBoolean(context, ALARM_BEFORE_30, isChecked)
            Log.d(TAG, "30분 스위치 - ${PreferenceHelper.getBoolean(context, ALARM_BEFORE_30)}")
        }
    }

    private fun initSwitchValues() {
        val context = requireContext()
        binding.alarmOnSwitch.isChecked = PreferenceHelper.getBoolean(context, ALARM_ON)
        binding.before10Switch.isChecked = PreferenceHelper.getBoolean(context, ALARM_BEFORE_10)
        binding.before30Switch.isChecked = PreferenceHelper.getBoolean(context, ALARM_BEFORE_30)
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

    override fun onResume() {
        super.onResume()
        initSwitchValues()
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
        const val GITHUB_ADDRESS: String = "https://github.com/mangbaam"
    }
}