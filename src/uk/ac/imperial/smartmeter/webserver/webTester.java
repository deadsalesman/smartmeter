package uk.ac.imperial.smartmeter.webserver;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class webTester {
	public static void main(String[] args)
	{
		doHttpURLConnection();
	}
	public static String doHttpURLConnection()
	{
		String url_string = "https://usm-server-dev-usm-dev2015.c9.io/";
		HttpURLConnection conn = null;
		BufferedReader reader = null;
		StringBuilder stringBuilder; 
		
		URL url = null;
		try {
			url = new URL(url_string);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setReadTimeout(15*1000);
			conn.connect();
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			stringBuilder = new StringBuilder();
			
			String line = null;
			//line = reader.readLine();
			while((line = reader.readLine()) != null){
				System.out.println("GOT" + line);
				stringBuilder.append(line + "\n");
				//line = reader.readLine();
			}
			System.out.println(url);
			System.out.println(conn.getResponseCode());
			System.out.println(stringBuilder.toString());
			return stringBuilder.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return url_string;
	}


}
