package cc.ddsakura.modernapp001

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import cc.ddsakura.modernapp001.databinding.FragmentBlankBinding
import java.util.function.BiFunction

class BlankFragment : Fragment(R.layout.fragment_blank) {
    private var _binding: FragmentBlankBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBlankBinding.bind(view)
        binding.myButton.setOnClickListener {
            startActivity(Intent(requireContext(), MainActivity2::class.java))
        }
        binding.myButton2.setOnClickListener {
            val func = BiFunction { a: Int, b: Int -> a + b }
            val result = "Desugaring ${func.apply(1, 2)}"
            Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show()
            Log.d(TAG, result)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "BlankFragment"
    }
}