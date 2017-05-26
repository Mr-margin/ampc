package ampc.com.gistone.extract.vertical;

public class ShellVertival {
	private String specieIndexFilePath;
	private String juzhenPath;
	private String nclPath;
	private String pngPath;
	
	@Override
	public String toString() {
		return "ShellVertival [specieIndexFilePath=" + specieIndexFilePath + ", juzhenPath=" + juzhenPath + ", nclPath="
				+ nclPath + ", pngPath=" + pngPath + "]";
	}

	public String getPngPath() {
		return pngPath;
	}

	public void setPngPath(String pngPath) {
		this.pngPath = pngPath;
	}

	public String getNclPath() {
		return nclPath;
	}

	public void setNclPath(String nclPath) {
		this.nclPath = nclPath;
	}

	public String getJuzhenPath() {
		return juzhenPath;
	}

	public void setJuzhenPath(String juzhenPath) {
		this.juzhenPath = juzhenPath;
	}

	public String getSpecieIndexFilePath() {
		return specieIndexFilePath;
	}

	public void setSpecieIndexFilePath(String specieIndexFilePath) {
		this.specieIndexFilePath = specieIndexFilePath;
	}

}
