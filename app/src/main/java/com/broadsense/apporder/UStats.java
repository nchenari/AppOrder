package com.broadsense.apporder;

        import android.app.usage.UsageEvents;
        import android.app.usage.UsageStats;
        import android.app.usage.UsageStatsManager;
        import android.content.Context;
        import android.content.pm.ApplicationInfo;
        import android.content.pm.PackageManager;
        import android.util.Log;

        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.HashSet;
        import java.util.List;
        import java.util.concurrent.TimeUnit;

/**
 * Created by nimachenari on 9/10/15.
 */
public class UStats {

    private Context context;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-yyyy HH:mm:ss");

    private HashSet<String> excludedSet;


    private static final String TAG = UStats.class.getSimpleName();


    public UStats(Context context) {
       this.context = context;

    }

    @SuppressWarnings("ResourceType")
    public static void getStats(Context context) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");
        int interval = UsageStatsManager.INTERVAL_YEARLY;
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long startTime = calendar.getTimeInMillis();
        Log.d(TAG, "Range start: " + dateFormat.format(startTime));
        Log.d(TAG, "Range end: " + dateFormat.format(endTime));

        UsageEvents uEvents = usageStatsManager.queryEvents(startTime, endTime);
        while (uEvents.hasNextEvent()) {
            UsageEvents.Event e = new UsageEvents.Event();
            uEvents.getNextEvent(e);

            if (e != null) {
                Log.d(TAG, "Event: " + e.getPackageName() + "\t" + e.getTimeStamp());
            }
        }
    }

    public static List<UsageStats> getUsageStatsList(Context context) {
        UsageStatsManager usageStatsManager = getUsageStatsManager(context);

        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long startTime = calendar.getTimeInMillis();
        Log.d(TAG, "Range start: " + dateFormat.format(startTime));
        Log.d(TAG, "Range end: " + dateFormat.format(endTime));

        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);

        return usageStatsList;
    }

    public static void printUsageStats(List<UsageStats> usageStatsList) {
        for (UsageStats u : usageStatsList) {
            Log.d(TAG, "Pkg: " + u.getPackageName() + "\t" + "ForegroundTime: "
                    + u.getTotalTimeInForeground());
        }
    }

    public static String getUsageStatsString(Context context) {
        List<UsageStats> usageStatsList = getUsageStatsList(context);

        StringBuilder sb = new StringBuilder();
        sb.append("Usage Stats: " + "\n");
        for (UsageStats u : usageStatsList) {
            sb.append("Pkg: " + u.getPackageName() + "\t" + "ForegroundTime: "
                    + u.getTotalTimeInForeground() + "\n");
        }

        return sb.toString();
    }

    /** App specific Usage Stats Formatting */
    public List<String> getUsageStatsListStrings(Context context) {
        List<UsageStats> usageStatsList = getUsageStatsList(context);

        List<String> stringList = new ArrayList<>();

        final PackageManager pm = context.getPackageManager();

        ApplicationInfo ai;

        for (UsageStats u : usageStatsList) {
            // For each UsageStat: get app name, total time time in foreground
            StringBuilder sb = new StringBuilder();
            // Get application name from usageStat package name
            try {
                ai = pm.getApplicationInfo(u.getPackageName(), 0);
            } catch (final PackageManager.NameNotFoundException e) {
                ai = null;
            }
            final String appName =
                    (String) (ai != null ? pm.getApplicationLabel(ai) : ("Pkg: " + u.getPackageName()));
            // Remove system or unwanted app names from lists
            if (!setContainsAppName(appName)) {
                // convert TotalTimeInForeground (milliseconds) to standard time format (hh:mm:ss)
                final long millis = u.getTotalTimeInForeground();
                final String usageTime = String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(millis),
                        TimeUnit.MILLISECONDS.toMinutes(millis) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                        TimeUnit.MILLISECONDS.toSeconds(millis) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

                sb.append(appName)
                        .append("\n\tUsage Time: ")
                        .append(usageTime);

                // Add String to arrayList
                stringList.add(sb.toString());
            }
        }

        // Return ArrayList of formatted Strings
        return stringList;
    }

    public List<String> getUsageSequenceListStrings(Context context) {
        List<String> usageSequence = new ArrayList<>();

        UsageStatsManager usageStatsManager = getUsageStatsManager(context);

        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long startTime = calendar.getTimeInMillis();
        Log.d(TAG, "Range start: " + dateFormat.format(startTime));
        Log.d(TAG, "Range end: " + dateFormat.format(endTime));


        // Variables used to get application names
        final PackageManager pm = context.getPackageManager();
        ApplicationInfo ai;

        // Query events between specified range
        UsageEvents uEvents = usageStatsManager.queryEvents(startTime, endTime);
        int order = 0;
        String prevApplicationName = "";
        while (uEvents.hasNextEvent()) {
            UsageEvents.Event e = new UsageEvents.Event();
            uEvents.getNextEvent(e);

            // Filter events for foreground events only
            if (e != null && e.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                // For each Event: get app name and provide number order in list
                StringBuilder sb = new StringBuilder();
                // Get application name from usageStat package name
                try {
                    ai = pm.getApplicationInfo(e.getPackageName(), 0);
                } catch (final PackageManager.NameNotFoundException error) {
                    ai = null;
                }
                String appName =
                        (String)( ai != null ? pm.getApplicationLabel(ai) : (" Pkg: " + e.getPackageName()));

                // Remove system or unwanted app names from lists
                if (setContainsAppName(appName)) {
                    Log.i(TAG, "App: \"" + appName + "\"" + " was part of excluded list");

                } else if (prevApplicationName.equals(appName)) {
                    // prevent app repeats due to multiple factors
                    // (ex: facebook startup reveals 3 separate foreground events)
                    // NOTE: Also discards duplicates from separate navigation to same app.
                    Log.i(TAG, "App: \"" + appName + "\"" + " discarded as duplicate");

                } else {
                        order++; // increment counter to determine order of event.
                        sb.append(order)
                                .append(" ")
                                .append(appName);

                        // add string to arraylist
                        usageSequence.add(sb.toString());
                        prevApplicationName = appName;
                }
            }
        }

        // Return list containing sequence of apps used identified by their strings
        return usageSequence;
    }

    public void initExcludedAppSet() {
        // initialize HashSet and add elements
        excludedSet = new HashSet<>();

        // exclude app's name
        excludedSet.add(context.getString(R.string.app_name));

        // exclude other apps
        excludedSet.add("Google App");
        excludedSet.add("System UI");
        excludedSet.add("Settings");
        // general keyword
        excludedSet.add("Launcher");

    }

    public void addExludedAppToSet(String string) {
        // Add string argument provided by user to excludedSet
        excludedSet.add(string);
        Log.i(TAG, string +  " added to set of excluded apps");
    }

    public boolean setContainsAppName(String appName) {
        // initialize excludedSet
        initExcludedAppSet();


        // If app name is included in set
        if (excludedSet.contains(appName)) {
           return true;
        } else {
            // Iterate over HashMap to see if app name contains keyword
            for (String s : excludedSet) {
                if (appName.contains(s)) {
                    return true;
                }
            }

            // If app name or keyword from app name is not included in hash set, return false
            return false;
        }
    }


    public static void printCurrentUsageStats(Context context) {
        printUsageStats(getUsageStatsList(context));
    }

    @SuppressWarnings("ResourceType")
    private static UsageStatsManager getUsageStatsManager(Context context) {
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
        return usm;
    }


}
