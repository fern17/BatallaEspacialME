package BatallaEspacial;
import java.util.Vector;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.ServiceRecord;

/**
 * Clase DeviceDiscoverer.
 * Se encarga de buscar dispositivos con conexión Bluetooth en las cercanías.
 */
public class DeviceDiscoverer implements DiscoveryListener {
	private Broadcaster bc = null;
	private Vector devices = null;
	private RemoteDevice[] rDevices = null;
	
	/**
	 * Constructor. Llama al constructor del padre y asigna el objeto Broadcaster
	 * @param _bc : Objeto Broadcaster de la aplicación
	 * @see Broadcaster
	 */
	public DeviceDiscoverer(Broadcaster _bc) {
		super();
		this.bc = _bc;
		devices = new Vector();
	}
	
	/**
	 * Encuentra un dispositivo y lo agrega al Vector de dispositivos
	 */
	public void deviceDiscovered(RemoteDevice remote,DeviceClass dClass) {
		devices.addElement(remote); 
	}
	
	/**
	 * Termina de buscar dispositivos. Crea un Vector de RemoteDevice, copia 
	 * los objetos de devices ahí y luego llama a Broadcaster::deviceInquiryFinished()
	 * con el vector de RemoteDevice. Luego vuelve todo a NULL.
	 * @see Broadcaster::deviceInquiryFinished() 
	 */
	public void inquiryCompleted(int descType) {
		rDevices = new RemoteDevice[devices.size()];
		for(int i=0;i<devices.size();i++)
			rDevices[i] = (RemoteDevice) devices.elementAt(i);
      
		bc.deviceInquiryFinished(rDevices);//call of a method from ChatController class
		
		devices.removeAllElements();
		bc = null;
		devices = null;
	}
	
	/**
	 * Sin uso.
	 */
	public void servicesDiscovered(int transId,ServiceRecord[] services) { }
	
	/**
	 * Sin uso.
	 */
	public void serviceSearchCompleted(int transId,int respCode) { }
}