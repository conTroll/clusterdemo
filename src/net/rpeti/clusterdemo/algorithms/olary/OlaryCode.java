package net.rpeti.clusterdemo.algorithms.olary;

/**
 * Generates a k-length Olary Code.
 * (k is defined in the constructor)
 * 
 * @author rpeti
 */
public class OlaryCode {
	
	private boolean[][] code;
	private int length;
	
	public OlaryCode(int length){
		code = generateOlaryCodes(length);
		this.length = length;
	}
	
	/**
	 * Returns the given length Olary code, with each sequence
	 * represented as boolean arrays. For. eg. the sequence
	 * 10110 will be the array {true, false, true, true, false}
	 * The 2nd digit of the 3rd sequence of the 4-length Olary
	 * code can be accessed like this: generateOlaryCodes(4)[2][1]
	 * @param length the length of the Olary code. A k-length
	 * Olary code will have k+1 sequences.
	 */
	private boolean[][] generateOlaryCodes(int length){
		if(length < 1) 
			throw new IllegalArgumentException(
					"Value of parameter 'length' shall be at least 1.");
		
		boolean[][] result = new boolean[length+1][];
		
		//generate the first sequence
		result[0] = new boolean[length];
		for(int i = 0; i < length; i++){
			result[0][i] = i % 2 == 0;
		}
		
		//generate the remaining sequences
		for (int i = 1; i < length + 1; i++){
			result[i] = new boolean[length];
			for (int j = 0; j < length; j++){
				boolean valueInPrevious = result[i - 1][j];
				result[i][j] = (j == length - i) ? 
						!valueInPrevious : valueInPrevious;
			}
		}
		
		return result;
	}
	
	/**
	 * Gets the given sequence from the constructed Olary code, 
	 * where the sequence is representated as an array of booleans.
	 * For eg., de sequence 10110 is representated as 
	 * {true, false, true, true, false}.
	 */
	public boolean[] getSequence(int sequenceNo){
		if(sequenceNo < 0 || sequenceNo > length)
			throw new IllegalArgumentException(
					"Sequences are indexed with the interval [0..length(" + length + ")]");
		return code[sequenceNo];
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length + 1; i++){
			for(int j = 0; j < length; j++){
				sb.append(code[i][j] ? "1" : "0");
			}
			sb.append(System.getProperty("line.separator"));
		}
		return sb.toString();
	}
	
	

}
