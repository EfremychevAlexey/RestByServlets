package restByServlets;

import restByServlets.db.ConnectionManager;
import restByServlets.model.Student;
import restByServlets.modelDAO.CourseDAO;
import restByServlets.modelDAO.StudentDAO;
import restByServlets.modelDAO.TeacherDAO;
import restByServlets.modelDTO.*;
import restByServlets.util.DBInit;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        DBInit.init(connectionManager);

        CourseDAO courseDAO = CourseDAO.getInstance();

        StudentDAO studentDAO = StudentDAO.getInstance();

        TeacherDAO teacherDAO = TeacherDAO.getInstance();


        TeacherOutDTO teacher = teacherDAO.findById(4L).get();

        System.out.println(teacher.getId() + " " + teacher.getName());
        System.out.println("КУрсы:");
        for (CourseNameDTO c : teacher.getCourseList()) {
            System.out.println(c.getName());
        }



//        List<TeacherOutDTO> teacherList = teacherDAO.findAll();
//
//        for (TeacherOutDTO t : teacherList) {
//            System.out.println(t.getId() + " " + t.getName());
//
//
//            System.out.println("КУрсы:");
//            for (CourseNameDTO c : t.getCourseList()) {
//                System.out.println(c.getName());
//            }
//        }

//        System.out.println(studentDAO.findById(2L).get().getId());
//        System.out.println(studentDAO.findById(2L).get().getName());
//        System.out.println(studentDAO.findById(2L).get().getCourse().getName());
//
//        System.out.println(studentDAO.save(new Student(null,
//                "adfadf",
//                null)).getId());
//
//        List<StudentOutDTO> studentOutDTOList = studentDAO.findAll();
//
//        for (StudentOutDTO s : studentOutDTOList) {
//            System.out.println(s.getCourse());
//        }
//
//        System.out.println(studentDAO.existsById(10L));


//        System.out.println(courseDAO.existsById(6L));


//        List<CourseOutDTO> courseList = courseDAO.findAll();
//
//        for (CourseOutDTO c : courseList) {
//            System.out.println(c.getId() + " " + c.getName());
//
//            System.out.println("Студенты:");
//            for (StudentNameDTO s : c.getStudentList()) {
//                System.out.println(s.getName());
//            }
//
//            System.out.println();
//            System.out.println("Учителя:");
//            for (TeacherNameDTO t : c.getTeacherList()) {
//                System.out.println(t.getName());
//            }
//            System.out.println();
//        }


//        CourseOutDTO course = null;
//        Optional<CourseOutDTO> optionalCourseOutDTO =  courseDAO.findById(5L);
//
//        if(optionalCourseOutDTO.isPresent()) {
//            course = optionalCourseOutDTO.get();
//            System.out.println(course.getId());
//            System.out.println(course.getName());
//
//            System.out.println("Список студентов:");
//            for (StudentNameDTO s : course.getStudentList()) {
//                System.out.println(s.getName());
//            }
//            System.out.println();
//            System.out.println("Список  учителей:");
//            for (TeacherNameDTO t : course.getTeacherList()) {
//                System.out.println(t.getName());
//            }
//        }


    }
}