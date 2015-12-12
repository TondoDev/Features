package org.tondo.dayseven;

public class TransactionManager {

	private static int trId = 1;
	
	
	public static int getTxId() {
		return trId;
	}
	
	public static void newTx() {
		trId++;
	}
}
