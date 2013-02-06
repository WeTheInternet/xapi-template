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

package wetheinter.net.dev.template;

import com.google.gwt.dev.ArgProcessorBase;
import com.google.gwt.util.tools.ArgHandlerInt;

public class ProcessingInstructionOptions extends ArgProcessorBase {

	int wordsToSkip = 0;
	int linesToSkip = 0;
	String textToInject = "";


	public ProcessingInstructionOptions() {
		registerHandler(new SkipWordsArg(this));
		registerHandler(new SkipLinesArg(this));
	}

	public static class SkipWordsArg extends ArgHandlerInt{
		private final ProcessingInstructionOptions opts;

		public SkipWordsArg(ProcessingInstructionOptions opts) {
			this.opts = opts;
		}

		@Override
		public void setInt(int value) {
		  opts.wordsToSkip = value;
		}

		@Override
		public String getPurpose() {
			return "How many words (space delimited character blocks) to skip.";
		}

		@Override
		public String getTag() {
			return "-skipwords";
		}

		@Override
		public String[] getTagArgs() {
			return new String[]{"1"};
		}
	}

	public static class SkipLinesArg extends ArgHandlerInt{
	  private final ProcessingInstructionOptions opts;

	  public SkipLinesArg(ProcessingInstructionOptions opts) {
	    this.opts = opts;
	  }

	  @Override
	  public void setInt(int value) {
	    opts.linesToSkip = value;
	  }

	  @Override
	  public String getPurpose() {
	    return "How many lines (newline delimited character blocks) to skip.";
	  }

	  @Override
	  public String getTag() {
	    return "-skiplines";
	  }

	  @Override
	  public String[] getTagArgs() {
	    return new String[]{"1"};
	  }
	}

	@Override
	protected String getName() {
		return "ProcessingInstructions";
	}

}
