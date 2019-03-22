package com.morrisc.concurrency.async_work_service;

import com.morrisc.concurrency.async_work_service.model.TestBussinessLogic;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AsyncWorkeServiceTest {

	private static TestBussinessLogic bLogic;
	 @BeforeClass
	 public static void init() {

		bLogic = new TestBussinessLogic();
	 }


   @Test
	public void testiIsAsyncWorkChainable() {
		
			
			bLogic.doOneWorkerThenChainAnother();
			
		
		System.out.println("chain finished");
	}


	@Test 
  public void testIsAsyncWorkConcurrentExecutable() {
				bLogic.doConcurrentWork();

				System.out.println("concurrent test finished");
	}

	@Test 
  public void testBusinessConcurrency() {

				bLogic.testBusinessConcurrency();

				System.out.println("business concurrent test finished");
	}

	@AfterClass
	public static void onTestEnd(){

		bLogic.onTestEnd();
	}

}
