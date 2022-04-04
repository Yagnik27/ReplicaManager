package Patient;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import Server.MTL_Server;
import Server.QUE_Server;
import Server.SHE_Server;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import CORBAInterface.DAMSInterface;
import CORBAInterface.DAMSInterfaceHelper;
import Constants.AppointmentConstants;
import Logger.Log;

public class Patient extends SHE_Server {
    public static final String USER_TYPE_PATIENT = "patient";
    public static final String USER_TYPE_ADMIN = "admin";
    public static final int PATIENT_BOOK_APPOINTMENT = 1;
    public static final int PATIENT_GET_APPOINTMENT_SCHEDULE = 2;
    public static final int PATIENT_CANCEL_APPOINTMENT = 3;
    public static final int PATIENT_SWAP_APPOINTMENT = 4;
    public static final int PATIENT_LOGOUT = 5;
    public static final int ADMIN_ADD_APPOINTMENT = 1;
    public static final int ADMIN_REMOVE_APPOINTMENT = 2;
    public static final int ADMIN_LIST_APPOINTMENT_AVAILABILITY = 3;
    public static final int ADMIN_BOOK_APPOINTMENT = 4;
    public static final int ADMIN_GET_APPOINTMENT_SCHEDULE = 5;
    public static final int ADMIN_CANCEL_APPOINTMENT = 6;
    public static final int ADMIN_SWAP_APPOINTMENT = 7;
    public static final int ADMIN_LOGOUT = 8;
    public static final int SHUTDOWN = 0;

    static Scanner input;

    public static void main(String[] args) throws Exception {
        try
        {
        	ORB orb_obj=ORB.init(args,null);
    		org.omg.CORBA.Object objRef = orb_obj.resolve_initial_references("NameService");
    		NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
    		init(ncRef);
        }
        catch (Exception e) {
            System.out.println("Client ORB init exception: " + e);
            e.printStackTrace();
        }
    }

