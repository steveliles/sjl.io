package com.sjl.io.streams;

import java.io.*;
import java.util.logging.*;

import com.sjl.util.*;

public class Streams {

	private static final Logger logger = Logger.getLogger(Streams.class.getName());
	private static final int BUFFER_SIZE = 512;

	private static final ExceptionHandler<IOException> CLOSE_EXCEPTION_HANDLER = new ExceptionHandler<IOException>() {
		public void onException(IOException anExc) {
			logger.log(Level.WARNING, "error closing stream", anExc);
		}
	};

	public static String toString(InputStream anInputStream) 
	throws IOException {
		return toString(anInputStream, BUFFER_SIZE);
	}

	public static String toString(InputStream anInputStream, String anEncoding) 
	throws IOException {
		return toString(anInputStream, BUFFER_SIZE, anEncoding);
	}
	
	public static String toString(InputStream anInputStream, int aBufferSize) 
	throws IOException {
		return toString(anInputStream, aBufferSize, "UTF-8");
	}
	
	public static String toString(InputStream anInputStream, int aBufferSize, String anEncoding) 
	throws IOException {		
		return new String(toByteArray(anInputStream, aBufferSize), anEncoding);
	}

	public static byte[] toByteArray(InputStream anInputStream) 
	throws IOException {
		return toByteArray(anInputStream, BUFFER_SIZE);
	}

	public static byte[] toByteArray(InputStream anInputStream, int aBufferSize) 
	throws IOException {
		ByteArrayOutputStream _out = new ByteArrayOutputStream(aBufferSize);
		copyAndClose(anInputStream, _out);
		return _out.toByteArray();
	}

	public static void copyAndClose(InputStream anInput, OutputStream anOutput) 
	throws IOException {
		copyAndClose(anInput, anOutput, BUFFER_SIZE);
	}

	public static void copyAndClose(InputStream anInput, OutputStream anOutput, int aBufferSize) 
	throws IOException {
		try {
			copy(anInput, anOutput, aBufferSize);
		} finally {
			close(anInput);
			close(anOutput);
		}
	}

	public static void copyAndClose(InputStream anInput, OutputStream anOutput, int aStartOffset, int anEndOffset) 
	throws IOException {
		try {
			copy(anInput, anOutput, aStartOffset, anEndOffset);
		} finally {
			close(anInput);
			close(anOutput);
		}
	}

	public static void copy(InputStream anInput, OutputStream anOutput) 
	throws IOException {
		copy(anInput, anOutput, BUFFER_SIZE);
	}

	public static void copy(InputStream anInput, OutputStream anOutput, int aBufferSize) 
	throws IOException {
		byte[] _buffer = new byte[aBufferSize];
		int _length;

		while ((_length = anInput.read(_buffer)) > -1) {
			anOutput.write(_buffer, 0, _length);
		}
		anOutput.flush();
	}

	public static void copy(InputStream anInput, OutputStream anOutput, int aStartOffset, int anEndOffset) 
	throws IOException {
		byte[] _buffer = new byte[BUFFER_SIZE];
		int _length;

		InputStream _input = new PartialRangeInputStream(anInput, aStartOffset, anEndOffset);

		while ((_length = _input.read(_buffer)) > -1) {
			anOutput.write(_buffer, 0, _length);
		}
		anOutput.flush();
	}

	public static void close(InputStream anInput) {
		close(anInput, CLOSE_EXCEPTION_HANDLER);
	}

	public static void close(InputStream anInput, ExceptionHandler<IOException> anExcHandler) {
		if (anInput != null) {
			try {
				anInput.close();
			} catch (IOException anExc) {
				anExcHandler.onException(anExc);
			}
		}
	}

	public static void close(OutputStream anOutput) {
		close(anOutput, CLOSE_EXCEPTION_HANDLER);
	}

	public static void close(OutputStream anOutput, ExceptionHandler<IOException> anExcHandler) {
		if (anOutput != null) {
			try {
				anOutput.close();
			} catch (IOException anExc) {
				anExcHandler.onException(anExc);
			}
		}
	}

}
