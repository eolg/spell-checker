import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class SpellChecker {

	private HashMap<Integer, ArrayList<String>> dictionary;

	/**creates a spell checker and builds a dictionary from a file in the 
	 * command line arguments, then spell checks another file in the command 
	 * line arguments
	 * @param args dictionary file and file to spell check**/
	public static void main(String[] args) throws FileNotFoundException {
		SpellChecker test = new SpellChecker();
		test.buildDictionary(args);
		test.checkWords(args);
	}
	
	/**creates a spell checker**/
	public SpellChecker(){
	}

	/**builds a HashMap out of a dictionary file. each word's key is computed
	 * by adding the ASCII value of each letter and words with the same key
	 * are stored in an ArrayList of strings
	 * @param args String[] containing dictionary file**/
	private void buildDictionary(String[] args)
		throws FileNotFoundException {
		File dictionaryFile = new File(args[0]);
		Scanner input = new Scanner(dictionaryFile);
		dictionary = new HashMap<Integer, ArrayList<String>>();
		while (input.hasNextLine()) {
			String word = input.nextLine();
			word = word.toLowerCase();
			int key = getKey(word);
			if (dictionary.get(key) != null) {
				dictionary.get(key).add(word);
			} else {
				ArrayList<String> dictionaryWords = new ArrayList<String>();
				dictionaryWords.add(word);
				dictionary.put(key, dictionaryWords);
			}
		}
	}

	/**parses the words of a file into strings and strips and leading or trailing
	 * punctuation. then spell checks each string and prints any misspellings with
	 * line number and possible correct spellings
	 * @param args String[] containing file to spell check**/
	private void checkWords(String[] args) throws FileNotFoundException {
		File spellCheckFile = new File(args[1]);
		Scanner input = new Scanner(spellCheckFile);
		int lineNum = 0;
		while (input.hasNext()) {
			String line = input.nextLine();
			line = line.toLowerCase();
			lineNum++;
			String[] wordsToCheck = line.split(" ");
			for (int i = 0; i < wordsToCheck.length; i++) {
				if (wordsToCheck[i].length() > 0) {
					if (!Character.isDigit(wordsToCheck[i].charAt(0))
						&& !Character.isLetter(wordsToCheck[i].charAt(0))) {
						wordsToCheck[i] = wordsToCheck[i].substring(1,
							wordsToCheck[i].length());
					}
				}
				if (wordsToCheck[i].length() > 1) {
					if (!Character.isDigit(
						wordsToCheck[i].charAt(wordsToCheck[i].length() - 1))
						&& !Character.isLetter(wordsToCheck[i]
							.charAt(wordsToCheck[i].length() - 1))) {
						wordsToCheck[i] = wordsToCheck[i].substring(0,
							wordsToCheck[i].length() - 1);
					}
				}
				ArrayList<String> correctSpellings = spellCheck(
					wordsToCheck[i]);
				if (correctSpellings != null) {
					System.out.println("Misspelled word \"" + wordsToCheck[i]
						+ "\" on line " + lineNum
						+ ". Possible correct spellings: " + correctSpellings);
				}
			}
		}
	}

	/**checks if a word is correctly spelled by checking if it is in the HashMap.
	 * this is done by computing its key value and checking if the word is
	 * found in the ArrayList associated with that key. if the word is not found
	 * in the HashMap, the list of possible correct spellings found by calling 
	 * the findCorrectSpellings method is returned (or if the word is not misspelled,
	 * no list is returned.
	 * @param wordToCheck word to spell check
	 * @return list of possible correct spellings of misspelled word**/
	private ArrayList<String> spellCheck(String wordToCheck) {
		int key = getKey(wordToCheck);
		if (dictionary.containsKey(key)) {
			if (dictionary.get(key).contains(wordToCheck)) {
				return null;
			} else {
				return (findCorrectSpellings(wordToCheck));
			}
		} else {
			return (findCorrectSpellings(wordToCheck));
		}
	}

	/**attempts to create a correctly spelled word from a misspelled word
	 * by adding each letter a-z or an apostrophe at each position in the word,
	 * deleting the letter at each position in the word, and rearranging 
	 * adjacent letters in the word
	 * @param misspelledWord misspelled word to find correct spellings for
	 * @return list of possible correct spellings of the misspelled word**/
	private ArrayList<String> findCorrectSpellings(String misspelledWord) {
		String word = misspelledWord;
		ArrayList<String> correctSpellings = new ArrayList<String>();
		if (misspelledWord.length() > 1) {
			for (int i = 0; i < misspelledWord.length() - 1; i++) {
				word = misspelledWord;
				char[] letters = word.toCharArray();
				char temp = letters[i];
				letters[i] = letters[i + 1];
				letters[i + 1] = temp;
				String newWord = new String(letters);
				int key = getKey(newWord);
				if (dictionary.containsKey(key)) {
					if (dictionary.get(key).contains(newWord)) {
						if (!correctSpellings.contains(newWord)) {
							correctSpellings.add(newWord);
						}
					}
				}
			}
		}
		for (int i = 0; i < misspelledWord.length(); i++) {
			word = misspelledWord;
			StringBuilder insertApostrophe = new StringBuilder(word);
			insertApostrophe.insert(i, "'");
			word = insertApostrophe.toString();
			int key = getKey(word);
			if (dictionary.containsKey(key)) {
				if (dictionary.get(key).contains(word)) {
					if (!correctSpellings.contains(word)) {
						correctSpellings.add(word);
					}
				}
			}
			word = misspelledWord;
			StringBuilder deleteLetters = new StringBuilder(word);
			deleteLetters.deleteCharAt(i);
			word = deleteLetters.toString();
			key = getKey(word);
			if (dictionary.containsKey(key)) {
				if (dictionary.get(key).contains(word)) {
					if (!correctSpellings.contains(word)) {
						correctSpellings.add(word);
					}
				}
			}
		}
		for (int i = 0; i < misspelledWord.length() + 1; i++) {
			for (int j = 97; j < 123; j++) {
				word = misspelledWord;
				char letter = (char) j;
				StringBuilder insertLetters = new StringBuilder(word);
				insertLetters.insert(i, letter);
				word = insertLetters.toString();
				int key = getKey(word);
				if (dictionary.containsKey(key)) {
					if (dictionary.get(key).contains(word)) {
						if (!correctSpellings.contains(word)) {
							correctSpellings.add(word);
						}
					}
				}
			}
		}
		return correctSpellings;

	}

	/**returns the key of a word by adding ASCII values of each character in 
	 * the word
	 * @param word word to compute the key of
	 * @return key of a word**/
	private int getKey(String word) {
		int hashValue = 0;
		for (int i = 0; i < word.length(); i++) {
			hashValue += (int) word.charAt(i);
		}
		return hashValue;
	}
}
