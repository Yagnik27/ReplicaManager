package InterfaceImplementation;

import Constants.AppointmentConstants;
import Constants.PatientConstants;
import Logger.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.omg.CORBA.ORB;

import CORBAInterface.DAMSInterfacePOA;

public class DAMSInterfaceImplementation extends DAMSInterfacePOA{
    public static final int Montreal_Server_Port = 7788;
    public static final int Quebec_Server_Port = 8899;
    public static final int Sherbrooke_Server_Port = 6677;
    private String serverID;
    private String serverName;
    private Map<String, TreeMap<String, AppointmentConstants>> allAppointments;  // HashMap<AppointmentType, HashMap <AppointmentID, Appointment>>    All server Appointments.
    private Map<String, Map<String, List<String>>> patientAppointments;   // HashMap<PatientID, HashMap <AppointmentType, List<AppointmentID>>>   Database for Patient Appointments.
    private Map<String, PatientConstants> serverPatients;       // HashMap<PatientID, Patient>   Storing All the Patients registered with this server
    private ORB orb_obj;

    public DAMSInterfaceImplementation(String serverID, String serverName) {
        super();
        this.serverID = serverID;
        this.serverName = serverName;
        allAppointments = new ConcurrentHashMap<>();
        allAppointments.put(AppointmentConstants.PHYSICIAN, new TreeMap<>());
        allAppointments.put(AppointmentConstants.SURGEON, new TreeMap<>());
        allAppointments.put(AppointmentConstants.DENTAL, new TreeMap<>());
        patientAppointments = new ConcurrentHashMap<>();
        serverPatients = new ConcurrentHashMap<>();
    }

    private static int getServerPort(String branchAcronym) {
        if (branchAcronym.equalsIgnoreCase("MTL")) {
            return Montreal_Server_Port;
        } else if (branchAcronym.equalsIgnoreCase("SHE")) {
            return Sherbrooke_Server_Port;
        } else if (branchAcronym.equalsIgnoreCase("QUE")) {
            return Quebec_Server_Port;
        }
        return 1;
    }
    
    public void setORB(ORB orb_val) {
        this.orb_obj = orb_val;
    }

