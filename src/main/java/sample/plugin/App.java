package sample.plugin;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

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
	
	public void execute() throws MojoExecutionException {
        getLog().info( "SourceFolder: " + sourceFolder);
        getLog().info( "ChapterName: " + chapterName);
        getLog().info( "TestCount: " + testCount);
        
        for (int i=1; i<=testCount; i++) 
        	generateTestCaseTemplate(chapterName, i);
    }
	
	private void generateTestCaseTemplate(String chapterName, int id) {
		// read file name
		StringTemplate template = new StringTemplate();
		
	}
}
