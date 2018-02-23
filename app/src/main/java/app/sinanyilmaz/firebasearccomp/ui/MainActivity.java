package app.sinanyilmaz.firebasearccomp.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import app.sinanyilmaz.firebasearccomp.R;



public class MainActivity extends AppCompatActivity implements MessageListFragment.MyFragmentListenerImpl {
    private static final String TAG = "MainActivity";

    private final NewPostFragment mComposerFragment = new NewPostFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public void onFabButtonClicked() {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mComposerFragment)
                .addToBackStack(null)
                .commit();
    }

}
