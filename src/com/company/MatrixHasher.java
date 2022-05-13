package com.company;

import java.util.List;

public class MatrixHasher implements Hasher{
    private final int tableSizeExponent;
    private final int keyBitsNum;
    private byte[][] hashMatrix;

    public MatrixHasher(int tableSizeExponent, int keyBitsNum) {
        this.tableSizeExponent = tableSizeExponent;
        this.keyBitsNum = keyBitsNum;
    }


    /**
     * Method to convert a number to a bit vector.
     *
     * @param num <code>long</code> to convert to bit vector of length equal to bits number provided.
     * @return <code>byte[]</code> representing the bit vector.
     */
    private byte[] convertToBits(long num){
        byte[] res = new byte[keyBitsNum];
        int i = 0;
        while(num != 0 && i < this.keyBitsNum){
            res[i++] = (byte) (num % 2);
            num /= 2;
        }
        return res;
    }

    /**
     * used to evaluate hx by adding columns from h whose positions in vector x is one
     * ex:
     *      x=[1010] means this function will add 1st and 3rd column in H
     * @param bitVector bit vector representation of a number provided as <code>byte[]</code>.
     * @return bit vector representation of hash index.
     */
    private byte[] computeHashVector(byte[] bitVector){
        byte[] res = new byte[tableSizeExponent];
        for(int i = 0; i < tableSizeExponent; i++){
            for(int j = 0; j < keyBitsNum; j++){
                res[i] ^= hashMatrix[i][j] * bitVector[j];
            }
        }
        return res;
    }

    /**
     * used to convert a bit vector to decimal <code>long</code>.
     *
     * @param bitVector bit vector represented as <code>byte[]</code>
     * @return decimal <code>long</code> equivalent of provided bit vector.
     **/
    private long vectorToDecimal(byte[] bitVector){
        long res = 0;
        for(int i = 0; i < tableSizeExponent; i++){
            res += (long) bitVector[i] * (1L << i);
        }
        return res;
    }

    @Override
    public void generateFunction() {
        for(int i = 0; i < tableSizeExponent; i++){
            for(int j = 0; j < keyBitsNum; j++){
                hashMatrix[i][j] = (byte) Math.round(Math.random());
            }
        }
    }

    @Override
    public long hash(long key) {
        return vectorToDecimal(computeHashVector(convertToBits(key)));
    }
}