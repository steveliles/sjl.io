package com.sjl.io.process;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.sjl.io.streams.*;

/**
 * @author steve
 */
public abstract class ExecutionContextImpl implements ExecutionContext {

	private Executor executor;	
	
	public ExecutionContextImpl(Executor anExecutor) {
		executor = anExecutor;		
	}
	public ExecutionContextImpl() { 
		this(Executors.newFixedThreadPool(3));
	}

	@Override
	public void connectStreams(final InputStream anInputStream, final OutputStream anOutputStream) {
		executor.execute(new Runnable() {
			public void run() {
				try {
					Streams.copyAndClose(anInputStream, anOutputStream);					
				} catch (IOException anExc) {
					throw new RuntimeException(anExc);
				}
			}			
		});
	}		
}
