package com.example.vlad.licenta;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vlad.licenta.model.Author;
import com.example.vlad.licenta.model.User;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class Administrator extends AppCompatActivity implements View.OnClickListener{

    int CAMERA_REQUEST =1;


    private AdministratorTabs mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private User currentUser;


    private Boolean isFabOpen = false;
    private FloatingActionButton fab_settings, fab_add_book, fab_scan;
    private Animation fab_open_scan, fab_close_scan, fab_open_add_book, fab_close_add_book;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator);
       // mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(1, true);
        final Activity activity=this;


        currentUser = (User) getIntent().getSerializableExtra("currentUser");


        mSectionsPagerAdapter = new AdministratorTabs(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);


        fab_settings = (FloatingActionButton)findViewById(R.id.fab_settings);
        fab_add_book = (FloatingActionButton)findViewById(R.id.fab_add_book);
        fab_scan = (FloatingActionButton)findViewById(R.id.fab_scan);
        fab_open_scan = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_scan_show);
        fab_close_scan = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_scan_hide);
        fab_open_add_book = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_add_show);
        fab_close_add_book = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_add_hide);
//        fab_open_scan = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
//        fab_close_scan = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
//        fab_open_add_book = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
//        fab_close_add_book = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        fab_settings.setOnClickListener(this);
        fab_add_book.setOnClickListener(this);
        fab_scan.setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();

            } else {
                String scanResult = result.getContents();
                Toast.makeText(this, scanResult, Toast.LENGTH_LONG).show();

                String[] tokenizer = scanResult.split(",");

                int userId = Integer.parseInt(tokenizer[0]);
                int bookId = Integer.parseInt(tokenizer[1]);
                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date now = new Date();
                String strDate = sdfDate.format(now);



            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fab_settings:

                animateFAB();
                break;
            case R.id.fab_scan:

                startScan();
                break;
            case R.id.fab_add_book:

                addBookToDB();
                break;
        }
    }

    public void animateFAB(){
        if(isFabOpen){
            fab_add_book.startAnimation(fab_close_add_book);
            fab_scan.startAnimation(fab_close_scan);
            fab_add_book.setClickable(false);
            fab_scan.setClickable(false);
            isFabOpen = false;
            Log.d("Raj", "close");

        } else {
            fab_add_book.startAnimation(fab_open_add_book);
            fab_scan.startAnimation(fab_open_scan);
            fab_add_book.setClickable(true);
            fab_scan.setClickable(true);
            isFabOpen = true;
            Log.d("Raj","open");

        }
    }

    private void startScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    private void addBookToDB() {
        Intent intent = new Intent(getApplicationContext(), AdministratorAddBook.class);
        startActivity(intent);
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
                   return "SCANNER";
                case 1:
                    return " BOOKS ";
                case 2:
                    return " HISTORY ";
            }
            return null;
        }


    }

    public User getCurrentUser() {
        return currentUser;
    }
}
