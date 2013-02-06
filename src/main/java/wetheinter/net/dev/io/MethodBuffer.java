/*
 * Copyright 2013, We The Internet Ltd.
 *
 * All rights reserved.
 *
 * Distributed under a modified BSD License as follow:
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistribution in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution, unless otherwise
 * agreed to in a written document signed by a director of We The Internet Ltd.
 *
 * Neither the name of We The Internet nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package wetheinter.net.dev.io;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import wetheinter.net.dev.template.CompilationFailed;


public class MethodBuffer extends PrintBuffer{

	private int modifier;
	protected SourceBuilder<?> context;
	private boolean once;
	private boolean useJsni = true;
	private String methodName;
	private final Set<String> generics;
	private final Set<String> parameters;
  private String returnType;

	public MethodBuffer(SourceBuilder<?> context) {
		super();
		this.context = context;
		generics = new HashSet<String>();
		parameters = new HashSet<String>();
	}
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(NEW_LINE+indent);
		b.append(Modifier.toString(modifier));
		b.append(" ");
		//generics
		if (generics.size() > 0) {
		  b.append("<");
		  String prefix = "";
		  for (String generic : generics) {
		    b.append(prefix);
		    b.append(generic);
		    prefix = ", ";
		  }
		  b.append("> ");
		}
		//return type
		b.append(returnType);

		b.append(" ");
		//method name
		b.append(methodName);
		//parameters
		b.append(" (");
	  String prefix = "";
	  for (String parameter : parameters) {
	    b.append(prefix);
	    b.append(parameter);
	    prefix = ", ";
	  }
		b.append(") ");
		final String suffix;
		if (Modifier.isAbstract(modifier)) {
		  prefix = ";\n";
		  suffix = "";
		} else if (Modifier.isNative(modifier)) {
		  if (useJsni) {
		    prefix = "/*-{\n";
		    suffix = NEW_LINE+indent+ "}-*/;";
		  } else {
		    prefix = ";\n";
		    suffix = "";
		  }
		} else {
		  prefix = "{\n";
		  suffix = NEW_LINE + indent + "}\n";
		}
		return b.toString()+prefix+super.toString()+suffix;
	}

	public void setDefinition(String definition) {
	  // JavaMetadata will extract all modifiers for us
		JavaMetadata metadata = new JavaMetadata(definition);
		modifier = metadata.getModifier();
		definition = metadata.getClassName();

		context.getImports().addImports(metadata.getImports());

		// Add any generics extracted
		if (metadata.hasGenerics()){
			generics.clear();
			String[] generic = metadata.getGenerics();
			for (String s : generic)
				generics.add(s);
		}

		// Add the return type
		int ind = definition.indexOf(' ');
		returnType = definition.substring(0, ind);
		definition = definition.substring(ind+1);
		ind = returnType.lastIndexOf('.');
		if (ind > 0) {
		  context.getImports().addImport(returnType);
		  returnType = returnType.substring(ind+1);
		}

		// Resolve parameters; reducing fqcn to import statements
		ind = definition.indexOf('(');
		assert ind > 0;
		methodName = definition.substring(0, ind);
		definition = definition.substring(ind+1, definition.lastIndexOf(')'));
		for (String param : definition.split(",")) {
		  param = param.trim();
		  ind = param.lastIndexOf('.');
		  if (ind > 0) {
		    context.getImports().addImport(param.substring(0, param.indexOf(' ')));
		    param = param.substring(ind+1);
		  }
		  parameters.add(param);
		}

		if (methodName.contains(" "))
		  throw new CompilationFailed("Found ambiguous method definition in "+definition);
		if (methodName.length() == 0)
			throw new CompilationFailed("Did not have a class name in class definition "+definition);
	}

	public void addParameters(String ... paramaters) {
		for (String parameter0 : paramaters){
		  for (String parameter : parameter0.split(",")){
  			parameter = parameter.trim();
  			int index = parameter.lastIndexOf('.');
  			if (index > 0){
  			  // trim the fqcn and import it
  			  int end = parameter.lastIndexOf(' ');
  			  assert end > 0;
  			  String fqcn = parameter.substring(0,end);
  				context.getImports().addImport(fqcn);
  				//keep the simple name
  				this.parameters.add(parameter.substring(index+1));
  			}else{
  			  //assume imports are handled externally
  				this.parameters.add(parameter);
  			}
  		}
		}
	}
	public void addGenerics(String ... generics) {
		for (String generic : generics){
			generic = generic.trim();
			boolean noImport = generic.contains("!");
			if (noImport){
				this.generics.add(generic.replace("!", ""));
			}else{
				//pull fqcn into import statements and shorten them
				for (String part : generic.split(" ")){
					int index = generic.lastIndexOf(".");
					if (index > 0){
						context.getImports().addImport(part);
						generic = generic.replace(part.substring(0, index+1), "");
					}
				}
				this.generics.add(generic);
			}
		}
	}

	public ClassBuffer createInnerClass(String classDef) {
	  ClassBuffer cls = new ClassBuffer(context);
	  cls.setDefinition(classDef, classDef.trim().endsWith("{"));
	  cls.indent = indent + INDENT;
	  assert cls.privacy == 0 : "A local class cannot be "+Modifier.toString(cls.privacy);
	  addToEnd(cls);
	  return cls;
	}

	@Override
	protected void onAppend() {
	  if (once) {
	    once = false;
	    onFirstAppend();
	  }
	  super.onAppend();
	}
	protected void onFirstAppend() {

	}
  /**
   * @param useJsni - Whether to encapsulate native methods with /*-{ }-* /
   */
  public void setUseJsni(boolean useJsni) {
    this.useJsni = useJsni;
  }


  // Sigh...  to create a fluent api w/out generics,
  // we have to override a bunch of methods...

  @Override
  public MethodBuffer append(boolean b) {
    super.append(b);
    return this;
  }

  @Override
  public MethodBuffer append(char c) {
    super.append(c);
    return this;
  }

  @Override
  public MethodBuffer append(char[] str) {
    super.append(str);
    return this;
  }

  @Override
  public MethodBuffer append(char[] str, int offset, int len) {
    super.append(str, offset, len);
    return this;
  }

  @Override
  public MethodBuffer append(CharSequence s) {
    super.append(s);
    return this;
  }

  @Override
  public MethodBuffer append(CharSequence s, int start, int end) {
    super.append(s, start, end);
    return this;
  }

  @Override
  public MethodBuffer append(double d) {
    super.append(d);
    return this;
  }

  @Override
  public MethodBuffer append(float f) {
    super.append(f);
    return this;
  }

  @Override
  public MethodBuffer append(int i) {
    super.append(i);
    return this;
  }

  @Override
  public MethodBuffer append(long lng) {
    super.append(lng);
    return this;
  }

  @Override
  public MethodBuffer append(Object obj) {
    super.append(obj);
    return this;
  }

  @Override
  public MethodBuffer append(String str) {
    super.append(str);
    return this;
  }

  @Override
  public MethodBuffer indent() {
    super.indent();
    return this;
  }

  @Override
  public MethodBuffer indentln(char[] str) {
    super.indentln(str);
    return this;
  }

  @Override
  public MethodBuffer indentln(CharSequence s) {
    super.indentln(s);
    return this;
  }

  @Override
  public MethodBuffer indentln(Object obj) {
    super.indentln(obj);
    return this;
  }

  @Override
  public MethodBuffer indentln(String str) {
    super.indentln(str);
    return this;
  }

  @Override
  public MethodBuffer outdent() {
    super.outdent();
    return this;
  }

  @Override
  public MethodBuffer println() {
    super.println();
    return this;
  }

  @Override
  public MethodBuffer println(char[] str) {
    super.println(str);
    return this;
  }

  @Override
  public MethodBuffer println(CharSequence s) {
    super.println(s);
    return this;
  }

  @Override
  public MethodBuffer println(Object obj) {
    super.println(obj);
    return this;
  }

  @Override
  public MethodBuffer println(String str) {
    super.println(str);
    return this;
  }

}
