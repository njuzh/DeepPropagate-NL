package njuzh.jdt;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.Before;
import org.junit.Test;


import junit.framework.TestCase;

public class JavaDocExtractorTest extends TestCase {
//	@Test
//	public void testProgressBar() {
//		ProgressBar progressBar = new ProgressBar();
//		for(int i=0;i<100;i++) {
//			progressBar.showBarByPoint(i);
//		}
//	}
	@Test
    public void testMethod1() {
		String path = "/Users/zhouhang/JavaDoc/javaProjects/dom4j";
		String projectName ="dom4j";
		FileSystemWalker fileSystemWalker = new FileSystemWalker(path);
		List<File> fileList = fileSystemWalker.getJavafiles();
		List<File> files = new ArrayList<File>();
		JavaDocExtractor javaDocExtractor = new JavaDocExtractor("test", fileList);
		javaDocExtractor.ExtractJavaDoc();
    }

}
