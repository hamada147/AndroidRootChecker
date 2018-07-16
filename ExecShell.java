import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by Ahmed Moussa on 7/15/18.
 * This class represent my interface with the shell commands
 */
public class ExecShell {

    /**
     * list of shell commands that I will be suing
     */
    public enum SHELL_CMD {
        check_su_binary(new String[] { "/system/xbin/which", "su" });
        String[] command;
        SHELL_CMD(String[] command) {
            this.command = command;
        }
    }

    /**
     * Run a shell command
     * @param shellCmd Shell command that will be executed
     * @return result of the given shell command
     */
    public ArrayList<String> executeCommand(SHELL_CMD shellCmd) {
        String line = null;
        ArrayList<String> fullResponse = new ArrayList<String>();
        Process localProcess = null;
        try {
            localProcess = Runtime.getRuntime().exec(shellCmd.command);
        } catch (Exception e) {
            return null;
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(localProcess.getInputStream()));
        try {
            while ((line = in.readLine()) != null) {
                fullResponse.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fullResponse;
    }

}
