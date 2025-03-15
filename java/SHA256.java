package com.erp.common;

import java.security.MessageDigest;

public class SHA256 {
	
    public String encrypt(String str){
    	try{
    		//Create SHA-256 instance 
    		MessageDigest md = MessageDigest.getInstance("SHA-256");
    		//Calculate digest
    		md.update(str.getBytes("UTF-8"));
    		//Return hash value after converting hexa string
    		return byteArrayToHex(md.digest()).toUpperCase();
    	}
    	catch(Exception e){
    		return null;
    	}
    }

    //byte[]->hex String test11
    private String byteArrayToHex(byte[] encrypted) {
        
        if(encrypted == null || encrypted.length == 0){
            return null;
        }
        
        StringBuffer sb = new StringBuffer();
        for (byte b : encrypted) {
        	sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
