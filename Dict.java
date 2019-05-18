import java.util.HashSet;
import java.util.Scanner;
import java.lang.Object;
import java.io.File;
import java.nio.file.NoSuchFileException;

public class Dict {
	public static void main(String [] argv) {
		String word = new Scanner(System.in).next();
		HashSet<String> set;
		int i = 0;
		int depth = 2;
		// Поле depth отвечает за то, насколько далёкое по ошибкам слово будет найдено от оригинала
		// Три ошибки ещё можно поситать на небольших словах, для глубины 4 даже трёхбуквенного не дождаться
		try {set = known();}
		catch(Exception e) {
			System.out.println(e);
			return;
		}

		if (set.contains(word.toLowerCase())) {
			System.out.println("The word is correct");
			return;
		}

		set.retainAll(edits2(word.toLowerCase(), depth));
		set.remove(word.toLowerCase());

		System.out.println("The word is incorrect. Do you mean:");
		for (Object w : set.toArray()) {
			i++;
			System.out.print(i + ": ");
			System.out.println((String)w);
		}
		if (i == 0)
			System.out.println("No such word found");
	}

	public static HashSet<String> known() throws NoSuchFileException{
		HashSet<String> dictWords = new HashSet<>();
		Scanner scan = null;

		try {
			File a = new File("Dict.txt");
			scan = new Scanner(a);
		}
		catch (Exception e) {
			throw new NoSuchFileException("The dictionary must be named \"Dict.txt\"");
		}

		while (scan.hasNext()) {
			dictWords.add(scan.next().toLowerCase());
		}
		try {scan.close();}
		catch(Exception e) {
			throw new NoSuchFileException("The dictionary must be named \"Dict.txt\"");
		}
		return dictWords;
	}

	public static HashSet<String> edits(String word) {
		char [] letters = new char [] {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
		int length = word.length();
		HashSet<String> out = new HashSet<>();
		StringBuilder [] lefts = new StringBuilder [length + 1];
		StringBuilder [] rights = new StringBuilder [length + 1];
		StringBuilder delete;
		StringBuilder transpose;
		StringBuilder replace;
		StringBuilder insert;

		for (int i = 0; i < length + 1; i++) {
			lefts[i] = new StringBuilder(word.subSequence(0, i));
			rights[i] = new StringBuilder(word.subSequence(i, length));
		}

		for (int i = 0; i < length; i++) {
			delete = new StringBuilder(lefts[i]);
			delete.append(rights[i].substring(1, rights[i].length()));
			out.add(delete.toString());
			if (rights[i].length() > 1) {
				transpose = new StringBuilder(lefts[i]);
				transpose.append(rights[i].charAt(1))
						  .append(rights[i].charAt(0))
						  .append(rights[i].substring(2, rights[i].length()));
				out.add(transpose.toString());
			}
			for (int j = 0; j < 26; j++) {
				replace = new StringBuilder(lefts[i]);
				replace.append(letters[j])
					   .append(rights[i].substring(1, rights[i].length()));
				out.add(replace.toString());

				insert = new StringBuilder(lefts[i]);
				insert.append(letters[j])
					  .append(rights[i]);
				out.add(insert.toString());
			}
		}
		return out;	
	}

	public static HashSet<String> edits2(String word, int depth) {
		HashSet<String> out = new HashSet<>();
		HashSet<String> editses = edits(word);
		int iter = 2;

		for (Object w : editses.toArray()) 
			out.addAll(edits((String)w));
		
		while (iter < depth) {
			for (Object w : out.toArray()) 
				out.addAll(edits((String)w));
			iter++;
		}
		
		return out;
	}
}