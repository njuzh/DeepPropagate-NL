package njuzh.jdt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class App {
	public static void main(String[] args) throws IOException {
		long startTime=System.currentTimeMillis();  
		if (args.length != 1)
			throw new IllegalArgumentException("you should put only a file path");
		String path = new String();
		String projectName = new String();
		path = args[0];
		projectName = args[0].split("/")[args[0].split("/").length - 1 ];
		
		//String path = "/Users/zhouhang/JavaDoc/javaProjects/dom4j";
		//String projectName ="dom4j";
		FileSystemWalker fileSystemWalker = new FileSystemWalker(path);
		List<File> fileList = fileSystemWalker.getJavafiles();
//		for(File f:fileList) {
//			System.out.println(f);
//		}
//		System.out.print(fileList.size());
		
		JavaDocExtractor jDocExtractor = new JavaDocExtractor(projectName, fileList);
		jDocExtractor.ExtractJavaDoc();
		long endTime=System.currentTimeMillis();
		System.out.println("excute time:"+(endTime-startTime)+"ms");
	}
}
