package restByServlets.modelDTO;

import java.util.List;

public class CourseOutDTO {
    private Long id;
    private String name;
    private List<StudentNameDTO> studentList;
    private List<TeacherNameDTO> teacherList;

    public CourseOutDTO(Long id, String name, List<StudentNameDTO> studentList, List<TeacherNameDTO> teacherList) {
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

    public List<StudentNameDTO> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<StudentNameDTO> studentList) {
        this.studentList = studentList;
    }

    public List<TeacherNameDTO> getTeacherList() {
        return teacherList;
    }

    public void setTeacherList(List<TeacherNameDTO> teacherList) {
        this.teacherList = teacherList;
    }
}
