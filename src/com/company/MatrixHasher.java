package com.company;

import java.util.Random;

public class MatrixHasher implements Hasher{
    private final int tableSizeExponent;
    private final int keyBitsNum;
    private byte[][] hashMatrix;

    public MatrixHasher(int tableSizeExponent, int keyBitsNum) {
        this.tableSizeExponent = tableSizeExponent;
        this.keyBitsNum = keyBitsNum;
        this.hashMatrix = new byte[tableSizeExponent][keyBitsNum];
    }


    /**
     * Method to convert a number to a bit vector.
     *
     * @param num <code>int</code> to convert to bit vector of length equal to bits number provided.
     * @return <code>byte[]</code> representing the bit vector.
     */
    private byte[] convertToBits(int num){
        byte[] res = new byte[keyBitsNum];
        if(num < 0) {
            num += 1<<(keyBitsNum-1);
            res[keyBitsNum - 1] = 1;
        }
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
     * used to convert a bit vector to decimal <code>int</code>.
     *
     * @param bitVector bit vector represented as <code>byte[]</code>
     * @return decimal <code>int</code> equivalent of provided bit vector.
     **/
    private int vectorToDecimal(byte[] bitVector){
        int res = 0;
        for(int i = 0; i < tableSizeExponent; i++){
            res += bitVector[i] * (1 << i);
        }
        return res;
    }

    /**
     * Method to generate the hash function from a universal hash family.
     */
    @Override
    public void generateFunction() {
        for(int i = 0; i < tableSizeExponent; i++){
            for(int j = 0; j < keyBitsNum; j++){
                hashMatrix[i][j] = (byte) new Random().nextInt(2);
            }
        }
    }

    /**
     * Method to hash an <code>int</code> key.
     *
     * @param key <code>int</code> key to hash.
     * @return hashed value of provided key.
     */
    @Override
    public int hash(int key) {
        return vectorToDecimal(computeHashVector(convertToBits(key)));
    }
}
