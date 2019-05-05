package com.infoshareacademy.web;

import com.infoshareacademy.dao.CourseDao;
import com.infoshareacademy.dao.TeacherDao;
import com.infoshareacademy.model.Course;
import com.infoshareacademy.model.CourseSummary;
import com.infoshareacademy.model.Teacher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

@WebServlet("/teacher")
@Transactional
public class TeacherServlet extends HttpServlet {

    private Logger LOG = LoggerFactory.getLogger(TeacherServlet.class);

    @Inject
    private TeacherDao teacherDao;

    @Inject
    private CourseDao courseDao;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        final String action = req.getParameter("action");
        LOG.info("Requested action: {}", action);
        if (action == null || action.isEmpty()) {
            resp.getWriter().write("Empty action parameter.");
            return;
        }

        if (action.equals("save")) {
            saveTeacher(req, resp);
        } else if (action.equals("update")) {
            updateTeacher(req, resp);
        } else if (action.equals("delete")) {
            deleteTeacher(req, resp);
        } else if (action.equals("findById")) {
            findByPesel(req, resp);
        } else if (action.equals("findAll")) {
            findAll(req, resp);


        } else {
            resp.getWriter().write("Unknown action.");
        }
    }

    private void updateTeacher(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        final String pesel = req.getParameter("pesel");
        LOG.info("Updating Teacher with pesel = {}", pesel);

        final Teacher existingTeacher = teacherDao.findByPesel(pesel);
        if (existingTeacher == null) {
            LOG.info("No Teacher found for pesel = {}, nothing to be updated", pesel);
        } else {
            existingTeacher.setPesel(req.getParameter("pesel"));
            existingTeacher.setName(req.getParameter("name"));
            existingTeacher.setSurname(req.getParameter("surname"));

            String courseName = req.getParameter("courseName");
            Course course = courseDao.findByName(courseName);
            existingTeacher.getCourses().add(course);

            teacherDao.update(existingTeacher);
            LOG.info("Teacher object updated: {}", existingTeacher);
        }

        // Return all persisted objects
        findAll(req, resp);
    }

    private void saveTeacher(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        final Teacher t = new Teacher();
        t.setPesel(req.getParameter("pesel"));
        t.setName(req.getParameter("name"));
        t.setSurname(req.getParameter("surname"));

        String courseName = req.getParameter("courseName");
        Course course = courseDao.findByName(courseName);
        t.setCourses(Arrays.asList(course));

        teacherDao.save(t);
        LOG.info("Saved a new Teacher object: {}", t);

        // Return all persisted objects
        findAll(req, resp);
    }

    private void deleteTeacher(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final String pesel = req.getParameter("pesel");
        LOG.info("Removing Teacher with id = {}", pesel);

        teacherDao.delete(pesel);

        // Return all persisted objects
        findAll(req, resp);
    }

    private void findAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final List<Teacher> result = teacherDao.findAll();
        LOG.info("Found {} objects", result.size());
        for (Teacher t : result) {
            resp.getWriter().write(t.toString() + "\n");
        }
    }

    private void findByPesel(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final String pesel = req.getParameter("pesel");
        final List<Teacher> result = Collections.singletonList(teacherDao.findByPesel(pesel));
        LOG.info("Found {} objects", result.size());
        for (Teacher t : result) {
            resp.getWriter().write(t.toString() + "\n");
        }


    }
}
