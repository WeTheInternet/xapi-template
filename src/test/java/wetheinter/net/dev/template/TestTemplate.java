package wetheinter.net.dev.template;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.junit.Assert;
import org.junit.Test;

import wetheinter.net.dev.io.SourceBuilder;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;

/**
 * A fairly simple test of our templating system;
 * this class uses itself as the generator for the template we are testing.
 *
 * The template we are using is:

//@repackaged(wetheinter.net.generated)//
package wetheinter.net.template;

//@imports()//
import java.util.Date;

//@classDefinition(public class Success)//
abstract class Success{

//@generateWith(wetheinter.net.dev.template.TestTemplate)//

  public static void main(String[] args){
    new Success().injected(args);
  }

//injected() //
abstract void injected(String ... args);

//@skipline(1)//
stuff to not compile! /
}

 *
 * This template, if successfully applied, will produce the source file:
 *
package wetheinter.net.generated;

import java.util.Date;
import org.junit.Assert;

public class Success {

  public static void main(String[] args){
    new Success().injected(args);
  }

  private void injected(String ... args){
    Assert.assertEquals(args[0], "success");
  }

}
 *
 *
 * @author "James X. Nelson (james@wetheinter.net)"
 *
 */
public class TestTemplate implements TemplateClassGenerator{

	private TreeLogger logger;

  @Override
	public void initialize(TreeLogger logger, TemplateGeneratorOptions options) {
		this.logger = logger;
	  logger.log(Type.INFO, "Initializing "+getClass().getName());
	}

	public void injected(TreeLogger logger, SourceBuilder<?> context, String payload){
		context
		  .setLinesToSkip(1)
		  .getImports().addImport(Assert.class.getName());
		context.getBuffer()
		  .indent()
		  .println("private void injected(String ... args){")
		  .indentln("Assert.assertEquals(args[0], \"success\");")
		  .println("}")
		  .outdent();
	}

	@Test
	public void testSimpleGeneration() throws Exception{
	  //apply template
	  TemplateToJava.main(new String[] {
	    "-template","wetheinter/net/template/Success.x",
	    "-output","/tmp/gen"
	  });

	  //compile the file
	  JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	  String junit = Assert.class.getProtectionDomain().getCodeSource()
	    .getLocation().toExternalForm().substring(5);//removes file: prefix
	  //select a classpath
	  String output = null;
	  //if we're running in maven, basedir will be set for us.
	  String basedir = System.getProperty("basedir");
	  if (basedir == null) {
	    //no maven? default to looking for the classes folder that loaded us
	    output = getClass().getProtectionDomain().getCodeSource().getLocation()
	      .toExternalForm().replace("file:", "");
	  }else {
	    File dir = new File(basedir, "target/test-classes");
	    if (!dir.isDirectory()) {
	      logger.log(Type.WARN, "Unable to run template generator test",
	        new FileNotFoundException(dir.getAbsolutePath()));
	      return;
	    }
	    output = dir.getAbsolutePath();
	  }
	  if (output == null)
	    throw new RuntimeException("Unable to find an output folder to compile into. " +
	    		"Ensure at least one writable folder is contained in the test classpath. " +
	    		"Preferably a folder which gets cleaned, like target/test-classes");
	  String[] args = new String[] {
	    "-cp", junit,
	    "-d", output
	    ,"/tmp/gen/wetheinter/net/generated/Success.java"
	  };
	  int result = compiler.run(System.in, System.out, System.err, args);
	  if (result != 0)
	    throw new RuntimeException("Java compile failed w/ status code "+result);

    //run it, reflectively
    Class<?> cls = compiler.getClass().getClassLoader()
      .loadClass("wetheinter.net.template.Success");
    Method method = cls.getMethod("main", String[].class);
    method.invoke(null, (Object)(new String[] {"success"}));
	}

}
