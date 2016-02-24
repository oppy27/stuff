package sample.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SmartPortForwarder {
	private static final int SLEEP_MS = 100; 
	
	static int localPort;
	static int remotePort;
	static String remoteHost;
	
	static Socket localSocket = null;
	static Socket remoteSocket = null;
	static ServerSocket server = null;
	
	static Router inout = null;
	static Router outin = null;
	
	public static void main(String args[]) {
		localPort = Integer.getInteger("localPort");
		remotePort = Integer.getInteger("remotePort");
		remoteHost = System.getProperty("remoteHost");
		
		System.out.println("LocalPort: " + localPort);
		System.out.println("RemoteHost:" + remoteHost);
		System.out.println("RemotePort: " + remotePort);
		
		while (true) {
			try {
				waitForRemoteHost();	
				waitForLocalHost();
				
				inout = new Router(localSocket.getInputStream(), remoteSocket.getOutputStream());
				outin = new Router(remoteSocket.getInputStream(), localSocket.getOutputStream());
				
				Thread t1 = new Thread(inout);
				Thread t2 = new Thread(outin);
				t1.start();
				t2.start();
				
				t1.join();
				t2.join();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try { localSocket.close();} catch (IOException ex) { /* ignore */ }
		        try { remoteSocket.close();} catch (IOException ex) { /* ignore */ }
			}
		}
	}

	private static void waitForRemoteHost() {
		System.out.print("Waiting for remote host ");
		while (true) {
			try {
				remoteSocket = new Socket(remoteHost, remotePort);
				System.out.println("Connected");
				break;
			} catch (Exception e) {
				System.out.print(".");
				try {
					Thread.sleep(SLEEP_MS);
				} catch (Exception ie) { /* Do Nothing */ }
			}
		}
	}
	
	private static void waitForLocalHost() throws Exception {
		System.out.print("Waiting for local host ... ");
		server = new ServerSocket(localPort);
		localSocket = server.accept();
		System.out.println("Connected");
		// kill server
		server.close();
	}
	
	public static void route(InputStream inputStream, OutputStream outputStream) throws IOException {
	    
	}
}

class Router implements Runnable {
	private static final int BUFFER_SIZE = 65535;
	
	InputStream inputStream = null;
	OutputStream outputStream = null;
	
	public Router(InputStream in, OutputStream out) {
		this.inputStream = in;
		this.outputStream = out;
	}
	
	public void run() {
		byte[] buffer = new byte[BUFFER_SIZE];
	    try {
	        while( true ) {
	            int b = inputStream.read(buffer, 0, BUFFER_SIZE);
	            if( b == - 1 ) {
	                System.out.println("No data available anymore. Closing stream.");
	                return;
	            }
	            outputStream.write(buffer, 0, b);
	        } 
	    } catch (IOException ioe){
        	ioe.printStackTrace();
        } finally {
	        try { inputStream.close();} catch (IOException ex) { /* ignore */ }
	        try { outputStream.close();} catch (IOException ex) { /* ignore */ }
	    }
	}
}
