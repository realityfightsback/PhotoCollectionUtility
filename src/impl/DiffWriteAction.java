package impl;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class DiffWriteAction extends Action {

	public DiffWriteAction(String sub, String repo, String outputDir) {
		super(sub, repo);
		this.outputDirectory = outputDir;
	}

	private String outputDirectory;
	private boolean initialized = false;

	@Override
	public void executeDupeAction(File file) {
		super.executeDupeAction(file);
		write("Dupes.txt", file);

	}

	@Override
	public void executeUniqueAction(File file) {
		super.executeUniqueAction(file);
		write("Uniques.txt", file);

	}

	// TODO bulk writes
	private void write(String outputFileName, File file) {
		if (initialized == false) {
			FileUtils.deleteQuietly(new File(outputDirectory + "Dupes.txt"));
			FileUtils.deleteQuietly(new File(outputDirectory + "Uniques.txt"));
			initialized = true;
		}
		try {
			FileUtils.writeStringToFile(new File(outputDirectory
					+ outputFileName), file.getAbsolutePath() + "\n", true);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
