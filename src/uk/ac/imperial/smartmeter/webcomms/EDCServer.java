package uk.ac.imperial.smartmeter.webcomms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import uk.ac.imperial.smartmeter.impl.EDCHandler;
import uk.ac.imperial.smartmeter.res.ElectronicDevice;

public class EDCServer{
	public EDCServer(int parseInt, Boolean verb) {
		portNum = parseInt;
		handler = new EDCHandler();
		verbose = verb;
	}

	private EDCHandler handler;
	private Boolean verbose;
	private int portNum;
	
	private Boolean addDevice(List<String> splitMsg)
	{
		return handler.addDevice(
				new ElectronicDevice(
						Boolean.parseBoolean(splitMsg.get(2)),
						Integer.parseInt(splitMsg.get(3)),
						splitMsg.get(4)
						));
	}
	private Boolean setState(List<String> splitMsg)
	{
		return handler.setState(
				splitMsg.get(2),
				Boolean.parseBoolean(splitMsg.get(3))
				);
	}
	private Boolean getState(List<String> splitMsg)
	{
		return handler.getState(splitMsg.get(2));
	}
	private Boolean removeDevice(List<String> splitMsg)
	{
		return handler.removeDevice(splitMsg.get(2));
	}
	private String boolToStr(Boolean b)
	{
		if (b==null){
			return "NULL";
		}
		else {
			return b ? "TRUE" : "FALSE";
		}
	}
	private String resultToStr(Boolean b)
	{
		return b ? "SUCCESS" : "FAILURE";
	}

	private String getDevice(List<String> splitMsg) {
		String ret = "";
		ElectronicDevice ed = handler.getDevice(splitMsg.get(2));
		if (ed!= null)
		{
			ret = ed.getState() + ","+ed.getType().ordinal()+","+ed.getId();
		}
		return ret;
	}
	private String recvMsg(String msg) {
		List<String> splitMsg = Arrays.asList(msg.split(",[ ]*"));
		switch (splitMsg.get(1)) {
		case ("ADD"): {
			return resultToStr(addDevice(splitMsg));
		}
		case ("SET"): {
			return resultToStr(setState(splitMsg));
		}
		case ("REM"): {
			return resultToStr(removeDevice(splitMsg));
		}
		case ("GETS"): {
			return boolToStr(getState(splitMsg));
		}
		case ("GETD"): {
			return getDevice(splitMsg);
		}
		default: {
			return "NUL";
		}
		}
	}

	public Boolean listen() throws IOException {

		try (ServerSocket serverSocket = new ServerSocket(portNum);
				Socket clientSocket = serverSocket.accept();
				@SuppressWarnings("resource")
				PrintWriter out = new PrintWriter(
						clientSocket.getOutputStream(), true);
				@SuppressWarnings("resource")
				BufferedReader in = new BufferedReader(new InputStreamReader(
						clientSocket.getInputStream()));) {

			String inputLine, outputLine;

			while ((inputLine = in.readLine()) != null) {
				outputLine = recvMsg(inputLine);
				out.println(outputLine);
                if (verbose)
                {
                	System.out.println("Input: " +inputLine);
                	System.out.println("Output: " + outputLine);
                }
				if (outputLine.equals("NUL"))
					return true;
			}
			out.println("NUL");
		} 
		catch (IOException e) {
			System.out
					.println("Exception caught when trying to listen on port "
							+ portNum + " or listening for a connection");
			System.out.println(e.getMessage());
		}
		return false;
	}

}
