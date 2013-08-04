package impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import org.apache.commons.io.FileUtils;

public class FileMovingTester {
	public static void main(String[] args) throws IOException {
		String subLocation = "C:\\Users\\Standard\\Desktop\\Folder A";
		// String repoLocation = "C:\\Users\\Standard\\Desktop\\Folder B";

		File subFolder = new File(subLocation);

		File[] subFileList = subFolder.listFiles();

		for (int i = 0; i < subFileList.length; i++) {
			File temp = subFileList[i];
			if (temp.isFile()) {
				FileUtils.moveFile(temp,
						new File(constructFilePath(true, temp)));
			}
		}

	}

	private static String constructFilePath(boolean unique, File f)
			throws IOException {
		StringBuilder sBuild = new StringBuilder();

		sBuild.append(f.getParent());

		if (unique) {
			sBuild.append(File.separatorChar);
			sBuild.append("unique");
			createDataFormattedPath(f, sBuild);

		} else {
			sBuild.append(File.separatorChar);
			sBuild.append("likelyDupe");
			createDataFormattedPath(f, sBuild);
		}

		return sBuild.toString();
	}

	private static void createDataFormattedPath(File f, StringBuilder sBuild)
			throws IOException {
		sBuild.append(File.separatorChar);
		Path path = FileSystems.getDefault().getPath(f.getAbsolutePath());

		BasicFileAttributes attrs = Files.readAttributes(path,
				BasicFileAttributes.class);
		FileTime fTime = attrs.lastModifiedTime();

		String tempTime = (fTime.toString()).substring(0, 7);

		sBuild.append(tempTime);

		sBuild.append(File.separatorChar);
		sBuild.append(f.getName().trim());
	}
}
