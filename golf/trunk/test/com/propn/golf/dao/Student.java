package com.propn.golf.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "Student")
@XmlRootElement(name = "Student")
public class Student extends Person {
    @Column(name = "grade")
    int grade;
    @Column(name = "major")
    String major;
    Person Counselor;
}
