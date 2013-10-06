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

import java.io.Serializable;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.moss.usbanknumbers.jaxb.RoutingNumberAdapter;

@XmlJavaTypeAdapter(RoutingNumberAdapter.class)
@SuppressWarnings("serial")
public class RoutingNumber implements Serializable {
	
	private static final int LENGTH = 9;
	private String number;
	
	public RoutingNumber(String numberMinusChecksum, int checksum) throws RoutingNumberException {
		init(numberMinusChecksum + Integer.toString(checksum));
	}
	
	public RoutingNumber(String number) throws RoutingNumberException {
		init(number);
	}
	
	private void init(String number) throws RoutingNumberException {
		
		if (number == null) {
			throw new NullPointerException();
		}
		
		number = number.trim();
		
		if (number.length() != LENGTH) {
			throw new RoutingNumberException("A routing number must be " + LENGTH + " digits in length.");
		}
		
		if (!isNumeric(number)) {
			throw new RoutingNumberException("A routing number must be entirely numeric.");
		}
		
		int[] digits = new int[number.length()];
		{
			for (int i=0; i<number.length(); i++) {
				digits[i] = Integer.parseInt(number.substring(i, i + 1));
			}
		}
		
		if (!checksumMatches(digits)) {
			throw new RoutingNumberException("This routing number is not valid.");
		}
		
		this.number = number;
	}
	
	public boolean equals(Object o) {
		return
			o != null
			&&
			o instanceof RoutingNumber
			&&
			((RoutingNumber)o).number.equals(number);
	}
	
	public String toString() {
		return number;
	}
	
	public int hashCode() {
		return number.hashCode();
	}
	
	private boolean isNumeric(String number) {
		
		for (char c : number.toCharArray()) {
			if (c < '0' || c > '9') {
				return false;
			}
		}
		
		return true;
	}

	public int checkDigit() {
		String lastDigit = number.substring(number.length() - 1, number.length());
		return Integer.parseInt(lastDigit);
	}
	
	public int dfiIdentity() {
		String id = number.substring(0, number.length() - 1);
		return Integer.parseInt(id);
	}
	
	private boolean checksumMatches(int[] digits) {
		
		if (digits.length != 9) {
			return false;
		}
		
		int checkDigit = checksumAlgorithm(digits);
		
		return digits[8] == checkDigit;
	}

	private static int checksumAlgorithm(String digitsString) {
		
		if (digitsString.length() < 8) {
			throw new IllegalArgumentException("cannot calculate a check digit with less than 8 input digits");
		}
		
		int[] digits = new int[8];
		
		for (int i=0; i<8; i++) {
			digits[i] = Integer.parseInt(digitsString.substring(i, i + 1));
		}
		
		return checksumAlgorithm(digits);
	}

	public static RoutingNumber fromNoChecksum(String numberMinusChecksum) throws RoutingNumberException {
		return new RoutingNumber(numberMinusChecksum, RoutingNumber.checksumAlgorithm(numberMinusChecksum));
	}
	
	public static int checksumAlgorithm(int[] digits){
		
		if (digits.length < 8) {
			throw new IllegalArgumentException("cannot calculate a check digit with less than 8 input digits");
		}
		
		int sum = 0;
		
		sum += digits[0] * 3;
		sum += digits[1] * 7;
		sum += digits[2] * 1;
		sum += digits[3] * 3;
		sum += digits[4] * 7;
		sum += digits[5] * 1;
		sum += digits[6] * 3;
		sum += digits[7] * 7;
		
		int nextHighestMultipleOf10 = sum;
		
		while (nextHighestMultipleOf10 % 10 != 0) {
			nextHighestMultipleOf10++;
		}
		
		int checkDigit = nextHighestMultipleOf10 - sum;
		
		return checkDigit;
	}
	
}
