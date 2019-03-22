package com.morrisc.concurrrency.async_work_service.models;


public interface ChainableDelegateWorker<T,R> {

	
    public R doInBackground(T valueFromPreviousWorker) throws Exception;
	
	public  R onBackgroundWorkSuccess(R r);
	
	
	public void onBackgroundWorkError(Throwable throwable);
	
	
}
