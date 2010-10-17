package com.sjl.io.process;

import java.io.*;
import java.nio.charset.*;

public interface ProcessOutput {
	
	public boolean isAvailable();
	
	public OutputStream getOutputStream()
	throws IOException;
		
	
	public static ProcessOutput NULL_OBJECT = new ProcessOutput() {
		public boolean isAvailable() {
			return false;
		}
		
		public OutputStream getOutputStream() {
			throw new UnsupportedOperationException();
		}		
	};
	
	public abstract class Impl implements ProcessOutput {
		public boolean isAvailable() {
			return true;
		}
	}
	
	public class Prepared extends Impl {
		protected OutputStream out;
		
		public Prepared(OutputStream anOut) {
			out = anOut;
		}
		
		@Override
		public OutputStream getOutputStream() 
		throws IOException {
			return out;
		}		
	}
	
	public class AsStringImpl extends Prepared {
		public AsStringImpl() {
			super(new ByteArrayOutputStream());
		}
		
		public String get(Charset aCharset) {
			try {
				return ((ByteArrayOutputStream)out).toString(aCharset.name());
			} catch (UnsupportedEncodingException anExc) {
				throw new RuntimeException(anExc);
			}
		}
		
		public String get() {
			return get(Charset.forName(System.getProperty("file.encoding")));
		}
	}		
}