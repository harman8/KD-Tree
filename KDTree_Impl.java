package kdtree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

/**
 * @author Manan Dineshkumar Paruthi (40192620) , Harman Preet Kaur (40198317) & Ravleen Kaur (40221236)
 */

public class KDTree_Impl {
	static int blockSize = 2;
	static ArrayList<ArrayList<Integer>> data = new ArrayList<ArrayList<Integer>>();
	static AttributeNode root;
	static boolean isRestructuringReq = false;

	public static void main(String args[]) {
		// addData(25, 60);
		// addData(45, 60);
		// addData(50, 75);
		// addData(50, 100);
		// addData(50, 120);
		// addData(70, 110);
		// addData(85, 140);
		// addData(30, 260);
		// addData(25, 400);
		// addData(45, 350);
		// addData(50, 275);
		// addData(60, 260);
		// display(root);
		// addRecord(root, 26, 70, 0);
		// addRecord(root, 40, 200, 0);
		// System.out.println("Tree after inserting ==> " + root);
		// deleteRecord(root, 26, 70, 0);
		// System.out.println("Tree after deleting simple case ==> ");
		// display(root);
		// System.out.println();
		// deleteRecord(root, 25, 60, 0);
		// System.out.println("Tree after deleting removing node ==> ");
		// display(root);
		// System.out.println();

		Scanner sc = new Scanner(System.in);
		System.out.println("------ Initializing the tree ------");
		System.out.println("Please enter the number of nodes in a tree");
		Integer noOfNodes = sc.nextInt();
		for (int i = 0; i < noOfNodes; i++) {
			System.out.println("Please enter Age for Node number " + (i+1));
			Integer ageNode = sc.nextInt();
			System.out.println("Please enter Salary in thousands for Node number " + (i+1));
			Integer SalaryNode = sc.nextInt();
			addData(ageNode, SalaryNode);

		}
		root = splitData(data, 0);

		Integer age = 0;
		Integer salary = 0;

		while (true) {
			System.out.println("-------------------");
			System.out.println("1. Add a Record");
			System.out.println("2. Delete a Record");
			System.out.println("3. Display the Tree");
			System.out.println("4. Exit");
			System.out.println("-------------------");

			Integer input = sc.nextInt();

			switch (input) {
				case 1:
					System.out.println("Please enter age");
					age = sc.nextInt();
					System.out.println("Please enter salary in thousands");
					salary = sc.nextInt();
					addRecord(root, age, salary, 0);
					break;
				case 2:
					System.out.println("Please enter age");
					age = sc.nextInt();
					System.out.println("Please enter salary in thousands");
					salary = sc.nextInt();

					boolean flag = false;
					for (ArrayList<Integer> val : data) {
						if (val.get(0) == age && val.get(1) == salary) {
							flag = true;
						}
					}

					if (flag) {
						deleteRecord(root, age, salary, 0);
					}
					else {
						System.out.println("No such record exists");
					}
					break;
				case 3:
					display(root);
					break;
				case 4:
					sc.close();
					System.exit(0);
					break;
			}
		}
	}

	public static void display(Node root) {
		int height = heightofTree(root);
		levelOrder(root, height);
	}

	public static void levelOrder(Node node, int height) {
		Queue<Node> queue = new LinkedList<>();
		queue.add(node);
		while (!queue.isEmpty()) {
			int queueSize = queue.size();
			for (int i = 0; i < queueSize; i++) {
				Node parentNode = queue.poll();
				for (int h = 0; h < height * 2.5 + 1; h++) {
					System.out.print("\t");
				}
				System.out.print(" " + parentNode);
				if (parentNode.typeOfNode == TypeOfNode.Attribute) {
					AttributeNode attributeNode = (AttributeNode) parentNode;
					queue.add(attributeNode.left);
					queue.add(attributeNode.right);
				}
			}
			height -= 1;
			System.out.println();
			System.out.println();
		}
	}

	public static int heightofTree(Node node) {
		if (node.typeOfNode == TypeOfNode.Pointer) {
			return 1;
		} else {
			AttributeNode attributeNode = (AttributeNode) node;
			int leftHeight = heightofTree(attributeNode.left);
			int rightHeight = heightofTree(attributeNode.right);
			return Math.max(leftHeight, rightHeight) + 1;
		}
	}

	public static void restructureTree() {
		ArrayList<ArrayList<Integer>> allRecords = getAllRecords(root, new ArrayList<ArrayList<Integer>>());
		root = splitData(allRecords, 0);
	}

	public static ArrayList<ArrayList<Integer>> getAllRecords(Node node, ArrayList<ArrayList<Integer>> data) {
		if (node.typeOfNode == TypeOfNode.Attribute) {
			AttributeNode attributeNode = (AttributeNode) node;
			getAllRecords(attributeNode.left, data);
			getAllRecords(attributeNode.right, data);
		} else if (node.typeOfNode == TypeOfNode.Pointer) {
			PointerNode pointerNode = (PointerNode) node;
			for (int i = 0; i < pointerNode.block.size(); i++) {
				data.add(pointerNode.block.get(i));
			}
		}
		return data;
	}

	public static void deleteRecord(Node node, int age, int salary, int nodeAttr) {
		deleteData(node, age, salary, nodeAttr);
		if (isRestructuringReq) {
			restructureTree();
			isRestructuringReq = false;
		}
	}

