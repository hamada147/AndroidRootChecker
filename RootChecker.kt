import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader


class RootChecker(private val context: Context) {

    /**
     * List of application that can be used to root the device
     */
    private val RootedAPKs = arrayOf(
        "com.noshufou.andriod.su",
        "com.thirdparty.superuser",
        "eu.chainfire.supersu",
        "com.koushikdutta.superuser",
        "com.devadvance.rootcloak2",
        "com.zachspong.temprootremovejb",
        "com.ramdroid.appquarantine"
    )

    /**
     * Main function to run to check if device is rooted or not
     * @return true if device is rooted, false if not
     */
    val isDeviceRooted: Boolean
        get() = checkRootMethod1() || checkRootMethod2() || checkRootMethod3() || checkRootMethod4(context)

    /**
     * Device is rooted if the build tags contains the provided tag
     * @return true if build tag exist, false if not
     */
    private fun checkRootMethod1(): Boolean {
        val buildTags = Build.TAGS
        val data = byteArrayOf(116, 101, 115, 116, 45, 107, 101, 121, 115)
        return buildTags != null && buildTags.contains(String(data))
    }

    /**
     * Device is rooted if the app have access to any of the provided directories
     * @return true if app have access to any of the directories, false if not
     */
    private fun checkRootMethod2(): Boolean {
        return try {
            val paths = arrayOf(
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
            false
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Run a shell command
     * @return true if command was executed and returned result, false if returned null
     */
    private fun checkRootMethod3(): Boolean {
        val execShell = ExecShell()
        return execShell.executeCommand(ExecShell.SHELL_CMD.check_su_binary) != null
    }

    /**
     * Check if the provided packages names are installed on the device or not
     * @param context current application context
     * @return true if any of the provided packages exist, false if none is installed
     */
    private fun checkRootMethod4(context: Context): Boolean {
        val packageManager: PackageManager = context.getPackageManager()
        for (s in RootedAPKs) {
            val intent = packageManager.getLaunchIntentForPackage(s)
            if (intent != null) {
                val list =
                    packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                return list.size > 0
            }
        }
        return false
    }

    /**
     * Created by Ahmed Moussa on 7/15/18.
     * This class represent my interface with the shell commands
     */
    private class ExecShell {
        /**
         * list of shell commands that I will be suing
         */
        enum class SHELL_CMD(var command: Array<String>) {
            check_su_binary(arrayOf<String>("/system/xbin/which", "su"));
        }

        /**
         * Run a shell command
         * @param shellCmd Shell command that will be executed
         * @return result of the given shell command
         */
        fun executeCommand(shellCmd: SHELL_CMD): ArrayList<String?>? {
            var line: String? = null
            val fullResponse = ArrayList<String?>()
            var localProcess: Process? = null
            localProcess = try {
                Runtime.getRuntime().exec(shellCmd.command)
            } catch (e: Exception) {
                return null
            }
            val IN = BufferedReader(InputStreamReader(localProcess.inputStream))
            try {
                while (IN.readLine().also { line = it } != null) {
                    fullResponse.add(line)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return fullResponse
        }
    }
}
