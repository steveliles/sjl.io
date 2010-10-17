package com.sjl.io.streams;

import static org.junit.Assert.*;

import java.io.*;

import org.junit.*;

public class PartialRangeInputStreamTest {

    private File f;
    
    @Before
    public void setUp() throws Exception {
        f = new File(System.getProperty("java.io.tmpdir"), "test-file.txt");
        f.delete();
        f.createNewFile();
        
        FileOutputStream _out = new FileOutputStream(f);
        _out.write("steve".getBytes());
        _out.close();
    }

    @After
    public void tearDown() throws Exception {
        f.delete();
    }

    @Test
    public void testReadingAllBytes()
    throws Exception {                
        PartialRangeInputStream _p = 
        	new PartialRangeInputStream(
        		new FileInputStream(f), 0, (int)f.length());
        
        ByteArrayOutputStream _bytes = new ByteArrayOutputStream();
        Streams.copyAndClose(_p, _bytes);
                
        assertEquals("steve", new String(_bytes.toByteArray()));
    }
    
    @Test
    public void testReadingOffsetFromStart()
    throws Exception {
        PartialRangeInputStream _p = 
        	new PartialRangeInputStream(
        		new FileInputStream(f), 2, (int)f.length());
        
        ByteArrayOutputStream _bytes = new ByteArrayOutputStream();
        Streams.copyAndClose(_p, _bytes);
                
        assertEquals("eve", new String(_bytes.toByteArray()));        
    }
    
    @Test
    public void testReadingOffsetFromEnd()
    throws Exception {
        PartialRangeInputStream _p = 
        	new PartialRangeInputStream(
        		new FileInputStream(f), 0, 2);
        
        ByteArrayOutputStream _bytes = new ByteArrayOutputStream();
        Streams.copyAndClose(_p, _bytes);
                
        assertEquals("ste", new String(_bytes.toByteArray()));        
    }
    
    @Test
    public void testReadingOffsetFromStartAndEnd()
    throws Exception {
        PartialRangeInputStream _p = 
        	new PartialRangeInputStream(
        		new FileInputStream(f), 1, 3);
        
        ByteArrayOutputStream _bytes = new ByteArrayOutputStream();
        Streams.copyAndClose(_p, _bytes);
                
        assertEquals("tev", new String(_bytes.toByteArray()));        
    }
}

