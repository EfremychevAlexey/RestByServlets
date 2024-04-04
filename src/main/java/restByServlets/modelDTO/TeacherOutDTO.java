package restByServlets.modelDTO;

import java.util.List;

public class TeacherOutDTO {
    private Long id;
    private String name;
    private List<CourseNameDTO> courseList;

    public TeacherOutDTO(Long id, String name, List<CourseNameDTO> courseList) {
        this.id = id;
        this.name = name;
        this.courseList = courseList;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<CourseNameDTO> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<CourseNameDTO> courseList) {
        this.courseList = courseList;
    }
}
