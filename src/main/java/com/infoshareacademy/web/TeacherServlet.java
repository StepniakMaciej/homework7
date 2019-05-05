package com.infoshareacademy.web;

import com.infoshareacademy.dao.CourseDao;
import com.infoshareacademy.dao.TeacherDao;
import com.infoshareacademy.model.Course;
import com.infoshareacademy.model.CourseSummary;
import com.infoshareacademy.model.Teacher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Transactional
@WebServlet(urlPatterns = "/teacher")
public class TeacherServlet extends HttpServlet {

    private Logger LOG = LoggerFactory.getLogger(TeacherServlet.class);

    @Inject
    private TeacherDao teacherDao;

    @Inject
    private CourseDao courseDao;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String action = req.getParameter("action");
        LOG.info("Requested action: {}", action);
        if (action == null || action.isEmpty()) {
            resp.getWriter().write("Empty action parameter.");
            return;
        }

        if (action.equals("findAll")) {
            findAll(req, resp);
        } else if (action.equals("add")) {
            addTeacher(req, resp);
        } else if (action.equals("delete")) {
            deleteTeacher(req, resp);
        } else if (action.equals("update")) {
            updateTeacher(req, resp);
        } else if (action.equals("bornAfter")) {
            summary(req, resp);
        } else {
            resp.getWriter().write("Unknown action.");
        }
    }

    private void summary(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final List<CourseSummary> result = courseDao.getCoursesSummary();
        LOG.info("Found {} objects", result.size());
        for (CourseSummary p : result) {
            resp.getWriter().write(p.toString() + "\n");
        }
    }

    private void updateTeacher(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        final Long pesel = Long.parseLong(req.getParameter("pesel"));
        LOG.info("Updating teacher with id = {}", pesel);

        final Teacher existingTeacher = teacherDao.findById(pesel);
        if (existingTeacher == null) {
            LOG.info("No teacher found with pesel = {}, nothing to be updated", pesel);
        } else {
            existingTeacher.setName(req.getParameter("name"));
            existingTeacher.setSurname(req.getParameter("surname"));

            String courseName = req.getParameter("courseName");
            Course course = courseDao.findByName(courseName);
            existingTeacher.getCourses().add(course);

            teacherDao.update(existingTeacher);
            LOG.info("Teacher object updated: {}", existingTeacher);
        }

        findAll(req, resp);
    }

    private void deleteTeacher(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final Long pesel = Long.parseLong(req.getParameter("pesel"));
        LOG.info("Removing teacher with id = {}", pesel);

        teacherDao.delete(pesel);

        findAll(req, resp);
    }

    private void addTeacher(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final Teacher teacher = new Teacher();
        teacher.setPesel(Long.parseLong(req.getParameter("pesel")));
        teacher.setName(req.getParameter("name"));
        teacher.setSurname(req.getParameter("surname"));

        String courseName = req.getParameter("courseName");
        Course course = courseDao.findByName(courseName);
        teacher.setCourses(Arrays.asList(course));

        teacherDao.save(teacher);
        LOG.info("Saved a new teacher object: {}", teacher);

        findAll(req, resp);
    }

    private void findAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final List<Teacher> result = teacherDao.findAll();
        LOG.info("Found {} objects", result.size());
        for (Teacher teacher : result) {
            resp.getWriter().write(teacher.toString() + "\n");
        }
    }
}


