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

import java.util.TreeSet;
import static wetheinter.net.dev.io.PrintBuffer.NEW_LINE;

public class ImportSection {

	private TreeSet<String> imports = new TreeSet<String>();
	private TreeSet<String> importStatic = new TreeSet<String>();

	public ImportSection() {
	}

	public void addImports(String ... imports){
		for (String iport :  imports)
			addImport(iport);
	}
	public void addImport(String importName){
		if (importName.startsWith("import "))
			imports.add(importName.trim());
		else
			imports.add("import "+importName+";");
	}

	public void addStatics(String ... imports){
	  for (String iport :  imports)
	    addStatic(iport);
	}
	public void addStatic(String importName){
	  if (importName.startsWith("import static "))
	    importStatic.add(importName.trim());
	  else
	    importStatic.add("import static "+importName+";");
	}

	@Override
	public String toString() {
	  StringBuilder b = new StringBuilder(NEW_LINE);
		for (String importName : imports){
			b.append(importName);
			b.append(NEW_LINE);
		}
		for (String importName : importStatic){
		  b.append(importName);
		  b.append(NEW_LINE);
		}
		return b.toString()+NEW_LINE;
	}
}
