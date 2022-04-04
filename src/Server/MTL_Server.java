package Server;

import Constants.AppointmentConstants;
import InterfaceImplementation.DAMSInterfaceImplementation;
import Logger.Log;
import Patient.Patient;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import CORBAInterface.DAMSInterface;
import CORBAInterface.DAMSInterfaceHelper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MTL_Server {

    private static String serverID;
    private static String serverName;
    //private static int serverRegistryPort;
    private static int serverUdpPort;

    public MTL_Server() {
        this.serverID = "MTL";
        this.serverName = AppointmentConstants.APPOINTMENT_SERVER_MONTREAL;
       // this.serverRegistryPort = Patient.SERVER_PORT_MONTREAL;
        this.serverUdpPort = DAMSInterfaceImplementation.Montreal_Server_Port;
    }

    public static void main(String args[]) throws Exception{

        MTL_Server mtl_server = new MTL_Server();
//        DAMSInterfaceImplementation remoteObject = new DAMSInterfaceImplementation(serverID, serverName);
//        Registry registry = LocateRegistry.createRegistry(serverRegistryPort);
//        registry.bind(Patient.REGISTRY_NAME, remoteObject);

        try {
            // create and initialize the ORB //// get reference to rootpoa &amp; activate
            // the POAManager
            ORB orb = ORB.init(args, null);
            // -ORBInitialPort 1050 -ORBInitialHost localhost
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // create servant and register it with the ORB
            DAMSInterfaceImplementation servant = new DAMSInterfaceImplementation(serverID, serverName);
            servant.setORB(orb);

            // get object reference from the servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(servant);
            DAMSInterface href = DAMSInterfaceHelper.narrow(ref);

            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            NameComponent[] path = ncRef.to_name(serverID);
            ncRef.rebind(path, href);
      
            System.out.println(serverName + " Server is Up & Running");
            Log.serverLog(serverID, " Server is Up & Running");
            new AddTestData(servant,"MTL");
            Runnable task = () -> {
            listenForRequest(servant, serverUdpPort, serverName, serverID);
            };
        	Thread thread = new Thread(task);
        	thread.start();
      
        	while(true)
        	{
        		orb.run();
        	}
        }
        catch (Exception e) {
//          System.err.println("Exception: " + e);
          e.printStackTrace(System.out);
          Log.serverLog(serverID, "Exception: " + e);
      }

      System.out.println(serverName + " Server Shutting down");
      Log.serverLog(serverID, " Server Shutting down");
    }


    private static void listenForRequest(DAMSInterfaceImplementation obj, int serverUdpPort, String serverName, String serverID) {
        DatagramSocket aSocket = null;
        String sendingResult = "";
        try {
            aSocket = new DatagramSocket(serverUdpPort);
            byte[] buffer = new byte[1000];
            System.out.println(serverName + " UDP Server Started at port " + aSocket.getLocalPort() + " ............");
            Log.serverLog(serverID, " UDP Server Started at port " + aSocket.getLocalPort());
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                String sentence = new String(request.getData(), 0,
                        request.getLength());
                String[] parts = sentence.split(";");
                String method = parts[0];
                String patientID = parts[1];
                String appointmentType = parts[2];
                String appointmentID = parts[3];
                if (method.equalsIgnoreCase("removeAppointment")) {
                    Log.serverLog(serverID, patientID, " UDP request received " + method + " ", " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ", " ...");
                    String result = obj.removeAppointmentUDP(appointmentID, appointmentType, patientID);
                    sendingResult = result + ";";
                } else if (method.equalsIgnoreCase("listAppointmentAvailability")) {
                    Log.serverLog(serverID, patientID, " UDP request received " + method + " ", " appointmentType: " + appointmentType + " ", " ...");
                    String result = obj.listAppointmentAvailabilityUDP(appointmentType);
                    sendingResult = result + ";";
                } else if (method.equalsIgnoreCase("bookAppointment")) {
                    Log.serverLog(serverID, patientID, " UDP request received " + method + " ", " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ", " ...");
                    String result = obj.bookAppointment(patientID, appointmentID, appointmentType);
                    sendingResult = result + ";";
                } else if (method.equalsIgnoreCase("cancelAppointment")) {
                    Log.serverLog(serverID, patientID, " UDP request received " + method + " ", " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ", " ...");
                    String result = obj.cancelAppointment(patientID, appointmentID);
                    sendingResult = result + ";";
                }
                byte[] sendData = sendingResult.getBytes();
                DatagramPacket reply = new DatagramPacket(sendData, sendingResult.length(), request.getAddress(),
                        request.getPort());
                aSocket.send(reply);
                Log.serverLog(serverID, patientID, " UDP reply sent " + method + " ", " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ", sendingResult);
            }
        } catch (SocketException e) {
            System.out.println("SocketException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
    }

}

