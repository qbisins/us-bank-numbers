/**
 * Copyright (C) 2013, Moss Computing Inc.
 *
 * This file is part of us-bank-numbers.
 *
 * us-bank-numbers is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * us-bank-numbers is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with us-bank-numbers; see the file COPYING.  If not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module.  An independent module is a module which is not derived from
 * or based on this library.  If you modify this library, you may extend
 * this exception to your version of the library, but you are not
 * obligated to do so.  If you do not wish to do so, delete this
 * exception statement from your version.
 */
package com.moss.usbanknumbers;

/**
 * fractional routing number (U.S. only) - 
 * also known as the transit number, consists of a denominator mirroring the first 4 digits of the routing number.
 * 
 * And a hyphenated numerator, also known as the ABA number, in which the first part is a city code (1-49), if the 
 * account is in one of 49 specific cities, or a state code (50-99) if it is not in one of those specific cities; 
 * 
 * the second part of the hyphenated numerator mirrors the 5th through 8th digits of the routing number with 
 * leading zeros removed.[9]
 */
public class FractionalRoutingNumber {
	private AbaNumber numerator;
	private String denominator;
	
	public FractionalRoutingNumber(AbaNumber.Prefix abaPrefix, RoutingNumber routingNumber){
		this(new AbaNumber(abaPrefix, routingNumber), routingNumber);
	}
	
	public FractionalRoutingNumber(AbaNumber numerator, RoutingNumber denominator) {
		if(numerator==null)
			throw new NullPointerException("numerator is null");
		this.numerator = numerator;
		
		String text = denominator.toString();
		if(text.length()>4)
			text = text.substring(0, 4);
		
		this.denominator = ParseUtil.stripLeadingZeros(text);
	}
	
	public FractionalRoutingNumber(String text) throws FractionalRoutingNumberException, AbaNumberException {
		if(text==null)
			throw new NullPointerException("null fraction text");
		text = text.trim();
		if(text.length()==0)
			throw new FractionalRoutingNumberException("Empty fraction text");
		
		String[] parts = text.split("/");
		if(parts.length!=2)
			genericError(text);
		
		init(new AbaNumber(parts[0]), parts[1]);
	}
	
	private void genericError(String text) throws FractionalRoutingNumberException {
		throw new FractionalRoutingNumberException("\"" + text + "\" is not a valid fractional routing number.");
	}
	
	public FractionalRoutingNumber(AbaNumber numerator, String denominator) throws FractionalRoutingNumberException {
		init(numerator, denominator);
	}
	
	private void init(AbaNumber numerator, String denominator) throws FractionalRoutingNumberException {
		this.numerator = numerator;
		
		if(denominator==null)
			throw new FractionalRoutingNumberException("Null denominator");
		denominator = denominator.trim();
		if(denominator.length()==0)
			throw new FractionalRoutingNumberException("Empty denominator");
		if(denominator.length()>4)
			throw new FractionalRoutingNumberException("\"" + denominator + "\"is not a valid denominator (too long)");
		try{
			Integer.parseInt(denominator);
		}catch(NumberFormatException e){
			throw new FractionalRoutingNumberException("\"" + denominator + "\"is not a valid denominator (not numeric)");
		}
		this.denominator = denominator;
	}
	
	public AbaNumber numerator() {
		return numerator;
	}
	
	public String denominator() {
		return denominator;
	}
	
	@Override
	public String toString() {
		return numerator.toString() + "/" + denominator;
	}
}
