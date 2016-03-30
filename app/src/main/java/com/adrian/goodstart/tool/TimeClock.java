package com.adrian.goodstart.tool;


public class TimeClock {

	private String TimeClockName;	// 用户名
	private String TimeClockTime;	//Serciver,device,friend
	private String TimeClockTimerepeat;	//ip地址
	private int TimeClockNo;	//MAC地址
	private boolean TimeClockState;//未接收消息数
	private String TimeClockAction;//类型
	private int TimeClockTimeinterval;//间隔时间

	public TimeClock(){

	}
	public void setTimeClockName(String TimeClockName){
		this.TimeClockName = TimeClockName;
	}
	public 	String getTimeClockName(){
		return TimeClockName;
	}
	public void setTimeClockTime(String TimeClockTime){
		this.TimeClockTime = TimeClockTime;
	}
	public 	String getTimeClockTime(){
		return TimeClockTime;
	}
	public void setTimeClockAction(String TimeClockAction){
		this.TimeClockAction = TimeClockAction;
	}
	public 	String getTimeClockAction(){
		return TimeClockAction;
	}
	public void setTimeClockTimerepeat(String TimeClockTimerepeat){
		this.TimeClockTimerepeat = TimeClockTimerepeat;
	}
	public 	String getTimeClockTimerepeat(){
		return TimeClockTimerepeat;
	}
	public void setTimeClockNo(int TimeClockNo){
		this.TimeClockNo = TimeClockNo;
	}
	public int getTimeClockNo(){
		return TimeClockNo;
	}
	public void setTimeClockTimeinterval(int TimeClockTimeinterval){
		this.TimeClockTimeinterval = TimeClockTimeinterval;
	}
	public 	int getTimeClockTimeinterval(){
		return TimeClockTimeinterval;
	}
	public void setTimeClockState(Boolean TimeClockState){
		this.TimeClockState = TimeClockState;
	}
	public boolean getTimeClockState(){
		return TimeClockState;
	}
}