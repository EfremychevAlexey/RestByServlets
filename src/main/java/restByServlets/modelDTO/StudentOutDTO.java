package restByServlets.modelDTO;

public class StudentOutDTO {
    private Long id;
    private String name;
    private CourseNameDTO course;

    public StudentOutDTO(Long id, String name, CourseNameDTO course) {
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

    public CourseNameDTO getCourse() {
        return course;
    }

    public void setCourse(CourseNameDTO course) {
        this.course = course;
    }
}
