package com.sjl.io.streams;

import java.io.*;
import java.nio.channels.*;

public class PartialRangeInputStream
extends InputStream {
	
    private int start;
    private int end;
    private int position = -1;
    
    private InputStream realStream; 
    
    public PartialRangeInputStream(InputStream anUnderlyingStream, int aStart, int anEnd) {
        start = aStart;
        end = anEnd;
        
        realStream = anUnderlyingStream;        
    }

    @Override
    public int read() 
    throws IOException {
        if (position < 0) {
            scrollToStart();
            position = start;
        }
                
        if (available() >= 0) {
            int _result = realStream.read();
            position++;
        
            return _result;
        } else {
            return -1;
        }
    }
            
    @Override
    public int available() 
    throws IOException {
        return Math.min(realStream.available(), (end - position));
    }

    @Override
    public void close() 
    throws IOException {
        realStream.close();
    }

    private void scrollToStart()
    throws IOException {
        if (realStream instanceof FileInputStream) {
            // Much quicker - Random Access :)
            FileChannel _fc = ((FileInputStream)realStream).getChannel();
            _fc.position(start);
        } else {
            for (int i=0; i<start; i++) {
                realStream.read();
            }           
        }
    }
    
}