package com.example.vlad.licenta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vlad.licenta.model.User;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Client extends AppCompatActivity implements LoggedInActivity{

    private ClientTabs mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private User currentUser;

    private boolean logout;
    private Timer logoutTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        currentUser = (User) getIntent().getSerializableExtra("currentUser");
        MiscFunctions.createToast(getApplicationContext(), "Welcome " + currentUser.getFullname() + "!");

        final Activity currentActivity = this;

        String url = ServerProperties.HOST +
                "/library/hasDueBooks?userId=" +
                currentUser.getId() +
                "&currentDate=" +
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        ServerRequestGET<String> hasDueBooks = new ServerRequestGET<>(url, TypeFactory.defaultInstance().constructType(String.class), new AsyncResponse<String>() {
            @Override
            public void actionCompleted(String obj) {
                if (obj != null && obj.equals("1")) {
                    MiscFunctions.createAlertDialogWithMessage(currentActivity, "Warning! \nYou have due books!");
                }
            }
        });
        hasDueBooks.execute();


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        mSectionsPagerAdapter = new ClientTabs(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        logout = false;
        logoutTimer = new Timer();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return " BOOKS ";
                case 1:
                    return " LISTS ";
                case 2:
                    return " HISTORY ";
            }
            return null;
        }

    }

    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public void onBackPressed() {
        if ( !logout ) {
            MiscFunctions.createToast(getApplicationContext(), "Press again to logout");
            logout = true;
            logoutTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    logout = false;
                }
            }, 5000); // 10 seconds
        }
        else
        {
            // logout
            Intent intent = new Intent(getApplicationContext(), LogIn.class);
            intent.putExtra("logout", true);
            startActivity(intent);
        }
    }
}
