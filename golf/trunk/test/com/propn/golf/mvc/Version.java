package com.propn.golf.mvc;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        // Connection conn = ConnUtils.getConn();
        // Statement stat = conn.createStatement();
        // stat.execute("  create   table   PERSON(PERSON_ID varchar(10), PERSON_NAME varchar(64),AGE number)");
        // stat.close();
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
    }

    // http://localhost:8080/golf/version/get2/12?aaa=23&aaa=24
    @GET
    @Path("/get2/{pathv}")
    public String getVersion2(HttpServletRequest request, HttpServletResponse response,
            ServletInputStream ServletInputStream, @PathParam(value = "pathv")
            String version, @QueryParam(value = "aaa")
            String p, @HeaderParam(value = "accept-language")
            String t, @CookieParam(value = "ys-healthcheck-summary-size-delta")
            String name) {
        /*
         * System.out.println(version); System.out.println(p); System.out.println(t); System.out.println(name);
         */
        return version;
    }

    @GET
    @Path("/getFile")
    @Produces({ MediaType.APPLICATION_OCTET_STREAM })
    public File getFile() throws Exception {
        File f = new File("E:/DOC/职称评定/《员工职称晋升申请表》-徐雷.xls");
        if (f.exists()) {
            return f;
        } else {
            throw new Exception("文件不存在!");
        }
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public View login(HttpServletRequest request, HttpServletResponse response, @FormParam(value = "personId")
    String personId, @FormParam(value = "personName")
    String personName) {
        response.setHeader("userId", personId);
        View view = new View(View.jsp, "/index.jsp", null);
        return view;
    }

    @POST
    @Path("/upload")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    @Produces({ MediaType.APPLICATION_JSON })
    public View uploadFile(FileInfo[] files, @FormParam(value = "text")
    String text) throws IOException {
        System.out.println(text);
        for (FileInfo fileInfo : files) {
            System.out.println(fileInfo.getFileName());
            fileInfo.WriteTO("C:\\" + fileInfo.getFileName());
        }
        return new View(View.jsp, "/index.jsp", null);
    }

    @GET
    @Path("/toLogin")
    @Produces({ MediaType.TEXT_HTML })
    public View toLogin() throws Exception {
        View view = new View(View.jsp, "/login/login.jsp", null);
        return view;
    }
}
