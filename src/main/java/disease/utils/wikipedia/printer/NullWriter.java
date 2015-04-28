package disease.utils.wikipedia.printer;

import java.io.IOException;


public class NullWriter extends java.io.Writer {

	public NullWriter() {
		
	}
	
	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flush() throws IOException {
	}

	@Override
	public void close() throws IOException {
	}

}
