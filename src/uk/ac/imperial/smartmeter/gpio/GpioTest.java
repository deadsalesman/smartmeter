package uk.ac.imperial.smartmeter.gpio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
 
class GpioTest{
	/**
	 * Tests calling a python script within Java.
	 * @param a
	 */
public static void main(String a[]){
try{
 
int number1 = 10;
int number2 = 32;
 
ProcessBuilder pb = new ProcessBuilder("python","test1.py",""+number1,""+number2);
Process p = pb.start();
 
BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
int ret = new Integer(in.readLine()).intValue();
System.out.println("value is : "+ret);
}catch(Exception e){System.out.println(e);}
}
}