package restByServlets.model;

import java.util.List;

public class Course {
    private Long id;
    private String name;
    private List<Student> studentList;
    private List<Teacher> teacherList;

    public Course() {
    }

    public Course(Long id, String name, List<Student> studentList, List<Teacher> teacherList) {
        this.id = id;
        this.name = name;
        this.studentList = studentList;
        this.teacherList = teacherList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Student> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<Student> studentList) {
        this.studentList = studentList;
    }

    public List<Teacher> getTeacherList() {
        return teacherList;
    }

    public void setTeacherList(List<Teacher> teacherList) {
        this.teacherList = teacherList;
    }
}
