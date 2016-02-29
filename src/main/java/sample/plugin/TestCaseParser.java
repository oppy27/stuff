package sample.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

@Mojo (name = "report")
public class TestCaseParser extends AbstractMojo {
	@Parameter(property="sourceFolder", defaultValue="${project.build.testSourceDirectory}")
	private String sourceFolder;
	
	@Parameter(property="outputFolder", defaultValue="${project.build.directory}")
	private String outputFolder;
	
	@Parameter(property="reportName", defaultValue="report.apt")
	private String reportName;
	
	private PrintWriter report;
	
	public void execute() throws MojoExecutionException {
		String outputFile = outputFolder + File.separator + reportName;
		getLog().info( "Source Folder: " + sourceFolder);
		getLog().info( "Output Report: " + outputFile);		
		
		// Build a list of all java files
		List<File> allFiles = new ArrayList<File>();
		Queue<File> dirs = new LinkedList<File>();
		dirs.add(new File(sourceFolder));
		while (!dirs.isEmpty()) {
		  for (File f : dirs.poll().listFiles()) {
		    if (f.isDirectory()) {
		      dirs.add(f);
		    } else if (f.isFile() && f.getName().endsWith(".java")) {
		      allFiles.add(f);
		    }
		  }
		}		
		getLog().info( "Files found: " + allFiles);
		
		try {
			report = new PrintWriter(new File(outputFolder + File.separator + reportName));
			report.println("*------+------+-----+------+");
			report.println("| ID | Station | Document | Status |");
			report.println("*------+------+-----+------+");
		} catch (Exception e) {
			getLog().error(e);
			return;
		}
		
		// Parse files
		for (File f : allFiles) 
			try {
				FileInputStream fio = new FileInputStream(f);
				CompilationUnit cu;
				cu = JavaParser.parse(fio);
				new MethodVisitor(report).visit(cu, null);
				fio.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		report.println("Index of test scripts");
		report.close();
	}
		
	/**
	 * Simple visitor implementation for visiting MethodDeclaration nodes. 
	 */
	private static class MethodVisitor extends VoidVisitorAdapter {

		private PrintWriter report;
		
		public MethodVisitor(PrintWriter report) {
			this.report = report;
		}
		
	    @Override
	    public void visit(MethodDeclaration n, Object arg) {	    	
	    	for (AnnotationExpr ae : n.getAnnotations()) 
	    		if (ae.toString().equals("@Test")) {	    			
	    			String id = getAnnotationValue(n, "Id");
	    			String station = getCategoryValue(n);
	    			String document = getAnnotationValue(n, "Document");
	    			String status = "New";
	    			report.println("| " + id + " | " + station + " | " + document + " | " + status + " |");
	    			report.println("*------+------+-----+------+");
	    			report.flush();
	    			System.out.println(n.getAnnotations());
	    			System.out.println(n.getDeclarationAsString());
	    			break;
	    		}
	      
	        super.visit(n, arg);
	    }
	    
	    /**
	     * Extracts the annotation value.
	     * 
	     * @param n
	     * @param a
	     * @return
	     */
	    private String getAnnotationValue(MethodDeclaration n, String a) {
	    	for (AnnotationExpr ae : n.getAnnotations()) 
	    		if (ae.toString().contains("@" + a)) {
	    			return ae.toString().substring(ae.toString().indexOf("\"") + 1, ae.toString().lastIndexOf("\""));
	    		}
	    	return null;
	    }
	    
	    /**
	     * Extracts the category
	     * 
	     * @param n
	     * @return
	     */
	    private String getCategoryValue(MethodDeclaration n) {
	    	for (AnnotationExpr ae : n.getAnnotations()) 
	    		if (ae.toString().contains("@Category")) {
	    			return ae.toString().substring(ae.toString().indexOf("(") + 1, ae.toString().lastIndexOf("."));
	    		}
	    	return null;
	    }
	}
}


