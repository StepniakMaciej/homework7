package com.infoshareacademy.rest;

import com.infoshareacademy.dao.ComputerDao;
import com.infoshareacademy.dao.StudentDao;
import com.infoshareacademy.model.Computer;
import com.infoshareacademy.model.Student;

import javax.inject.Inject;
import javax.websocket.server.PathParam;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/")
public class StudentAndComputerService {

    @Inject
    private StudentDao studentDao;

    @Inject
    ComputerDao computerDao;

    @GET
    @Path("/students")
    @Produces(MediaType.TEXT_PLAIN)
    public Response allStudents() {
        List<Student> allStudents = new ArrayList<>(studentDao.findAll());

        if (allStudents.isEmpty()) {
            return Response.noContent().build();
        }
        return Response.ok(allStudents).build();
    }


    @GET
    @Path("/students/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response studentByName(@PathParam("name") String name) {
        Student student = (Student) studentDao.findByName(name);

        if (student == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(student).build();
    }

    @POST
    @Path("/computers")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addComputer(Computer computer) {
        computerDao.save(computer);
        List<Computer> allComputer = new ArrayList<>(computerDao.findAll());

        return Response.ok(allComputer).build();
    }

    @DELETE
    @Path("/computers/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteComputer(@QueryParam("id") Long id) {

        if (computerDao.findById(id) == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            computerDao.delete(id);
            {
                return Response.status(Response.Status.OK).build();
            }
        }
    }
}
