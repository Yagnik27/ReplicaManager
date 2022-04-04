package Server;

import Constants.AppointmentConstants;
import InterfaceImplementation.DAMSInterfaceImplementation;

public class AddTestData {
    private DAMSInterfaceImplementation remoteObject;
    private String serverID;
    public AddTestData(DAMSInterfaceImplementation remoteObject, String serverID){
        this.remoteObject = remoteObject;
        this.serverID = serverID;
        switch (serverID) {
            case "MTL":
                remoteObject.addNewAppointment("MTLM090322", AppointmentConstants.PHYSICIAN, 2);
                remoteObject.addNewAppointment("MTLA090322", AppointmentConstants.PHYSICIAN, 2);
                remoteObject.addNewAppointment("MTLA080522", AppointmentConstants.DENTAL, 2);
                remoteObject.addNewAppointment("MTLE230322", AppointmentConstants.SURGEON, 1);
                remoteObject.addNewAppointment("MTLA150622", AppointmentConstants.DENTAL, 8);
                remoteObject.addNewPatientToClients("MTLP1101");
                remoteObject.addNewPatientToClients("MTLA1102");
                remoteObject.addNewPatientToClients("MTLP1103");
                break;
            case "QUE":
                remoteObject.addNewAppointment("QUEA150322", AppointmentConstants.DENTAL, 5);
                remoteObject.addNewAppointment("MTLM150222", AppointmentConstants.DENTAL, 6);
                remoteObject.addNewAppointment("MTLE150422", AppointmentConstants.DENTAL, 3);
                remoteObject.addNewPatientToClients("QUEP1234");
                remoteObject.addNewPatientToClients("QUEP1235");
                remoteObject.addNewPatientToClients("QUEA4114");
                break;
            case "SHE":
                remoteObject.addNewAppointment("SHEE110322", AppointmentConstants.PHYSICIAN, 3);
                remoteObject.addNewAppointment("SHEA080422", AppointmentConstants.DENTAL, 2);
                break;
        }
    }
}
