package njuzh.jdt;

/**
 * Hello world!
 *
 */
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.jdt.core.JavaCore;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import com.csvreader.CsvWriter;
public class JavaDocExtractor 
{
	private String projectName;
	private List<File> files;
	private int allMethodCount;
	private int methodDocCount;
	private int allFilesCount;
	//private int analyzedFilesCount;
	//private ProgressBar progressBar;
	
	private List<String> writeBufferList;
	
	private String preAST=new String();

    public JavaDocExtractor(String projectName, List<File> files) {
		this.projectName = projectName;
		this.files = files;
		this.methodDocCount = 0;
		this.allMethodCount = 0;
		//this.analyzedFilesCount = 0;
		this.writeBufferList = new ArrayList<String>();
		this.allFilesCount = files.size();
		//this.progressBar = new ProgressBar(allFilesCount,20);
	}
    public String getMethodString(MethodDeclaration method, String src) {
    	Javadoc javadoc = method.getJavadoc();
    	int docStart = javadoc.getStartPosition();
    	int codeStart = docStart + javadoc.getLength() + 1;
    	int codeEnd= codeStart + method.getLength() - (codeStart - docStart);
    	String methodBody = new String();
    	try {
    		methodBody = src.substring(codeStart, codeEnd);
    	}catch (Exception e){
    		methodBody = method.toString();
    	}
    	return methodBody;
    }
    public void ExtractJavaDoc() {
    	System.out.println(">>>>extracting method info of " + projectName + "..." );
    	for (File f: files) {
    		GetJavaDoc(f);
    		//this.analyzedFilesCount +=1;
    		//this.progressBar.showBarByPoint(analyzedFilesCount);
    		try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    		
    	}
    	System.out.println(">>>>writing to csv files...");
    	String csvName = new String();
    	csvName = projectName + "#" + allFilesCount + "#" + allMethodCount + "#" + methodDocCount + ".csv";
    	writeCSV(csvName);
    	System.out.println(">>>>done.");
    }
    public void writeCSV(String path) {
        String csvFilePath = path;
        
        try {
            
            // 创建CSV写对象 例如:CsvWriter(文件路径，分隔符，编码格式);
            CsvWriter csvWriter = new CsvWriter(csvFilePath, ',', Charset.forName("UTF-8"));
            // 写内容
            String[] headers = {"index","file","methodName","methodParameterType","methodParameterName","methodBody","AST","SBT","methodDoc"};
            csvWriter.writeRecord(headers);
            for(int i=0;i<writeBufferList.size();i++){
                String[] writeLine=writeBufferList.get(i).split("#@");
                //System.out.println(writeLine);
                csvWriter.writeRecord(writeLine);
            }
                       
            csvWriter.close();
//            System.out.println("analyzed project name:"+ projectName);
//            System.out.println("analyzed java files:"+ allFilesCount);
//            System.out.println("all method count:"+ allMethodCount);
//            System.out.println("methodWithDoc count:"+ methodDocCount);
//            System.out.println("--------CSV file has been generated--------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void GetJavaDoc(File file) {
    	//System.out.println("SCANNING______" +file );
        ASTParser parser = ASTParser.newParser(AST.JLS8); //设置Java语言规范版本
        parser.setKind(ASTParser.K_COMPILATION_UNIT);

        parser.setCompilerOptions(null);
        parser.setResolveBindings(true);

        Map<String, String> compilerOptions = JavaCore.getOptions();
        compilerOptions.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8); //设置Java语言版本
        compilerOptions.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
        compilerOptions.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
        parser.setCompilerOptions(compilerOptions); //设置编译选项

        String src = null;
        try {
            src = FileUtils.readFileToString(file,"UTF-8");  //要解析的文件
        } catch (Exception e) {
            e.printStackTrace();
        }
        parser.setSource(src.toCharArray());
      
        CompilationUnit unit = (CompilationUnit) parser.createAST(null);
        
        PackageDeclaration packageDeclaration = unit.getPackage();
        String packageName = new String();
        if(packageDeclaration!=null) {
            packageName = packageDeclaration.getName().getFullyQualifiedName();
        }
        
        
        if(unit.types().size()>0&&unit.types().get(0)instanceof TypeDeclaration) {
        	TypeDeclaration type = (TypeDeclaration) unit.types().get(0);
        	MethodDeclaration[] methods = type.getMethods();
            allMethodCount += methods.length;
        	for(int i=0; i<methods.length;i++){
            	MethodDeclaration method = methods[i];
            	Javadoc doc = method.getJavadoc();
            	
            	if(doc != null) {
            		
            		preAST = new String();
            		String methodNameString = packageName+"."+type.getName()+"."+method.getName().getFullyQualifiedName();
            		String methodBodyString = getMethodString(method, src);
            		String methodParameterNameString  = new String();
            		String methodParameterTypeString = new String();
            		List<SingleVariableDeclaration> parameterList  = method.parameters();
            		for(SingleVariableDeclaration a:parameterList) {
            			methodParameterNameString += a.getName() + "#";
            			methodParameterTypeString += a.getType() + "#";
            		}
            		if(methodParameterTypeString.isEmpty()) {
            			methodParameterNameString = "/";
            			methodParameterTypeString = "/";
            		}
            		
            		method.accept(new ASTVisitor() {
            	    	   @Override
            	    	public void postVisit(ASTNode node) {
            	    		// TODO Auto-generated method stub
            	    		String typeString = node.getClass().toString().split("\\.")[node.getClass().toString().split("\\.").length-1];
            	    		preAST += typeString+"#";
            	    		super.postVisit(node);
            	    	}
//            	    	   @Override
//            	    	
            	       });
            		ASTExtractor astExtractor = new ASTExtractor(method);
            		String sbtString = astExtractor.sbt;
        
            		String line = new String();
            		line = methodDocCount+"#@"+file+"#@"+methodNameString+"#@"+methodParameterTypeString+"#@"+methodParameterNameString+"#@"+methodBodyString+"#@"+preAST+"#@"+sbtString+"#@"+doc;
            		//System.out.println("EXTRCTING_FORM____:"+file+"  funcname:"+type.getName()+'.'+method.getName().getFullyQualifiedName());
            		//System.out.println(doc);
            		this.writeBufferList.add(line);
            		methodDocCount++;
            		
            		
            	}
            }
        }
        
        
    }

}
