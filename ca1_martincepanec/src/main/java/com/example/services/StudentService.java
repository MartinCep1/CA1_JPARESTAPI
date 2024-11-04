package com.example.services;

import com.example.entities.Student;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/students")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StudentService {

    @PersistenceContext
    private EntityManager entityManager;

    @GET
    public Response getAllStudents() {
        List<Student> students = entityManager.createQuery("SELECT s FROM Student s", Student.class).getResultList();
        if (students.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No students found in the database.")
                    .build();
        }
        return Response.ok(students).build();
    }

    @POST
    @Transactional
    public Response addStudent(Student student) {
        entityManager.persist(student);
        return Response.status(Response.Status.CREATED)
                .entity(student)
                .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateStudent(@PathParam("id") String id, Student updatedStudent) {
        Student existingStudent = entityManager.find(Student.class, id);
        if (existingStudent == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Student not found with ID: " + id)
                    .build();
        }
        existingStudent.setName(updatedStudent.getName());
        existingStudent.setStudentNumber(updatedStudent.getStudentNumber());
        existingStudent.setPhoneNumber(updatedStudent.getPhoneNumber());
        existingStudent.setAddress(updatedStudent.getAddress());
        existingStudent.setProgrammeCode(updatedStudent.getProgrammeCode());
        entityManager.merge(existingStudent);
        return Response.ok(existingStudent).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteStudent(@PathParam("id") String id) {
        Student student = entityManager.find(Student.class, id);
        if (student == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Student not found with ID: " + id)
                    .build();
        }
        entityManager.remove(student);
        return Response.noContent().build();
    }
}
