package uk.ac.imperial.smartmeter.webcomms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class LCClient{
	private String eDCHost;
	private int eDCPort;
	private String hLCHost;
	private int hLCPort;
	public LCClient(String eDCHostName, int eDCPortNum, String hLCHostName,int hLCPortNum) {
		eDCHost = eDCHostName;
		eDCPort = eDCPortNum;
		hLCHost = hLCHostName;
		hLCPort = hLCPortNum;
	}
	private Boolean addDevice(List<String> splitMsg)
	{
		return false;
	}
	public Boolean setState(String deviceId, Boolean val)
	{
		String input = deviceId + '.' + boolToStr(val);
		try {
			return Boolean.parseBoolean(connectEDC(input).get(0));
		} catch (IOException e) {
			return false;
		}
	}
	private Boolean getState(List<String> splitMsg)
	{
		return false;//handler.getState(splitMsg.get(1));
	}
	private Boolean removeDevice(List<String> splitMsg)
	{
		return false;//handler.removeDevice(splitMsg.get(1));
	}
	private String getDevice(List<String> splitMsg) {

		return "";//ret;
	}
	private String boolToStr(Boolean b)
	{
		return b ? "TRUE" : "FALSE";
	}
	public ArrayList<String> connectEDC(String input) throws IOException {
		ArrayList<String> inputArr = new ArrayList<String>();
		inputArr.add(input);
		return connectEDC(inputArr);
	}
	public ArrayList<String> connectEDC(ArrayList<String> input) throws IOException {
		ArrayList<String> ret =  new ArrayList<String>();
		try (Socket kkSocket = new Socket(eDCHost, eDCPort);
				PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));) {
			for (String s : input) {
				out.println(s);
			}

			String fromServer;
			while ((fromServer = in.readLine()) != null) {
				ret.add(fromServer);
				System.out.println("Server: " + fromServer);
				if (fromServer.equals("NUL"))
					break;
			}

		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + eDCHost);
			System.exit(1);
		}
		return ret;
	}

	public ArrayList<String> connectHLC(ArrayList<String> input) throws IOException {
		ArrayList<String> ret =  new ArrayList<String>();
		try (Socket kkSocket = new Socket(hLCHost, hLCPort);
				PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));) {
			for (String s : input) {
				out.println(s);
			}

			String fromServer;
			while ((fromServer = in.readLine()) != null) {
				ret.add(fromServer);
				System.out.println("Server: " + fromServer);
				if (fromServer.equals("NUL"))
					break;
			}

		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hLCHost);
			System.exit(1);
		}
		return ret;
	}
}
