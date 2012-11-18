package com.propn.golf.mvc;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.propn.golf.dao.Person;
import com.propn.golf.dao.Student;
import com.propn.golf.dao.Teacher;
import com.propn.golf.tools.JsonUtils;
import com.propn.golf.tools.XmlUtils;

public class XmlViewTest {

    public static void main(String[] args) throws Exception {
        try {

            Person teacher = new Person();
            teacher.set("personId", "03023001");
            teacher.set("personName", "常继科");
            teacher.set("age", 28);

            Student st = new Student();
            st.set("grade", 2003);
            st.set("major", "computer");
            st.set("Counselor", teacher);
            st.set("personId", "03023152");
            st.set("personName", "徐雷");
            st.set("age", 18);
//            marshaller.marshal(st, System.out);
            System.out.println(XmlUtils.toXml(st));
            
            Teacher t=new Teacher();
            t.set("personId", "03023001");
            t.set("personName", "333继科");
            t.set("age", 43);
            t.set("hight", 12);
            
            System.out.println(XmlUtils.toXml(t));
            
            System.out.println(JsonUtils.toJson(st));
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
