package edu.iastate.ece.sd.dec1505.models;

import java.util.LinkedList;

import edu.iastate.ece.sd.dec1505.fragments.HomeFragment;

public class Navigation {

    /** NAVIGATION **/
    private static LinkedList<NavigationItem> navigationItems;

    public static LinkedList<NavigationItem> getNavigationItems() {
        navigationItems = new LinkedList<NavigationItem>();

        // Add fragmentItems here
        add("Home", HomeFragment.class);

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
