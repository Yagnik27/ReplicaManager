package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import CORBAInterface.DAMSInterface;
import CORBAInterface.DAMSInterfaceHelper;
import Constants.AppointmentConstants;
import InterfaceImplementation.DAMSInterfaceImplementation;
import Logger.Log;

public class ReplicaManager {

	static List<String> request_queue = new ArrayList<>();
	static int reqProcessed = 0;
	static int reqPending = 0;

	static DAMSInterface mtlServerObj = null;
	static DAMSInterface queServerObj = null;
	static DAMSInterface sheServerObj = null;

	static Logger logger = null;
	static String[] args = {"-ORBInitialPort", "1050", "-ORBInitialHost", "localhost"};
	ReplicaManager() {


	}
	public static void StartServerMtl() {
		String serverName = AppointmentConstants.APPOINTMENT_SERVER_MONTREAL;
		String serverID = "MTL";
		try{
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

			// wait for invocations from clients
			for (;;){
				orb.run();
			}
		} 

		catch (Exception e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}

		System.out.println("MTL Server Exiting ...");
	}
	public static void StartServerQue() {
		String serverName = AppointmentConstants.APPOINTMENT_SERVER_QUEBEC;
		String serverID = "QUE";
		try{
			// create and initialize the ORB //// get reference to rootpoa &amp; activate the POAManager
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
			// wait for invocations from clients
			for (;;){
				orb.run();
			}
		} 

		catch (Exception e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}

		System.out.println("Quebec Server Exiting ...");
	}

	public static void StartServerShe() {
		String serverName = AppointmentConstants.APPOINTMENT_SERVER_SHERBROOKE;
		String serverID = "SHE";
		try{
			// create and initialize the ORB //// get reference to rootpoa &amp; activate the POAManager
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

			// wait for invocations from clients
			for (;;){
				orb.run();
			}
		} 

		catch (Exception e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}

		System.out.println("Sherbrook Server Exiting ...");
	}

    // Admin Client Methods
	public static void addAppointment(String appointmentId, String appointmentType, int capacity) {
		System.out.println(appointmentId);System.out.println(appointmentType);System.out.println(capacity);
        String server = appointmentId.substring(0,3);
		DAMSInterface ams = null;
        if(server.equals("MTL")) {
            ams = mtlServerObj;
        }
        else if(server.equals("QUE")) {
            ams = queServerObj;
        }
        else if(server.equals("SHE")) {
            ams = sheServerObj;
        }
        String status = ams.addAppointment(appointmentId, appointmentType, capacity);

        System.out.println(status);
    }

    public static void removeAppointment(String appointmentId, String appointmentType) {
        String server = appointmentId.substring(0,3);
		DAMSInterface ams = null;
        if(server.equals("MTL")) {
            ams = mtlServerObj;
        }
        else if(server.equals("QUE")) {
            ams = queServerObj;
        }
        else if(server.equals("SHE")) {
            ams = sheServerObj;
        }
        String status = ams.removeAppointment(appointmentId, appointmentType);
        System.out.println(status);
    }

    public static void listAppointmentAvailability(String appointmentType, String adminId) {
    	String server = adminId.substring(0,3);
		DAMSInterface ams = null;
	  	if(server.equals("MTL")) {
	        ams = mtlServerObj;
	    }
	    else if(server.equals("QUE")) {
	        ams = queServerObj;
	    }
	    else if(server.equals("SHE")) {
	        ams = sheServerObj;
	    }
        String res = ams.listAppointmentAvailability(appointmentType);
        System.out.println(res);
    }

    // Patient Client Methods
    public static void bookAppointment(String patientID, String appointmentId, String appointmentType) {
        String server = appointmentId.substring(0,3);
		DAMSInterface ams = null;
        if(server.equals("MTL")) {
            ams = mtlServerObj;
        }
        else if(server.equals("QUE")) {
            ams = queServerObj;
        }
        else if(server.equals("SHE")) {
            ams = sheServerObj;
        }
        String status = ams.bookAppointment(patientID, appointmentId, appointmentType);
        System.out.println(status);
    }

    public static void getAppointmentSchedule(String patientId) {
  	  String server = patientId.substring(0,3);
		DAMSInterface ams = null;
  	  if(server.equals("MTL")) {
            ams = mtlServerObj;
        }
        else if(server.equals("QUE")) {
            ams = queServerObj;
        }
        else if(server.equals("SHE")) {
            ams = sheServerObj;
        }
        String res = ams.getAppointmentSchedule(patientId);
        System.out.println(res);
    }

