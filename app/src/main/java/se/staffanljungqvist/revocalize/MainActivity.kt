package se.staffanljungqvist.revocalize

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.viewModels
import se.staffanljungqvist.revocalize.databinding.ActivityMainBinding
import se.staffanljungqvist.revocalize.viewmodels.ViewModel

val TAG = "revodebug"

class MainActivity : AppCompatActivity() {



    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Göm navigation bar och action bar
        getSupportActionBar()?.hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        val model : ViewModel by viewModels()

    }
}