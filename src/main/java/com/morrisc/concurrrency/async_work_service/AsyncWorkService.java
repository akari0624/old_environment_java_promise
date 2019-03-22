package com.morrisc.concurrrency.async_work_service;

/**
 * JS ES6 Promise like async service , can be use to made blocking work do in background thread
 * need the dependency of Google Guava 11 (since this project is based on JDK 5, you can not use the newest version)
 * 
 * author: Morris Chen
 * Date: 2018/01/08
 * **/

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.morrisc.concurrrency.async_work_service.models.ChainableDelegateWorker;
import com.morrisc.concurrrency.async_work_service.models.InitDelegateWorker;
import com.morrisc.concurrrency.async_work_service.models.InitDelegateWorkerErrorCanReturn;

public class AsyncWorkService {
	
	private static final Logger m_Logger = LoggerFactory.getLogger(AsyncWorkService.class);
	
	private static int THREAD_POOL_THREAD_SIZE = 15;
	private final ListeningExecutorService guavaExecutorThreadPoolController;
	
	//private List<ListenableFuture<R>> promiseList = new ArrayList<ListenableFuture<R>>();
	
	private static class SingletonHolder {
		
		private static final AsyncWorkService serviceSingleton = new AsyncWorkService();
		
	}
	
	private AsyncWorkService(){
		
		this.guavaExecutorThreadPoolController = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(THREAD_POOL_THREAD_SIZE));
		m_Logger.info("AsyncWorkService.java  thread pool創立成功,裡頭的執行緒數量 :"+THREAD_POOL_THREAD_SIZE);
		 
		
	}
	
	public static AsyncWorkService getInstance(){
	
		return SingletonHolder.serviceSingleton;
	}
	 
	/*
	 * 版本一 
	 *   不return onBackgroundWorkError裡的 errorMsg到最後的resultList裡
	 *   而是return 一個 defaultValue , 把最後resultList拿來跑迴圈時,就能知道哪些成功，哪些失敗
	 *   
	 * */
	public <R,T> ListenableFuture<R> sendWorkToBackgroundExecutorService(final InitDelegateWorker<R,T> delegateWorker, final R defaultReturnValueWhenThisWorkFail, final T parameterForDoInBackGround){
		
		
		 ListenableFuture<R> thePromise = this.guavaExecutorThreadPoolController.submit(new Callable<R>(){
			
			
			public R call() throws Exception {
				
				
				try{
					return delegateWorker.onBackgroundWorkSuccess(delegateWorker.doInBackground(parameterForDoInBackGround));
				}catch(Exception e){
					
					delegateWorker.onBackgroundWorkError(e);
				}
				
				return defaultReturnValueWhenThisWorkFail;
				
				
			}
		});
		
		
		return thePromise;
	}
	
	
	/*
	 * 版本二 
	 *    return onBackgroundWorkError裡的 errorMsg到最後的resultList裡
	 *    把最後resultList拿來跑迴圈時,就能夠取到所有 程式碼流程裡  安排到最後的 值
	 * */
	public <R, T_FOR_ERROR_DEFAULT, T> ListenableFuture<R> sendWorkToBackgroundExecutorService_ErrorCanReturn(final InitDelegateWorkerErrorCanReturn<R, T_FOR_ERROR_DEFAULT, T> delegateWorker, final T_FOR_ERROR_DEFAULT defaultParameterToOnError, final T parameterForDoInBackGround){
		
		
		 ListenableFuture<R> thePromise = this.guavaExecutorThreadPoolController.submit(new Callable<R>(){
			
			
			public R call() throws Exception {
				
				R errorObj;
				try{
					return delegateWorker.onBackgroundWorkSuccess(delegateWorker.doInBackground(parameterForDoInBackGround));
				}catch(Exception e){
					
					errorObj = delegateWorker.onBackgroundWorkError(defaultParameterToOnError, e);
				}
				
				return errorObj;
				
				
			}
		});
		
		
		return thePromise;
	}
	
	
	public <R,T> ListenableFuture<T> chain_AndKeepDoInBackground(final ListenableFuture<R> promise, final ChainableDelegateWorker<R,T> worker, final T defaultReturnValueWhenThisWorkFail){
		
	  
		 ListenableFuture<T> thePromise = null;
		try {
			thePromise = this.guavaExecutorThreadPoolController.submit(new Callable<T>(){
					
				 final R value = AsyncWorkService.getSingleResult(promise);	
				 
					public T call() throws Exception {
						
						
						try{
							return worker.onBackgroundWorkSuccess(worker.doInBackground(value));
						}catch(Exception e){
							
							worker.onBackgroundWorkError(e);
						}
						
						return defaultReturnValueWhenThisWorkFail;
						
						
					}
				});
		} catch (InterruptedException e) {
			
			m_Logger.error(e.getMessage());
		} catch (ExecutionException e) {
			
			m_Logger.error(e.getMessage());
		}
			
			
			return thePromise;
		
	}
		
	

	
	public static <R> R getSingleResult(ListenableFuture<R> aFuture) throws InterruptedException, ExecutionException{
		
		 ListenableFuture<List<R>> resultList = Futures.allAsList(aFuture);
		 
		 return resultList.get().get(0);
	}
	
	public static <R> List<R> getMultipleResult(List<ListenableFuture<R>> futureList) throws InterruptedException, ExecutionException{
		
		 ListenableFuture<List<R>> resultList = Futures.allAsList(futureList);
		 
		 return resultList.get();
	}
	
	public void shutdown_TheGuava_ListeningExecutorService_ThreadPool(){
		
		m_Logger.info("prepared to shutdown the thread pool with "+THREAD_POOL_THREAD_SIZE+" threads...");
		
		if(this.guavaExecutorThreadPoolController != null){
			
		this.guavaExecutorThreadPoolController.shutdown();
		
		}
		m_Logger.info("the ThreadPool has shuting down gracefully, bye bye ~~~");
		
	}
}
