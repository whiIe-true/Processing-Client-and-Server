package de.whiletrue.serverandclientapi.util;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;

public class ByteSender{

	//All bits
	private boolean bits[];
	//Index
	private int index;

	/**
	 * @param length the allocated length for the bits
	 */
	public ByteSender(int length){
		//Creates the bits array
		this.bits=new boolean[length % 8 == 0?length:((length / 7 + 1) * 7)];
	}

	/*
	 * Interprets the bits from the bytes
	 */
	public ByteSender(byte[] bytes){
		//Creates the bit array
		this.bits=this.toBooleanArray(bytes);

		//Resets the index
		this.index=0;
	}

	/**
	 * @param allocates the length depending on the given data types
	 * 
	 * b = byte (1 byte)
	 * s = short (2 byte),
	 * i = int (4 byte),
	 * l = long (8 byte),
	 * f = float (4 byte),
	 * d = double (8 byte)
	 */
	public ByteSender(String allocator){		
		//Calls the constructor with the length
		this(
			//Calculates the length of bytes used
			allocator.toLowerCase().chars().map(i->{
			//Switches the datatypes
			switch(i) {
				case 'b':
					return 1;
				case 's':
					return 2;
				case 'i': case 'f':
					return 4;
				case 'l': case 'd':
					return 8;
				default:
					return 0;
			}
		}).findAny().getAsInt());
	}

	/**
	 * Converts a byte array to a boolean array
	 *
	 * Method by
	 * https://stackoverflow.com/questions/26944282/java-boolean-to-byte-and-back/26944869#26944869?newreg=be3c9cd4ec3d44e98cf206cd52505734
	 */
	private boolean[] toBooleanArray(byte... bytes){
		BitSet bits=BitSet.valueOf(bytes);
		boolean[] bools=new boolean[bytes.length * 8];
		for(int i=bits.nextSetBit(0); i != -1; i=bits.nextSetBit(i + 1))
			bools[i]=true;
		
		return bools;
	}

	/**
	 * Converts a byte array to a boolean array
	 * 
	 * Method by
	 * https://stackoverflow.com/questions/26944282/java-boolean-to-byte-and-back/26944869#26944869?newreg=be3c9cd4ec3d44e98cf206cd52505734
	 */
	private byte[] toByteArray(boolean[] bools){
		BitSet bits=new BitSet(bools.length);
		for(int i=0; i < bools.length; i++)
			if(bools[i])
				bits.set(i);

		byte[] bytes=bits.toByteArray();

		if(bytes.length * 8 >= bools.length)
			return bytes;
			
		return Arrays.copyOf(bytes,bools.length / 8 + (bools.length % 8 == 0?0:1));
	}
	
	/*
	 * Returns a byte array with the given amount of requested bytes
	 * */
	private byte[] getBytes(int amount) {
		byte[] bytes = new byte[amount];
		for(int i=0;i<amount;i++)
			bytes[i]=this.getByte();
		return bytes;
	}

	public void addByte(byte b){
		for(boolean bo : this.toBooleanArray(b))
			this.bits[this.index++]=bo;
	}

	
	public byte getByte(){
		byte b=this.toByteArray(Arrays.copyOfRange(this.bits,this.index,this.index + 8))[0];
		this.index+=8;
		return b;
	}
	
	public void addShort(short i) {
		for(byte b:ByteBuffer.allocate(2).putShort(i).array())
			this.addByte(b);
	}
	
	public short getShort() {
		return ByteBuffer.wrap(this.getBytes(2)).getShort();
	}
	
	public void addInt(int i) {
		for(byte b:ByteBuffer.allocate(4).putInt(i).array())
			this.addByte(b);
	}
	
	public int getInt() {
		return ByteBuffer.wrap(this.getBytes(4)).getInt();
	}

	public boolean getBoolean(){
		return this.bits[this.index++];
	}

	public void addBoolean(boolean b){
		this.bits[this.index++]=b;
	}

//	//String thats length fits into a short (32,767)
//	public void addShortString(String s) {
//		this.addShort((short)s.length());
//		byte[] raw = s.getBytes();
//		for(short i=0;i<(short)s.length();i++)
//			this.addByte(raw[i]);
//	}
//	
//	//Gets a string thats length fits into a short
//	public String getShortString() {
//		short len = this.getShort();
//		byte[] raw = new byte[len];
//		for(short i=0;i<len;i++)
//			raw[i]=this.getByte();
//		return new String(raw);
//	}

	public boolean[] getBits(){
		return this.bits;
	}

	/*
	 * Returns the allocated bits as bytes
	 */
	public byte[] toBytes(){
		return this.toByteArray(this.bits);
	}

}
