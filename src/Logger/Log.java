package Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
    public static final int LOG_TYPE_SERVER = 1;
    public static final int LOG_TYPE_PATIENT = 0;

    public static void patientLog(String patientID, String requestType, String requestParams, String response) throws IOException {
        FileWriter fileWriter = new FileWriter(getFileName(patientID, LOG_TYPE_PATIENT), true);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println("====================================================");
        printWriter.println(" | DATE: " + getFormattedDate());
        printWriter.println(" | RequestType: " + requestType);
        printWriter.println(" | RequestParameters: " + requestParams);
        printWriter.println(" | Server Response: " + response);

        printWriter.close();
    }

    public static void patientLog(String patientID, String msg) throws IOException {
        FileWriter fileWriter = new FileWriter(getFileName(patientID, LOG_TYPE_PATIENT), true);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println("====================================================");
        printWriter.println(" | DATE: " + getFormattedDate());
        printWriter.println(" | " + msg);

        printWriter.close();
    }

    public static void serverLog(String serverID, String patientID, String requestType, String requestParams, String serverResponse) throws IOException {

        FileWriter fileWriter = new FileWriter(getFileName(serverID, LOG_TYPE_SERVER), true);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println("====================================================");
        printWriter.println("DATE: " + getFormattedDate() + " PatientID: " + patientID);
        printWriter.println(" | RequestType: " + requestType);
        printWriter.println(" | RequestParameters: " + requestParams);
        printWriter.println(" | ServerResponse: " + serverResponse);

        printWriter.close();
    }

    public static void serverLog(String serverID, String msg) throws IOException {

        FileWriter fileWriter = new FileWriter(getFileName(serverID, LOG_TYPE_SERVER), true);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println("====================================================");
        printWriter.println("| DATE: " + getFormattedDate());
        printWriter.println(" | " + msg);

        printWriter.close();
    }

    public static void deleteALogFile(String ID) throws IOException {

        String fileName = getFileName(ID, LOG_TYPE_PATIENT);
        File file = new File(fileName);
        file.delete();
    }

    private static String getFileName(String ID, int logType) {
        final String dir = System.getProperty("user.dir");
        String fileName = dir;
        if (logType == LOG_TYPE_SERVER) {
            if (ID.equalsIgnoreCase("MTL")) {
                fileName = dir + "\\src\\Logs\\Server\\Montreal.txt";
            } else if (ID.equalsIgnoreCase("SHE")) {
                fileName = dir + "\\src\\Logs\\Server\\Sherbrooke.txt";
            } else if (ID.equalsIgnoreCase("QUE")) {
                fileName = dir + "\\src\\Logs\\Server\\Quebec.txt";
            }
        } else {
            if(ID.substring(3,4).equals("A")){
                fileName = dir + "\\src\\Logs\\Users\\Admins\\" + ID + ".txt";
            }else{
                fileName = dir + "\\src\\Logs\\Users\\Patients\\" + ID + ".txt";
            }

        }
        return fileName;
    }

    private static String getFormattedDate() {
        Date date = new Date();

        String strDateFormat = "yyyy-MM-dd hh:mm:ss a";

        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);

        return dateFormat.format(date);
    }
}