    public static void cancelAppointment(String patientId, String appointmentId) {
        String server = appointmentId.substring(0,3);
		DAMSInterface ams = null;
        if(server.equals("MTL")) {
            ams = mtlServerObj;
        }
        else if(server.equals("QUE")) {
            ams = queServerObj;
        }
        else if(server.equals("SHE")) {
            ams = sheServerObj;
        }
        String status = ams.cancelAppointment(patientId, appointmentId);
        System.out.println(status);
    }

    public static void swapAppointment (String patientID, String oldAppointmentID, String oldAppointmentType, String newAppointmentID, String newAppointmentType) {
		String server = oldAppointmentID.substring(0,3);
		DAMSInterface ams = null;
		if(server.equals("MTL")) {
			ams = mtlServerObj;
		}
		else if(server.equals("QUE")) {
			ams = queServerObj;
		}
		else if(server.equals("SHE")) {
			ams = sheServerObj;
		}
		String status = ams.swapAppointment(patientID, oldAppointmentID,oldAppointmentType,newAppointmentID, newAppointmentType);
		System.out.println(status);

	}


    // UDP Communication Methods
	public static void sendRequest() {
		
		System.out.println("sendRequest worker ready and waiting ...");
		while(true) {
			try {
				ORB orb = ORB.init(args, null);
				org.omg.CORBA.Object objRef =   orb.resolve_initial_references("NameService");
				NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
				mtlServerObj = (DAMSInterface) DAMSInterfaceHelper.narrow(ncRef.resolve_str("MTL"));
				queServerObj = (DAMSInterface) DAMSInterfaceHelper.narrow(ncRef.resolve_str("QUE"));
				sheServerObj = (DAMSInterface) DAMSInterfaceHelper.narrow(ncRef.resolve_str("SHE"));
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			System.out.println("sendRequest: "+reqPending+" "+reqProcessed);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(reqPending > reqProcessed) {
				String[] req = request_queue.get(reqProcessed).split(",");
				reqProcessed++;
				System.out.print("Processing Req: ");
				for(String re: req) {
					System.out.print(re + " ");
				}
				System.out.println();
				if(req[0].equals("addAppointment")) {
					addAppointment(req[1], req[2], Integer.parseInt(req[3]));
				}
				else if(req[0].equals("listAppointmentAvailability")) {
					listAppointmentAvailability(req[1], req[2]);
				}
				else if(req[0].equals("removeAppointment")) {
					removeAppointment(req[1], req[2]);
				}
				else if(req[0].equals("bookAppointment")) {
					bookAppointment(req[1], req[2], req[3]);
				}
				else if(req[0].equals("getAppointmentSchedule")) {
					getAppointmentSchedule(req[1]);
				}
				else if(req[0].equals("cancelAppointment")) {
					cancelAppointment(req[1], req[2]);
				}
				else if(req[0].equals("swapAppointment")) {
					swapAppointment(req[1], req[2], req[3], req[4], req[5]);
				}

			}
		}
	}

	public static void startLeader() {

		System.out.println("startLeader worker ready and waiting ...");
		DatagramSocket ds = null;

		try {

			ds = new DatagramSocket(6666);

			while (true) {

				byte[] buffer = new byte[65535];
				DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
				ds.receive(dp);
				byte[] data = dp.getData();
				String dpData = new String(data).trim();
				reqPending++;
				request_queue.add(dpData);
				System.out.println("request_queue: "+request_queue.toString());
			}


		} catch (SocketException e) {
			System.out.println(e.getMessage());
		} catch (UnknownHostException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	public static void main(String[] args) {
		Runnable t1 = () -> {
			try {
			 StartServerMtl();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
		Thread thread1 = new Thread(t1);
		thread1.start();

		Runnable t2 = () -> {
			try {
				StartServerQue();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
		Thread thread2 = new Thread(t2);
		thread2.start();

		Runnable t3 = () -> {
			try {
				StartServerShe();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
		Thread thread3 = new Thread(t3);
		thread3.start();

		Runnable t4 = () -> {
			startLeader();
		};
		Thread thread4 = new Thread(t4);
		thread4.start();

		Runnable t5 = () -> {
			sendRequest();
		};
		Thread thread5 = new Thread(t5);
		thread5.start();
	}
}
