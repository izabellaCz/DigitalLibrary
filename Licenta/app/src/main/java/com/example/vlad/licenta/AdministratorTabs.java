package com.example.vlad.licenta;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

//import com.example.vlad.licenta.Client.PlaceholderFragment;


public class AdministratorTabs extends FragmentPagerAdapter {

    public AdministratorTabs(FragmentManager fm) {
        super(fm);
    }


    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return " BOOKS ";
            case 1:
                return " HISTORY ";
        }
        return null;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new BooksList();
            case 1:
                return new HistoryList();

            default:
                return null;
        }
    }
}

