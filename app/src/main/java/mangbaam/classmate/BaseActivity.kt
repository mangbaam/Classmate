package mangbaam.classmate

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import mangbaam.classmate.databinding.ActivityBaseBinding

class BaseActivity : AppCompatActivity() {

    private var mBinding: ActivityBaseBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "BaseActivity - onCreate() called")

        mBinding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment

        val navController = navHostFragment.navController

        NavigationUI.setupWithNavController(binding.bottomNavView, navController)
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }

    companion object {
        const val TAG: String = "로그"
    }
}