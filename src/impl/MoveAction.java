package impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;

public class MoveAction extends Action {

	public MoveAction(String sub, String repo) {
		super(sub, repo);
	}

	@Override
	public void executeDupeAction(File file) {
		super.executeDupeAction(file);
		try {
			moveFile(file, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void executeUniqueAction(File file) {
		super.executeUniqueAction(file);
		try {
			moveFile(file, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void moveFile(File temp, boolean isDupe) throws IOException {
		String fPath = createFilePath(isDupe, temp);

		fPath = checkForNameConflicts(fPath);

		FileUtils.moveFile(temp, new File(fPath));

	}

	private String createFilePath(boolean isDupe, File f) throws IOException {
		StringBuilder sBuild = new StringBuilder();

		sBuild.append(subLocation);

		sBuild.append(File.separatorChar);
		if (isDupe)
			sBuild.append("likelyDupe");
		else
			sBuild.append("unique");

		createDataFormattedPath(f, sBuild);

		if (filesProcessed % 100 == 0)
			System.out.println("Sample file move location" + sBuild.toString());

		return sBuild.toString();
	}

	private static void createDataFormattedPath(File f, StringBuilder sBuild)
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

	/**
	 * Ensures a file with the same name doesn't exist. If it does we begin
	 * appending parenthesized number to the file. Example: File.jpg ->
	 * File(1).jpg, we check again. If a pre-existing file of that name still
	 * conflicts, -> File(2).jpg, etc
	 * 
	 * @param fPath
	 * @return The non-conflicting String
	 */
	private static String checkForNameConflicts(String fPath) {
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

}
