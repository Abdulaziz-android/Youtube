package com.abdulaziz.youtube

import android.animation.Animator
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.abdulaziz.youtube.databinding.ActivityMainBinding
import com.abdulaziz.youtube.fragments.*
import com.abdulaziz.youtube.utils.OnBackPressedListener
import com.google.android.material.bottomnavigation.LabelVisibilityMode

class MainActivity : AppCompatActivity(){

    lateinit var binding: ActivityMainBinding
    protected var onBackPressedListener: OnBackPressedListener? = null


    companion object {
       // val API_KEY = "AIzaSyAOAKkoa1hELCRMBn9jKXv3t4126MPvimU"

         val API_KEY2 = "AIzaSyBvoZ0_bnRCNvtheb1Njmvkr0XX-Me3pYQ"
        //val API_KEY2 = "AIzaSyDpSIcvAQNruVSQUOouHkSUCcBCIXIu9Cs"
    }
    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val handler = Handler(Looper.getMainLooper())
        val runnable = Runnable {
            binding.welcomeLayout.animate()
                .x(2.0f)
                .translationZ(5.0f)
                .translationY(0.0f)
                .alpha(0.0f)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator?) {

                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        binding.welcomeLayout.visibility = View.GONE
                        window.statusBarColor =
                            ContextCompat.getColor(this@MainActivity, R.color.status_bar_color)
                        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                            HomeFragment()).commit()
                    }

                    override fun onAnimationCancel(p0: Animator?) {

                    }

                    override fun onAnimationRepeat(p0: Animator?) {

                    }

                }).duration = 600
        }



        handler.postDelayed(runnable, 2000)
        binding.searchIv.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                SearchVideoFragment())
                .addToBackStack("Search").commit()
        }
        binding.bottomNavigation.setOnItemSelectedListener {
            var selectedFragment: Fragment? = null
            when (it.itemId) {
                R.id.home_menu -> {
                    selectedFragment = HomeFragment()
                }
                R.id.subscription_menu -> {
                    selectedFragment = SubscriptionFragment()
                }
                R.id.library_menu -> {
                    selectedFragment = LibraryFragment()
                }

                else -> HomeFragment()
            }
            val transaction = supportFragmentManager.beginTransaction()
            while (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStackImmediate()
            }
            transaction.replace(R.id.fragment_container,
                selectedFragment!!).commit()
            true
        }


        binding.bottomNavigation.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED

    }

    fun settOnBackPressedListener(onBackPressedListener: OnBackPressedListener?) {
        this.onBackPressedListener = onBackPressedListener
    }

    override fun onBackPressed() {
        if (onBackPressedListener != null)
            onBackPressedListener!!.doBack()
        else
            super.onBackPressed()
    }


    fun hideActionBar() {
        binding.fakeActionBar.visibility = View.GONE
    }

    fun showActionBar() {
        binding.fakeActionBar.visibility = View.VISIBLE
    }



}