    @Override
    public String addAppointment(String appointmentID, String appointmentType, int appointmentCapacity) {
        String response;
        if (allAppointments.get(appointmentType).containsKey(appointmentID)) {
                response = "Failed: Appointment Already Exists";
                try {
                    Log.serverLog(serverID, "Admin", " CORBA addAppointment ", " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " appointmentCapacity " + appointmentCapacity + " ", response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;

        }
        if (AppointmentConstants.detectAppointmentServer(appointmentID).equals(serverName)) {
            AppointmentConstants appointment = new AppointmentConstants(appointmentType, appointmentID, appointmentCapacity);
            TreeMap<String, AppointmentConstants> appointmentHashMap = allAppointments.get(appointmentType);
            appointmentHashMap.put(appointmentID, appointment);
            allAppointments.put(appointmentType, appointmentHashMap);
            response = "Success: Appointment " + appointmentID + " added successfully";
            try {
                Log.serverLog(serverID, "Admin", " CORBA addAppointment ", " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " appointmentCapacity " + appointmentCapacity + " ", response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        } else {
            response = "Failed: Cannot Add Appointment to servers other than " + serverName;
            try {
                Log.serverLog(serverID, "Admin", " CORBA addAppointment ", " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " appointmentCapacity " + appointmentCapacity + " ", response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
    }

    @Override
    public String removeAppointment(String appointmentID, String appointmentType) {
        String response;
        if (AppointmentConstants.detectAppointmentServer(appointmentID).equals(serverName)) {
            if (allAppointments.get(appointmentType).containsKey(appointmentID)) {
                List<String> registeredClients = allAppointments.get(appointmentType).get(appointmentID).getBookedPatientIDs();
                allAppointments.get(appointmentType).remove(appointmentID);
                //String nextAvailableAppointment = allAppointments.get(appointmentType).floorKey(appointmentID);
                addPatientsToNextSameAppointment(appointmentID, appointmentType, registeredClients);
                response = "Success: Appointment Removed Successfully";
                try {
                    Log.serverLog(serverID, "Admin", " CORBA removeAppointment ", " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ", response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            } else {
                response = "Failed: AppointmentID " + appointmentID + " does not exist";
                try {
                    Log.serverLog(serverID, "Admin", " CORBA removeAppointment ", " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ", response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;

            }
        } else {
            response = "Failed: Cannot Remove Appointment from servers other than " + serverName;
            try {
                Log.serverLog(serverID, "Admin", " CORBA removeAppointment ", " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ", response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
    }

    @Override
    public String listAppointmentAvailability(String appointmentType) {
        String response;
        Map<String, AppointmentConstants> Appointments = allAppointments.get(appointmentType);
        StringBuilder builder = new StringBuilder();
        builder.append(serverName + " Server " + appointmentType + ":\n");
        if (Appointments.size() == 0) {
            builder.append("No Appointments of Type " + appointmentType + "\n");
        } else {
            for (AppointmentConstants Appointment :
                    Appointments.values()) {
                builder.append(Appointment.toString() + "\n");
            }
            builder.append("\n=====================================\n");
        }
        String otherServer1, otherServer2;
        if (serverID.equals("MTL")) {
            otherServer1 = sendUDPMessage(Sherbrooke_Server_Port, "listAppointmentAvailability", "Admin", appointmentType, "null");
            otherServer2 = sendUDPMessage(Quebec_Server_Port, "listAppointmentAvailability", "Admin", appointmentType, "null");
        } else if (serverID.equals("SHE")) {
            otherServer1 = sendUDPMessage(Quebec_Server_Port, "listAppointmentAvailability", "Admin", appointmentType, "null");
            otherServer2 = sendUDPMessage(Montreal_Server_Port, "listAppointmentAvailability", "Admin", appointmentType, "null");
        } else {
            otherServer1 = sendUDPMessage(Montreal_Server_Port, "listAppointmentAvailability", "Admin", appointmentType, "null");
            otherServer2 = sendUDPMessage(Sherbrooke_Server_Port, "listAppointmentAvailability", "Admin", appointmentType, "null");
        }
        builder.append(otherServer1).append(otherServer2);
        response = builder.toString();
        try {
            Log.serverLog(serverID, "Admin", " CORBA listAppointmentAvailability ", " appointmentType: " + appointmentType + " ", response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public String bookAppointment(String patientID, String appointmentID, String appointmentType) {
        String response;
        serverPatients.putIfAbsent((new PatientConstants(patientID)).getPatientID(), new PatientConstants(patientID));
        patientAppointments.putIfAbsent((new PatientConstants(patientID)).getPatientID(),new ConcurrentHashMap<>());
//        if (!serverPatients.containsKey(patientID)) {
//            addNewPatientToClients(patientID);
//        }
        if (AppointmentConstants.detectAppointmentServer(appointmentID).equals(serverName)) {
            AppointmentConstants bookedAppointments = allAppointments.get(appointmentType).get(appointmentID);
                if (!bookedAppointments.isFull()) {
                    if (patientAppointments.containsKey(patientID)) {
                        if (patientAppointments.get(patientID).containsKey(appointmentType)) {
                                if (!patientAppointments.get(patientID).get(appointmentType).contains(appointmentID)) {
                                    if(!sameAppointmentDay(patientAppointments.get(patientID).get(appointmentType),appointmentID)){
                                        patientAppointments.get(patientID).get(appointmentType).add(appointmentID);
                                    } else {
                                            response = "Failed: You can only book one appointment of same AppointmentType in a Day! ";
                                            try {
                                                Log.serverLog(serverID, patientID, " CORBA bookAppointment ", " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ", response);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            return response;
                                        }
                                    }else{
                                        response = "Failed: Appointment " + appointmentID + " Already Booked";
                                        try {
                                            Log.serverLog(serverID, patientID, " CORBA bookAppointment ", " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ", response);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        return response;
                                    }//
                        } else {
                            List<String> temp = new ArrayList<>();
                            temp.add(appointmentID);
                            patientAppointments.get(patientID).put(appointmentType, temp);
                        }
                    } else {
                        Map<String, List<String>> temp = new ConcurrentHashMap<>();
                        List<String> temp2 = new ArrayList<>();
                        temp2.add(appointmentID);
                        temp.put(appointmentType, temp2);
                        patientAppointments.put(patientID, temp);
                    }
                    if (allAppointments.get(appointmentType).get(appointmentID).addBookedPatientIDs(patientID) == AppointmentConstants.ADD_SUCCESS) {
                        response = "Success: Appointment " + appointmentID + " Booked Successfully";
                    } else if (allAppointments.get(appointmentType).get(appointmentID).addBookedPatientIDs(patientID) == AppointmentConstants.APPOINTMENT_FULL) {
                        response = "Failed: Appointment " + appointmentID + " is Full";
                    } else {
                        response = "Failed: Cannot Add You To Appointment " + appointmentID;
                    }
                    try {
                        Log.serverLog(serverID, patientID, " CORBA bookAppointment ", " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ", response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    response = "Failed: Appointment " + appointmentID + " is Full";
                    try {
                        Log.serverLog(serverID, patientID, " CORBA bookAppointment ", " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ", response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            return response;
        } else {
            if (!exceedWeeklyLimit(patientID, appointmentID.substring(4))) {
                String serverResponse = sendUDPMessage(getServerPort(appointmentID.substring(0, 3)), "bookAppointment", patientID, appointmentType, appointmentID);
                if (serverResponse.startsWith("Success:")) {
                    if (patientAppointments.get(patientID).containsKey(appointmentType)) {
                        patientAppointments.get(patientID).get(appointmentType).add(appointmentID);
                    } else {
                        List<String> temp = new ArrayList<>();
                        temp.add(appointmentID);
                        patientAppointments.get(patientID).put(appointmentType, temp);
                    }
                }
                try {
                    Log.serverLog(serverID, patientID, " CORBA bookAppointment ", " appointmentType: " + appointmentID + " appointmentType: " + appointmentType + " ", serverResponse);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return serverResponse;
            } else {
                response = "Failed: You Cannot Book Appointment in Other Servers For This Week(Max Weekly Limit = 3)";
                try {
                    Log.serverLog(serverID, patientID, " CORBA bookAppointment ", " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ", response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            }
        }
    }

    @Override
    public String getAppointmentSchedule(String patientID) {
        String response;
        if (!serverPatients.containsKey(patientID)) {
            serverPatients.putIfAbsent((new PatientConstants(patientID)).getPatientID(), new PatientConstants(patientID));
            patientAppointments.putIfAbsent((new PatientConstants(patientID)).getPatientID(),new ConcurrentHashMap<>());
            //addNewPatientToClients(patientID);
            response = "Booking Schedule Empty For " + patientID;
            try {
                Log.serverLog(serverID, patientID, " CORBA getBookingSchedule ", "Admin", response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
        Map<String, List<String>> Appointments = patientAppointments.get(patientID);
        if (Appointments.size() == 0) {
            response = "Booking Schedule Empty For " + patientID;
            try {
                Log.serverLog(serverID, patientID, " CORBA getBookingSchedule ", "Admin", response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
        StringBuilder builder = new StringBuilder();
        for (String AppointmentType :
                Appointments.keySet()) {
            builder.append(AppointmentType + ":\n");
            for (String AppointmentID :
                    Appointments.get(AppointmentType)) {
                builder.append(AppointmentID + " ||");
            }
            builder.append("\n=====================================\n");
        }
        response = builder.toString();
        try {
            Log.serverLog(serverID, patientID, " CORBA getBookingSchedule ", "Admin", response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public String cancelAppointment(String patientID, String appointmentID) {
        String response;
        String appointmentTypes[] = {AppointmentConstants.PHYSICIAN,AppointmentConstants.SURGEON,AppointmentConstants.DENTAL};
        String appointmentType="";
        //String type;
        //int flag=0;
        for(String type:appointmentTypes){
            if(patientAppointments.get(patientID).get(type).contains(appointmentID)){
                appointmentType = type;
                //flag=1;
                break;
            }
        }
        if (AppointmentConstants.detectAppointmentServer(appointmentID).equals(serverName)) {
            if (patientID.substring(0, 3).equals(serverID)) {
                if (!serverPatients.containsKey(patientID)) {
                    serverPatients.putIfAbsent((new PatientConstants(patientID)).getPatientID(), new PatientConstants(patientID));
                    patientAppointments.putIfAbsent((new PatientConstants(patientID)).getPatientID(),new ConcurrentHashMap<>());
                    //addNewPatientToClients(patientID);
                    response = "Failed: You " + patientID + " Are Not Registered in " + appointmentID;
                    try {
                        Log.serverLog(serverID, patientID, " CORBA cancelAppointment ", " appointmentID: " + appointmentID + " ", response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return response;
                } else {
                    if (patientAppointments.get(patientID).get(appointmentType).remove(appointmentID)) {
                            allAppointments.get(appointmentType).get(appointmentID).removeBookedPatientID(patientID);
                            response = "Success: Appointment " + appointmentID + " Canceled for " + patientID;
                            try {
                                Log.serverLog(serverID, patientID, " CORBA cancelAppointment ", " appointmentID: " + appointmentID + " ", response);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return response;
                    } else {
                            response = "Failed: You " + patientID + " Are Not Registered in " + appointmentID;
                            try {
                                Log.serverLog(serverID, patientID, " CORBA cancelAppointment ", " appointmentID: " + appointmentID + " ", response);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return response;
                    }
                }
            } else {
                if (allAppointments.get(appointmentType).get(appointmentID).removeBookedPatientID(patientID)) {
                    response = "Success: Appointment " + appointmentID + " Canceled for " + patientID;
                    try {
                        Log.serverLog(serverID, patientID, " CORBA cancelAppointment ", " appointmentID: " + appointmentID + " ", response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return response;
                } else {
                    response = "Failed: You " + patientID + " Are Not Registered in " + appointmentID;
                    try {
                        Log.serverLog(serverID, patientID, " CORBA cancelAppointment ", " appointmentID: " + appointmentID + " ", response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return response;
                }
            }
        } else {
            if (patientID.substring(0, 3).equals(serverID)) {
                if (!serverPatients.containsKey(patientID)) {
                    serverPatients.putIfAbsent((new PatientConstants(patientID)).getPatientID(), new PatientConstants(patientID));
                    patientAppointments.putIfAbsent((new PatientConstants(patientID)).getPatientID(),new ConcurrentHashMap<>());
                    //addNewPatientToClients(patientID);
                } else {
                    if (patientAppointments.get(patientID).get(appointmentType).remove(appointmentID)) {
                        return sendUDPMessage(getServerPort(appointmentID.substring(0, 3)), "cancelAppointment", patientID, appointmentType, appointmentID);
                    }
                }
            }
            return "Failed: You " + patientID + " Are Not Registered in " + appointmentID;
        }
    }

    @Override
    public String swapAppointment(String patientID, String newAppointmentID, String newAppointmentType, String oldAppointmentID, String oldAppointmentType) {
        String response;
        if (!serverPatients.containsKey(patientID)) {
            serverPatients.putIfAbsent((new PatientConstants(patientID)).getPatientID(), new PatientConstants(patientID));
            patientAppointments.putIfAbsent((new PatientConstants(patientID)).getPatientID(),new ConcurrentHashMap<>());
            response = "Failed: You " + patientID + " Are Not Registered in " + oldAppointmentID;
            try {
                Log.serverLog(serverID, patientID, " CORBA swapAppointment ", " oldAppointmentID: " + oldAppointmentID + " oldAppointmentType: " + oldAppointmentType + " newAppointmentID: " + newAppointmentID + " newAppointmentType: " + newAppointmentType + " ", response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        } else {
            if (clientHasAppointment(patientID, oldAppointmentType, oldAppointmentID)) {
                String bookResp="";
                String cancelResp="";
                synchronized (this) {
                    try{
                    	bookResp = bookAppointment(patientID, newAppointmentID, newAppointmentType);
                    	if(bookResp.startsWith("Success")) {
                    		cancelResp = cancelAppointment(patientID, oldAppointmentID);
                    	}                    	

                   
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                if (bookResp.startsWith("Success:") && cancelResp.startsWith("Success:")) {
                    response = "Success: Appointment " + oldAppointmentID + " swapped with " + newAppointmentID;
                } else {
                    response = "Failed: Swapping Appointment " + newAppointmentID + " Booking reason: " + bookResp + " and oldAppointment " + oldAppointmentID;
                }
                try {
                    Log.serverLog(serverID, patientID, " CORBA swapEvent ", " oldEventID: " + oldAppointmentID + " oldEventType: " + oldAppointmentType + " newEventID: " + newAppointmentID + " newEventType: " + newAppointmentType + " ", response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            } else {
                response = "Failed: You " + patientID + " Are Not Registered in " + oldAppointmentID;
                try {
                    Log.serverLog(serverID, patientID, " CORBA swapEvent ", " oldEventID: " + oldAppointmentID + " oldEventType: " + oldAppointmentType + " newEventID: " + newAppointmentID + " newEventType: " + newAppointmentType + " ", response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            }
        }
    }

    public String removeAppointmentUDP(String oldAppointmentID, String appointmentType, String patientID) throws RemoteException {
        if (!serverPatients.containsKey(patientID)) {
            serverPatients.putIfAbsent((new PatientConstants(patientID)).getPatientID(), new PatientConstants(patientID));
            patientAppointments.putIfAbsent((new PatientConstants(patientID)).getPatientID(),new ConcurrentHashMap<>());
            //addNewPatientToClients(patientID);
            return "Failed: You " + patientID + " Are Not Registered in " + oldAppointmentID;
        } else {
            if (patientAppointments.get(patientID).get(appointmentType).remove(oldAppointmentID)) {
                return "Success: Appointment " + oldAppointmentID + " Was Removed from " + patientID + " Schedule";
            } else {
                return "Failed: You " + patientID + " Are Not Registered in " + oldAppointmentID;
            }
        }
    }

    public String listAppointmentAvailabilityUDP(String appointmentType) throws RemoteException {
        Map<String, AppointmentConstants> Appointments = allAppointments.get(appointmentType);
        StringBuilder builder = new StringBuilder();
        builder.append(serverName + " Server " + appointmentType + ":\n");
        if (Appointments.size() == 0) {
            builder.append("No Appointments of Type " + appointmentType + "\n");
        } else {
            for (AppointmentConstants Appointment :
                    Appointments.values()) {
                builder.append(Appointment.toString() + "\n");
            }
        }
        builder.append("\n=====================================\n");
        return builder.toString();
    }

    private String sendUDPMessage(int serverPort, String method, String patientID, String appointmentType, String appointmentID) {
        DatagramSocket aSocket = null;
        String result = "";
        String dataFromClient = method + ";" + patientID + ";" + appointmentType + ";" + appointmentID;
        try {
            Log.serverLog(serverID, patientID, " UDP request sent " + method + " ", " appointmentID " + appointmentID + " appointmentType: " + appointmentType + " ", " ... ");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            aSocket = new DatagramSocket();
            byte[] message = dataFromClient.getBytes();
            InetAddress aHost = InetAddress.getByName("localhost");
            DatagramPacket request = new DatagramPacket(message, dataFromClient.length(), aHost, serverPort);
            aSocket.send(request);

            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

            aSocket.receive(reply);
            result = new String(reply.getData());
            String[] parts = result.split(";");
            result = parts[0];
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
        try {
            Log.serverLog(serverID, patientID, " UDP reply received" + method + " ", " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ", result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }


    private boolean exceedWeeklyLimit(String patientID, String appointmentDate) {
        int limit = 0;
        for (int i = 0; i < 3; i++) {
            List<String> registeredIDs = new ArrayList<>();
            switch (i) {
                case 0:
                    if (patientAppointments.get(patientID).containsKey(AppointmentConstants.PHYSICIAN)) {
                        registeredIDs = patientAppointments.get(patientID).get(AppointmentConstants.PHYSICIAN);
                    }
                    break;
                case 1:
                    if (patientAppointments.get(patientID).containsKey(AppointmentConstants.SURGEON)) {
                        registeredIDs = patientAppointments.get(patientID).get(AppointmentConstants.SURGEON);
                    }
                    break;
                case 2:
                    if (patientAppointments.get(patientID).containsKey(AppointmentConstants.DENTAL)) {
                        registeredIDs = patientAppointments.get(patientID).get(AppointmentConstants.DENTAL);
                    }
                    break;
            }
            for (String appointmentID :
                    registeredIDs) {
                if (appointmentID.substring(6, 8).equals(appointmentDate.substring(2, 4)) && appointmentID.substring(8, 10).equals(appointmentDate.substring(4, 6))) {
                    int week1 = Integer.parseInt(appointmentID.substring(4, 6)) / 7;
                    int week2 = Integer.parseInt(appointmentDate.substring(0, 2)) / 7;
                    if (week1 == week2) {
                        limit++;
                    }
                }
                if (limit == 3)
                    return true;
            }
        }
        return false;
    }
    public void addNewAppointment(String appointmentID, String appointmentType, int appointmentCapacity) {
        AppointmentConstants sampleConf = new AppointmentConstants(appointmentType, appointmentID, appointmentCapacity);
        allAppointments.get(appointmentType).put(appointmentID, sampleConf);
    }

    private boolean clientHasAppointment(String patientID, String appointmentType, String appointmentID) {
        if (patientAppointments.get(patientID).containsKey(appointmentType)) {
            return patientAppointments.get(patientID).get(appointmentType).contains(appointmentID);
        } else {
            return false;
        }
    }

    private boolean onTheSameWeek(String newEventDate, String appointmentID) {
        if (appointmentID.substring(6, 8).equals(newEventDate.substring(2, 4)) && appointmentID.substring(8, 10).equals(newEventDate.substring(4, 6))) {
            int week1 = Integer.parseInt(appointmentID.substring(4, 6)) / 7;
            int week2 = Integer.parseInt(newEventDate.substring(0, 2)) / 7;
//                    int diff = Math.abs(day2 - day1);
            return week1 == week2;
        } else {
            return false;
        }
    }

    public void addNewPatientToClients(String patientID) {
        serverPatients.putIfAbsent((new PatientConstants(patientID)).getPatientID(), new PatientConstants(patientID));
        patientAppointments.putIfAbsent((new PatientConstants(patientID)).getPatientID(),new ConcurrentHashMap<>());
    }

    private boolean sameAppointmentDay(List<String> presentAppointmentID, String toBookAppointmentID){
        int date2 = Integer.parseInt(toBookAppointmentID.substring(8, 10) + toBookAppointmentID.substring(6, 8) + toBookAppointmentID.substring(4, 6));
        for(String id : presentAppointmentID){
            int date1 = Integer.parseInt(id.substring(8, 10) + id.substring(6, 8) + id.substring(4, 6));
            if(date1 == date2){
                return true;
            }
        }
        return false;
    }

    private void addPatientsToNextSameAppointment(String oldAppointmentID, String appointmentType, List<String> registeredPatients) {
//
        for (String patientID :
                registeredPatients) {
            if (patientID.substring(0, 3).equals(serverID)) {
                patientAppointments.get(patientID).get(appointmentType).remove(oldAppointmentID);
                String nextAvailableAppointmentID = allAppointments.get(appointmentType).floorKey(oldAppointmentID);
                //String nextSameAppointmentResult = getNextSameAppointment(allAppointments.get(appointmentType).keySet(), appointmentType, oldAppointmentID);
                if (!allAppointments.get(appointmentType).get(nextAvailableAppointmentID).isFull()) {
                    bookAppointment(patientID, nextAvailableAppointmentID, appointmentType);
                }else {
                    return;
                }
            } else {
                sendUDPMessage(getServerPort(patientID.substring(0, 3)), "removeAppointment", patientID, appointmentType, oldAppointmentID);
            }
        }
    }

	

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

 }
