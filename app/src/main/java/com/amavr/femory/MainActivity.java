package com.amavr.femory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.amavr.femory.utils.XPoint;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;

public class MainActivity
        extends AppCompatActivity {

    private static final String TAG = "XDBG.Main";

    private Toolbar toolbar;

    Fragment frgLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        frgLists = ListsFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.flContent, frgLists)
                .commit();

        XPoint.create(this);

        handleIntent();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        handleIntent();
    }

    private void handleIntent() {
        // Get the intent set on this activity
        Intent intent = getIntent();

        // Get the uri from the intent
        Uri uri = intent.getData();

        // Do not continue if the uri does not exist
        if (uri == null) {
            Log.d(TAG, "NO URI");
            return;
        }

        Log.d(TAG, uri.toString());

        String key = uri.getPath().replace("/share/", "");
        XPoint.getInstance().addListKey(key);
        XPoint.getInstance().getStorage().queryListByKey(key);
//        XPoint.getInstance().getStorage().notifySubs();

//        // Let the deep linker do its job
//        Bundle data = mDeepLinker.buildBundle(uri);
//        if (data == null) {
//            return;
//        }
//
//        // See if we have a valid link
//        DeepLinker.Link link = DeepLinker.getLinkFromBundle(data);
//        if (link == null) {
//            return;
//        }
//
//        // Do something with the link
//        switch (link) {
//        ...
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        return super.onOptionsItemSelected(item);
    }

    public void setAppHeader(String Text) {
        getSupportActionBar().setTitle(Text);
    }
}
