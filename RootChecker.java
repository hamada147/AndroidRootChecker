import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.io.File;
import java.util.List;

// This will change based on where you set the ExecShell class
import ExecShell;

/**
 * Created by Ahmed Moussa on 7/15/18.
 * This is the main class that is responsible for checking if the device is rooted or not
 */
public class RootChecker {

    /**
     * application context to use to search the installed packages for a rooting application
     */
    private Context context;

    /**
     * List of application that can be used to root the device
     * TODO: Make this list populated from a remote DB Source
     */
    private final String[] RootedAPKs = new String[] {
            "com.noshufou.andriod.su" ,
            "com.thirdparty.superuser",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser",
            "com.devadvance.rootcloak2"
    };

    /**
     * RootChecker Constructor
     * @param context access to package manager which will be used to check if rooted application are installed or not
     */
    public RootChecker(Context context) {
        this.context = context;
    }

    /**
     * Main function to run to check if device is rooted or not
     * @return true if device is rooted, false if not
     */
    public boolean isDeviceRooted() {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3() || checkRootMethod4(this.context);
    }

    /**
     * Device is rooted if the build tags contains the provided tag
     * @return true if build tag exist, false if not
     */
    private boolean checkRootMethod1() {
        String buildTags = android.os.Build.TAGS;
        byte[] data = {116, 101, 115, 116, 45, 107, 101, 121, 115};
        return buildTags != null && buildTags.contains(new String(data));
    }

    /**
     * Device is rooted if the app have access to any of the provided directories
     * @return true if app have access to any of the directories, false if not
     */
    private boolean checkRootMethod2() {
        try {
            String[] paths = {
                    "/system/app/Superuser.apk",
                    "/sbin/su",
                    "/system/bin/su",
                    "/system/xbin/su",
                    "/data/local/xbin/su",
                    "/data/local/bin/su",
                    "/system/sd/xbin/su",
                    "/system/bin/failsafe/su",
                    "/data/local/su",
                    "/su/bin/su",
                    "/sbin/su",
                    "/system/su",
                    "/system/bin/.ext/.su"
            };
            for (String path : paths) {
                if (new File(path).exists()) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Run a shell command
     * @return true if command was executed and returned result, false if returned null
     */
    private boolean checkRootMethod3() {
        ExecShell execShell = new ExecShell();
        return execShell.executeCommand(ExecShell.SHELL_CMD.check_su_binary) != null;
    }

    /**
     * Check if the provided packages names are installed on the device or not
     * @param context current application context
     * @return true if any of the provided packages exist, false if none is installed
     */
    private boolean checkRootMethod4(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        for (String s : this.RootedAPKs) {
            Intent intent = packageManager.getLaunchIntentForPackage(s);
            if (intent != null) {
                List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                return list.size() > 0;
            }
        }
        return false;
    }

}
