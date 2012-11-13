/**
 * 
 */
package com.propn.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import com.propn.dao.sql.Po;

/**
 * @author Thunder.Hsu
 * 
 */
@Entity
@Table(name = "Person")
@XmlRootElement(name = "Person")
public class Person extends Po {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    @NotNull(message = "may not be null")
    private String personId;

    @Column(name = "person_Name", nullable = false)
    private String personName;

    @Column(name = "age")
    @Min(value = 18, message = "You have to be 18 to drive a car")
    private int age;

}
