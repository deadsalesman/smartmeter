package uk.ac.imperial.smartmeter.institutions;

import uk.ac.imperial.smartmeter.interfaces.HLCServerIFace;
import uk.ac.imperial.smartmeter.webcomms.HLCServer;

/**
 * Conglomerates implemented interfaces for the {@link HLCServer} to allow proper RMI exporting.
 * @author bwindo
 *
 */
public interface ServerCapitalIFace extends HLCServerIFace, GlobalCapitalIFace, InstitutionIFace{

}
