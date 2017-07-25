package ontologizer.io.dot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import ontologizer.ontology.Ontology;
import ontologizer.ontology.RelationMeaning;
import ontologizer.ontology.TermID;
import ontologizer.util.VersionInfo;

public class OntologyDotWriter
{
	private static Logger logger = Logger.getLogger(OntologyDotWriter.class.getCanonicalName());

	/**
	 * Encode the term id string of the given term id.
	 *
	 * @param termId to be encoded
	 * @return the encoded term id for using it as node's name within the dot file.
	 *
	 * TODO: Properly escape possibly existing underscore
	 */
	public static String encodeTermID(TermID termId)
	{
		return termId.toString().replace(':', '_');
	}

	/**
	 * Decode the string that was encoded via {@link #encodeTermID(TermID)}
	 *
	 * @param encodedTermId the string to be decoded
	 * @return the decoded string
	 */
	public static TermID decodeTermID(String encodedTermId)
	{
		return new TermID(encodedTermId.replace('_', ':'));
	}

	/**
	 * Writes out a basic dot file which can be used within graphviz. All terms
	 * of the terms parameter are included in the graph if they are within the
	 * sub graph originating at the rootTerm. In other words, all nodes
	 * representing the specified terms up to the given rootTerm node are
	 * included.
	 *
	 * @param graph the ontology
	 * @param file
	 * 			defines the file in which the output is written to.
	 * @param rootTerm
	 *          defines the first term of the sub graph which should
	 *          be considered.
	 *
	 * @param terms
	 * 			defines which terms should be included within the
	 *          graphs.
	 * @param provider
	 *          should provide for every property an appropiate id.
	 */
	public static void writeDOT(Ontology graph, File file, TermID rootTerm, Set<TermID> terms, ITermDotAttributesProvider provider)
	{
		writeDOT(graph, file, rootTerm, terms, provider, "nodesep=0.4;", false, false, null);
	}

	/**
	 * Writes out a basic dot file which can be used within graphviz. All terms
	 * of the terms parameter are included in the graph if they are within the
	 * sub graph originating at the rootTerm. In other words, all nodes
	 * representing the specified terms up to the given rootTerm node are
	 * included.
	 *
	 * @param graph the ontology
	 * @param file
	 * 			defines the file in which the output is written to.
	 * @param rootTerm
	 *          defines the first term of the sub graph which should
	 *          be considered.
	 *
	 * @param terms
	 * 			defines which terms should be included within the
	 *          graphs.
	 * @param provider
	 *          should provide for every property an appropiate id.
	 * @param reverseDirection spec
	 * @param edgeLabels
	 * @param ignoreTerms
	 */
	public static void writeDOT(final Ontology graph, File file, TermID rootTerm, Set<TermID> terms, final ITermDotAttributesProvider provider, final String graphAttrs, final boolean reverseDirection, final boolean edgeLabels, Set<TermID> ignoreTerms)
	{
		/* Collect terms starting from the terms upto the root term and place them into nodeSet */
		HashSet<TermID> nodeSet = new HashSet<TermID>();
		for (TermID term : terms)
		{
			if (!graph.termExists(term))
				throw new IllegalArgumentException("Requested term " + term.toString() + " couldn't be found in the graph");

			if (!nodeSet.contains(term))
			{
				for (TermID it : graph.getTermsOfInducedGraph(rootTerm,term))
					nodeSet.add(it);
			}
		}

		if (ignoreTerms != null)
		{
			for (TermID it : ignoreTerms)
				nodeSet.remove(graph.getTerm(it).getID());
		}

		/* We now have a list of nodes which can be placed into the output */
		try
		{
			DotWriter.write(graph.getGraph(),new FileOutputStream(file), nodeSet, new DotAttributesProvider<TermID>()
					{
						/* Note that the default direction is assumed to be the opposite direction */
						private String direction = reverseDirection?"":"dir=\"back\"";

						@Override
						public String getDotNodeName(TermID vt) { return OntologyDotWriter.encodeTermID(vt); }

						@Override
						public String getDotNodeAttributes(TermID vt) { return provider.getDotNodeAttributes(vt);	}

						@Override
						public String getDotGraphAttributes() { return graphAttrs; }

						@Override
						public String getDotHeader(){ return "/* Generated with OntologizerLib " + VersionInfo.getVersion() + " */"; }

						@Override
						public String getDotEdgeAttributes(TermID src, TermID dest)
						{
							String color;
							String relationName;
							String label;

							RelationMeaning rel = graph.getDirectRelation(src, dest).meaning();

							/* TODO: Use fancy name from RelationType */
							switch (rel)
							{
								case	IS_A: relationName = "is a"; break;
								case	PART_OF_A: relationName = "is part of"; break;
								case	REGULATES: relationName = "regulates"; break;
								case	POSITIVELY_REGULATES: relationName = "positively regulates"; break;
								case	NEGATIVELY_REGULATES: relationName = "negatively regulates"; break;
								default: relationName = "";
							}

							switch (rel)
							{
								case	IS_A: color = "black"; break;
								case	PART_OF_A: color = "blue"; break;
								case	REGULATES:  /* Falls through */
								case	POSITIVELY_REGULATES:  /* Falls through */
								case	NEGATIVELY_REGULATES: color ="green"; break;
								default: color = "black"; break;
							}

							if (edgeLabels)
							{
								label = provider.getDotEdgeAttributes(src, dest);
								if (label == null)
									label = "label=\"" + relationName + "\"";
							} else label = null;

							String tooltip = "tooltip=\"" + graph.getTerm(dest).getName() + " " + relationName + " " + graph.getTerm(src).getName() + "\"";
							return "color=" + color + "," + direction + "," + tooltip + (label!=null?(","+label):"");
						}
					});
		} catch (IOException e)
		{
			logger.severe("Unable to create dot file: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

}
