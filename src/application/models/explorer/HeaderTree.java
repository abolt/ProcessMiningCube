package application.models.explorer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import application.models.attribute.abstr.Attribute;
import application.models.condition.ConditionUtils;
import application.models.condition.abstr.Condition;
import application.models.condition.factory.ConditionFactory;

public class HeaderTree {
	private Node root;
	private Map<Integer, Attribute> layerToAttributeMap;
	private int layers;

	public class Node implements Cloneable {
		public ArrayList<Node> children;
		public Node parent;
		public List<Condition> values;

		public Node() {
			children = new ArrayList<Node>();
			values = new ArrayList<Condition>();
		}

		@Override
		public Node clone() {
			Node newNode = new Node();
			newNode.parent = this.parent;
			newNode.values.addAll(this.values);
			newNode.children.addAll(this.children);
			return newNode;
		}
	}

	public HeaderTree(List<Attribute> attributes) {
		layerToAttributeMap = new TreeMap<Integer, Attribute>();
		layers = 0;
		for (Attribute a : attributes)
			layerToAttributeMap.put(layers++, a);

		List<Attribute> atts = new ArrayList<Attribute>();
		atts.addAll(layerToAttributeMap.values());

		root = new Node();
		root.parent = null;
		addNodesRecursive(atts);
	}

	public void addNodesRecursive(List<Attribute> remainingAttributes) {
		if (!remainingAttributes.isEmpty()) {
			List<Node> leafs = getLeafs(layers - remainingAttributes.size());
			for (Node leaf : leafs)
				for (Object s : remainingAttributes.get(0).getValueSet()) {
					Condition newCondition = ConditionFactory.createCondition(remainingAttributes.get(0));
					ConditionUtils.addConditionToTail(newCondition, Condition.EQUALS, s.toString());
					addElement(leaf, newCondition);
				}
			remainingAttributes.remove(0);
			addNodesRecursive(remainingAttributes);
		}

	}

	public Node addElement(Node currentNode, Condition child) {
		Node newNode = new Node();
		newNode.parent = currentNode;
		newNode.values.add(child);
		currentNode.children.add(newNode);
		return newNode;
	}

	/**
	 * This function returns all the leaf nodes of a specific layer if layer is
	 * < 0, then we use the last layer
	 * 
	 * @param layer
	 * @return List of all the nodes on that layer
	 */
	public List<Node> getLeafs(int layer) {
		List<Node> list = new ArrayList<Node>();
		if (layer < 0)
			layer = layers;
		getLeafRecursive(list, layer, root);
		return list;
	}

	private void getLeafRecursive(List<Node> list, int layer, Node node) {
		if (getDistanceToRoot(node) == layer)
			list.add(node);
		else if (getDistanceToRoot(node) < layer && !node.children.isEmpty())
			for (Node children : node.children)
				getLeafRecursive(list, layer, children);
	}

	public List<Node> getAccumulatedLeafs(int layer) {
		List<Node> list = new ArrayList<Node>();
		for (Node node : getLeafs(layer))
			list.add(node.clone());
		for (Node node : list)
			addParentsDataToLeaf(node, node.parent);
		return list;
	}

	private void addParentsDataToLeaf(Node node, Node parent) {
		if (parent != null) {
			List<Condition> values = new ArrayList<Condition>();
			values.addAll(node.values);

			node.values.clear();

			node.values.addAll(parent.values);
			node.values.addAll(values);
			addParentsDataToLeaf(node, parent.parent);
		}
	}

	/**
	 * This method calculates the distance from any node to the root
	 * 
	 * @param a
	 *            node
	 * @return distance from the node to the root of the tree
	 */
	private int getDistanceToRoot(Node node) {
		if (node.equals(root))
			return 0;
		else
			return 1 + getDistanceToRoot(node.parent);
	}
}
