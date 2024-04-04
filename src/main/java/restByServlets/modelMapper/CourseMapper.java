package restByServlets.modelMapper;

import restByServlets.model.Course;
import restByServlets.modelDTO.CourseNameDTO;
import restByServlets.modelDTO.CourseOutDTO;
import restByServlets.modelDTO.CourseUpdateDTO;

import java.util.List;

public class CourseMapper {
    private StudentMapper studentMapper = new StudentMapper();
    private TeacherMapper teacherMapper = new TeacherMapper();

    /**
     * Преобразует входящий дто в модель
     * @param courseNameDTO
     * @return
     */
        public Course mapNameToModel(CourseNameDTO courseNameDTO) {
        return new Course(
                null,
                courseNameDTO.getName(),
                List.of(),
                List.of()
        );
    }

    /**
     * Преобразует обновляющий дто в модель
     * @param courseUpdateDTO
     * @return
     */
    public Course mapUpdateToModel(CourseUpdateDTO courseUpdateDTO) {
        return new Course(courseUpdateDTO.getId(),
                courseUpdateDTO.getName(),
                List.of(),
                List.of());
    }

    /**
     * Преобразует модель в исходящий дто
     * @param course
     * @return
     */
    public CourseOutDTO mapModelToOut(Course course) {
        return new CourseOutDTO(
                course.getId(),
                course.getName(),
                studentMapper.mapModelListToNameList(course.getStudentList()),
                teacherMapper.mapModelListToNameList(course.getTeacherList())
        );
    }

    /**
     * Преобразует список моделей в список выходящих дто
     * @param courseList
     * @return
     */
    public List<CourseNameDTO> mapModelListToOutList(List<Course> courseList) {
        return courseList.stream().map(this::mapModelToName).toList();
    }

    /**
     * Преобразует модель в дто Name
     * @param course
     * @return
     */
    public CourseNameDTO mapModelToName(Course course) {
        return new CourseNameDTO(course.getName());
    }

    /**
     * Преобразует список моделей в список дто Name
     * @param courseList
     * @return
     */
    public List<CourseNameDTO> mapModelListToNameList(List<Course> courseList) {
        return courseList.stream().map(this::mapModelToName).toList();
    }


}