	public static Node deleteData(Node node, int age, int salary, int nodeAttr) {
		if (node.typeOfNode == TypeOfNode.Pointer) {
			PointerNode currNode = (PointerNode) node;
			int index = 0;
			for (int i = 0; i < currNode.block.size(); i++) {
				ArrayList<Integer> record = currNode.block.get(i);
				if (record.get(0) == age && record.get(1) == salary) {
					index = i;
					break;
				}
			}
			currNode.block.remove(index);

			if (currNode.block.size() == 0) {
				isRestructuringReq = true;
			}
			return currNode;
		} else {
			AttributeNode currNode = (AttributeNode) node;
			if (currNode.name == "Salary") {
				if (salary < currNode.value) {
					Node newNode = deleteData(currNode.left, age, salary, 1);
					currNode.left = newNode;
				} else {
					Node newNode = deleteData(currNode.right, age, salary, 1);
					currNode.right = newNode;
				}
			} else if (currNode.name == "Age") {
				if (age < currNode.value) {
					Node newNode = deleteData(currNode.left, age, salary, 0);
					currNode.left = newNode;
				} else {
					Node newNode = deleteData(currNode.right, age, salary, 0);
					currNode.right = newNode;
				}
			}
		}
		return node;
	}

	public static Node addRecord(Node node, int age, int salary, int nodeAttr) {
		if (node.typeOfNode == TypeOfNode.Pointer) {
			PointerNode currNode = (PointerNode) node;
			if (currNode.block.size() < blockSize) { // block has space
				ArrayList<Integer> newRecord = new ArrayList<Integer>(2);
				newRecord.add(age);
				newRecord.add(salary);
				currNode.block.add(newRecord);
				return node;
			} else { // split and add a new attribute node
				ArrayList<Integer> newRecord = new ArrayList<Integer>(2);
				newRecord.add(age);
				newRecord.add(salary);
				ArrayList<ArrayList<Integer>> data = currNode.block;
				data.add(newRecord);
				return splitData(data, nodeAttr);
			}
		} else {
			AttributeNode currNode = (AttributeNode) node;
			if (currNode.name == "Salary") {
				if (salary < currNode.value) {
					Node newNode = addRecord(currNode.left, age, salary, 1);
					currNode.left = newNode;
				} else {
					Node newNode = addRecord(currNode.right, age, salary, 1);
					currNode.right = newNode;
				}
			} else if (currNode.name == "Age") {
				if (age < currNode.value) {
					Node newNode = addRecord(currNode.left, age, salary, 0);
					currNode.left = newNode;
				} else {
					Node newNode = addRecord(currNode.right, age, salary, 0);
					currNode.right = newNode;
				}
			}
		}
		return node;
	}

	public static AttributeNode splitData(ArrayList<ArrayList<Integer>> data, int lastAttributeSplit) { // lastAttributeSplit - 0 => age , 1 => salary // toggle
		AttributeNode node = null;
		if (lastAttributeSplit == 0) {
			int salaryVal = getAvgVal(1, data);
			ArrayList<ArrayList<Integer>> leftArr = new ArrayList<ArrayList<Integer>>();
			ArrayList<ArrayList<Integer>> rightArr = new ArrayList<ArrayList<Integer>>();

			for (ArrayList<Integer> val : data) {
				if (val.get(1) < salaryVal) {
					leftArr.add(val);
				} else {
					rightArr.add(val);
				}
			}
			node = new AttributeNode("Salary", salaryVal, new PointerNode(leftArr), new PointerNode(rightArr));
			lastAttributeSplit = 1;
		} else if (lastAttributeSplit == 1) {
			int ageVal = getAvgVal(0, data);
			ArrayList<ArrayList<Integer>> leftArr = new ArrayList<ArrayList<Integer>>();
			ArrayList<ArrayList<Integer>> rightArr = new ArrayList<ArrayList<Integer>>();

			for (ArrayList<Integer> val : data) {
				if (val.get(0) < ageVal) {
					leftArr.add(val);
				} else {
					rightArr.add(val);
				}
			}
			node = new AttributeNode("Age", ageVal, new PointerNode(leftArr), new PointerNode(rightArr));
			lastAttributeSplit = 0;
		}

		PointerNode leftData = (PointerNode) node.left;
		PointerNode rightData = (PointerNode) node.right;
		if (leftData.block.size() > 2) {
			node.left = splitData(leftData.block, lastAttributeSplit);
		}
		if (rightData.block.size() > 2) {
			node.right = splitData(rightData.block, lastAttributeSplit);
		}
		return node;
	}

	public static int getAvgVal(int index, ArrayList<ArrayList<Integer>> data) { // index = 0 => age & index = 1 => salary
		int total = 0;
		int count = 0;
		for (ArrayList<Integer> val : data) {
			total += val.get(index);
			count += 1;
		}
		return (int) total / count;
	}

	public static void addData(int age, int salary) {
		ArrayList<Integer> record = new ArrayList<Integer>(2);
		record.add(age);
		record.add(salary);
		data.add(record);
	}
}

class Node {
	TypeOfNode typeOfNode;

	public Node(TypeOfNode typeOfNode) {
		this.typeOfNode = typeOfNode;
	}
}

enum TypeOfNode {
	Attribute,
	Pointer
}

class AttributeNode extends Node { // intermediate node - stores attribute name and value
	String name;
	int value;
	Node left;
	Node right;

	public AttributeNode(String name, int value, Node left, Node right) {
		super(TypeOfNode.Attribute);
		this.name = name;
		this.value = value;
		this.left = left;
		this.right = right;
	}

	@Override
	public String toString() {
		return "[name= " + name + ", value= " + value + "] ";
	}
}

class PointerNode extends Node { // leaf node - stores record data / pointers
	ArrayList<ArrayList<Integer>> block;

	public PointerNode(ArrayList<ArrayList<Integer>> val) {
		super(TypeOfNode.Pointer);
		this.block = val;
	}

	@Override
	public String toString() {
		return "[block=" + block + "]   ";
	}

}