package com.android.test.scrollviewtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by gauravbhola on 21/02/16.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openSlidingDrawerWithSlidingActionBar(View v) {
        startActivity(new Intent(this, SimpleSlidingDrawerWithActionBarSliding.class));
    }

    public void openSlidingDrawerWithoutSlidingActionBar(View v) {
        startActivity(new Intent(this, SlidingDrawerWithoutActionBarSliding.class));
    }

    public void openSlidingDrawerWithoutActionBar(View v) {
        startActivity(new Intent(this, SlidingDrawerWithoutActionBar.class));
    }

    public void openSlidingDrawerWithTransparentActionBar(View v) {
        startActivity(new Intent(this, SlidingDrawerWithTransparentActionBarActivity.class));
    }

}
