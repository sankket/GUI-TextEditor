package dictionary;
import java.io.Serializable;
import java.util.ArrayList;

public class Node implements Serializable
{
	private static final long serialVersionUID = 1553643845173588298L;
	static final int ALPHABET_SIZE = 26;
	Node[] children;
	boolean stop;
	ArrayList<String> words;
	String prefix;

	Node()
	{
		children = new Node[ALPHABET_SIZE];
		words = new ArrayList<String>();
		stop = false;
		prefix = null;
	}
}
