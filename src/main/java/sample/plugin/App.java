package sample.plugin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.stringtemplate.v4.ST;

/**
 * Hello world!
 *
 */
@Mojo (name = "SayHi")
public class App extends AbstractMojo {
	
	@Parameter(property="sourceFolder", defaultValue="${project.build.testSourceDirectory}")
	private String sourceFolder;
	
	@Parameter(property="chapter")
	private String chapterName;
	
	@Parameter(property="count")
	private int testCount;
	
	@Parameter(property="basePackage", defaultValue="org.thales.tato")
	private String basePackage;
	
	@Parameter(property="testGroup", defaultValue="weiche")
	private String testGroup;
	
	private final static String TEST_TEMPLATE = "TestTemplate.java";
		
	public void execute() throws MojoExecutionException {
        getLog().info( "SourceFolder: " + sourceFolder);
        getLog().info( "ChapterName: " + chapterName);
        getLog().info( "TestCount: " + testCount);
        
        for (int i=1; i<=testCount; i++) {
			try {
				generateTestCaseTemplate(i);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
	
	private void generateTestCaseTemplate(int id) throws IOException {
		// Read class template
		String line;
		StringBuffer template = new StringBuffer();
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(TEST_TEMPLATE);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		while ((line = br.readLine()) != null) {
			template.append(line + "\n");
		}	
		
		ST st = new ST(template.toString());
		
		String className = "Test" + chapterName.replace(".", "_") + "_" + id;
		st.add("className", className);
		
		String packageName = basePackage + "." + testGroup + ".c" + chapterName.replace(".", "_");
		st.add("packageName", packageName);
		
		String testId = chapterName + "." + id;
		st.add("testId", testId);
		
		System.out.println(st.render());
		
		// Determine file path
		String fileName = sourceFolder + File.separator + basePackage.replace(".", File.separator) + File.separator + 
				testGroup + File.separator + "c" + chapterName.replace(".", "_") + File.separator + className + ".java";
		// Create file path
		File f = new File(fileName).getParentFile();
		File parent = f.getParentFile();
		parent.mkdirs();
		// Write class file
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
		bw.write(st.render());
		bw.close();
	}
}
