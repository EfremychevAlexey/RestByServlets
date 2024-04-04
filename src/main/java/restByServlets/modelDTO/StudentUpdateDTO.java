package restByServlets.modelDTO;

public class StudentUpdateDTO {
    private Long id;
    private String name;
    private Long courseId;

    public StudentUpdateDTO(Long id, String name, Long courseId) {
        this.id = id;
        this.name = name;
        this.courseId = courseId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getCourseId() {
        return courseId;
    }
}
