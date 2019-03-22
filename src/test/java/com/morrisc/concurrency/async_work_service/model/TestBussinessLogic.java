package com.morrisc.concurrency.async_work_service.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import com.morrisc.concurrrency.async_work_service.AsyncWorkService;
import com.morrisc.concurrrency.async_work_service.models.InitDelegateWorker;
import com.morrisc.test_tool.ExceptionProducer;
import com.morrisc.concurrrency.async_work_service.models.ChainableDelegateWorker;

import com.google.common.util.concurrent.ListenableFuture;
import static org.junit.Assert.assertEquals;

public class TestBussinessLogic {

	private static final long SIMULATE_LONG_TASK_THREAD_SLEEP_TIME = 2000;
	
	public void doOneWorkerThenChainAnother(){
		

		ListenableFuture<String> aFuture = AsyncWorkService.getInstance().sendWorkToBackgroundExecutorService(new InitDelegateWorker<String,String>(){

			public String doInBackground(String str) throws Exception {
				
				System.out.println("background work stated on :"+Thread.currentThread().getName());
				
				try {
					Thread.sleep(3000);
					
				//	throw new InterruptedException("help !!");
					
					System.out.println("Thread awake and job is done");
				} catch (InterruptedException e) {
					
				  throw new InterruptedException(e.getMessage());
				}
				return "this is the result of thread:"+Thread.currentThread().getName();
			}

			public String onBackgroundWorkSuccess(String r) {
				
				System.out.println("what will print in onSuccess  ? :"+r);
				return r;
			}

			public void onBackgroundWorkError(Throwable throwable) {
				
				System.out.println("on Error"+throwable.getMessage());
				
			}
		
		}, "someThing wrong !!!","myDefaultParameter");
		
		
		System.out.println("will this line print first??");
		
		
	ListenableFuture<Integer> transFuture	= AsyncWorkService.getInstance().chain_AndKeepDoInBackground(aFuture, new ChainableDelegateWorker<String, Integer>() {

		public Integer doInBackground(String valueFromPreviousWorker) throws Exception {
			
			System.out.println("2nd chain");
			System.out.println("thread name :"+Thread.currentThread().getName());
			
			Thread.sleep(4000);
			return 300;
		}

		public Integer onBackgroundWorkSuccess(Integer r) {
			
			return r+4000;
		}

		public void onBackgroundWorkError(Throwable throwable) {
			
			
			System.out.println(throwable.getMessage());
		}

	
		
		
	}, 700);
		
	System.out.println("will this line print before chainInBackground ??");	
	
		int myResult = 0;
		
		
		
		
		try {
			myResult = AsyncWorkService.getSingleResult(transFuture);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("the last One :"+myResult);
		
		
		
	}
	
	
	public void doConcurrentWork(){
		
		

		
		long start = System.currentTimeMillis();
		
		List<ListenableFuture<Integer>> fList = new ArrayList<ListenableFuture<Integer>>();
		for(int i =0; i<40; i+=1){
		ListenableFuture<Integer> aFuture = AsyncWorkService.getInstance().sendWorkToBackgroundExecutorService(new InitDelegateWorker<Integer,Integer>(){

			public Integer doInBackground(Integer i) throws Exception {
				
				System.out.println("background work stated on :"+Thread.currentThread().getName());
				
				try {
					
					System.out.println("parameter from outter: "+i);
					Thread.sleep(5000);
					
					System.out.println("Thread awake and job is done");
					
					Random randomGen = new Random();
					 int randomInt = randomGen.nextInt(50)+1;
					 
					if(randomInt % 2 == 0){
						
					  ExceptionProducer.makeException();
					}
					
				} catch (InterruptedException e) {
					
				  throw new InterruptedException(e.getMessage());
				}
				return 20;
			}

			public Integer onBackgroundWorkSuccess(Integer r) {
				
				System.out.println("what will print in onSuccess  ? :"+r);
				return r;
			}

			public void onBackgroundWorkError(Throwable throwable) {
				
				System.out.println("on Error "+throwable.getMessage());
				
			}

		
		}, 999,i);
		
		fList.add(aFuture);
		
		}	
		System.out.println("will this line print first??");

		
		try {
			System.out.println(Arrays.toString(AsyncWorkService.getMultipleResult(fList).toArray()  ));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long end = System.currentTimeMillis();
		
		System.out.println((end-start)/1000);
		
		
	}


	public void testBusinessConcurrency(){
		
		
		
		
		List<LandOfficeVO> landOfficeIdentifierList = new ArrayList<LandOfficeVO>();
		
		
		landOfficeIdentifierList.add(new LandOfficeVO("EI","172.16.74.32"));
		landOfficeIdentifierList.add(new LandOfficeVO("EG","172.16.74.32"));
		landOfficeIdentifierList.add(new LandOfficeVO("EA","172.16.74.32"));
		landOfficeIdentifierList.add(new LandOfficeVO("RA","172.16.74.32"));
		landOfficeIdentifierList.add(new LandOfficeVO("RB","172.16.74.32"));
		landOfficeIdentifierList.add(new LandOfficeVO("RG","172.16.74.32"));
		
		

		List<ListenableFuture<String>> fList = new ArrayList<ListenableFuture<String>>();
		
		long start = System.currentTimeMillis();
		
		for(int i=0; i<landOfficeIdentifierList.size(); i+=1){

			ListenableFuture<String> currFuture = AsyncWorkService.getInstance().sendWorkToBackgroundExecutorService(new InitDelegateWorker<String, LandOfficeVO>() {

			public String doInBackground(LandOfficeVO landOfficeInfo) throws Exception {
				
				String currThreadName = Thread.currentThread().getName();
				System.out.println("backgroundWork on "+currThreadName+" parameter :"+landOfficeInfo.getLandOfficeId());

				Thread.sleep(SIMULATE_LONG_TASK_THREAD_SLEEP_TIME); // simulate long time network I/O operation
 				return landOfficeInfo.getLandOfficeId()+" OK";
			}

			public String onBackgroundWorkSuccess(String r) {
				System.out.println("parameter :"+r);
				return  r+" process in success";
			}

			public void onBackgroundWorkError(Throwable throwable) {
				
				throwable.printStackTrace();
			}
		}, landOfficeIdentifierList.get(i).getLandOfficeId()+" failed", landOfficeIdentifierList.get(i));
			

			fList.add(currFuture);
	}
		
System.out.println("will this line print first??");

        List<String> result = null;
        
		try {
			 result = AsyncWorkService.getMultipleResult(fList);
			
			System.out.println(Arrays.toString(result.toArray()  ));

	
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long end = System.currentTimeMillis();
		long executeTime = (end-start)/1000;
		System.out.println("執行時間(秒) :"+ executeTime);
		
		
		assertEquals(result.get(0),"EI OK process in success");
		assertEquals(result.get(1),"EG OK process in success");
		assertEquals(result.get(2),"EA OK process in success");
		assertEquals(result.get(3),"RA OK process in success");
		
		// should be equal, since all task execute concurrently
		assertEquals(executeTime, SIMULATE_LONG_TASK_THREAD_SLEEP_TIME/ 1000);
	}



	public void onTestEnd(){

		AsyncWorkService.getInstance().shutdown_TheGuava_ListeningExecutorService_ThreadPool();

	}
	
}
