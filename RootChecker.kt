import android.content.Context
import android.content.pm.PackageManager
import java.io.File
/**
 * Created by Ahmed Moussa on 7/15/18.
 * This is the main class that is responsible for checking if the device is rooted or not
 */
class RootChecker (context:Context) {

    /**
     * application context to use to search the installed packages for a rooting application
     */
    private val context:Context

    /**
     * List of application that can be used to root the device
     */
    private val RootedAPKs = arrayOf<String>(
            "com.noshufou.andriod.su",
            "com.thirdparty.superuser",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser",
            "com.devadvance.rootcloak2"
    )

    /**
     * RootChecker Constructor
     * @param context access to package manager which will be used to check if rooted application are installed or not
     */
    init {
        this.context = context
    }

    /**
     * Main function to run to check if device is rooted or not
     * @return true if device is rooted, false if not
     */
    val isDeviceRooted:Boolean
        get() {
            return checkRootMethod1() || checkRootMethod2() || checkRootMethod3() || checkRootMethod4(this.context)
        }

    /**
     * Device is rooted if the build tags contains the provided tag
     * @return true if build tag exist, false if not
     */
    private fun checkRootMethod1():Boolean {
        val buildTags = android.os.Build.TAGS
        val data = byteArrayOf(116, 101, 115, 116, 45, 107, 101, 121, 115)
        return buildTags != null && buildTags.contains(String(data))
    }

    /**
     * Device is rooted if the app have access to any of the provided directories
     * @return true if app have access to any of the directories, false if not
     */
    private fun checkRootMethod2():Boolean {
        try
        {
            val paths = arrayOf<String>(
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
            )

            for (path in paths) {
                if (File(path).exists()) {
                    return true
                }
            }
            return false
        }
        catch (e:Exception) {
            return false
        }
    }
    /**
     * Run a shell command
     * @return true if command was executed and returned result, false if returned null
     */
    private fun checkRootMethod3():Boolean {
        val execShell = ExecShell()
        return execShell.executeCommand(ExecShell.SHELL_CMD.check_su_binary) != null
    }
    /**
     * Check if the provided packages names are installed on the device or not
     * @param context current application context
     * @return true if any of the provided packages exist, false if none is installed
     */
    private fun checkRootMethod4(context:Context):Boolean {
        val packageManager = context.packageManager
        for (s in this.RootedAPKs) {
            val intent = packageManager.getLaunchIntentForPackage(s)
            if (intent != null) {
                val list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                return list.size > 0
            }
        }
        return false
    }
}
