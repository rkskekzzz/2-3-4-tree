import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class HW2 {

	public static void main(String[] args) {
		Tree234<String, Integer> st = new Tree234<>();
		Scanner sc = new Scanner(System.in);
		System.out.print("file name? ");
		String fname = sc.nextLine();
		System.out.print("seed for random value? ");
		long seed = sc.nextLong();
		Random rand = new Random(seed);
		sc.close();
		try {
			sc = new Scanner(new File(fname));
			long start = System.currentTimeMillis();
			while (sc.hasNext()) {
				String word = sc.next();
				if (!st.contains(word))
					st.put(word, 1);
				else	st.put(word, st.get(word) + 1);
			}
			long end = System.currentTimeMillis();
			System.out.println("Input Done: Time = " + (end-start) + "ms");

			System.out.println("### Tree info");
			print_tree(st);
			ArrayList<String> keyList = (ArrayList<String>) st.keys();
			int loopCount = (int)(keyList.size() * 0.95);
			for (int i = 0; i < loopCount; i++) {
				int deletedIndex = rand.nextInt(keyList.size());
				String key = keyList.get(deletedIndex);
				//st.delete(key);									// yet
				keyList.remove(deletedIndex);
			}
			//System.out.println("\n### 키 삭제 후 트리 정보");			// yet
			//print_tree(st);										// yet
		} catch (FileNotFoundException e) { e.printStackTrace(); }
		if (sc != null)
			sc.close();
	}

	private static void print_tree(Tree234<String, Integer> st) {
		System.out.println("word count in file = " + st.size());
		System.out.println("depth of Tree = " + st.depth());

		String maxKey = "";
		int maxValue = 0;
		for (String word : st.keys())
			if (st.get(word) > maxValue) {
				maxValue = st.get(word);
				maxKey = word;
			}
		System.out.println("most frequent words and word count : " + maxKey + " " + maxValue);
	}
}




class Tree234<K extends Comparable<K>, V> {
	protected Node<K, V> root;
	int count = 0;

	class Node<K, V> {
		K key_left, key_middle, key_right;
		V value_left, value_middle, value_right;


		Node<K, V> left, left_middle, right_middle, right, parent;

		public Node(K key_left, V value_left) {
			this.key_left = key_left;
			this.value_left = value_left;
		}

		public Node(K key_left, K key_middle, V value_left, V value_middle) {
			this.key_left = key_left;
			this.key_middle = key_middle;
			this.value_left = value_left;
			this.value_middle = value_middle;
		}

		public Node(K key_left, K key_middle, K key_right, V value_left, V value_middle, V value_right) {
			this.key_left = key_left;
			this.key_middle = key_middle;
			this.key_right = key_right;
			this.value_left = value_left;
			this.value_middle = value_middle;
			this.value_right = value_right;

		}

	}

	public void put(K key, V value) {
		if (root == null) {
			root = new Node<K, V>(key, value);
			count++;
			return;
		}
		Node<K, V> getnode = getNode(key, root);
		if (key.equals(getnode.key_left)) {
			getnode.value_left = value;
			return;
		}
		if (getnode.key_middle == null) {
			if(key.compareTo(getnode.key_left)<0) {
				getnode.key_middle = getnode.key_left;
				getnode.value_middle = getnode.value_left;
				getnode.key_left = key;
				getnode.value_left = value;
			}
			else {
				getnode.key_middle = key;
				getnode.value_middle = value;
			}
			count++;
			return;
		}
		if (key.equals(getnode.key_middle)) {
			getnode.value_middle = value;
			return;
		}
		if (getnode.key_right == null) {
			if(key.compareTo(getnode.key_left)<0) {
				getnode.key_right = getnode.key_middle;
				getnode.value_right = getnode.value_middle;
				getnode.key_middle = getnode.key_left;
				getnode.value_middle = getnode.value_left;
				getnode.key_left = key;
				getnode.value_left = value;
			}else if(key.compareTo(getnode.key_middle)<0) {
				getnode.key_right = getnode.key_middle;
				getnode.value_right = getnode.value_middle;
				getnode.key_middle = key;
				getnode.value_middle = value;
			}
			else {
				getnode.key_right = key;
				getnode.value_right = value;
			}
			count++;
			return;
		}
		if (key.equals(getnode.key_right)) {
			getnode.value_right = value;
		} else {
			count++;
			Node<K, V> newNode = new Node<>(key, value);
			split(getnode, newNode);
		}

	}

