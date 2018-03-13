package com.creeps.appkiller.core.services.model;

public class Week{
	/* THESE are the constants that the class must be instantiated with.
	* These constants are bitmasks and will reduce data storage by a huge amount.
	* */
	public static byte SUNDAY=0x01;private final static byte SUN_RS=0;
	public static byte MONDAY=0x02;private final static byte MON_RS=1;
	public static byte TUESDAY=0x04;private final static byte TUE_RS=2;
	public static byte WEDNESDAY=0x08;private final static byte WED_RS=3;
	public static byte THURSDAY=0x16;private final static byte THU_RS=4;
	public static byte FRIDAY=0x32;private final static byte FRI_RS=5;
	public static byte SATURDAY=0x64;private final static byte SAT_RS=6;
	private byte days;
	public Week(byte weekdays){
		this.days=weekdays;
	}
	public Week(){
		this((byte)(SUNDAY|MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY));
	}
	public Week remove(int bitmask){
		this.days=(byte)(this.days & notWithMsb((byte)bitmask));
		return this;
	}
	public Week add(int bitmask){
		this.days= (byte)(this.days|bitmask);
		return this;
	}
	public boolean isDayActive(byte day){
		return ((this.days & day ) >> getRightShiftCount(day))==1;
	}
	private static byte getRightShiftCount(byte a){
		
			if(a== SUNDAY) return SUN_RS;
			if(a== MONDAY) return MON_RS;
			if(a==TUESDAY) return TUE_RS;
			if(a==WEDNESDAY) return WED_RS;
			if(a==THURSDAY) return THU_RS;
			if(a==FRIDAY) return FRI_RS;
			if(a== SATURDAY) return SAT_RS;
		
		return -1;
	}
	private static byte notWithMsb(byte a){
		return (byte)((~a) & 0x7f);
	}

}
