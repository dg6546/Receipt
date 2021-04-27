package com.hkucs.receipt;

public class StringIntTuple{
    public final int intValue;
    public final String stringValue;
    public StringIntTuple(int intValue, String stringValue){
        this.intValue = intValue;
        this.stringValue = stringValue;
    }
    public String toString(){
        return "(" + this.intValue + ", " + this.stringValue + ")";
    }

    public String getStringValue(){
        return this.stringValue;
    }

    public int getIntValue(){
        return this.intValue;
    }

    public boolean compare(StringIntTuple b) {
        return ((Integer)this.intValue) < ((Integer)b.intValue);
    }
}
