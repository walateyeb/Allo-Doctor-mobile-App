package com.application;

public class InfoRowdata {
	 	public boolean isclicked=false;
	    public int index;
	    public String serviceid;
	    public String strAmount;
	    public String servicename;
	    
	    public InfoRowdata(boolean isclicked,int index,String serviceid,String strAmount,String serviceName)
	    {
	        this.index=index;
	        this.isclicked=isclicked;
	        this.serviceid = serviceid;
	        
	        this.strAmount=strAmount;
	        this.servicename = serviceName;
	    }
	    
	    public int getIndex()
	    {
	    	return index;
	    }
	    public void setIndex(int index) {
			this.index = index;
		}
	    
	    public String getAmount()
	    {
	    	return strAmount;
	    }
	    public void setAmount(String amount) {
			this.strAmount = amount;
		}
	    
	    public String getServiceId()
	    {
	    	return serviceid;
	    }
	    public void setServiceId(String id) {
			this.serviceid = id;
		}
	    
	    public String getServiceName()
	    {
	    	return servicename;
	    }
	    public void setServiceName(String service) {
			this.servicename = service;
		}
	    
	    public boolean isChecked() {
	    	  return isclicked;
	    }
	    public void setChecked(boolean selected) {
	    	  this.isclicked = selected;
	    }
	    	  
	    
	    
}