	private void split(Node<K, V> start, Node<K, V> newNode) {
		Node<K, V> needMerge = null,temp = null;
		Node<K,V> parent = start.parent;
		if (start == null || newNode == null)
			return;
		if (newNode.key_left.compareTo(start.key_left) < 0) {
			needMerge = new Node<>(start.key_middle, start.value_middle);
			linkChild(needMerge, newNode, start, null, null);
			linkChild(newNode, newNode.left, newNode.left_middle, start.left_middle, null);
			linkChild(start, start.right_middle, start.right, null, null);
			newNode.key_middle = start.key_left;
			newNode.value_middle = start.value_left;
			start.key_left = start.key_right;
			start.value_left = start.value_right;
			start.key_middle = null;
			start.value_middle = null;

		} else if (newNode.key_left.compareTo(start.key_middle) < 0) {
			needMerge = new Node<>(start.key_middle, start.value_middle);
			temp = new Node<>(start.key_right,start.value_right);
			linkChild(temp,start.right_middle,start.right,null,null);
			linkChild(start, start.left, newNode.left, newNode.left_middle, null);
			linkChild(needMerge, start, temp, null, null);
			start.key_middle = newNode.key_left;
			start.value_middle = newNode.value_left;

		} else if (newNode.key_left.compareTo(start.key_right) < 0) {
			needMerge = new Node<>(newNode.key_left, newNode.value_left);
			temp = new Node<>(start.key_right,start.value_right);
			linkChild(temp,newNode.left_middle,start.right,null,null);
			linkChild(start, start.left, start.left_middle, newNode.left, null);
			linkChild(needMerge, start, temp, null, null);
		} else {
			needMerge = new Node<>(start.key_right, start.value_right);
			linkChild(needMerge, start, newNode, null, null);

		}
		start.key_right = null;
		start.value_right = null;

		if (start == root) {
			root = needMerge;
			return;
		}
		merge(parent, needMerge);

	}
	public int depth() {
		int n=0;

		Node<K,V> startNode = root;
		while(startNode!=null) {
			startNode = startNode.left;
			n++;
		}
		return n;
	}
	public boolean contains(K word) {
		Node<K,V> searchNode = getNode(word,root);
		if(searchNode==null)
			return false;
		if(word.equals(searchNode.key_left)||word.equals(searchNode.key_middle)||word.equals(searchNode.key_right))
			return true;
		else
			return false;
	}
	public int size() {
		return count;
	}
	public V get(K word) {
		Node<K,V> searchNode = getNode(word,root);
		if(searchNode==null)
			return null;
		if(word.equals(searchNode.key_left))
			return searchNode.value_left;
		else if(word.equals(searchNode.key_middle))
			return searchNode.value_middle;
		else if(word.equals(searchNode.key_right))
			return searchNode.value_right;
		else
			return null;
	}
	private void merge(Node<K, V> parent, Node<K, V> needMerge) {
		if (parent.key_middle == null) {
			if (needMerge.key_left.compareTo(parent.key_left) > 0) {
				parent.key_middle = needMerge.key_left;
				parent.value_middle = needMerge.value_left;

				linkChild(parent, parent.left, needMerge.left,needMerge.left_middle,null);
			} else {
				parent.key_middle = parent.key_left;
				parent.value_middle = parent.value_left;
				parent.key_left = needMerge.key_left;
				parent.value_left = needMerge.value_left;

				linkChild(parent,needMerge.left,needMerge.left_middle ,parent.left_middle,null);
			}
		} else if (parent.key_right == null) {
			if (needMerge.key_left.compareTo(parent.key_left) < 0) {
				parent.key_right = parent.key_middle;
				parent.value_right = parent.value_middle;
				parent.key_middle = parent.key_left;
				parent.value_middle = parent.value_left;
				parent.key_left = needMerge.key_left;
				parent.value_left = needMerge.value_left;

				linkChild(parent, needMerge.left,needMerge.left_middle ,parent.left_middle ,parent.right_middle );
			} else if (needMerge.key_left.compareTo(parent.key_middle) < 0) {
				parent.key_right = parent.key_middle;
				parent.value_right = parent.value_middle;
				parent.key_middle = needMerge.key_left;
				parent.value_middle = needMerge.value_left;

				linkChild(parent,parent.left ,needMerge.left ,needMerge.left_middle ,parent.right_middle );
			} else {
				parent.key_right = needMerge.key_left;
				parent.value_right = needMerge.value_left;

				linkChild(parent,parent.left ,parent.left_middle ,needMerge.left,needMerge.left_middle );
			}
		} else {
			split(parent, needMerge);
		}
	}

