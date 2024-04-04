package restByServlets.modelMapper;

import restByServlets.model.Course;
import restByServlets.model.Student;
import restByServlets.modelDTO.StudentNameDTO;
import restByServlets.modelDTO.StudentOutDTO;
import restByServlets.modelDTO.StudentUpdateDTO;

import java.util.List;

public class StudentMapper {
    CourseMapper courseMapper = new CourseMapper();

    /**
     * Преобразует входящий дто в модель
     * @param studentNameDTO
     * @return
     */
    public Student mapNameToModel(StudentNameDTO studentNameDTO) {
        return new Student(
                null,
                studentNameDTO.getName(),
                null
        );
    }

    /**
     * Преобразует обновляющий дто в модель
     * @param studentUpdateDTO
     * @return
     */
    public Student mapUpdateToModel(StudentUpdateDTO studentUpdateDTO) {
        Course course = new Course();
        course.setId(studentUpdateDTO.getId());
        return new Student(
                studentUpdateDTO.getId(),
                studentUpdateDTO.getName(),
                course
        );
    }

    /**
     * Преобразует модель в исходящий дто
     * @param student
     * @return
     */
    public StudentOutDTO mapModelToOut(Student student) {
        return new StudentOutDTO(
                student.getId(),
                student.getName(),
                courseMapper.mapModelToName(student.getCourse())
        );
    }

    /**
     * Преобразует список моделей в список выходящих дто
     * @param studentList
     * @return
     */
    public List<StudentOutDTO> mapModelListToOutList(List<Student> studentList) {
        return studentList.stream().map(this::mapModelToOut).toList();
    }

    /**
     * Преобразует модель в дто Name
     * @param student
     * @return
     */
    public StudentNameDTO mapModelToName(Student student) {
        return new StudentNameDTO(
                student.getName()
        );
    }

    /**
     * Преобразует список моделей в список дто Name
     * @param studentList
     * @return
     */
    public List<StudentNameDTO> mapModelListToNameList(List<Student> studentList) {
        return studentList.stream().map(this::mapModelToName).toList();
    }
}
