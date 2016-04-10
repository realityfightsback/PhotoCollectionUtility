package impl;

import java.io.File;

public abstract class Action {

	public int filesProcessed = 0;

	public String subLocation = "";
	public String repoLocation = "";

	public Action(String sub, String repo) {
		subLocation = sub;
		repoLocation = repo;
	}

	/**
	 * To be used for any photos considered to already be in the Reposet
	 * 
	 * @param file
	 */
	public void executeDupeAction(File file){
		filesProcessed++;
	}

	/**
	 * To be used for any photo not considered to be in the Reposet
	 * 
	 * @param file
	 */
	public void executeUniqueAction(File file){
		filesProcessed++;
	}

}
