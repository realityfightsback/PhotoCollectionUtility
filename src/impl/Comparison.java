package impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import util.StopWatch;

public class Comparison extends Object {

	// @formatter:off
	// TODO To prevent specific portions of Java code from being formatted, go to "Window > Preferences > Java > Code Style > Formatter". Click the "Edit..." button, go to the "Off/On Tags" tab and enable the tags
	/*
	 * Want to offer the following f(x)
	 * 1) Ability to take a folder full of photos (submissionFolder) and check whether they exist in a repository folder (Family Pictures folder)
	 * 		Restructure the submission folder.
	 * 			 Non-matched files are put into a folder (/submissionFolder/unique/{date-formats})
	 * 			 Matched files are put into a folder (/submissionFolder/likelyDupes/{date-formats})
	 * 	 	 		{date-formats} are determined based on date of photo creation (example : 2008-9)
	 * 
	 * Matches are done on the basis of file size + name
	 * Initial implementation ideas:
	 * Create a Set (RepoSet) based on the files within the Repo folder, FileRepresentations should override equals() and hashCode()
	 * Create folders within subFolder for unique and likelyDupes
	 * Run through submission folder Files, create FileRepresentations, check against RepoSet
	 * Move as appropriate, creating folder if necessary for a new {date-format}
	 *  
	 */
	// @formatter:on

	private static final String INDEX_KEY_VAL_SEPERATOR = "___---___";

	private static String indexLocation = "C:\\Users\\Standard\\Desktop\\Results\\DropboxCompare.index";

	private static String candidateFilelocation =
	// "C:\\Users\\Standard\\Desktop\\Add";
	"I:\\DCIM\\100MEDIA";
	// "E:\\Family Stuff\\DAD pics";
	private static String repoLocation =
	// "C:\\Users\\Standard\\Desktop\\Repo";
	"E:\\Dropbox just in case";
	// "F:\\Family Stuff\\Family Pictures";
	private static String outputDir = "C:\\Users\\Standard\\Desktop\\Results\\";

	public static void main(String[] args) throws IOException {

		Action actionToTake = new DiffWriteAction(candidateFilelocation,
				repoLocation, outputDir);

		HashSet<String> excludedFileNames = createExcludedSet();

		HashMap<String, Integer> repoSet = createRepoSet();

		runCandidateFilesAgainstRepo(repoSet, excludedFileNames, actionToTake);

	}

	private static void runCandidateFilesAgainstRepo(
			HashMap<String, Integer> repoSet,
			HashSet<String> excludedFileNames, Action actionToTake)
			throws IOException {
		StopWatch stopWatch = new StopWatch();

		File subFolder = new File(candidateFilelocation);

		Collection<File> collectionSubFiles = FileUtils.listFiles(subFolder,
				null, true);

		File[] candidateFiles = collectionSubFiles.toArray(new File[0]);

		for (File candidateFile : candidateFiles) {

			if (candidateFile.isFile()
					&& excludedFileNames.contains(candidateFile.getName()) == false)
				if (repoSet.containsValue(generateHash(candidateFile)))
					actionToTake.executeDupeAction(candidateFile);
				else
					actionToTake.executeUniqueAction(candidateFile);

			// May take a while
			if (actionToTake.filesProcessed % 100 == 0)
				System.out
						.println(String
								.format("Still running. Currently %d files processed in %d millis",
										actionToTake.filesProcessed,
										stopWatch.getDuration()));
		}

		System.out.println("Candidate comparison completed.");

	}

	/**
	 * 
	 * @param temp
	 * @return Hash based on file contents. -1 for files over 100MB
	 * @throws IOException
	 */
	private static Integer generateHash(File temp) throws IOException {

		if (temp.length() > 100_000_000) {// File too large to safely hash
			System.out.println(temp.getName());
			return -1;
		}
		return FileUtils.readFileToString(temp).hashCode();

	}

