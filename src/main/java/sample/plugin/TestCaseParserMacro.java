package sample.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import org.apache.maven.doxia.macro.AbstractMacro;
import org.apache.maven.doxia.macro.MacroExecutionException;
import org.apache.maven.doxia.macro.MacroRequest;
import org.apache.maven.doxia.sink.Sink;

/**
 * @plexus.component role="org.apache.maven.doxia.macro.Macro" role-hint="testCaseParserMacro"
 */
public class TestCaseParserMacro extends AbstractMacro {
	
	/**
	 * Simple visitor implementation for visiting MethodDeclaration nodes. 
	 */
	private static class MethodVisitor extends VoidVisitorAdapter {

		private Sink sink;
		
		public MethodVisitor(Sink sink) {
			this.sink = sink;
		}
		
	    @Override
	    public void visit(MethodDeclaration n, Object arg) {	    	
	    	for (AnnotationExpr ae : n.getAnnotations()) 
	    		if (ae.toString().equals("@Test")) {	    			
	    			String id = getAnnotationValue(n, "Id");
	    			String station = getCategoryValue(n);
	    			String document = getAnnotationValue(n, "Document");
	    			String status = "New";
	    			
	    			sink.tableRow();
	    			sink.tableCell();sink.text(id);sink.tableCell_();
	    			sink.tableCell();sink.text(station);sink.tableCell_();
	    			sink.tableCell();sink.text(document);sink.tableCell_();
	    			sink.tableCell();sink.text(status);sink.tableCell_();
	    			sink.tableRow_();
	    			
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

	public void execute(Sink sink, MacroRequest req) throws MacroExecutionException {
		String sourceFolder = (String) req.getParameter("sourceFolder");
		getLog().info( "Source Folder: " + sourceFolder);
			
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
		
		sink.table();
		sink.tableRow();
		sink.tableHeaderCell();
		sink.text("ID");
		sink.tableHeaderCell_();
		sink.tableHeaderCell();
		sink.text("Station");
		sink.tableHeaderCell_();
		sink.tableHeaderCell();
		sink.text("Document");
		sink.tableHeaderCell_();
		sink.tableHeaderCell();
		sink.text("Status");
		sink.tableHeaderCell_();
		sink.tableRow_();
				
		// Parse files
		for (File f : allFiles) 
			try {
				FileInputStream fio = new FileInputStream(f);
				CompilationUnit cu;
				cu = JavaParser.parse(fio);
				new MethodVisitor(sink).visit(cu, null);
				fio.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		sink.table_();
		sink.flush();
	}
}


