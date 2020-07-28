package personal.ivan.kotlin_flow_practice.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import personal.ivan.kotlin_flow_practice.R
import personal.ivan.kotlin_flow_practice.io.util.IoStatus
import personal.ivan.kotlin_flow_practice.viewmodel.MainViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainFragment : Fragment(R.layout.main_fragment) {

    companion object {
        val TAG = MainFragment::class.java.simpleName
        fun newInstance() = MainFragment()
    }

    private val viewModel by viewModels<MainViewModel>()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        initGithubUser(view = view)
        initGithubUserDetails(view = view)
        initTry(view = view)
    }

    // region Github User List

    private fun initGithubUser(view: View) {
        view
            .findViewById<Button>(R.id.button1)
            .setOnClickListener {
                viewModel.getUserList().observe(
                    viewLifecycleOwner,
                    Observer {
                        when (it) {
                            is IoStatus.Loading -> Log.d(TAG, "status loading")
                            is IoStatus.Failed -> Log.d(TAG, "status failed")
                            is IoStatus.Succeed -> Log.d(TAG, "status succeed")
                        }
                    })
            }
    }

    // endregion

    // region Github User Details

    private fun initGithubUserDetails(view: View) {
        view
            .findViewById<Button>(R.id.button2)
            .setOnClickListener {
                viewModel.getUserDetails().observe(
                    viewLifecycleOwner,
                    Observer {
                        when (it) {
                            is IoStatus.Loading -> Log.d(TAG, "status loading")
                            is IoStatus.Failed -> Log.d(TAG, "status failed")
                            is IoStatus.Succeed -> Log.d(TAG, "status succeed")
                        }
                    })
            }
    }

    // endregion

    private fun initTry(view: View) {
        view
            .findViewById<Button>(R.id.button3)
            .setOnClickListener { viewModel.aaa() }
    }
}