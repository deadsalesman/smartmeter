package uk.ac.imperial.smartmeter.db;

import java.util.List;
import java.util.Map;

/**
 * Basic wrapper class for a list of mappings from strings to objects.
 * We mourn the typedef.
 * @author bwindo
 *
 */
public class LocalSet {
 public List<Map<String,Object>> data;
 public LocalSet(List<Map<String,Object>> lst)
 {
	 data = lst;
 }
 
}
