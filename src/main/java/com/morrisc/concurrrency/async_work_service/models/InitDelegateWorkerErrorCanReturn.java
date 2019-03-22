package com.morrisc.concurrrency.async_work_service.models;



public interface InitDelegateWorkerErrorCanReturn<RETURN_TYPE, TYPE_OF_DEFAULT_PARAMETER_TO_ON_ERROR, PARAMETER_FOR_doINBACKGROUND_TYPE> {

	
	public RETURN_TYPE doInBackground(PARAMETER_FOR_doINBACKGROUND_TYPE parameter) throws Exception;
	
	public  RETURN_TYPE onBackgroundWorkSuccess(RETURN_TYPE r);
	
	
	public RETURN_TYPE onBackgroundWorkError(TYPE_OF_DEFAULT_PARAMETER_TO_ON_ERROR defaultParameter,Throwable throwable);
	
	
}

