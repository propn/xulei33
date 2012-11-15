/**
 * 
 */
package com.propn.golf.dao.trans;

/**
 * @author Administrator
 * 
 */
public abstract class Trans {
    /* 默认 */
    public static int REQUIRED = 0;

    /* 新建事务 */
    public static int REQUIRES_NEW = 1;

    /* 新建隔离事务 */
    public static int REQUIRED_NEW_ALONE = 0;

}
