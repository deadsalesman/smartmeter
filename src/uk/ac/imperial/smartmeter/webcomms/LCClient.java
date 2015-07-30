package uk.ac.imperial.smartmeter.webcomms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import uk.ac.imperial.smartmeter.impl.LCHandler;

public class LCClient{
	private String eDCHost;
	private int eDCPort;
	private String hLCHost;
	private int hLCPort;
	private static LCHandler handler;
	
	
	public LCClient(String eDCHostName, int eDCPortNum, String hLCHostName,int hLCPortNum, String userID) {
		eDCHost = eDCHostName;
		eDCPort = eDCPortNum;
		hLCHost = hLCHostName;
		hLCPort = hLCPortNum;
		handler = new LCHandler(userID);
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
	

	public Boolean addDevice(Boolean state, Integer type, String deviceID)
	{
		String inputLine = "ADD," + Boolean.toString(state) + "," + Integer.toString(type) + "," + deviceID;
		ArrayList<String> input = new ArrayList<String>();
		input.add(inputLine);
		input.add("END");
		try {
			ArrayList<String> ret = connectEDC(input);
			if (ret.get(0).equals("SUCCESS"))
			{
				return true;
			}
		} catch (IOException e) {
		}
		return false;
	}
	
	public Boolean setState(String deviceId, Boolean val)
	{
		return false;
	}
	public Boolean getState(String deviceID)
	{
		String inputLine = "GETS," + deviceID;
		ArrayList<String> input = new ArrayList<String>();
		input.add(inputLine);
		input.add("END");
		try {
			ArrayList<String> ret = connectEDC(input);
			return Boolean.parseBoolean(ret.get(0));
		} catch (IOException e) {
		}
		return null;
	}
	public Boolean removeDevice(String deviceID)
	{
		String inputLine = "REM," + deviceID;
		ArrayList<String> input = new ArrayList<String>();
		input.add(inputLine);
		input.add("END");
		try {
			ArrayList<String> ret = connectEDC(input);
			if (ret.get(0).equals("SUCCESS"))
			{
				return true;
			}
		} catch (IOException e) {
		}
		return false;
	}
}
