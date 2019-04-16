package smppSimpleReceiver.smppReceiver;

import com.logica.smpp.Data;
import com.logica.smpp.Session;
import com.logica.smpp.TCPIPConnection;
import com.logica.smpp.pdu.BindReceiver;
import com.logica.smpp.pdu.BindRequest;
import com.logica.smpp.pdu.BindResponse;
import com.logica.smpp.pdu.DeliverSM;
import com.logica.smpp.pdu.PDU;

/**
 * Hello world!
 *
 */
public class App 
{
	private Session sessao = null;
	private String ip = "10.13.5.46";
	private String idSistema = "smppclient1";
	private String senha = "password";
	private int porta = 2775;
	
    public static void main( String[] args )
    {
    	App simpleReceiver = new App();
    	simpleReceiver.bindToSmsc();
		
		while(true) {
			simpleReceiver.receiveSms();
		}
    }
    
    private void bindToSmsc() {
		try {
			// setup connection
			TCPIPConnection conexao = new TCPIPConnection(ip, porta);
			conexao.setReceiveTimeout(20 * 1000);
			sessao = new Session(conexao);

			// set request parameters
			BindRequest request = new BindReceiver();
			request.setSystemId(idSistema);
			request.setPassword(senha);

			// send request to bind
			BindResponse response = sessao.bind(request);
			if (response.getCommandStatus() == Data.ESME_ROK) {
				System.out.println("Sms receiver is connected to SMPPSim.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    private void receiveSms() {
		try {

			PDU pdu = sessao.receive(1500);

			if (pdu != null) {
				DeliverSM sms = (DeliverSM) pdu;
				
				if ((int)sms.getDataCoding() == 0 ) {
					//message content is English
					System.out.println("***** New Message Received *****");
					System.out.println("From: " + sms.getSourceAddr().getAddress());
					System.out.println("To: " + sms.getDestAddr().getAddress());
					System.out.println("Content: " + sms.getShortMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
}
