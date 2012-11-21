/**
 * 
 */
package com.propn.golf.dao.trans;

/**
 * @author Administrator
 *
 */
public class ConnUtilsTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        ConnUtils.setTransStatus("13");
        System.out.println(ConnUtils.getLastTrans());
        System.out.println(ConnUtils.getLastTrans());
        System.out.println(ConnUtils.getLastTrans());
        System.out.println(ConnUtils.getLastTrans());
        System.out.println(ConnUtils.getLastTrans());
    }

}
