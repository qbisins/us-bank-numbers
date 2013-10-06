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

import com.moss.usbanknumbers.jaxb.AccountNumberAdapter;

@XmlJavaTypeAdapter(AccountNumberAdapter.class)
@SuppressWarnings("serial")
public class AccountNumber implements Serializable {
	
	private String number;
	
	public AccountNumber(String number) throws AccountNumberException {
		
		if (number == null) {
			throw new NullPointerException();
		}
		
		number = number.trim();
		
		if (number.length() == 0) {
			throw new AccountNumberException("An account number must be greater than 0 digits in length");
		}
		
		if (!isNumeric(number)) {
			throw new AccountNumberException("An account number must be entirely numeric.");
		}
		
		int[] digits = new int[number.length()];
		{
			for (int i=0; i<number.length(); i++) {
				digits[i] = Integer.parseInt(number.substring(i, i + 1));
			}
		}
		
		this.number = number;
	}
	
	public boolean equals(Object o) {
		return
			o != null
			&&
			o instanceof AccountNumber
			&&
			((AccountNumber)o).number.equals(number);
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
}
