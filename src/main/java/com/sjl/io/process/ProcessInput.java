package com.sjl.io.process;

import java.io.*;
import java.nio.charset.*;

public interface ProcessInput {		
	
	public boolean isAvailable();
	
	public InputStream getInputStream() 
	throws IOException;
	
	
	public static ProcessInput NULL_OBJECT = new ProcessInput() {
		public boolean isAvailable() {
			return false;
		}
		
		public InputStream getInputStream() {
			throw new UnsupportedOperationException();
		}		
	};
	
	public abstract class Impl implements ProcessInput {
		public boolean isAvailable() {
			return true;
		}
	}
	
	public class Prepared extends Impl {
		protected InputStream in;
		
		public Prepared(InputStream anInputStream) {
			in = anInputStream;
		}
		
		public InputStream getInputStream() throws IOException {
			return in;
		}		
	}
	
	public class FromStringImpl extends Prepared {
		public FromStringImpl(String anInput, Charset aCharset) {
			super(new ByteArrayInputStream(anInput.getBytes(aCharset)));			
		}
		
		public FromStringImpl(String anInput) {
			this(anInput, Charset.forName(System.getProperty("file.encoding")));
		}
	};	
}