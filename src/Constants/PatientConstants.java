package Constants;

public class PatientConstants {
    public static final String CLIENT_TYPE_ADMIN = "ADMIN";
    public static final String CLIENT_TYPE_PATIENT = "PATIENT";
    public static final String CLIENT_SERVER_MONTREAL = "MONTREAL";
    public static final String CLIENT_SERVER_QUEBEC = "QUEBEC";
    public static final String CLIENT_SERVER_SHERBROOKE = "SHERBROOKE";
    private String patientType;
    private String patientID;
    private String patientServer;

    public PatientConstants(String patientID) {
        this.patientID = patientID;
        this.patientType = detectPatientType();
        this.patientServer = detectPatientServer();
    }

    private String detectPatientServer() {
        if (patientID.substring(0, 3).equalsIgnoreCase("MTL")) {
            return CLIENT_SERVER_MONTREAL;
        } else if (patientID.substring(0, 3).equalsIgnoreCase("QUE")) {
            return CLIENT_SERVER_QUEBEC;
        } else {
            return CLIENT_SERVER_SHERBROOKE;
        }
    }

    private String detectPatientType() {
        if (patientID.substring(3, 4).equalsIgnoreCase("M")) {
            return CLIENT_TYPE_ADMIN;
        } else {
            return CLIENT_TYPE_PATIENT;
        }
    }

    public String getPatientType() {
        return patientType;
    }


    public String getPatientID() {
        return patientID;
    }


    public String getPatientServer() {
        return patientServer;
    }



    @Override
    public String toString() {
        return getPatientType() + "(" + getPatientID() + ") on " + getPatientServer() + " Server.";
    }
}
