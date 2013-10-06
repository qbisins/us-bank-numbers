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
public class AbaNumber {
	
	private Prefix prefix;
	private String suffix;
	
	public AbaNumber(Prefix prefix, RoutingNumber routingNumber) {
		super();
		
		if(prefix==null)
			throw new NullPointerException("Null prefix");
		if(routingNumber==null)
			throw new NullPointerException("Null routing number");
		
		this.prefix = prefix;
		
		String num = routingNumber.toString();
		int end = num.length();
		if(end>8)
			end = 8;
		
		this.suffix = ParseUtil.stripLeadingZeros(num.substring(4, end));
		
	}
	
	public AbaNumber(String text) throws AbaNumberException {
		if(text==null)
			throw new NullPointerException("null aba text");
		text = text.trim();
		if(text.length()==0)
			throw new AbaNumberException("Emtpy aba text");
		
		String[] parts = text.split("-");
		if(parts.length!=2)
			throw new AbaNumberException("\"" + text + "\" is not a valid aba number.");
		
		int prefixNum;
		try {
			prefixNum = Integer.parseInt(parts[0]);
		} catch (NumberFormatException e) {
			throw new AbaNumberException("\"" + text + "\" is not a valid aba number.  The prefix is not a number.");
		}
		
		Prefix prefix = Prefix.forNumber(prefixNum);
		
		if(prefix==null)
			throw new AbaNumberException("\"" + text + "\" is not a valid aba number.  \"" + prefixNum + "\" is not a known prefix.");
		init(prefix, parts[1]);
	}
	
	
	public AbaNumber(Prefix prefix, String suffix) throws AbaNumberException {
		init(prefix, suffix);
	}
	private void init(Prefix prefix, String suffix) throws AbaNumberException {
		this.prefix = prefix;
		if(suffix==null)
			throw new AbaNumberException("Null Suffix");
		suffix = suffix.trim();
		if(suffix.length()==0)
			throw new AbaNumberException("Empty Suffix");
		if(suffix.length()>4)
			throw new AbaNumberException("\"" + suffix + "\"is not a valid suffix (too long)");
		try{
			Integer.parseInt(suffix);
		}catch(NumberFormatException e){
			throw new AbaNumberException("\"" + suffix + "\"is not a valid suffix (not numeric)");
		}
		this.suffix = suffix;
	}

	public Prefix prefix() {
		return prefix;
	}
	
	public String suffix() {
		return suffix;
	}
	
	@Override
	public String toString() {
		return prefix.number + "-" + suffix;
	}
	
	public enum Prefix{
		New_York_NY(1),
		Chicago_IL(2),
		Philadelphia_PA(3),
		St_Louis_MO(4),
		Boston_MA(5),
		Cleveland_OH(6),
		Baltimore_MD(7),
		Pittsburgh_PA(8),
		Detroit_MI(9),
		Buffalo_NY(10),
		San_Francisco_CA(11),
		Milwaukee_WI(12),
		Cincinnati_OH(13),
		New_Orleans_LA(14),
		Washington_DC(15),
		Los_Angeles_CA(16),
		Minneapolis_MN(17),
		Kansas_City_MO(18),
		Seattle_WA(19),
		Indianapolis_IN(20),
		Louisville_KY(21),
		St_Paul_MN(22),
		Denver_CO(23),
		Portland_OR(24),
		Columbus_OH(25),
		Memphis_TN(26),
		Omaha_NE(27),
		Spokane_WA(28),
		Albany_NY(29),
		San_Antonio_TX(30),
		Salt_Lake_City_UT(31),
		Dallas_TX(32),
		Des_Moines_IA(33),
		Tacoma_WA(34),
		Houston_TX(35),
		St_Joseph_MO(36),
		Fort_Worth_TX(37),
		Savannah_GA(38),
		Oklahoma_City_OK(39),
		Wichita_KS(40),
		Sioux_City_IA(41),
		Pueblo_CO(42),
		Lincoln_NE(43),
		Topeka_KS(44),
		Dubuque_IA(45),
		Galveston_TX(46),
		Cedar_Rapids_IA(47),
		Waco_TX(48),
		Muskogee_OK(49),
		New_York(50),
		Connecticut(51),
		Maine(52),
		Massachusetts(53),
		New_Hampshire(54),
		New_Jersey(55),
		Ohio(56),
		Rhode_Island(57),
		Vermont(58),
		Alaska_American_Samoa_Guam_Hawaii_Puerto_Rico_Virgin_Islands(59),
		Pennsylvania(60),
		Alabama(61),
		Delaware(62),
		Florida(63),
		Georgia(64),
		Maryland(65),
		North_Carolina(66),
		South_Carolina(67),
		Virginia(68),
		West_Virginia(69),
		Illinois(70),
		Indiana(71),
		Iowa(72),
		Kentucky(73),
		Michigan(74),
		Minnesota(75),
		Nebraska(76),
		North_Dakota(77),
		South_Dakota(78),
		Wisconsin(79),
		Missouri(80),
		Arkansas(81),
		Arizona_1(82),
		Kansas(83),
		Louisiana(84),
		Mississippi(85),
		Oklahoma(86),
		Tennessee(87),
		Texas(88),
		California(90),
		Arizona_2(91),
		Idaho(92),
		Montana(93),
		Nevada(94),
		New_Mexico(95),
		Oregon(96),
		Utah(97),
		Washington(98),
		Wyoming(99),
		Assigned(101);
		
		int number;
		
		Prefix(int number){
			this.number = number;
		}
		
		public static Prefix forNumber(int number){
			for(Prefix p :Prefix.values()){
				if(p.number==number)
					return p;
			}
			return null;
		}
		
	}
}
