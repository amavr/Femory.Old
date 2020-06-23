package com.amavr.femory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
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

//        String node_id = "lists/abc234";
//        Tests.initDB(this, this, node_id);
//        ListInfo li = Tests.generateList(this);
//        Tests.testWriteDB(node_id,li);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        return super.onOptionsItemSelected(item);
    }

    public void setAppHeader(String Text){
        getSupportActionBar().setTitle(Text);
    }
}
