package uk.ac.imperial.smartmeter.webcomms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class HLCServer {
	private int portNum;
	public HLCServer(int parseInt) {
		portNum = parseInt;
	}
	private String recvMsg(String msg)
	{
		switch(msg.substring(0, 3))
		{
		case ("REQ"):
		{
			return "REQ";
		}
		case ("TKT"):
		{
			return "TKT";
		}
		default:
		{
			return "NUL";
		}
		}
	}
	public void listen() throws IOException
	{

        try ( 
            ServerSocket serverSocket = new ServerSocket(portNum);
            Socket clientSocket = serverSocket.accept();
            PrintWriter out =
                new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        ) {
         
            String inputLine, outputLine;
             
            while ((inputLine = in.readLine()) != null) {
                outputLine = recvMsg(inputLine);
                out.println(outputLine);
                if (outputLine.equals("NUL"))
                    break;
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                + portNum + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
	 
}
