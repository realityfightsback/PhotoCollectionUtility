package impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.io.FileUtils;

import util.FileRepresentation;

public class Comparison extends Object {

	// @formatter:off
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

	private static int filesMoved = 0;
	private static String subLocation = "E:\\Family Stuff\\DAD pics";
	private static String repoLocation = "F:\\Family Stuff\\Family Pictures";

	public static void main(String[] args) throws IOException {

		HashSet<String> excludedFileNames = new HashSet<String>();
		String[] excludedArray = { "ZbThumbnail.info", "Thumbs.db" };

		for (int i = 0; i < excludedArray.length; i++) {
			excludedFileNames.add(excludedArray[i]);
		}

		HashSet<FileRepresentation> repoSet = new HashSet<FileRepresentation>();

		File repoFolder = new File(repoLocation);

		Collection<File> collectionRepoFiles = FileUtils.listFiles(repoFolder,
				null, true);

		File[] repoAllFiles = collectionRepoFiles.toArray(new File[0]);

		for (int i = 0; i < repoAllFiles.length; i++) {
			File temp = repoAllFiles[i];
			if (temp.isFile()) {
				FileRepresentation fp = new FileRepresentation(temp.getName(),
						temp.length());
				repoSet.add(fp);
			}
		}

		System.out.println("Repo set contains " + repoSet.size());

		File subFolder = new File(subLocation);

		Collection<File> collectionSubFiles = FileUtils.listFiles(subFolder,
				null, true);

		File[] subAllFiles = collectionSubFiles.toArray(new File[0]);

		for (int i = 0; i < subAllFiles.length; i++) {
			File temp = subAllFiles[i];
			if (temp.isFile()) {
				FileRepresentation fp = new FileRepresentation(temp.getName(),
						temp.length());
				if (excludedFileNames.contains(temp.getName()) == false)
					if (repoSet.contains(fp))
						moveFile(temp, true);// its probably a duplicate
					else
						moveFile(temp, false);

			}

			// May take a while
			if (filesMoved % 100 == 0)
				System.out.println("Still running. Currently " + filesMoved
						+ " files moved");

		}

	}

	private static void moveFile(File temp, boolean isDupe) throws IOException {
		filesMoved++;
		String fPath = createFilePath(false, temp);

		fPath = checkForNameConflicts(fPath);

		FileUtils.moveFile(temp, new File(fPath));

	}

	/**
	 * Ensures a file with the same name doesn't exist. If it does we begin
	 * appending parenthesized number to the file. Example: File.jpg ->
	 * File(1).jpg, we check again. If a pre-existing file of that name still
	 * conflicts, -> File(2).jpg, etc
	 * 
	 * @param fPath
	 * @return The non-conflicting String
	 */
	public static String checkForNameConflicts(String fPath) {
		File checkForExistence = new File(fPath);

		StringBuilder b = null;
		boolean firstRun = true; // First time through we just look for ".", if
									// we have to do a second run because there
									// is a xxx(1).xxx already need to change
									// the digit.
		int sequenceNum = 1;

		while (checkForExistence.exists()) {
			// Determine file extension length (.jpg .jgeg)

			if (firstRun) {
				int periodPosition = fPath.lastIndexOf("\\.");

				b = new StringBuilder(fPath);

				b.insert(periodPosition - 1, "(" + sequenceNum + ")");
			} else {
				int openingParen = fPath.lastIndexOf("(");
				int closingParen = fPath.lastIndexOf(")");

				b = new StringBuilder(fPath);

				b.replace(openingParen + 1, closingParen, sequenceNum + "");
			}

			fPath = b.toString();
			checkForExistence = new File(fPath);
		}

		return fPath;

	}

	public static String createFilePath(boolean isDupe, File f)
			throws IOException {
		StringBuilder sBuild = new StringBuilder();

		sBuild.append(subLocation);

		sBuild.append(File.separatorChar);
		if (isDupe)
			sBuild.append("likelyDupe");
		else
			sBuild.append("unique");

		createDataFormattedPath(f, sBuild);

		if (filesMoved % 100 == 0)
			System.out.println("Sample file move location" + sBuild.toString());

		return sBuild.toString();
	}

	public static void createDataFormattedPath(File f, StringBuilder sBuild)
			throws IOException {
		sBuild.append(File.separatorChar);
		Path path = FileSystems.getDefault().getPath(f.getAbsolutePath());

		BasicFileAttributes attrs = Files.readAttributes(path,
				BasicFileAttributes.class);

		FileTime fTime = attrs.lastModifiedTime();

		// String rawTempTime = fTime.toString();
		//
		// String tempTime = (rawTempTime).substring(0, 7);

		Calendar cal = Calendar.getInstance();

		// TODO this is hacky. Look at other timezone conversion stuff
		long timeZoneConversion = fTime.toMillis() - (8 * 60 * 60 * 1000);
		cal.setTimeInMillis(timeZoneConversion);

		String tempTime = cal.get(Calendar.YEAR) + "-"
				+ cal.get(Calendar.MONTH);

		sBuild.append(tempTime);

		sBuild.append(File.separatorChar);
		sBuild.append(f.getName().trim());
	}

	public static String getSubLocation() {
		return subLocation;
	}

	public static void setSubLocation(String subLocation) {
		Comparison.subLocation = subLocation;
	}

	public static String getRepoLocation() {
		return repoLocation;
	}

	public static void setRepoLocation(String repoLocation) {
		Comparison.repoLocation = repoLocation;
	}

}
