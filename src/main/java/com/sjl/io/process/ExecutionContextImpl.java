package com.sjl.io.process;

import java.io.*;
import java.util.concurrent.*;

import com.sjl.io.streams.*;

/**
 * @author steve
 */
public abstract class ExecutionContextImpl implements ExecutionContext {

	private ExecutorService executor;	
	
	public ExecutionContextImpl(ExecutorService anExecutor) {
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
	
	public void dispose() {
        executor.shutdown();
    }
	
	@Override
	public File getWorkingDirectory() {
		return null; // means "here" - where the java process is invoked from
	}
}
