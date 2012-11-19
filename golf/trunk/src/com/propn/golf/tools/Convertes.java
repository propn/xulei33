/**
 * 
 */
package com.propn.golf.tools;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propn.golf.mvc.GolfFilter;

/**
 * @author Thunder.Hsu
 * 
 */
public class Convertes {

    private static final Logger log = LoggerFactory.getLogger(GolfFilter.class);

    public static Object convert(Object obj, Class distClass) {
        if (obj.getClass().equals(distClass)) {
            return obj;
        }
        log.debug("Convert Object Type: " + obj.getClass().getName() + " " + distClass.getName());
        if (obj.getClass().equals(BigDecimal.class) && distClass.equals(int.class)) {
            return ((BigDecimal) obj).intValue();
        }
        return obj;
    }

    // public static Object convert(Object obj, Class srcClass, BigDecimal.class) {
    // if (distClass.equals(BigDecimal.class)) {
    //
    // }
    // return distClass;
    // }

    /**
     * @param args
     */
    public static void main(String[] args) {

    }
}
