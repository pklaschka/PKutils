package ljlutils;

import javax.swing.*;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;

public class ErrorManager {
    private static class Error {
        public int errorCode;
        public String devMsg;
        public String userMsg;
        public boolean critical;

        public Error(int errorCode, String devMsg, String userMsg, boolean critical) {
            this.errorCode = errorCode;
            this.devMsg = devMsg;
            this.userMsg = userMsg;
            this.critical = critical;
        }

        @Override
        public String toString() {
            return "Error{" +
                    "errorCode=" + errorCode +
                    ", devMsg='" + devMsg + '\'' +
                    ", userMsg='" + userMsg + '\'' +
                    ", critical=" + critical +
                    '}';
        }
    }

    private static ArrayList<Error> errors = new ArrayList<>();
    private static boolean devMode = false;

    private static int nextErrorCode = 2;

    private static String userPreText = "An Error occured. Details:";
    private static String userPostText = "For support, please ask the developer of this software.";

    static {
        errors.add(0, new Error(0, "(ErrorManager Internal) The reserved error code -199 or -198 was used while calling ljlutils.ErrorManager.registerError(...).", "This is a non-critical error.", false));
        errors.add(1, new Error(1, "(ErrorManager Internal) The errorCode specified when calling ljlutils.ErrorManager.triggerError(int errorCode) wasn't found.", "This is a non-critical error.", false));
    }

    /**
     * Registers an error that can be called and returns its error number.
     * @param devMsg The message shown in developer mode (may contain Class names, line numbers etc.)
     * @param userMsg The message shown to the end user (which should explain the problem in plain language
     * @param critical Determines if an error window opens when the error gets triggered
     *
     * @return int The error code generated by the ErrorManager
     */
    public static int registerError(String devMsg, String userMsg, boolean critical) {
        if (nextErrorCode == 0 || nextErrorCode == 1) {
            triggerError(0);
            return -1;
        }
        errors.add(nextErrorCode, new Error(nextErrorCode, devMsg, userMsg, critical));
        return nextErrorCode++;
    }

    /**
     * Registers an error that can be called and returns its error number.
     * @param devMsg The message shown in developer mode (may contain Class names, line numbers etc.)
     * @param userMsg The message shown to the end user (which should explain the problem in plain language
     *
     * @return int The error code generated by the ErrorManager
     */
    public static int registerError(String devMsg, String userMsg) {
        return registerError(devMsg, userMsg, true);
    }

    /**
     * Registers an error that can be called and returns its error number.
     * @param msg The message shown to the end user and the developer
     *
     * @return int The error code generated by the ErrorManager
     */
    public static int registerError(String msg) {
        return registerError(msg, msg, true);
    }

    /**
     * Registers an error that can be called and returns its error number.
     * @param msg The message shown to the end user and the developer
     * @param critical Determines if an error window opens when the error gets triggered
     *
     * @return int The error code generated by the ErrorManager
     */
    public static int registerError(String msg, boolean critical) { return registerError(msg, msg, critical); }

    /**
     * Triggers the error with the specified code
     * @param errorCode The index of the error (by default beginning with 100)
     */
    public static void triggerError(int errorCode) {
        if (errors.get(errorCode) != null) {
            Error currentError = errors.get(errorCode);

            if (devMode) {
                System.err.println(currentError.errorCode + ": " + currentError.devMsg + "(Error triggered by Error Manager).");
            } else if (currentError.critical) {
                JOptionPane.showMessageDialog(new JFrame(), userPreText + "\n" + currentError.errorCode + ": " + currentError.userMsg + "\n" + userPostText, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Creates a list of all registered errors.
     * @return Multiline list of all registered errors.
     */
    public static String getRegisteredErrorsList() {
        StringBuilder s = new StringBuilder();
        s.append("Errors (generated: ");
        s.append(Date.from(Instant.now()).toLocaleString());
        s.append("):");

        for (Error e : errors) {
            s.append("\n- ");
            s.append(e.errorCode);
            s.append(": ");
            s.append(e.devMsg);
        }

        return s.toString();
    }

    public static boolean isDevMode() {
        return devMode;
    }

    public static void setDevMode(boolean devMode) {
        ErrorManager.devMode = devMode;
    }

    public static String getUserPreText() {
        return userPreText;
    }

    public static void setUserPreText(String userPreText) {
        ErrorManager.userPreText = userPreText;
    }

    public static String getUserPostText() {
        return userPostText;
    }

    public static void setUserPostText(String userPostText) {
        ErrorManager.userPostText = userPostText;
    }
}
