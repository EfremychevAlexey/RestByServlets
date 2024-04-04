package restByServlets.model;

public class CourseToTeacher {
    private Long id;
    private Long courseId;
    private Long teacherId;

    public CourseToTeacher() {
    }

    public CourseToTeacher(Long id, Long courseId, Long teacherId) {
        this.id = id;
        this.courseId = courseId;
        this.teacherId = teacherId;
    }

    public Long getId() {
        return id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public Long getTeacherId() {
        return teacherId;
    }
}
