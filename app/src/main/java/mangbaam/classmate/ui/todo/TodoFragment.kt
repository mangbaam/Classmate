package mangbaam.classmate.ui.todo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import mangbaam.classmate.Constants.Companion.TAG
import mangbaam.classmate.databinding.FragmentTodoBinding

class TodoFragment : Fragment() {
    private var _binding: FragmentTodoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "HomeFragment - onCreateView() called")
        _binding = FragmentTodoBinding.inflate(inflater, container, false)

        initViews()

        return binding.root
    }

    private fun initViews() {
        binding.addTodoButton.setOnClickListener {
            val intent = Intent(binding.addTodoButton.context, AddTodoActivity::class.java)
            startActivity(intent)
        }
        binding.menuButton.setOnClickListener {
            // TODO Drawable 메뉴 구성
        }
    }

    override fun onDestroyView() {
        Log.d(TAG, "HomeFragment - onDestroyView() called")
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        Log.d(TAG, "HomeFragment - onDestroy() called")
        super.onDestroy()
    }
}