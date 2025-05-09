/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */
/* ------------------
 * CycleDetector.java
 * ------------------
 * (C) Copyright 2004-2008, by John V. Sichi and Contributors.
 *
 * Original Author:  John V. Sichi
 * Contributor(s):   Christian Hammer
 *
 * $Id: CycleDetector.java,v 1.1 2010/07/14 14:09:02 mkuper Exp $
 *
 * Changes
 * -------
 * 16-Sept-2004 : Initial revision (JVS);
 * 07-Jun-2005 : Made generic (CH);
 *
 */
package ags.graph;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.traverse.*;


/**
 * Performs cycle detection on a graph. The <i>inspected graph</i> is specified
 * at construction time and cannot be modified. Currently, the detector supports
 * only directed graphs.
 *
 * @author John V. Sichi
 * @since Sept 16, 2004
 */
public class CycleDetector<V, E>
{
    //~ Instance fields --------------------------------------------------------

    /**
     * Graph on which cycle detection is being performed.
     */
    private DirectedGraph<V, E> graph;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a cycle detector for the specified graph. Currently only directed
     * graphs are supported.
     *
     * @param graph the DirectedGraph in which to detect cycles
     */
    public CycleDetector(DirectedGraph<V, E> graph)
    {
        this.graph = graph;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Performs yes/no cycle detection on the entire graph.
     *
     * @return true iff the graph contains at least one cycle
     */
    @SuppressWarnings({ "unchecked", "static-access" })
	public List<V> detectCycles()
    {
        try {
            execute(null, null);
        } catch (CycleDetectedException ex) {
            return ex.path;
        }

        return null;
    }

    /**
     * Performs yes/no cycle detection on an individual vertex.
     *
     * @param v the vertex to test
     *
     * @return true if v is on at least one cycle
     */
    public boolean detectCyclesContainingVertex(V v)
    {
        try {
            execute(null, v);
        } catch (CycleDetectedException ex) {
            return true;
        }

        return false;
    }


    /**
     * Finds the vertex set for the subgraph of all cycles which contain a
     * particular vertex.
     *
     * <p>REVIEW jvs 25-Aug-2006: This implementation is not guaranteed to cover
     * all cases. If you want to be absolutely certain that you report vertices
     * from all cycles containing v, it's safer (but less efficient) to use
     * StrongConnectivityInspector instead and return the strongly connected
     * component containing v.
     *
     * @param v the vertex to test
     *
     * @return set of all vertices reachable from v via at least one cycle
     */
    public Set<V> findCyclesContainingVertex(V v)
    {
        Set<V> set = new HashSet<V>();
        execute(set, v);

        return set;
    }

    private void execute(Set<V> s, V v)
    {
        ProbeIterator iter = new ProbeIterator(s, v);

        while (iter.hasNext()) {
            iter.next();
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * Exception thrown internally when a cycle is detected during a yes/no
     * cycle test. Must be caught by top-level detection method.
     */
    private static class CycleDetectedException
        extends RuntimeException
    {
    	@SuppressWarnings({ "unchecked", "static-access" })
		public CycleDetectedException(List path)
    	{
    		this.path = path;
    	}
        private static final long serialVersionUID = 3834305137802950712L;
        @SuppressWarnings("unchecked")
		protected static List path;
    }

    /**
     * Version of DFS which maintains a backtracking path used to probe for
     * cycles.
     */
    private class ProbeIterator
        extends DepthFirstIterator<V, E>
    {
        private List<V> path;
        private Set<V> cycleSet;
        private V root;

        ProbeIterator(Set<V> cycleSet, V startVertex)
        {
            super(graph, startVertex);
            root = startVertex;
            this.cycleSet = cycleSet;
            path = new ArrayList<V>();
        }

        /**
         * {@inheritDoc}
         */
        protected void encounterVertexAgain(V vertex, E edge)
        {
            super.encounterVertexAgain(vertex, edge);

            int i;

            if (root != null) {
                // For rooted detection, the path must either
                // double back to the root, or to a node of a cycle
                // which has already been detected.
                if (vertex == root) {
                    i = 0;
                } else if ((cycleSet != null) && cycleSet.contains(vertex)) {
                    i = 0;
                } else {
                    return;
                }
            } else {
                i = path.indexOf(vertex);
            }

            if (i > -1) {
                if (cycleSet == null) {
                    // we're doing yes/no cycle detection
                	path.add(vertex);
                    throw new CycleDetectedException(path);
                } else {
                    for (; i < path.size(); ++i) {
                        cycleSet.add(path.get(i));
                    }
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        protected V provideNextVertex()
        {
            V v = super.provideNextVertex();

            // backtrack
            for (int i = path.size() - 1; i >= 0; --i) {
                if (graph.containsEdge(path.get(i), v)) {
                    break;
                }

                path.remove(i);
            }

            path.add(v);

            return v;
        }
    }
}

// End CycleDetector.java
