package org.tondo.adventofcode2020.days;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.tondo.adventofcode2020.DayTaskBase;

public class TestDay4 extends DayTaskBase{
	
	private static final Set<String> PASSPORT_FIELDS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
			"byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid", "cid"
			)));
	
	private static final Set<String> EYE_COLORS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
			"amb", "blu", "brn", "gry",  "grn", "hzl", "oth"
			)));

	public TestDay4() {
		super(4);
	}
	
	
	@Test
	public void testPartOne() throws Exception {
		assertEquals("245", getPartOneSolution());
	}
	
	@Test
	public void testPartTwo() throws Exception {
		assertEquals("133", getPartTwoSolution());
	}
	
	@Override
	public String getPartOneSolution() throws Exception {
		int validPassCnt = 0;
		try (BufferedReader reader = getPartOneInput()) {
			Map<String, String> passport = readPassport(reader);
			
			while (passport != null) {
				
				if (isPassportValid(passport)) {
					validPassCnt++;
				}
				passport = readPassport(reader);
			}
		}
		return "" + validPassCnt;
	}
	
	
	
	@Override
	public String getPartTwoSolution() throws Exception {
		int validPassCnt = 0;
		try (BufferedReader reader = getPartOneInput()) {
			Map<String, String> passport = readPassport(reader);
			
			while (passport != null) {
				
				if (isPassportValid(passport) && validatePassportValues(passport)) {
					validPassCnt++;
				}
				passport = readPassport(reader);
			}
		}
		return "" + validPassCnt;
	}
	
	protected boolean validatePassportValues(Map<String, String> passport) {
		
		for(Map.Entry<String, String> entry : passport.entrySet()) {
			if ("byr".equals(entry.getKey())) {
				if (!validateNumberRange(entry.getValue(), 1920, 2002, 4)) {
					return false;
				}
			} else if ("iyr".equals(entry.getKey())) {
				if (!validateNumberRange(entry.getValue(), 2010, 2020, 4)) {
					return false;
				}
				
			} else if ("eyr".equals(entry.getKey())) {
				if (!validateNumberRange(entry.getValue(), 2020, 2030, 4)) {
					return false;
				}
				
			} else if ("hgt".equals(entry.getKey())) {
				if (!validateHeight(entry.getValue())) {
					return false;
				}
			} else if ("hcl".equals(entry.getKey())) {
				if(!validateHairColor(entry.getValue())) {
					return false;
				}
			} else if ("ecl".equals(entry.getKey())) {
				if(!EYE_COLORS.contains(entry.getValue())) {
					return false;
				}
			} else if ("pid".equals(entry.getKey())) {
				if (!validatePid(entry.getValue())) {
					return false;
				}
			}
		}
		return true;
	}
	
	protected Map<String, String> readPassport(BufferedReader reader) throws IOException {
		
		StringBuilder sb = new StringBuilder();
		String line = reader.readLine();
		while (line != null && !line.isEmpty()) {
			sb.append(" ").append(line);
			line = reader.readLine();
		}
		String passportCode = sb.toString().trim();
		if (passportCode.isEmpty()) {
			return null;
		}
		return  parsePassport(passportCode);
	}
	
	protected Map<String, String> parsePassport(String str) {
		String[] entries = str.split("\\s+");
		Map<String, String> retVal = new HashMap<>();
		
		for (String e : entries) {
			String[] kv = e.split(":");
			retVal.put(kv[0], kv[1]);
		}
		
		
		return retVal;
	}
	
	protected boolean isPassportValid(Map<String, String> passport) {
		
		Set<String> template = new HashSet<>(PASSPORT_FIELDS);
		template.removeAll(passport.keySet());
		// "cid" is an optional field
		return  (template.isEmpty() || template.size() == 1 && "cid".equals(template.iterator().next()));
	}
	
	protected boolean validateNumberRange(String str, int min, int max, int numOfDigits) {
		if (str == null || str.length() != numOfDigits) {
			return false;
		}
		try {
			int value = Integer.parseInt(str);
			return value >= min && value <= max;
			
		} catch (NumberFormatException  e) {
			return false;
		}
		
	}
	
	protected boolean validatePid(String str) {
		if (str == null || str.length() != 9) {
			return false;
		}
		
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c < '0' || c > '9') {
				return false;
			}
		}
		
		return true;
	}
	
	protected boolean validateHairColor(String str) {
		if (str == null || str.length() != 7) {
			return false;
		}
		
		if (!str.startsWith("#")) {
			return false;
		}
		
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if ((c < '0' && c  > '9') && (c < 'a' && c  > 'f')) {
				return false;
			}
		}
		
		return true;
	}
	
	protected boolean validateHeight(String str) {
		if (str == null || str.isEmpty()) {
			return false;
		}
		
		if (str.endsWith("in")) {
			int idx = str.indexOf("in");
			return (idx + 2 == str.length()) && validateNumberRange(str.substring(0, idx), 59, 76, 2);
			
		} else if (str.endsWith("cm")) {
			int idx = str.indexOf("cm");
			return (idx + 2 == str.length()) && validateNumberRange(str.substring(0, idx), 150, 193, 3);
		} else {
			return false;
		}
	}
	
	@Test
	public void testValidPassport() {
		assertTrue(isPassportValid(parsePassport("ecl:gry pid:860033327 eyr:2020 hcl:#fffffd\nbyr:1937 iyr:2017 cid:147 hgt:183cm")));
		
		assertFalse(isPassportValid(parsePassport("iyr:2013 ecl:amb cid:350 eyr:2023 pid:028048884\nhcl:#cfa07d byr:1929")));
		
		assertTrue(isPassportValid(parsePassport(
				"hcl:#ae17e1 iyr:2013\n"
				+ "eyr:2024\n"
				+"ecl:brn pid:760753108 byr:1931\n" 
				+"hgt:179cm")));
		
		assertFalse(isPassportValid(parsePassport("hcl:#cfa07d eyr:2025 pid:166559648\niyr:2011 ecl:brn hgt:59in")));
	}
	
	@Test
	public void testInvalidPasswordFields() {
		assertFalse(validatePassportValues(parsePassport("eyr:1972 cid:100\n"
				+ "hcl:#18171d ecl:amb hgt:170 pid:186cm iyr:2018 byr:1926")));
		
		assertFalse(validatePassportValues(parsePassport("iyr:2019\n"
				+ "hcl:#602927 eyr:1967 hgt:170cm\n"
				+ "ecl:grn pid:012533040 byr:1946")));
		
		assertFalse(validatePassportValues(parsePassport("hcl:dab227 iyr:2012\n"
				+ "ecl:brn hgt:182cm pid:021572410 eyr:2020 byr:1992 cid:277")));
		
		assertFalse(validatePassportValues(parsePassport("hgt:59cm ecl:zzz\n"
				+ "eyr:2038 hcl:74454a iyr:2023\n"
				+ "pid:3556412378 byr:2007")));
	}
	
	@Test
	public void testValidPasswordFields() {
		assertTrue(validatePassportValues(parsePassport("pid:087499704 hgt:74in ecl:grn iyr:2012 eyr:2030 byr:1980\n"
				+ "hcl:#623a2f")));
		
		assertTrue(validatePassportValues(parsePassport("eyr:2029 ecl:blu cid:129 byr:1989\n"
				+ "iyr:2014 pid:896056539 hcl:#a97842 hgt:165cm")));
		
		assertTrue(validatePassportValues(parsePassport("hcl:#888785\n"
				+ "pid:545766238 ecl:hzl\n"
				+ "hgt:164cm byr:2001 iyr:2015 cid:88 eyr:2022")));
		
		
		assertTrue(validatePassportValues(parsePassport("iyr:2010 hgt:158cm hcl:#b6652a ecl:blu byr:1944 eyr:2021 pid:093154719")));
	}
	
	@Test
	public void testMisc() {
		assertTrue(validateNumberRange("1000", 1000, 2000, 4));
		assertTrue(validateNumberRange("2000", 1000, 2000, 4));
		assertTrue(validateNumberRange("1500", 1000, 2000, 4));
		
		assertFalse(validateNumberRange("9", 1000, 2000, 4));
		assertFalse(validateNumberRange("2100", 1000, 2000, 4));
		assertFalse(validateNumberRange("a1000", 1000, 2000, 4));
		assertFalse(validateNumberRange("1000b", 1000, 2000, 4));
		
		assertTrue(validatePid("123456789"));
		assertTrue(validatePid("000000000"));
		
		assertFalse("too long", validatePid("1123456789"));
		assertFalse("too short", validatePid("12345678"));
		assertFalse("invalid char", validatePid("1234a5678"));
		
		
		assertTrue(validateHairColor("#123abf"));
		
		assertFalse(validateHairColor("#fffff"));
		assertFalse(validateHairColor("ffffff"));
		assertFalse(validateHairColor("#fffffff"));
		assertTrue(validateHairColor("#1x3abf"));
	}
	
	@Test
	public void testHeigth() {
		assertTrue(validateHeight("60in"));
		assertTrue(validateHeight("59in"));
		assertTrue(validateHeight("76in"));
		
		assertFalse(validateHeight("58in"));
		assertFalse(validateHeight("77in"));
		assertFalse(validateHeight("60in77in"));
		assertFalse(validateHeight("60"));
		
		assertTrue(validateHeight("160cm"));
		assertTrue(validateHeight("150cm"));
		assertTrue(validateHeight("193cm"));
		assertTrue(validateHeight("160cm"));
		
		assertFalse(validateHeight("149cm"));
		assertFalse(validateHeight("194cm"));
		assertFalse(validateHeight("19a3cm"));
		
	}
	

}
