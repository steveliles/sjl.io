package com.sjl.io.process;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicReference;

import com.sjl.io.streams.*;
import com.sjl.util.strings.*;


public class ExternalProcess {

	private String path;
	private String cmd;
	
	public ExternalProcess(String aPath, String aCmd) {
		path = aPath;
		cmd = aCmd;		
	}
	
	public void execute(String[] anArgs, ExecutionContext aCallback)
	throws Exception {
		ExternalProcess.execute(path, cmd, anArgs, aCallback);
	}
	
    public static void execute(String aPath, String aCmd, String[] anArgs, ExecutionContext aCallback) 
    throws Exception {
        Process _process = null;
        InputStream _in = null;
        OutputStream _out = null;
        OutputStream _err = null;
        try {
            _process = Runtime.getRuntime().exec(aPath + aCmd + " " + Strings.joinWithDelimiter(" ", anArgs));
            
            CyclicBarrier _barrier = newCyclicBarrier(
            	aCallback.getInput().isAvailable(),
            	aCallback.getOutput().isAvailable(),
            	aCallback.getErrorOutput().isAvailable()
            );
            
            _in = attachInput(_process, aCallback, _barrier);
            _out = attachOutput(_process, aCallback, _barrier);
            _err = attachError(_process, aCallback, _barrier);            
                             
            _barrier.await();
            
            int _result = _process.waitFor();                        
            
            if(_result == 0) {
                aCallback.onNormalTermination();            	
            } else {
            	aCallback.onAbnormalTermination(_result);
            }
        } finally {
            if( _process != null ) {
                Streams.close(_process.getErrorStream());
                Streams.close(_process.getOutputStream());
                Streams.close(_process.getInputStream());
                _process.destroy();
            }
            Streams.close(_in);
            Streams.close(_out);
            Streams.close(_err);
        }        
    }

    private static CyclicBarrier newCyclicBarrier(boolean... aCountTrues) {
    	int _count = 1; // 1 for the main thread
    	for (boolean _b : aCountTrues)
    		if (_b) _count++; // add 1 for each stream we'll be processing
    	return new CyclicBarrier(_count);
    }
    
    private static InputStream attachInput(Process aProcess, ExecutionContext aCtx, CyclicBarrier aBarrier) 
    throws IOException {
    	ProcessInput _pi = aCtx.getInput();
    	if ((_pi != null) && (_pi.isAvailable())) {
    		InputStream _in = _pi.getInputStream();
    		aCtx.connectStreams(_in, new BarrierOutputStream(aProcess.getOutputStream(), aBarrier));
    		return _in;
    	}
    	return null;
    }
    
    private static OutputStream attachOutput(Process aProcess, ExecutionContext aCtx, CyclicBarrier aBarrier) 
    throws IOException {
    	ProcessOutput _po = aCtx.getOutput();
    	if ((_po != null) && (_po.isAvailable())) {
    		OutputStream _out = _po.getOutputStream();
    		aCtx.connectStreams(aProcess.getInputStream(), new BarrierOutputStream(_out, aBarrier));
    		return _out;
    	}
    	return null;
    }
    
    private static OutputStream attachError(Process aProcess, ExecutionContext aCtx, CyclicBarrier aBarrier) 
    throws IOException {
    	ProcessOutput _pe = aCtx.getErrorOutput();
    	if ((_pe != null) && (_pe.isAvailable())) {
    		OutputStream _err = _pe.getOutputStream();
    		aCtx.connectStreams(aProcess.getErrorStream(), new BarrierOutputStream(_err, aBarrier));
    		return _err;
    	}
    	return null;
    }
    
    private static class BarrierOutputStream 
    extends OutputStream {
    	private OutputStream out;
    	private AtomicReference<CyclicBarrier> barrier;
    	
    	public BarrierOutputStream(OutputStream anOutputStream, CyclicBarrier aBarrier) {
    		out = anOutputStream;
    		barrier = new AtomicReference<CyclicBarrier>(aBarrier);
    	}
    	
    	@Override
		public void write(byte[] b, int off, int len) throws IOException {
			out.write(b, off, len);
		}

		@Override
		public void write(byte[] b) throws IOException {
			out.write(b);
		}

		@Override
		public void write(int aByte) throws IOException {
			out.write(aByte);
		}
		@Override
		public void flush() throws IOException {
			out.flush();
		}

		@Override
		public void close() throws IOException {
			try {
				out.close();
			} finally {
				try {
					CyclicBarrier _barrier = barrier.getAndSet(null);
					if (_barrier != null)
						_barrier.await();
				} catch (Exception anExc) {
					throw new IOException(anExc);
				}
			}
		}
    }
}
