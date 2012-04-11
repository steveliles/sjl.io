package com.sjl.io.process;

import java.io.*;

public interface ExecutionContext {

	/**
	 * @return an object that provides input to the process's standard input stream
	 */
	ProcessInput getInput()
	throws IOException;
	
	/**
	 * @return an object that consumes output from the process's standard output stream
	 */
	ProcessOutput getOutput()
	throws IOException;
	
	/**
	 * @return an object that consumes output from the process's standard error stream
	 */
	ProcessOutput getErrorOutput()
	throws IOException;
	
	/**
	 * set up a thread to copy the input stream to the outputstream as and when data is available.
	 * The Streams must be closed when the copy is completed.
	 */
	void connectStreams(InputStream anInputStream, OutputStream anOutputStream);
	
	/**
	 * invoked if the process completes normally (exit code 0)
	 */
	void onNormalTermination()
	throws Exception;
	
	/**
	 * invoked if the process does not complete normally (exit code != 0)	
	 */
	void onAbnormalTermination(int anExitCode)
	throws Exception;
	
	/**
     * @return the working directory for the process (can use null for current directory)
     */
    File getWorkingDirectory();
    
    /**
     * Invoke in a finally block after completion!
     */
    public void dispose();
}