    public static void init(NamingContextExt ncRef) throws IOException {
        input = new Scanner(System.in);
        String userID;
        int serverport;
        System.out.println("Please Enter your UserID:");
        userID = input.next().trim().toUpperCase();
        Log.patientLog(userID, " login attempt");
        if(checkUserType(userID) == USER_TYPE_PATIENT){
            try {
                System.out.println("Patient Login successful (" + userID + ")");
                Log.patientLog(userID, " Patient Login successful");
//                serverport = getServerPort(userID.substring(0, 3));
                patient(userID, ncRef);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(checkUserType(userID) == USER_TYPE_ADMIN){
            try {
                System.out.println("Admin Login successful (" + userID + ")");
                Log.patientLog(userID, " Admin Login successful");
//                serverport = getServerPort(userID.substring(0, 3));
                admin(userID, ncRef);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("!!Please enter the UserID again!!");
            Log.patientLog(userID, " UserID is not in correct format");
            Log.deleteALogFile(userID);

        }
        init(ncRef);
   }

    private static String getServerID(String patientID) {
        String branchAcronym = patientID.substring(0, 3);
        if (branchAcronym.equalsIgnoreCase("MTL")) {
            return branchAcronym;
        } else if (branchAcronym.equalsIgnoreCase("SHE")) {
            return branchAcronym;
        } else if (branchAcronym.equalsIgnoreCase("QUE")) {
            return branchAcronym;
        }
        return "1";
    }

    private static String checkUserType(String userID) {
        if (userID.length() == 8) {
            if (userID.substring(0, 3).equalsIgnoreCase("MTL") ||
                    userID.substring(0, 3).equalsIgnoreCase("QUE") ||
                    userID.substring(0, 3).equalsIgnoreCase("SHE")) {
                if (userID.substring(3, 4).equalsIgnoreCase("P")) {
                    return USER_TYPE_PATIENT;
                } else if (userID.substring(3, 4).equalsIgnoreCase("A")) {
                    return USER_TYPE_ADMIN;
                }else{
                    System.out.println("Please enter the correct User Type (e.g 'A' for Admin and 'P' for Patient)");
                }
            }else{
                System.out.println("Please enter the correct ServerID (e.g QUE)");
            }
        }else{
            System.out.println("The UserID should be of 8 characters!");
        }
        return "";
    }

    private static void patient(String patientID, NamingContextExt ncRef) throws Exception {
        String ServerID=getServerID(patientID);
    	if (ServerID.equals("1")) {
            init(ncRef);
        }
//        Registry registry = LocateRegistry.getRegistry(serverPort);
//        DAMSInterface servant = (DAMSInterface) registry.lookup(REGISTRY_NAME);
    	DAMSInterface servant = DAMSInterfaceHelper.narrow(ncRef.resolve_str(ServerID));
        boolean repeat = true;
        printMenu(USER_TYPE_PATIENT);
        int menuSelection = input.nextInt();
        String appointmentType;
        String appointmentID;
        String serverResponse;
        switch (menuSelection) {
            case PATIENT_BOOK_APPOINTMENT:
                appointmentType = promptForAppointmentType();
                appointmentID = promptForAppointmentID();
                Log.patientLog(patientID, " attempting to bookAppointment");
                serverResponse = servant.bookAppointment(patientID, appointmentID, appointmentType);
                System.out.println(serverResponse);
                Log.patientLog(patientID, " bookAppointment", " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ", serverResponse);
                break;
            case PATIENT_GET_APPOINTMENT_SCHEDULE:
                Log.patientLog(patientID, " attempting to getAppointmentSchedule");
                serverResponse = servant.getAppointmentSchedule(patientID);
                System.out.println(serverResponse);
                Log.patientLog(patientID, " getAppointmentSchedule", " null ", serverResponse);
                break;
            case PATIENT_CANCEL_APPOINTMENT:
                appointmentType = promptForAppointmentType();
                appointmentID = promptForAppointmentID();
                Log.patientLog(patientID, " attempting to cancelAppointment");
                serverResponse = servant.cancelAppointment(patientID, appointmentID);
                System.out.println(serverResponse);
                Log.patientLog(patientID, " cancelAppointment", " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ", serverResponse);
                break;
            case PATIENT_SWAP_APPOINTMENT:
                System.out.println("Please Enter the OLD Appointment Type to be replaced");
                appointmentType = promptForAppointmentType();
                appointmentID = promptForAppointmentID();
                System.out.println("Please Enter the NEW APPOINTMENT to be replaced");
                String newAppointmentType = promptForAppointmentType();
                String newAppointmentID = promptForAppointmentID();
                Log.patientLog(patientID, " attempting to swapAppointment");
                serverResponse = servant.swapAppointment(patientID, newAppointmentID, newAppointmentType, appointmentID, appointmentType);
                System.out.println(serverResponse);
                Log.patientLog(patientID, " swapAppointment", " oldappointmentID: " + appointmentID + " oldappointmentType: " + appointmentType + " newAppointmentID: " + newAppointmentID + " newAppointmentType: " + newAppointmentType + " ", serverResponse);
                break;
            case SHUTDOWN:
                Log.patientLog(patientID, " attempting ORB shutdown");
                servant.shutdown();
                Log.patientLog(patientID, " shutdown");
                return;
            	
            case PATIENT_LOGOUT:
                repeat = false;
                Log.patientLog(patientID, " attempting to Logout");
                init(ncRef);
                break;
        }
        if (repeat) {
            patient(patientID, ncRef);
        }
    }

    private static void admin(String adminID, NamingContextExt ncRef) throws Exception {
        String ServerID=getServerID(adminID);
    	if (ServerID.equals("1")) {
            init(ncRef);
        }
//        Registry registry = LocateRegistry.getRegistry(serverPort);
//        DAMSInterface servant = (DAMSInterface) registry.lookup(REGISTRY_NAME);
    	DAMSInterface servant = DAMSInterfaceHelper.narrow(ncRef.resolve_str(ServerID));
        boolean repeat = true;
        printMenu(USER_TYPE_ADMIN);
        String patientID;
        String appointmentType;
        String appointmentID;
        String serverResponse;
        int capacity;
        int menuSelection = input.nextInt();
        switch (menuSelection) {
            case ADMIN_ADD_APPOINTMENT:
                appointmentType = promptForAppointmentType();
                appointmentID = promptForAppointmentID();
                capacity = promptForCapacity();
                Log.patientLog(adminID, " attempting to addAppointment");
                serverResponse = servant.addAppointment(appointmentID, appointmentType, capacity);
                System.out.println(serverResponse);
                Log.patientLog(adminID, " addAppointment", " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " appointmentCapacity: " + capacity + " ", serverResponse);
                break;
            case ADMIN_REMOVE_APPOINTMENT:
                appointmentType = promptForAppointmentType();
                appointmentID = promptForAppointmentID();
                Log.patientLog(adminID, " attempting to removeAppointment");
                serverResponse = servant.removeAppointment(appointmentID, appointmentType);
                System.out.println(serverResponse);
                Log.patientLog(adminID, " removeAppointment", " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ", serverResponse);
                break;
            case ADMIN_LIST_APPOINTMENT_AVAILABILITY:
                appointmentType = promptForAppointmentType();
                Log.patientLog(adminID, " attempting to listAppointmentAvailability");
                serverResponse = servant.listAppointmentAvailability(appointmentType);
                System.out.println(serverResponse);
                Log.patientLog(adminID, " listAppointmentAvailability", " appointmentType: " + appointmentType + " ", serverResponse);
                break;
            case ADMIN_BOOK_APPOINTMENT:
                patientID = askForPatientIDFromAdmin(adminID.substring(0, 3));
                appointmentType = promptForAppointmentType();
                appointmentID = promptForAppointmentID();
                Log.patientLog(adminID, " attempting to bookAppointment");
                serverResponse = servant.bookAppointment(patientID, appointmentID, appointmentType);
                System.out.println(serverResponse);
                Log.patientLog(adminID, " bookAppointment", " patientID: " + patientID + " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ", serverResponse);
                break;
            case ADMIN_GET_APPOINTMENT_SCHEDULE:
                patientID = askForPatientIDFromAdmin(adminID.substring(0, 3));
                Log.patientLog(adminID, " attempting to getAppointmentSchedule");
                serverResponse = servant.getAppointmentSchedule(patientID);
                System.out.println(serverResponse);
                Log.patientLog(adminID, " getAppointmentSchedule", " patientID: " + patientID + " ", serverResponse);
                break;
            case ADMIN_CANCEL_APPOINTMENT:
                patientID = askForPatientIDFromAdmin(adminID.substring(0, 3));
                appointmentType = promptForAppointmentType();
                appointmentID = promptForAppointmentID();
                Log.patientLog(adminID, " attempting to cancelAppointment");
                serverResponse = servant.cancelAppointment(patientID, appointmentID);
                System.out.println(serverResponse);
                Log.patientLog(adminID, " cancelAppointment", " patientID: " + patientID + " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ", serverResponse);
                break;
            case ADMIN_SWAP_APPOINTMENT:
                System.out.println("Please Enter the OLD Appointment Type to be replaced");
                appointmentType = promptForAppointmentType();
                appointmentID = promptForAppointmentID();
                System.out.println("Please Enter the NEW APPOINTMENT to be replaced");
                String newAppointmentType = promptForAppointmentType();
                String newAppointmentID = promptForAppointmentID();
                Log.patientLog(adminID, " attempting to swapAppointment");
                serverResponse = servant.swapAppointment(adminID, newAppointmentID, newAppointmentType, appointmentID, appointmentType);
                System.out.println(serverResponse);
                Log.patientLog(adminID, " swapAppointment", " oldappointmentID: " + appointmentID + " oldappointmentType: " + appointmentType + " newAppointmentID: " + newAppointmentID + " newAppointmentType: " + newAppointmentType + " ", serverResponse);
                break;
            case SHUTDOWN:
                Log.patientLog(adminID, " attempting ORB shutdown");
                servant.shutdown();
                Log.patientLog(adminID, " shutdown");
                return;
            case ADMIN_LOGOUT:
                repeat = false;
                Log.patientLog(adminID, "attempting to Logout");
                init(ncRef);
                break;
        }
        if (repeat) {
            admin(adminID, ncRef);
        }
    }

    private static String askForPatientIDFromAdmin(String branchAcronym) {
        System.out.println("Please enter a patientID in" + branchAcronym + " Server):");
        String userID = input.next().trim().toUpperCase();
        if (checkUserType(userID) != USER_TYPE_PATIENT || !userID.substring(0, 3).equals(branchAcronym)) {
            System.out.println("Please enter correct patientID in " + branchAcronym + " Server):");
            return askForPatientIDFromAdmin(branchAcronym);
        } else {
            return userID;
        }
    }

    private static void printMenu(String userType) {
        System.out.println("*************************************");
        System.out.println("Please choose an option below:");
        if (userType == USER_TYPE_PATIENT) {
        	System.out.println("0.Shutdown");
            System.out.println("1.Book Appointment");
            System.out.println("2.Get Appointment Schedule");
            System.out.println("3.Cancel Appointment");
            System.out.println("4.Swap Appointment");
            System.out.println("5.Logout");
        } else if (userType == USER_TYPE_ADMIN) {
        	System.out.println("0.Shutdown");
            System.out.println("1.Add Appointment");
            System.out.println("2.Remove Appointment");
            System.out.println("3.List Appointment Availability");
            System.out.println("4.Book Appointment");
            System.out.println("5.Get Appointment Schedule");
            System.out.println("6.Cancel Appointment");
            System.out.println("7.Swap Appointment");
            System.out.println("8.Logout");
        }
    }

    private static String promptForAppointmentType() {
        System.out.println("*************************************");
        System.out.println("Please select the type of Appointment from the options below:");
        System.out.println("1.Physician");
        System.out.println("2.Surgeon");
        System.out.println("3.Dental");
        int option  = input.nextInt();
        if(option == 1){
            return AppointmentConstants.PHYSICIAN;
        }else if(option == 2){
            return AppointmentConstants.SURGEON;
        }else if(option == 3){
            return AppointmentConstants.DENTAL;
        }else{
            System.out.println("Please select from the provided options!");

        }
        return promptForAppointmentType();
    }

    private static String promptForAppointmentID() {
        System.out.println("*************************************");
        System.out.println("Please enter the correct AppointmentID (e.g MTLM190120)");
        String appointmentID = input.next().trim().toUpperCase();
        if (appointmentID.length() == 10) {
            if (appointmentID.substring(0, 3).equalsIgnoreCase("MTL") ||
                    appointmentID.substring(0, 3).equalsIgnoreCase("SHE") ||
                    appointmentID.substring(0, 3).equalsIgnoreCase("QUE")) {
                if (appointmentID.substring(3, 4).equalsIgnoreCase("M") ||
                        appointmentID.substring(3, 4).equalsIgnoreCase("A") ||
                        appointmentID.substring(3, 4).equalsIgnoreCase("E")) {
                    return appointmentID;
                }else{
                    System.out.println("Please enter the correct time slot of Appointment (e.g 'A' for afternoon, 'E' for Evening)");
                }
            }else{
                System.out.println("Please enter the correct Server in AppointmentID (e.g MTL)");
            }
        }else{
            System.out.println("The AppointmentID should be of 10 characters!");
        }
        return promptForAppointmentID();
    }

    private static int promptForCapacity() {
        System.out.println("*************************************");
        System.out.println("Please enter the Appointment capacity:");
        return input.nextInt();
    }
}
