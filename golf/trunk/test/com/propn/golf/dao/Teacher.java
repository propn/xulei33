/**
 * 
 */
package com.propn.golf.dao;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Administrator
 * 
 */
@XmlRootElement(name = "Teacher")
@XmlAccessorType(XmlAccessType.NONE)
public class Teacher extends Person {
    @XmlAttribute
    int hight;
}
