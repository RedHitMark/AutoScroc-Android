package it.scroking.autoscroc.ui.your_scrounge;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import it.scroking.autoscroc.ui.your_scrounge.your_purchases.YourPurchasesFragment;
import it.scroking.autoscroc.ui.your_scrounge.your_rents.YourRentsFragment;

class TabAdapter extends FragmentStatePagerAdapter {
    private int totalTabs;

    public TabAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm, FragmentStatePagerAdapter.BEHAVIOR_SET_USER_VISIBLE_HINT);
        this.totalTabs = totalTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new YourPurchasesFragment();
            case 1:
                return new YourRentsFragment();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}