package search;

import java.io.*;
import java.util.*;

/**
 * This class encapsulates an occurrence of a keyword in a document. It stores the
 * document name, and the frequency of occurrence in that document. Occurrences are
 * associated with keywords in an index hash table.
 * 
 * @author Sesh Venugopal
 * 
 */
class Occurrence {
	/**
	 * Document in which a keyword occurs.
	 */
	String document;
	
	/**
	 * The frequency (number of times) the keyword occurs in the above document.
	 */
	int frequency;
	
	/**
	 * Initializes this occurrence with the given document,frequency pair.
	 * 
	 * @param doc Document name
	 * @param freq Frequency
	 */
	public Occurrence(String doc, int freq) {
		document = doc;
		frequency = freq;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(" + document + "," + frequency + ")";
	}
}

/**
 * This class builds an index of keywords. Each keyword maps to a set of documents in
 * which it occurs, with frequency of occurrence in each document. Once the index is built,
 * the documents can searched on for keywords.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in descending
	 * order of occurrence frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash table of all noise words - mapping is from word to itself.
	 */
	HashMap<String,String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashMap<String,String>(100,2.0f);
	}
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.put(word,word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeyWords(docFile);
			mergeKeyWords(kws);
		}
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeyWords(String docFile) 
	throws FileNotFoundException {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
        HashMap<String,Occurrence> kw = new HashMap<String,Occurrence>();
        Scanner sc =new Scanner(new File(docFile));
        while(sc.hasNext())
        {
        String text = sc.next();
            String keyword = getKeyWord(text);
            if (keyword != null) {
                if (!kw.containsKey(keyword)) {
                    kw.put(keyword, new Occurrence(docFile, 1));
                } else
                    kw.get(keyword).frequency++;
            }
        }
        return kw;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeyWords(HashMap<String,Occurrence> kws) {
		// COMPLETE THIS METHOD
		//ArrayList<Occurrence> oc = new ArrayList<Occurrence>();
		for(String key: kws.keySet())
		{
			if(keywordsIndex.containsKey(key))
			{
				keywordsIndex.get(key).add(kws.get(key));
				insertLastOccurrence(keywordsIndex.get(key));
			}
			else
			{
                ArrayList<Occurrence> oc = new ArrayList<Occurrence>();
			    oc.add(kws.get(key));
				keywordsIndex.put(key, oc);
			}
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * TRAILING punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyWord(String word) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
        if(!word.matches(".*[a-zA-Z].*"))
        {
            return null;
        }
		StringTokenizer st = new StringTokenizer(word,",!?.:;-'");
        String wordie = st.nextToken();
		if(st.hasMoreTokens())
		{
			return null;
		}
        wordie = wordie.replaceAll("[^a-zA-z]" ,"").toLowerCase();
        if (noiseWords.containsValue(wordie)) {
            return null;
        }
        else
            {
                return wordie;
            }
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * same list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion of the last element
	 * (the one at index n-1) is done by first finding the correct spot using binary search, 
	 * then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
        if(occs.size() == 1)
        {
            return null;
        }
        ArrayList<Integer> midpoints = new ArrayList<Integer>();
        Occurrence temp = occs.remove(occs.size()-1);
        int lo = 0;
        int hi = occs.size() - 1;
        int mid = 0;
        while (lo <= hi) {
            // Key is in a[lo..hi] or not present.
            mid = lo + (hi - lo) / 2;
			midpoints.add(mid);
			if(occs.get(mid).frequency == temp.frequency)
            {
                occs.add(mid+1,temp);
                return midpoints;
            }
            else
                if(temp.frequency < occs.get(mid).frequency) {
                lo = mid + 1;
            }
            else if (temp.frequency > occs.get(mid).frequency) {
                hi = mid - 1;
            }
        }
        if(temp.frequency < occs.get(mid).frequency)
        {
            occs.add(mid+1,temp);
        }
        else
        {
            occs.add(mid,temp);
        }

		return midpoints;
	}
	private int DocMatch(ArrayList<Occurrence> s, String document)
	{
		int i = 0;
		for(Occurrence doc: s)
		{
			if(doc.document.equals(document))
			{
				return i;
			}
			i++;
		}
			return -1;
	}
	private int FreqMatch(ArrayList<Occurrence> s, int freq)
	{
		int i = 0;
		int j = -1;
		for(Occurrence doc: s)
		{
			if(doc.frequency == freq)
			{
				j = i;
			}
			i++;
		}
		return j;
	}

	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of occurrence frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will appear before doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matching documents, the result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of NAMES of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matching documents,
	 *         the result is null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
        kw1 = kw1.toLowerCase();
        kw2 = kw2.toLowerCase();
		ArrayList<Occurrence> top = new ArrayList<Occurrence>();
		//ArrayList<Occurrence> top2 = new ArrayList<Occurrence>();
		ArrayList<String> result = new ArrayList<String>();
		if(keywordsIndex.containsKey(kw1))
		{
			for(int i = 0; i<keywordsIndex.get(kw1).size();i++) {
					top.add(keywordsIndex.get(kw1).get(i));
			}
			if(keywordsIndex.containsKey(kw2))
			{
				for(int i = 0; i<keywordsIndex.get(kw2).size();i++)
				{
					int inside = DocMatch(top,keywordsIndex.get(kw2).get(i).document);
					if(inside == -1)
					{
						int samef = FreqMatch(top,keywordsIndex.get(kw2).get(i).frequency);
						if(samef == -1) {
							top.add(keywordsIndex.get(kw2).get(i));
							insertLastOccurrence(top);
						}
						else
						{
							top.add(samef+1,keywordsIndex.get(kw2).get(i));
						}
					}
					else
					{
						if(keywordsIndex.get(kw2).get(i).frequency > top.get(inside).frequency)
						{
							top.remove(inside);
							top.add(keywordsIndex.get(kw2).get(i));
							insertLastOccurrence(top);
						}
					}

				}
			}
		}
		else {
            if (keywordsIndex.containsKey(kw2)) {
                for (int i = 0; i < keywordsIndex.get(kw2).size(); i++) {
                    top.add(keywordsIndex.get(kw2).get(i));
                }

            }
        }
		if (top.isEmpty())
		{
			return null;
		}
		for(int i = 0; result.size()<5;i++)
		{
		    if(i > top.size()-1)
            {
                break;
            }
		        //System.out.println(top.get(i).frequency);
                result.add(top.get(i).document);
            }
			return result;
	}
}
