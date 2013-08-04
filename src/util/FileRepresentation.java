package util;

public class FileRepresentation {

	long fileSize;
	String fileName;

	public FileRepresentation(String name, long length) {
		fileName = name;
		fileSize = length;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.fileName == null) ? 0 : this.fileName.hashCode());
		result = prime * result
				+ (int) (this.fileSize ^ (this.fileSize >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileRepresentation other = (FileRepresentation) obj;
		if (this.fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!this.fileName.equals(other.fileName))
			return false;
		if (this.fileSize != other.fileSize)
			return false;
		return true;
	}

}
