package com.paic.sync.feature.w2v;

import java.io.*;
import java.util.*;

/**
 * description: Word2VEC
 * date: 2020/11/12 9:43 上午
 * author: gallup
 * version: 1.0
 */
public class Word2VEC {
//    InputStream inputStream = this.getClass().getResourceAsStream("/model/word2vec");
    public static void main(String[] args) throws IOException {


        Word2VEC vec = new Word2VEC();
//        vec.loadJavaModel("model/word2vec/wiki.zh.text.vector");
        vec.loadGoogleModel("model/word2vec/wiki.zh.text.vector");

         System.out.println("中国" + "\t" +
         Arrays.toString(vec.getWordVector("中国")));
        // ;
        // System.out.println("毛泽东" + "\t" +
        // Arrays.toString(vec.getWordVector("毛泽东")));
        // ;
        // System.out.println("足球" + "\t" +
        // Arrays.toString(vec.getWordVector("足球")));

        // Word2VEC vec2 = new Word2VEC();
        // vec2.loadGoogleModel("library/vectors.bin") ;
        //
        //
        String str = "毛泽东";
        long start = System.currentTimeMillis();

        System.out.println(vec.getWordVector(str));

        System.out.println(System.currentTimeMillis() - start);

        System.out.println(System.currentTimeMillis() - start);

    }

    private HashMap<String, float[]> wordMap = new HashMap<String, float[]>();
    private int words;
    private int size;
    private int topNSize = 40;

    /**
     * 加载模型
     *
     * @param path
     *            模型的路径
     * @throws IOException
     */
    public void loadGoogleModel(String path) throws IOException {
        InputStream instream = this.getClass().getClassLoader().getResourceAsStream(path);
        DataInputStream dis = new DataInputStream(instream);
        double len = 0;
        float vector = 0;
        try {

            // //读取词数
            words = Integer.parseInt(readString(dis));
            // //大小
            size = Integer.parseInt(readString(dis));
            String word;
            float[] vectors = null;
            for (int i = 0; i < words; i++) {
                word = readString(dis);


                vectors = new float[size];
                len = 0;
                for (int j = 0; j < size; j++) {
                    vector = readFloat(dis);
                    len += vector * vector;
                    vectors[j] = (float) vector;
                    if (i == 0){
                        System.out.print(vector);
                        System.out.print('\n');
                    }
                }
                if(i==0){
                    System.out.print(word+'\n');

                }
//                len = Math.sqrt(len);
//
//                for (int j = 0; j < size; j++) {
//                    vectors[j] /= len;
//                }

                wordMap.put(word, vectors);
                dis.read();
            }
        } finally {
            dis.close();
        }
    }

    /**
     * 加载模型
     *
     * @param path
     *            模型的路径
     * @throws IOException
     */
    public void loadJavaModel(String path) throws IOException {
        InputStream instream = this.getClass().getClassLoader().getResourceAsStream(path);
        DataInputStream dis = new DataInputStream(instream);
//        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(path)));
        if ( dis != null ) {
            words = dis.readInt();
            size = dis.readInt();

            float vector = 0;

            String key = null;
            float[] value = null;
            for (int i = 0; i < words; i++) {
                double len = 0;
                try {
                    key = dis.readUTF();
                    value = new float[size];
                    for (int j = 0; j < size; j++) {
                        vector = dis.readFloat();
                        len += vector * vector;
                        value[j] = vector;
                    }

                    len = Math.sqrt(len);

                    for (int j = 0; j < size; j++) {
                        value[j] /= len;
                    }
                    wordMap.put(key, value);
                } catch (EOFException e) {
                    continue;
                }
            }

        }
    }

    private static final int MAX_SIZE = 50;





    private float[] sum(float[] center, float[] fs) {
        // TODO Auto-generated method stub

        if (center == null && fs == null) {
            return null;
        }

        if (fs == null) {
            return center;
        }

        if (center == null) {
            return fs;
        }

        for (int i = 0; i < fs.length; i++) {
            center[i] += fs[i];
        }

        return center;
    }

    /**
     * 得到词向量
     *
     * @param word
     * @return
     */
    public float[] getWordVector(String word) {
        return wordMap.get(word);
    }

    public static float readFloat(InputStream is) throws IOException {
        byte[] bytes = new byte[4];
        is.read(bytes);
        return getFloat(bytes);
    }

    /**
     * 读取一个float
     *
     * @param b
     * @return
     */
    public static float getFloat(byte[] b) {
        int accum = 0;
        accum = accum | (b[0] & 0xff) << 0;
        accum = accum | (b[1] & 0xff) << 8;
        accum = accum | (b[2] & 0xff) << 16;
        accum = accum | (b[3] & 0xff) << 24;
        return Float.intBitsToFloat(accum);
    }

    /**
     * 读取一个字符串
     *
     * @param dis
     * @return
     * @throws IOException
     */
    private static String readString(DataInputStream dis) throws IOException {
        // TODO Auto-generated method stub
        byte[] bytes = new byte[MAX_SIZE];
        byte b = dis.readByte();
        int i = -1;
        StringBuilder sb = new StringBuilder();
        while (b != 32 && b != 10) {
            i++;
            bytes[i] = b;
            b = dis.readByte();
            if (i == 49) {
                sb.append(new String(bytes));
                i = -1;
                bytes = new byte[MAX_SIZE];
            }
        }
        sb.append(new String(bytes, 0, i + 1));
        return sb.toString();
    }

    public int getTopNSize() {
        return topNSize;
    }

    public void setTopNSize(int topNSize) {
        this.topNSize = topNSize;
    }

    public HashMap<String, float[]> getWordMap() {
        return wordMap;
    }

    public int getWords() {
        return words;
    }

    public int getSize() {
        return size;
    }


}
