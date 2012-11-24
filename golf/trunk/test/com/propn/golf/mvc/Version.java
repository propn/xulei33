package com.propn.golf.mvc;

import java.util.List;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.propn.golf.dao.Person;
import com.propn.golf.dao.Student;

@Path("/version")
@Consumes({ MediaType.TEXT_PLAIN, MediaType.TEXT_HTML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.TEXT_HTML })
public class Version {

    @POST
    @Path("/addPerson")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Person addPerson(@FormParam(value = "personId")
    String personId, @FormParam(value = "personName")
    String personName, @FormParam(value = "age")
    int age, Person person) throws Exception {
//        Connection conn = ConnUtils.getConn();
//        Statement stat = conn.createStatement();
//        stat.execute("  create   table   PERSON(PERSON_ID varchar(10), PERSON_NAME varchar(64),AGE number)");
//        stat.close();
        // System.out.println(person.toJson());
        Person p = new Person();
        p.setAge(age);
        p.setPersonId(personId);
        p.setPersonName(personName);
        p.save();
        return p;
    }

    @GET
    @Path("/getPerson")
    public List<Person> getPerson() throws Exception {
        // System.out.println(person.toJson());
        Person p = new Person();
        p.setPersonId("1");
        List<Person> ps = p.qryList();
        return ps;
    }

    @GET
    @Path("/get")
    public String getVersion() {
        String version = "1.0";
        return version;
    }

    @GET
    @Path("/get/student")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Student getStudent() throws Exception {
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
        return st;
        // throw new Exception("22");
    }

    // http://localhost:8080/golf/version/get2/12?aaa=23&aaa=24
    @GET
    @Path("/get2/{pathv}")
    public String getVersion2(ServletRequest request, ServletResponse response, ServletInputStream ServletInputStream,
            @PathParam(value = "pathv")
            String version, @QueryParam(value = "aaa")
            String p, @HeaderParam(value = "accept-language")
            String t, @CookieParam(value = "ys-healthcheck-summary-size-delta")
            String name) {
        /*
         * System.out.println(version); System.out.println(p); System.out.println(t); System.out.println(name);
         */
        return version;
    }
}
