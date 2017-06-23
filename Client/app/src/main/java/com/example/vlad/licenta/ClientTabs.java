package com.example.vlad.licenta;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ClientTabs extends FragmentPagerAdapter {

    public ClientTabs(FragmentManager fm) {
        super(fm);
    }


    @Override
    public int getCount() {
        return 3;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return " BOOKS ";
            case 1:
                return "FAVOURITES ";
            case 2:
                return "HISTORY";
        }
        return null;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new BooksList();
            case 1:
                return new FavouritesList();
            case 2:
                return new HistoryList();
        }
        return null;
    }
}