	private static HashMap<String, Integer> createRepoSet() throws IOException {

		StopWatch stopWatch = new StopWatch();

		HashMap<String, Integer> repoSet = new HashMap<String, Integer>();

		// Get pre-generated hashes (prior runs)
		readIndexFileIn(repoSet);

		File repoFolder = new File(repoLocation);

		Collection<File> collectionRepoFiles = FileUtils.listFiles(repoFolder,
				null, true);

		File[] repoAllFiles = collectionRepoFiles.toArray(new File[0]);

		for (int i = 0; i < repoAllFiles.length; i++) {
			File temp = repoAllFiles[i];
			if (temp.isFile()) {
				String key = temp.getName() + temp.length();
				// If file was not pregenerated, add it
				if (repoSet.containsKey(key) == false) {
					repoSet.put(key, generateHash(temp));
				}

				if (i % 100 == 0) {
					System.out
							.println(String
									.format("Repo set building currently at %d elements in %d millis ",
											i, stopWatch.getDuration()));
				}
			}
		}

		System.out.println(String.format(
				"Repo set generation took %d millis. Contains %d elements ",
				stopWatch.getDuration(), repoSet.size()));

		stopWatch.reset();
		// Overwrite index
		writeOutIndexFile(repoSet);

		writeOutIndexFileNatively(repoSet);

		System.out.println(String.format("Repo index writeout took %d millis.",
				stopWatch.getDuration()));

		return repoSet;
	}

	private static void writeOutIndexFileNatively(
			HashMap<String, Integer> repoSet) throws IOException {

		FileOutputStream f = new FileOutputStream(new File(indexLocation
				+ ".serial"));
		ObjectOutput s = new ObjectOutputStream(f);
		s.writeObject(repoSet);
		s.flush();

	}

	private static void writeOutIndexFile(HashMap<String, Integer> repoSet)
			throws IOException {
		File index = new File(indexLocation);
		FileUtils.deleteQuietly(index);

		Set<Entry<String, Integer>> entries = repoSet.entrySet();

		StringBuilder stringBuilder = new StringBuilder();
		for (Entry<String, Integer> entry : entries) {
			stringBuilder.append(entry.getKey() + INDEX_KEY_VAL_SEPERATOR
					+ entry.getValue() + "\n");
		}
		FileUtils.writeStringToFile(index, stringBuilder.toString());
	}

	private static void readIndexFileIn(HashMap<String, Integer> repoSet)
			throws IOException {
		File index = new File(indexLocation);

		if (index.exists() == false) {
			return;
		}

		List<String> lines = FileUtils.readLines(new File(indexLocation));

		for (String line : lines) {
			String[] kv = line.split(INDEX_KEY_VAL_SEPERATOR);
			if (kv.length == 2) {
				repoSet.put(kv[0], Integer.parseInt(kv[1]));
			}
		}

	}

	private static HashMap<String, Integer> readIndexFileInNatively()
			throws IOException, ClassNotFoundException {
		try (FileInputStream in = new FileInputStream("tmp");
				ObjectInputStream s = new ObjectInputStream(in);) {
			return (HashMap<String, Integer>) s.readObject();
		}
	}

	private static HashSet<String> createExcludedSet() {
		HashSet<String> excludedFileNames = new HashSet<String>();
		String[] excludedArray = { "ZbThumbnail.info", "Thumbs.db" };

		for (int i = 0; i < excludedArray.length; i++) {
			excludedFileNames.add(excludedArray[i]);
		}
		return excludedFileNames;
	}

	public static String getSubLocation() {
		return candidateFilelocation;
	}

	public static void setSubLocation(String subLocation) {
		Comparison.candidateFilelocation = subLocation;
	}

	public static String getRepoLocation() {
		return repoLocation;
	}

	public static void setRepoLocation(String repoLocation) {
		Comparison.repoLocation = repoLocation;
	}

}
