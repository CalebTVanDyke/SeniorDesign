package edu.iastate.ece.sd.dec1505.models;

import java.util.LinkedList;

import edu.iastate.ece.sd.dec1505.fragments.HistoryFragment;
import edu.iastate.ece.sd.dec1505.fragments.HomeFragment;
import edu.iastate.ece.sd.dec1505.fragments.LiveDataFragment;

public class Navigation {

    /** NAVIGATION **/
    private static LinkedList<NavigationItem> navigationItems;

    public static LinkedList<NavigationItem> getNavigationItems() {
        navigationItems = new LinkedList<NavigationItem>();

        // Add fragmentItems here
        add("History", HistoryFragment.class);
        add("Home", HomeFragment.class);
        add("Live Data", LiveDataFragment.class);


        return navigationItems;
    }

    private static void add(String title, Class fragmentClass) {
        NavigationItem navigationItem = new NavigationItem();
        navigationItem.title = title;
        navigationItem.fragmentClass = fragmentClass;
        navigationItems.add(navigationItem);
    }

    public static class NavigationItem {
        public String title;
        public Class fragmentClass;
    }

}