	private void linkChild(Node<K, V> parent, Node<K, V> lchild, Node<K, V> lmchild, Node<K, V> rmchild,
			Node<K, V> rchild) {
		if (parent != null) {
			parent.left = lchild;
			parent.left_middle = lmchild;
			parent.right_middle = rmchild;
			parent.right = rchild;
		}
		if (lchild != null)
			lchild.parent = parent;
		if (lmchild != null)
			lmchild.parent = parent;
		if (rmchild != null)
			rmchild.parent = parent;
		if (rchild != null)
			rchild.parent = parent;
	}

	private Node<K, V> getNode(K key, Node<K, V> start) {
		if (start == null)
			return null;
		if (start.left == null)
			return start;
		if (key.compareTo(start.key_left) < 0)
			return getNode(key, start.left);
		if (key.equals(start.key_left))
			return start;
		if (start.key_middle == null || key.compareTo(start.key_middle) < 0)
			return getNode(key, start.left_middle);
		if (key.equals(start.key_middle))
			return start;
		if (start.key_right == null || key.compareTo(start.key_right) < 0)
			return getNode(key, start.right_middle);
		if (key.equals(start.key_right))
			return start;
		else
			return getNode(key, start.right);
	}
	public Iterable<K> keys(){
		if(root==null)return null;
		ArrayList<K> keyList = new ArrayList<K>(size());
		inorder(root, keyList);
		return keyList;
	}

	private void inorder(Node<K, V> x,ArrayList<K> keyList) {
		if(x==null) return;
		if (x.left==null) {
			keyList.add(x.key_left);
			if(x.key_middle!=null)
				keyList.add(x.key_middle);
			if(x.key_right!=null)
				keyList.add(x.key_right);
		}
		else if(x.key_middle==null) {
			inorder(x.left,keyList);
			keyList.add(x.key_left);
			inorder(x.left_middle,keyList);
		}
		else if(x.key_right==null) {
			inorder(x.left,keyList);
			keyList.add(x.key_left);
			inorder(x.left_middle,keyList);
			keyList.add(x.key_middle);
			inorder(x.right_middle,keyList);
		}
		else {
			inorder(x.left,keyList);
			keyList.add(x.key_left);
			inorder(x.left_middle,keyList);
			keyList.add(x.key_middle);
			inorder(x.right_middle,keyList);
			keyList.add(x.key_right);
			inorder(x.right,keyList);
		}

	}

	public void print(Node<K,V> start) {
		if(start==null) return;
		System.out.println(
				"left : " + start.key_left + "," + start.value_left
				+ "  mid : " + start.key_middle + "," + start.value_middle
				+ "  right : " + start.key_right + "," + start.value_right);
	}

}
