package BatallaEspacial;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.ServiceRecord;

/**
 * Clase ServiceDiscoverer.
 * Se encarga de buscar servicios en los equipos detectados por DeviceDiscoverer
 * @see DeviceDiscoverer
 */
public class ServiceDiscoverer implements DiscoveryListener{
	private Broadcaster bc = null;
	private ServiceRecord service = null;
	
	 /**
	  * Constructor. Llama al constructor del padre y asigna el objeto Broadcaster
	  * @param _bc : Objeto Broadcaster de la aplicación.
	  * @see Broadcaster
	  */
	public ServiceDiscoverer(Broadcaster _bc){
		super();
		this.bc = _bc;
	}
	
	/**
	 * Sin uso.
	 */
	public void deviceDiscovered(RemoteDevice remote,DeviceClass dClass) { }
	
	/**
	 * Sin uso.
	 */
	public void inquiryCompleted(int descType) { }
	
	/**
	 *  Obtiene los servicios encontrados y asigna el primero al miembro service.
	 * @see service
	 */
	public void servicesDiscovered (int transId,ServiceRecord[] services) {
		service = services[0];
	}
	
	/**
	 * Termina de buscar servicios y le informa a Broadcaster llamando a 
	 * Broadcaster::serviceSearchFinished(). Luego vuelve todos los valores a NULL.
	 * @see Broadcaster::serviceSearchFinished()
	 */
	public void serviceSearchCompleted(int transId,int respCode) {	
		bc.serviceSearchFinished(service);//calling a method from Broadcaster class
	    bc = null;
	    service = null;
	} 
}