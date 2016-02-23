package cn.gridx.scala.lang.generics.TypeErasure;

import java.util.Vector;

/**
 * Created by tao on 11/23/15.
 */
public class JavaExamples {
    public static void main(String[] args) {
        GenericArray a = new GenericArray("Hello");
        String[] A = (String[])a.createGenericArray();
        for (int i=0; i<A.length; i++)
            A[i] = String.valueOf(i);
        for (int i=0; i<A.length; i++)
            System.out.println(A[i]);
    }

    public static void castingWithArrays() {
        String[] vector = new String[3];
        vector[0] = "0"; vector[1] = "1"; vector[2] = "2";

        Object[] objects = vector;

        /**
         * 编译正常，但是运行异常
         *   Exception in thread "main" java.lang.ArrayStoreException: java.lang.Object
         */
        objects[0] = new Object();
    }


    public static void castingWithGenericTypes() {
        Vector<String> vector   = new Vector<String>();
        //Vector<Object> objects  = (Vector<Object>)vector;  //  无法转换： inconvertible types

    }


    public static class GenericArray<T> {
        private Class<T> tClz;

        public GenericArray(T t) {
            tClz = (Class<T>)t.getClass();
        }

        public T[] createGenericArray() {
            // `java.lang.reflect.Array.newInstance`返回的类型是`Object[]`
            return (T[])java.lang.reflect.Array.newInstance(tClz, 10);
        }
    }



}
