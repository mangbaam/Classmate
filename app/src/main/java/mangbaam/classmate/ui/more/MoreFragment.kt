package mangbaam.classmate.ui.more

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mangbaam.classmate.databinding.FragmentMoreBinding

class MoreFragment : Fragment() {
    private var mBinding: FragmentMoreBinding? = null
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "SettingFragment - onCreateView() called")
        mBinding = FragmentMoreBinding.inflate(inflater)

        return binding.root
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
        const val TAG: String = "로그"
    }
}