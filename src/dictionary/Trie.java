package dictionary;

import java.io.Serializable;
import java.util.ArrayList;

public class Trie implements Serializable
{
	private static final long serialVersionUID = 5972251272171783459L;
	private Node root;

	Trie()
	{
		root = new Node();
	}

	/**
	 * 
	 * @param word
	 *            The String to be searched.
	 * @return Returns true if the String exists, false otherwise.
	 */
	public boolean hasWord(String word)
	{
		Node temp = search(word);
		if(temp != null && temp.stop == true)
			return true;
		return false;
	}

	/**
	 * 
	 * @param word
	 *            The String whose Node is to be returned.
	 * @return Returns the Node of the searched String.
	 */
	private Node search(String word)
	{
		Node curr = root;
		int index = 0;
		boolean found = true;
		while(true)
		{
			if(index >= word.length())
				break;
			else if(curr.children[word.charAt(index) - 'a'] == null)
			{
				found = false;
				break;
			}
			else
			{
				curr = curr.children[word.charAt(index) - 'a'];
				index++;
			}
		}
		if(!found)
			return null;
		return curr;
	}

	/**
	 * 
	 * @param word
	 *            The string which is to be added to the trie.
	 */
	public void addWord(String word)
	{
		if(hasWord(word))
			return;
		Node curr = root;
		for(int index = 0; index < word.length(); index++)
		{
			curr.words.add(word.substring(index, word.length()));
			Node next = curr.children[word.charAt(index) - 'a'];
			if(next != null)
				curr = next;
			else
			{
				curr.children[word.charAt(index) - 'a'] = new Node();
				curr = curr.children[word.charAt(index) - 'a'];
				curr.prefix = word.substring(0, index);
			}
		}
		curr.stop = true;
	}

	/**
	 * 
	 * @param prefix
	 *            The string for which suggestions are needed.
	 * @return Returns an ArrayList of Strings which are valid suffixes of the
	 *         suggestions. Returns null if prefix is invalid.
	 */
	public ArrayList<String> suggest(String prefix)
	{
		Node temp = search(prefix);
		if(temp != null && !temp.stop)
			return temp.words;
		return null;
	}
}
