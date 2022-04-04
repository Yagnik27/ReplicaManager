package CORBAInterface;


/**
* CORBAInterface/_DAMSInterfaceStub.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Interface.idl
* Wednesday, 23 February, 2022 4:05:38 PM EST
*/

public class _DAMSInterfaceStub extends org.omg.CORBA.portable.ObjectImpl implements CORBAInterface.DAMSInterface
{


  //administrator Role
  public String addAppointment (String AppointmentID, String AppointmentType, int capacity)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("addAppointment", true);
                $out.write_string (AppointmentID);
                $out.write_string (AppointmentType);
                $out.write_long (capacity);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return addAppointment (AppointmentID, AppointmentType, capacity        );
            } finally {
                _releaseReply ($in);
            }
  } // addAppointment

  public String removeAppointment (String AppointmentID, String AppointmentType)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("removeAppointment", true);
                $out.write_string (AppointmentID);
                $out.write_string (AppointmentType);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return removeAppointment (AppointmentID, AppointmentType        );
            } finally {
                _releaseReply ($in);
            }
  } // removeAppointment

  public String listAppointmentAvailability (String AppointmentType)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("listAppointmentAvailability", true);
                $out.write_string (AppointmentType);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return listAppointmentAvailability (AppointmentType        );
            } finally {
                _releaseReply ($in);
            }
  } // listAppointmentAvailability


  //Patient Role
  public String bookAppointment (String patientID, String AppointmentID, String AppointmentType)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("bookAppointment", true);
                $out.write_string (patientID);
                $out.write_string (AppointmentID);
                $out.write_string (AppointmentType);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return bookAppointment (patientID, AppointmentID, AppointmentType        );
            } finally {
                _releaseReply ($in);
            }
  } // bookAppointment

  public String getAppointmentSchedule (String patientID)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("getAppointmentSchedule", true);
                $out.write_string (patientID);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return getAppointmentSchedule (patientID        );
            } finally {
                _releaseReply ($in);
            }
  } // getAppointmentSchedule

  public String cancelAppointment (String patientID, String AppointmentID)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("cancelAppointment", true);
                $out.write_string (patientID);
                $out.write_string (AppointmentID);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return cancelAppointment (patientID, AppointmentID        );
            } finally {
                _releaseReply ($in);
            }
  } // cancelAppointment

  public String swapAppointment (String patientID, String oldAppointmentID, String oldAppointmentType, String newAppointmentID, String newAppointmentType)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("swapAppointment", true);
                $out.write_string (patientID);
                $out.write_string (oldAppointmentID);
                $out.write_string (oldAppointmentType);
                $out.write_string (newAppointmentID);
                $out.write_string (newAppointmentType);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return swapAppointment (patientID, oldAppointmentID, oldAppointmentType, newAppointmentID, newAppointmentType        );
            } finally {
                _releaseReply ($in);
            }
  } // swapAppointment

  public void shutdown ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("shutdown", false);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                shutdown (        );
            } finally {
                _releaseReply ($in);
            }
  } // shutdown

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:CORBAInterface/DAMSInterface:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }

  private void readObject (java.io.ObjectInputStream s) throws java.io.IOException
  {
     String str = s.readUTF ();
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     org.omg.CORBA.Object obj = orb.string_to_object (str);
     org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate ();
     _set_delegate (delegate);
   } finally {
     orb.destroy() ;
   }
  }

  private void writeObject (java.io.ObjectOutputStream s) throws java.io.IOException
  {
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     String str = orb.object_to_string (this);
     s.writeUTF (str);
   } finally {
     orb.destroy() ;
   }
  }
} // class _DAMSInterfaceStub
