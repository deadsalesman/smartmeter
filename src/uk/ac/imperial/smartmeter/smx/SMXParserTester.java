package uk.ac.imperial.smartmeter.smx;

/**
 * @deprecated
 * Test code for development.
 * @author bwindo
 *
 */
public class SMXParserTester {
  @SuppressWarnings("unused")
public static void main(String[] args)
  {
	  String get = "get  Consumator_01 P_cons=;Q_cons=;   Consumator_07 P_cons=;U_R=;     Consumator_23 P_cons=;U_R=;I_R=;";
	  String ret = "result-get    Consumator_01 P_cons=7.42;Q_cons=1.31;     Consumator_07     P_cons=2.12;U_R=214;    Consumator_23 P_cons=0.58;U_R=226;I_R=31.5;";
	  SMXHandler handle = new SMXHandler("192.168.1.221",1111);

	  
	  String req = handle.generateHTTPRequest(1);
	  
	  String out = "result-get    Consumator_01 Q_cons=3;P_cons=4;U_R=5;I_R=6;   Consumator_04 Q_cons=3;P_cons=4;U_R=5;I_R=6;   Consumator_01 Q_cons=3;P_cons=4;U_R=5;I_R=6;   Consumator_02 Q_cons=3;P_cons=4;U_R=5;I_R=6;   Consumator_03 Q_cons=3;P_cons=4;U_R=5;I_R=6;   Consumator_99 Q_cons=3;P_cons=4;U_R=5;I_R=6;";
	  

	  //SMXReading[] y =  handle.parseSMXInput(out);
	  SMXReading[] z = handle.sendRequest(req);
	 
	  System.out.println("end");
  }
}
