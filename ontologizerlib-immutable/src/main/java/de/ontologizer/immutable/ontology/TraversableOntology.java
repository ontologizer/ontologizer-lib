package de.ontologizer.immutable.ontology;

import de.ontologizer.immutable.graph.algorithms.VertexVisitor;
import java.io.Serializable;
import java.util.Collection;
import java.util.EnumSet;
import ontologizer.ontology.Term;
import ontologizer.ontology.TermID;
import ontologizer.ontology.TermRelation;

/**
 * Decorator for {@link Ontology} for traversing the ontology graph in various
 * interesting ways.
 * 
 * <p>
 * The same conventions for parameter and return types as for {@link Ontology}
 * apply.
 * </p>
 * 
 * @author Sebastian Bauer
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface TraversableOntology extends TermMap, Serializable {

	/**
	 * Query whether a directed path from <code>sourceID</code> to
	 * <code>destID</code> exists.
	 *
	 * @param sourceID
	 *            {@link TermID} of the source term
	 * @param destID
	 *            {@link TermID}of the destination term
	 */
	public boolean doesPathExist(TermID sourceID, TermID destID);

	/**
	 * Starting at <code>termId</code>, walk to the source of the DAG and visit
	 * each vertex with <code>visitor</code>.
	 *
	 * @param termId
	 *            {@link TermID} to start with (note that <code>visitor</code>
	 *            also visits the start vertex.
	 * @param visitor
	 *            {@link VertexVisitor} to use for callback
	 */
	public void walkToSource(TermID termId, VertexVisitor<Term, OntologyEdge> visitor);

	/**
	 * Starting at the given {@link TermID}s, walk to the source of the DAG and
	 * visit each vertex with <code>visitor</code> only using the relations in
	 * {@link relations}.
	 *
	 * @param termID
	 *            {@link TermID}s to start at (note that <code>visitor</code>
	 *            also visits the start vertices)
	 * @param vistingVertex
	 *            {@link VertexVisitor} to use for callback
	 * @param relations
	 *            {@link Collection} of {@link TermRelation}s to follow
	 */
	public void walkToSource(TermID termId, VertexVisitor<Term, OntologyEdge> visitor,
			EnumSet<TermRelation> relationsToFollow);

	/**
	 * Starting at all {@link Term}s with the given {@link TermID}s, walk to the
	 * source of the DAG and visit each vertex with <code>visitor</code>.
	 *
	 * @param termIds
	 *            {@link Collection} of {@link TermID}s to start at (note that
	 *            <code>visitor</code> also visits the start vertices)
	 * @param visitor
	 *            {@link VertexVisitor} to use for callback
	 */
	public void walkToSource(Collection<TermID> termIds, VertexVisitor<Term, OntologyEdge> vistingVertex);

	/**
	 * Starting at all {@link Term}s with the given {@link TermID}s, walk to the
	 * source of the DAG and visit each vertex with <code>visitor</code> only
	 * using the relations in {@link relations}.
	 *
	 * @param termIDSet
	 *            {@link Collection} of {@link TermID}s to start at (note that
	 *            <code>visitor</code> also visits the start vertices)
	 * @param vistingVertex
	 *            {@link VertexVisitor} to use for callback
	 * @param relations
	 *            {@link Collection} of {@link TermRelation}s to follow
	 */
	public void walkToSource(Collection<TermID> termIdSet, VertexVisitor<Term, OntologyEdge> visitor,
			EnumSet<TermRelation> relationsToFollow);

	/**
	 * Starting at all {@link Term}s with the given {@link TermID}s, walk to the
	 * sinks of the DAG and visit each vertex with <code>visitor</code>.
	 *
	 * @param termId
	 *            {@link TermID} to start with (note that <code>visitor</code>
	 *            also visits the start vertex
	 * @param visitor
	 *            {@link VertexVisitor} to use for callback
	 */
	public void walkToSinks(TermID termId, VertexVisitor<Term, OntologyEdge> visitor);

	/**
	 * Starting at the given {@link TermID}s, walk to the sinks of the DAG and
	 * visit each vertex with <code>visitor</code> only using the relations in
	 * {@link relations}.
	 *
	 * @param termId
	 *            {@link TermID}s to start with (note that <code>visitor</code>
	 *            also visits the start vertex).
	 * @param visitor
	 *            {@link VertexVisitor} to use for callback
	 * @param relations
	 *            {@link Collection} of {@link TermRelation}s to follow
	 */
	public void walkToSinks(TermID termId, VertexVisitor<Term, OntologyEdge> visitor,
			EnumSet<TermRelation> relationsToFollow);

	/**
	 * Starting at all {@link Term}s with the given {@link TermID}s, walk to the
	 * sinks of the DAG and visit each vertex with <code>visitor</code>.
	 *
	 * @param termIds
	 *            {@link Collection} of {@link TermID}s to start with (note that
	 *            <code>visitor</code> also visits the start vertices)
	 * @param visitor
	 *            {@link VertexVisitor} to use for callback
	 */
	public void walkToSinks(Collection<TermID> termIds, VertexVisitor<Term, OntologyEdge> visitor);

	/**
	 * Starting at all {@link Term}s with the given {@link TermID}s, walk to the
	 * sinks of the DAG and visit each vertex with <code>visitor</code> only
	 * using the relations in {@link relations}.
	 *
	 * @param termIds
	 *            {@link Collection} of {@link TermID}s to start with (note that
	 *            <code>visitor</code> also visits the start vertices)
	 * @param visitor
	 *            {@link VertexVisitor} to use for callback
	 * @param relations
	 *            {@link Collection} of {@link TermRelation}s to follow
	 */
	public void walkToSinks(Collection<TermID> termIds, VertexVisitor<Term, OntologyEdge> visitor,
			EnumSet<TermRelation> relationsToFollow);

}
