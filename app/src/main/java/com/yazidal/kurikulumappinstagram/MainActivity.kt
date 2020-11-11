package com.yazidal.kurikulumappinstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yazidal.kurikulumappinstagram.fragment.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        botNav.setOnNavigationItemSelectedListener(onBottomNavListener)

        val frag = supportFragmentManager.beginTransaction()
        frag.add(R.id.frameLayout,
            HomeFragment()
        )
        frag.commit()
    }
    private val onBottomNavListener = BottomNavigationView.OnNavigationItemSelectedListener {i->
        var selectedFragment: Fragment =
            HomeFragment()


        when(i.itemId){
            R.id.home -> {
                selectedFragment =
                    HomeFragment()

            }
            R.id.addPost ->{
                startActivity(Intent(this, AddPostActivity::class.java))
            }

            R.id.search -> {
                selectedFragment =
                    SearchFragment()
            }

            R.id.activity -> {
                selectedFragment =
                    ActivityFragment()
            }

            R.id.profile -> {
                selectedFragment =
                    ProfileFragment()
            }
        }
        val frag = supportFragmentManager.beginTransaction()
        frag.replace(R.id.frameLayout, selectedFragment)
        frag.commit()

        true
        }
    }
