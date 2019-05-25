package dictionary;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Dictionary
{
	Trie WT;
	File dictFile;
	char[] alphabet = new char[26];

	public Dictionary(String fileName)
	{
		dictFile = new File(fileName);
		WT = new Trie();
		for(int i = 0; i < 26; i++)
			alphabet[i] = (char)('a' + i);		
		generateTrieFromWordlist();
		//		initializeDictionary();
	}
	private void initializeDictionary()
	{
		try
		{
			generateTrieFromObjectFile();
		} catch (IOException e)
		{
			generateTrieFromWordlist();
			writeTrieToFile();
		}
	}
	public boolean hasWord(String word)
	{
		return WT.hasWord(word.toLowerCase());
	}
	public void generateTrieFromObjectFile() throws FileNotFoundException, IOException
	{
		ObjectInputStream O = new ObjectInputStream(new BufferedInputStream(new FileInputStream("Trie")));
		try
		{
			WT = (Trie)O.readObject();
		} catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		O.close();
	}
	public void generateTrieFromWordlist()
	{
		Scanner fileReader;
		try
		{
			fileReader = new Scanner(dictFile);
			while(fileReader.hasNext())
				WT.addWord(fileReader.next().toLowerCase());
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	public void writeTrieToFile()
	{
		try
		{
			ObjectOutputStream O = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("Trie")));
			O.writeObject(WT);
			O.close();
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}		
	}
	public ArrayList<String> getSuggestions(String word)
	{
		if(word == null  || word.length()<=1)
			return null;
		if(Character.isUpperCase(word.charAt(0)))
			word = word.toLowerCase();
		if(word.length()>=3)
		{
			ArrayList<String> s = WT.suggest(word.toLowerCase());
			return s;
		}
		else
			return null;
	}
	public Set<String> getSuggestionsForWord(String word, int num)
	{
		Set<String> a = new HashSet<String>();
		if(word==null || word.length() == 0)
			return a;
		boolean isFirstCapital = false;		
		if(Character.isUpperCase(word.charAt(0)))
		{
			isFirstCapital = true;
			word = word.toLowerCase();
		}
		Set<String> s = getEdits1(word);
		int i = 0;
		for(String w : s)
		{
			if(i > num)
				return a;
			if(WT.hasWord(w))
			{
				if(isFirstCapital)
					w = Character.toUpperCase(w.charAt(0))+(w.length()>1?w.substring(1):"");
				a.add(w);
				i++;
			}
		}
		s = getEdits2(word);
		for(String w : s)
		{
			if(i > num)
				return a;
			if(isFirstCapital)
				w = Character.toUpperCase(w.charAt(0))+(w.length()>1?w.substring(1):"");
			a.add(w);
			i++;
		}
		return a;
	}
		
	/**
	 * 
	 * @param word String for which suggestions are needed
	 * @return Set<String> containing *all* Strings within edit distance 1 of word.
	 */
	private Set<String> getEdits1(String word)
	{
		Set<String> edits = new HashSet<String>();
		
		// Deletes
		for(int i = 0; i < word.length(); i++)
			edits.add((i > 0 ? word.substring(0, i) : "") + (i+1 < word.length() ? word.substring(i+1) : ""));
		
		// Transposes
		for(int i = 0; i < word.length() - 1; i++)
			edits.add((i > 0 ? word.substring(0, i) : "") + word.charAt(i+1) + "" + word.charAt(i) + (i+2 < word.length() ? word.substring(i+2) : ""));
		
		// Replaces
		for(int i = 0; i < word.length(); i++)
			for(char c : alphabet)
				edits.add((i > 0 ? word.substring(0, i) : "") + c + (i+1 < word.length() ? word.substring(i+1) : ""));
		
		// Inserts
		for(int i = 0; i <= word.length(); i++)
			for(char c : alphabet)
				edits.add((i > 0 ? word.substring(0, i) : "") + c + (i < word.length() ? word.substring(i) : ""));
		
		return edits;
		
	}
	
	/**
	 * 
	 * @param word String for which suggestions are needed
	 * @return A Set<String> containing words *from the dictionary* of 2 edit distance with word.
	 */
	private Set<String> getEdits2(String word)
	{
		Set<String> edits = getEdits1(word);
		Set<String> edits2 = new HashSet<String>();
		for(String s : edits)
		{
			Set<String> editscurr = getEdits1(s);
			for(String s2 : editscurr)
				if(WT.hasWord(s2))
					edits2.add(s2);
		}
		return edits2;
	}
}