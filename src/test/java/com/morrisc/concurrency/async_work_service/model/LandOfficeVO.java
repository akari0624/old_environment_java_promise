package com.morrisc.concurrency.async_work_service.model;

public class LandOfficeVO {

	
	private String landOfficeId;
	
	private String landOfficeIp;
	
	
	
	public LandOfficeVO(){}
	
	
	public LandOfficeVO(String landOfficeId, String landOfficeIp){
		
		this.landOfficeId = landOfficeId;
		this.landOfficeIp = landOfficeIp;
		
		
	}
	
	

	public String getLandOfficeId() {
		return landOfficeId;
	}

	public void setLandOfficeId(String landOfficeId) {
		this.landOfficeId = landOfficeId;
	}

	public String getLandOfficeIp() {
		return landOfficeIp;
	}

	public void setLandOfficeIp(String landOfficeIp) {
		this.landOfficeIp = landOfficeIp;
	}
	
	
	
	
	
	
}
