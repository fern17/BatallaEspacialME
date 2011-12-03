

import java.util.Vector;

import javax.bluetooth.RemoteDevice;
import javax.bluetooth.DeviceClass;

import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.ServiceRecord;

public class DeviceDiscoverer implements DiscoveryListener
{
 private Broadcaster bc = null;
 private Vector devices = null;
 private RemoteDevice[] rDevices = null;
 
 public DeviceDiscoverer(Broadcaster _bc)
	{
     super();
     this.bc = _bc;
	 devices = new Vector();
	}

 public void deviceDiscovered(RemoteDevice remote,DeviceClass dClass)
	{
     devices.addElement(remote); 
	}

 public void inquiryCompleted(int descType)
	{
	 String message = "";

     switch(descType)
		{
	     case DiscoveryListener.INQUIRY_COMPLETED:
			     message = "INQUIRY_COMPLETED";
		         break;
	     case DiscoveryListener.INQUIRY_TERMINATED:
			     message = "INQUIRY_TERMINATED";
		         break;
	     case DiscoveryListener.INQUIRY_ERROR:
			     message = "INQUIRY_ERROR";
		         break;
	    }

      rDevices = new RemoteDevice[devices.size()];
	  for(int i=0;i<devices.size();i++)
		 rDevices[i] = (RemoteDevice)devices.elementAt(i);
      
	  bc.deviceInquiryFinished(rDevices);//call of a method from ChatController class
	  devices.removeAllElements();

	  bc = null;
      devices = null;
	}

 public void servicesDiscovered(int transId,ServiceRecord[] services)
	{}

 public void serviceSearchCompleted(int transId,int respCode)
	{}
 
}