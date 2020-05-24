package com.nabiki.corona.system;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;

public class TimePrintStream extends PrintStream {
	private Object sync = new Object();
	
	public TimePrintStream(OutputStream out) {
		super(out, true, Charset.forName("UTF-8"));
	}

	@Override
	public void println() {
		synchronized(sync) {
			super.print(LocalDateTime.now().toString());
			super.print(" ");
			super.println();
		}
	}

	@Override
	public void println(boolean x) {
		synchronized(sync) {
			super.print(LocalDateTime.now().toString());
			super.print(" ");
			super.println(x);
		}
	}

	@Override
	public void println(char x) {
		synchronized(sync) {
			super.print(LocalDateTime.now().toString());
			super.print(" ");
			super.println(x);
		}
	}

	@Override
	public void println(int x) {
		synchronized(sync) {
			super.print(LocalDateTime.now().toString());
			super.print(" ");
			super.println(x);
		}
	}

	@Override
	public void println(long x) {
		synchronized(sync) {
			super.print(LocalDateTime.now().toString());
			super.print(" ");
			super.println(x);
		}
	}

	@Override
	public void println(float x) {
		synchronized(sync) {
			super.print(LocalDateTime.now().toString());
			super.print(" ");
			super.println(x);
		}
	}

	@Override
	public void println(double x) {
		synchronized(sync) {
			super.print(LocalDateTime.now().toString());
			super.print(" ");
			super.println(x);
		}
	}

	@Override
	public void println(char[] x) {
		synchronized(sync) {
			super.print(LocalDateTime.now().toString());
			super.print(" ");
			super.println(x);
		}
	}

	@Override
	public void println(String x) {
		synchronized(sync) {
			super.print(LocalDateTime.now().toString());
			super.print(" ");
			super.println(x);
		}
	}

	@Override
	public void println(Object x) {
		synchronized(sync) {
			super.print(LocalDateTime.now().toString());
			super.print(" ");
			super.println(x);
		}
	}

}