package com.propn.golf.ioc;

import static org.junit.Assert.*;

import org.junit.Test;

import com.propn.golf.dao.Person;
import com.propn.golf.dao.Student;
import com.propn.golf.dao.sql.Po;

public class BeanUtilsTest {

    @Test
    public void testRegistBeanStringArray() {
        fail("Not yet implemented");
    }

    @Test
    public void testRegistBeanSetOfClassOfQ() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetBean() {
        Class<? extends Person> a = Student.class.asSubclass(Person.class);
        Ipab.class.asSubclass(Ipa.class);
        Ipab.class.asSubclass(Ip.class);
        Ipa.class.asSubclass(Ip.class);
        
        
        System.out.println(Ipa.class.isAssignableFrom(Ipab.class));
        System.out.println(Ipa.class.isAssignableFrom(Ipab.class));
        System.out.println(Ip.class.isAssignableFrom(Ipab.class));
        
        
    }

}
