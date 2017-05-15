package de.ontologizer.immutable.annotations;

import java.util.Map;
import ontologizer.ontology.TermID;

/**
 * Interface to implement for maps from term to information content.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface InformationContentMap extends Map<TermID, Double> {

}
