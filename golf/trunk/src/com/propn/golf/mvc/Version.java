package com.propn.golf.mvc;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
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

    @GET
    @Path("/get")
    public String getVersion() {
        String version = "1.0";
        return version;
    }

    @GET
    @Path("/get/student")
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
    }

    // /get2/121?aaa=21
    @GET
    @Path("/get2/{pathv}")
    public String getVersion2(ServletRequest request, ServletResponse response, ServletInputStream inputStream,
            @PathParam(value = "pathv")
            String version, @QueryParam(value = "aaa")
            String p, @HeaderParam(value = "t")
            String t, @CookieParam(value = "name")
            String name) {
        System.out.println(version);
        System.out.println(p);
        System.out.println(t);
        System.out.println(name);
        return version;
    }
}
