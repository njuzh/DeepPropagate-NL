package njuzh.jdt;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class FileSystemWalker {
	public List<File> javafiles;
	
	public FileSystemWalker(String path) {
		File file = new File(path);
		this.javafiles = new ArrayList<File>();
		dfs(file);
	}
	public List<File> getJavafiles() {
		
		return javafiles;
	}
	public void setJavafiles(List<File> javafiles) {
		
		this.javafiles = javafiles;
	}

	public void dfs(File file) {
		//List<File> filelist = new ArrayList<File>();
		File[] files = file.listFiles();
		for (File f:files) {
			if(f.isDirectory()) {
				dfs(f);
			}
			if(f.isFile()&&f.getName().endsWith("java")&&!f.toString().contains("/test")) {
				this.javafiles.add(f);
			}
		}
	}
}
