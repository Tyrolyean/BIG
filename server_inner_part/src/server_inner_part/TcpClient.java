package server_inner_part;

import java.io.DataOutputStream;
//import java.net.InetAddress;
//import java.net.InterfaceAddress;
//import java.net.NetworkInterface;
import java.net.Socket;
//import java.util.Enumeration;
//import java.util.Iterator;

import org.bukkit.entity.Player;

public class TcpClient {

	public static void transfer_player(Player p, int world_id) {
		try {
			if (JoinLeave.debug()) {
				System.out.println(
						"Transfering Player with uuid " + p.getUniqueId().toString() + " to world_id " + world_id);
			}

			// There is a Problem that you shouldbn't broadcast with TCP which I
			// didn't knew about :(
			/*
			 * Enumeration<NetworkInterface>
			 * interfaces=NetworkInterface.getNetworkInterfaces();
			 * 
			 * while(interfaces.hasMoreElements()){ NetworkInterface
			 * ni=interfaces.nextElement(); if(ni.isLoopback()){ continue; }
			 * Iterator<InterfaceAddress>
			 * itad=ni.getInterfaceAddresses().iterator();
			 * 
			 * while(itad.hasNext()){ InetAddress i =
			 * itad.next().getBroadcast(); if(i==null){
			 * 
			 * continue; } try{ if(JoinLeave.debug()){
			 * System.out.println("Connecting to host with ip "+i.getHostAddress
			 * ()); }
			 * 
			 * Socket socket=new Socket(i.getHostAddress(),1945);
			 * DataOutputStream dos=new
			 * DataOutputStream(socket.getOutputStream());
			 * dos.writeUTF(p.getUniqueId().toString()+"\n");
			 * dos.writeUTF(world_id+"\n"); dos.flush(); dos.close();
			 * socket.close(); }catch(Exception e){ e.printStackTrace(); } } }
			 */

			Socket socket = new Socket("192.168.0.17", 1945);
			while (!socket.isConnected()) {
			}
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			while (dos == null) {
			}
			dos.writeBytes(p.getDisplayName() + "\n");
			dos.writeBytes(world_id + "\n");
			dos.flush();
			dos.close();
			socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}
