package com.sjl.io.process;

import java.io.*;

import org.junit.*;

public class ExternalProcessTest  {

	@Before
	public void setUp() throws Exception {
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			public void uncaughtException(Thread t, Throwable e) {
				e.printStackTrace();
			}
		});
	}

	@Test
	public void testInvokeWithNoStreamInput() 
	throws Exception {
		ExternalProcess.execute("", "convert", new String[]{ "-version" }, new ExecutionContextImpl() {
			
			ProcessOutput.AsStringImpl error = new ProcessOutput.AsStringImpl();
			ProcessOutput.AsStringImpl output = new ProcessOutput.AsStringImpl();
			
			@Override
			public ProcessInput getInput() {
				return ProcessInput.NULL_OBJECT;
			}

			@Override
			public ProcessOutput getOutput() throws IOException {
				return output;					
			}
			
			@Override
			public ProcessOutput getErrorOutput() {
				return error;
			}
			
			public void onAbnormalTermination(int anExitCode) throws Exception {
				System.out.println("abnormal termination: " + anExitCode + ", errors: " + error.get());				
			}

			@Override
			public void onNormalTermination() throws Exception {
				System.out.println("success - " + output.get() + " ... " + error.get());
			}
		});
	}
	
	@Test
	public void testCanInvokeMoreComplexProcess() 
	throws Exception {
		ExternalProcess.execute("", "convert", new String[]{ "png:fd:0", "gif:fd:1" }, new ExecutionContextImpl() {
			
			ProcessOutput.AsStringImpl error = new ProcessOutput.AsStringImpl();
			
			@Override
			public ProcessInput getInput() {
				return new ProcessInput.Prepared(ExternalProcessTest.class.getResourceAsStream("logo.png"));
			}

			@Override
			public ProcessOutput getOutput() throws IOException {
				return new ProcessOutput.Prepared(new FileOutputStream(new File("test.gif")));					
			}
			
			@Override
			public ProcessOutput getErrorOutput() {
				return error;
			}
			
			public void onAbnormalTermination(int anExitCode) throws Exception {
				System.out.println("abnormal termination: " + anExitCode + ", errors: " + error.get());				
			}

			@Override
			public void onNormalTermination() throws Exception {
				System.out.println("success");
			}
		});
	}
	
	@Test
	public void testManyIterations()
	throws Exception {
		testCanInvokeMoreComplexProcess();
		testCanInvokeMoreComplexProcess();
		testCanInvokeMoreComplexProcess();
	}
}