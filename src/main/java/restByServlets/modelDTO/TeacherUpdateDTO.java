package restByServlets.modelDTO;

import java.util.List;

public class TeacherUpdateDTO {
    private Long id;
    private String name;
    private CourseUpdateDTO course;

    public TeacherUpdateDTO() {
    }

    public TeacherUpdateDTO(Long id, String name, CourseUpdateDTO course) {
        this.id = id;
        this.name = name;
        this.course = course;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CourseUpdateDTO getCourse() {
        return course;
    }
}
