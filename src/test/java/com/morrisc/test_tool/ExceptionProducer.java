package com.morrisc.test_tool;


public class ExceptionProducer {
	
	public static void makeException() throws InterruptedException{
		
		throw new InterruptedException(" InterruptedException !!!!");
	}

}
