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

import wetheinter.net.dev.template.CompilationFailed;

public class SourceBuilder <Payload> {

  private final PrintBuffer head;
	private PrintBuffer buffer;
	private ImportSection imports;
	private ClassBuffer classDef;
	private Payload payload;
	private String repackage;
  private int skip;

	public SourceBuilder() {
		head = buffer = new PrintBuffer();
	}
	public SourceBuilder(String classDef) {
	  this();
	  setClassDefinition(classDef, classDef.trim().endsWith("{"));
	}

	public PrintBuffer getBuffer() {
		return buffer;
	}

	public ClassBuffer getClassBuffer(){
		if (classDef == null)
			throw new CompilationFailed(
			  "setClassDefinition() has not been called yet.\n" +
			  "If you are running the template generator, your template " +
			  "does include a //@classDefinition()// declaration,\n" +
				"or your generator is attempting to access the class " +
				"definition before it is parsed.");
		return classDef;
	}

	public SourceBuilder<Payload> setClassDefinition(String definition, boolean wellFormatted){
		if (classDef == null){
		  // make sure import buffer comes before class def
		  getImports();
			classDef = new ClassBuffer(this);
			//create a new print buffer for content after class definition
			head.addToEnd(classDef);
			addBuffer(new PrintBuffer());
		}
		classDef.setDefinition(definition, wellFormatted);
		return this;
	}

	public ImportSection getImports(){
		if (imports == null){
		  imports = new ImportSection();
		}
		return imports;
	}

	public Payload getPayload() {
		return payload;
	}

	protected SourceBuilder<Payload> addBuffer(PrintBuffer newBuffer){
		if (newBuffer == buffer)
			return this;
		head.addToEnd(newBuffer);
		buffer = newBuffer;
		return this;
	}

	public void setPayload(Payload payload) {
		this.payload = payload;
	}

	public String getRepackage() {
		return repackage;
	}

	public SourceBuilder<Payload> setPackage(String repackage) {
		if (repackage.endsWith(";")){
			repackage = repackage.substring(0, repackage.length()-1);
		}
		if (repackage.startsWith("package "))
			repackage = repackage.substring(8);
		this.repackage = repackage;
		return this;
	}

  public SourceBuilder<Payload> setLinesToSkip(int i) {
    this.skip = i;
    return this;
  }

  public int getLinesToSkip() {
    try{
      return skip;
    }finally {
      skip = 0;
    }
  }

  @Override
  public String toString() {
    StringBuilder source = new StringBuilder();
    String body = head.toString();
    if (repackage != null) {
      if (body.trim().startsWith("package")) {
        int ind = body.indexOf(';', body.indexOf("package"));
        body = body.substring(ind+1);
      }
      source.append("package "+repackage+";\n\n");
    }
    if (imports != null)
      source.append(imports.toString());
    source.append(body);
    return source.toString();
  }

}
