package wetheinter.net.dev.template;

import org.junit.Assert;
import org.junit.Test;

import wetheinter.net.dev.io.SourceBuilder;

public class TestCodegen {

  @Test
  public void testGenericImports() {
    SourceBuilder<Object> b = new SourceBuilder<Object>(
      "public static abstract class wetheinter.net.Test");
    b.setPackage("wetheinter.net.test");
    b.getClassBuffer()
      .addGenerics("K","V extends java.util.Date")
      .addInterfaces("java.util.Iterator");
    Assert.assertTrue(b.toString().contains("import java.util.Date;"));
    Assert.assertTrue(b.toString().contains("import java.util.Iterator;"));
  }

  @Test
  public void testMethodWriter() {
    SourceBuilder<Object> b = new SourceBuilder<Object>(
      "public static abstract class Test");
    b.setPackage("wetheinter.net.test");
    b.getClassBuffer()
      .createMethod("public <T extends java.util.Date> void Test(java.lang.String t) {")
      .indentln("System.out.println(\"Hellow World\");")
      .createInnerClass("class InnerClass")
      .createMethod("void innerMethod()")
      ;
    Assert.assertTrue(b.toString().contains("import java.lang.String;"));
    Assert.assertTrue(b.toString().contains("import java.util.Date;"));
    Assert.assertTrue(b.toString().contains("<T extends Date>"));
  }

}
