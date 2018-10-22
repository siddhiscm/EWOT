package app.json.com.myjson.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.json.com.myjson.R;



public class FragDashBoard extends Fragment {

    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 6;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private static final long START_TIME_IN_MILLIS1 = 1001000;
    private static final long START_TIME_IN_MILLIS2 = 1201000;
    private static final long START_TIME_IN_MILLIS3 = 1140000;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

          View view = inflater.inflate(R.layout.dashboardfrag, container, false);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = view.findViewById(R.id.viewPager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        return view;
    }
    // adapter of the slide pages
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return new ProtocolPage1();

                case 1:
                    // Fragment # 1 - This will show FirstFragment different title
                    return new ProtocolPage2();
                case 2: // Fragment # 2 - This will show SecondFragment
                    return new ProtocolPage3();
                case 3: //Fragment #  3 -this will show 3rd fragment
                    return  new ProtocolPage4();
                case 4:
                    return new ProtocolPage5();
                case 5:
                    return new ProtocolPage6();
                default:
                    return null;
            }
        }




    }
}
