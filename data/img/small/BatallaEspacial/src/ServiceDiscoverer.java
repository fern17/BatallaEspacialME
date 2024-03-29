

import javax.bluetooth.RemoteDevice;
import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;

import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.ServiceRecord;

public class ServiceDiscoverer implements DiscoveryListener
{
 private static final String SERVICE_NAME = "BatallaEspacialME";
 private Broadcaster bc = null;
 private ServiceRecord service = null;

 public ServiceDiscoverer(Broadcaster _bc)
	{
     super();
	 this.bc = _bc;
	}

 public void deviceDiscovered(RemoteDevice remote,DeviceClass dClass)
	{}

 public void inquiryCompleted(int descType)
	{}

 public void servicesDiscovered(int transId,ServiceRecord[] services)
	{
     for(int j=0;j<services.length;j++)
		{
	     DataElement dataElementName = services[j].getAttributeValue(0x0100);
         
		 String serviceName = (String)dataElementName.getValue();

		 if(serviceName.equals(SERVICE_NAME))
			         service = services[j];
		 
		 break;	 
	    }
	}

 public void serviceSearchCompleted(int transId,int respCode)
	{
     String message = "";
	 
	 switch(respCode)
		{
	     case DiscoveryListener.SERVICE_SEARCH_COMPLETED:
			       message = "SERVICE_SEARCH_COMPLETED";
		           break;
	     case DiscoveryListener.SERVICE_SEARCH_ERROR:
			       message = "SERVICE_SEARCH_ERROR";
		           break;
	     case DiscoveryListener.SERVICE_SEARCH_TERMINATED:
			       message = "SERVICE_SEARCH_TERMINATED";
		           break;
	     case DiscoveryListener.SERVICE_SEARCH_NO_RECORDS:
			       message = "SERVICE_SEARCH_NO_RECORDS";
		           break;
	     case DiscoveryListener.SERVICE_SEARCH_DEVICE_NOT_REACHABLE:
			       message = "SERVICE_SEARCH_DEVICE_NOT_REACHABLE";
		           break;
	    }
     
	 bc.serviceSearchFinished(service);//calling a method from ChatController class
  
     bc = null;
	 service = null;
	} 
}