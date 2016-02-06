package application.operations.miner;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.ListenableDirectedGraph;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxRectangle;

public class FastMiner {

	public static List<Object> execute(final XLog log, double threshold, int time) {
		// first we create the attribute list
		final List<String> activity_list = create_activity_list(log);

		// then we create and initialize the directly follows matrix
		final int[][] directly_follows_matrix = new int[activity_list.size()][activity_list.size()];
		for (int i = 0; i < directly_follows_matrix.length; i++)
			for (int j = 0; j < directly_follows_matrix[i].length; j++)
				directly_follows_matrix[i][j] = 0;

		// build a time-bounded directly follows matrix

		ExecutorService executor = Executors.newFixedThreadPool(1);
		Future<?> future = executor.submit(new Runnable() {
			@Override
			public void run() {
				fill_directly_follows_matrix(directly_follows_matrix, activity_list, log); // time
																							// bounded
			}
		});
		executor.shutdown(); // <-- reject all further submissions

		try {
			future.get(time, TimeUnit.SECONDS); // <-- wait 'threshold' seconds
												// to finish
		} catch (TimeoutException e) {
			future.cancel(true); // <-- interrupt the job
			System.out.println("timeout");
		} catch (Exception e) { // <-- possible error cases
			System.out.println("job was interrupted, cause :");
			e.printStackTrace();
		}

		// if still doesnt finish, wait all unfinished tasks for 1 sec
		try {
			if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
				// force them to quit by interrupting
				executor.shutdownNow();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// now we build the graph
		ListenableDirectedGraph<String, LabeledEdge> g = create_graph_model(directly_follows_matrix, activity_list,
				threshold);
		List<Object> result = new Vector<Object>();
		result.add(g);

		return result;
	}

	public static JComponent get_visual_results(List<Object> results) {
		mxGraphComponent graphComponent = null;
		if (results.get(0) instanceof ListenableDirectedGraph) {
			@SuppressWarnings("unchecked")
			ListenableDirectedGraph<String, LabeledEdge> graph = (ListenableDirectedGraph<String, LabeledEdge>) results
					.get(0);

			JGraphXAdapter<String, LabeledEdge> jgxAdapter = new JGraphXAdapter<String, LabeledEdge>(graph);

			// positioning via jgraphx layouts
			graphComponent = new mxGraphComponent(jgxAdapter);
			graphComponent.setConnectable(false);

			// set vertex default size
			int size = graph.vertexSet().size();
			mxRectangle[] bounds = new mxRectangle[size];
			for (int i = 0; i < size; i++)
				bounds[i] = new mxRectangle(i * 10, i * 10, 100, 30);

			jgxAdapter.resizeCells(graph.vertexSet().toArray(), bounds);

			jgxAdapter.setCellsEditable(true);
			jgxAdapter.setCellsMovable(true);
			jgxAdapter.setCellsSelectable(true);
			jgxAdapter.setCellsResizable(true);
			jgxAdapter.setEventsEnabled(true);

			// mxHierarchicalLayout layout = new
			// mxHierarchicalLayout(jgxAdapter, SwingConstants.WEST);
			mxHierarchicalLayout layout = new mxHierarchicalLayout(jgxAdapter, SwingConstants.WEST);
			layout.setIntraCellSpacing(100);
			layout.execute(jgxAdapter.getDefaultParent());

			// graphComponent.setPreferredSize(new Dimension(600,600));
			// graphComponent.setBorder(BorderFactory.createLineBorder(Color.black));

			return graphComponent;

		} else {
			System.out.println("failure");
			JLabel label = new JLabel(" ");
			// label.setPreferredSize(new Dimension(600,600));
			// label.setBorder(BorderFactory.createLineBorder(Color.black));
			return label;
		}

	}

	public static List<String> create_activity_list(XLog log) {
		List<String> activity_list;
		SortedSet<String> activity_set = new TreeSet<String>();

		for (XTrace trace : log) // here i get all the possible activity names
			for (XEvent event : trace)
				for (XAttribute attribute : event.getAttributes().values())
					if (attribute.getKey().matches("concept:name"))
						activity_set.add(attribute.toString());

		activity_list = new Vector<String>();
		for (String s : activity_set)
			activity_list.add(s);
		return activity_list;
	}

	public static int[][] fill_directly_follows_matrix(int[][] directly_follows_matrix, List<String> activity_list,
			XLog log) {

		// initialize the directly_follows_matrix with 0s
		for (int i = 0; i < directly_follows_matrix.length; i++)
			for (int j = 0; j < directly_follows_matrix.length; j++)
				directly_follows_matrix[i][j] = 0;

		XEvent last = null;

		for (XTrace trace : log)
			for (XEvent event : trace)
				for (XAttribute attribute : event.getAttributes().values())
					if (attribute.getKey().matches("concept:name")) {
						if (last != null) {
							for (XAttribute last_attribute : last.getAttributes().values()) {
								if (last_attribute.getKey().matches("concept:name")) {
									directly_follows_matrix[activity_list.indexOf(
											last_attribute.toString())][activity_list.indexOf(attribute.toString())]++;
								}
							}
						}
						last = event;
					}
		return directly_follows_matrix;
	}

	public static ListenableDirectedGraph<String, LabeledEdge> create_graph_model(int[][] directly_follows_matrix,
			List<String> activity_list, double threshold) {
		// now we calculate the rows sum for calculating the percentual
		// occurence of directly follows relations
		int[] row_sum = null;
		if (directly_follows_matrix != null) {
			row_sum = new int[directly_follows_matrix.length];
			for (int i = 0; i < directly_follows_matrix.length; i++) {
				row_sum[i] = 0; // initialize
				for (int j = 0; j < directly_follows_matrix[i].length; j++)
					row_sum[i] = row_sum[i] + directly_follows_matrix[i][j];
			}
		}

		// now we build the graph structure

		// create a JGraphT graph
		ListenableDirectedGraph<String, LabeledEdge> g = new ListenableDirectedGraph<String, LabeledEdge>(
				LabeledEdge.class);

		// create a visualization using JGraph, via an adapter

		// adds all vertex from the activity list
		for (String s : activity_list)
			g.addVertex(s);

		// now adds all edges from directly follows matrix
		for (int i = 0; i < directly_follows_matrix.length; i++)
			for (int j = 0; j < directly_follows_matrix[i].length; j++) {
				if (threshold <= ((double) directly_follows_matrix[i][j] / (double) row_sum[i])) // if
																									// the
																									// relation
																									// is
																									// stronger
																									// than
																									// the
																									// threshold
					g.addEdge(activity_list.get(i), activity_list.get(j), new LabeledEdge(activity_list.get(i),
							activity_list.get(j), ((Integer) directly_follows_matrix[i][j]).toString()));
			}

		return g;

	}

}
