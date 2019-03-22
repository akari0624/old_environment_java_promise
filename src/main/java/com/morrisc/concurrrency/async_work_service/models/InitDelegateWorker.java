package com.morrisc.concurrrency.async_work_service.models;

public interface InitDelegateWorker<RETURN_TYPE,PARAMETER_FOR_doINBACKGROUND_TYPE> {

	
	public RETURN_TYPE doInBackground(PARAMETER_FOR_doINBACKGROUND_TYPE parameter) throws Exception;
	
	public  RETURN_TYPE onBackgroundWorkSuccess(RETURN_TYPE r);
	
	
	public void onBackgroundWorkError(Throwable throwable);
	
	
}
