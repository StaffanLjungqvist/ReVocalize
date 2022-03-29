package se.staffanljungqvist.revocalize

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import se.staffanljungqvist.revocalize.databinding.ActivityMainBinding
import se.staffanljungqvist.revocalize.ui.ExitFragment
import se.staffanljungqvist.revocalize.ui.InGameFragment
import se.staffanljungqvist.revocalize.ui.StageSelectFragment

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
    }



    override fun onBackPressed() {

        if(supportFragmentManager.fragments.first() is StageSelectFragment)
        {
            super.onBackPressed()
        }
        if(supportFragmentManager.fragments.first() is InGameFragment)
        {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainerView, ExitFragment()).addToBackStack(null)
                .commit()
        }
    }
}