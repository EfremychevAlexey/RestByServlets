package restByServlets.modelMapper;

import restByServlets.model.Course;
import restByServlets.model.Teacher;
import restByServlets.modelDTO.CourseNameDTO;
import restByServlets.modelDTO.TeacherNameDTO;
import restByServlets.modelDTO.TeacherOutDTO;
import restByServlets.modelDTO.TeacherUpdateDTO;

import java.util.ArrayList;
import java.util.List;

public class TeacherMapper {
    private static TeacherMapper instance;
    private static final CourseMapper courseMapper = CourseMapper.getInstance();

    private TeacherMapper() {
    }

    public static synchronized TeacherMapper getInstance() {
        if (instance == null) {
            instance = new TeacherMapper();
        }
        return instance;
    }

    /**
     * Преобразует входящий дто в модель
     * @param teacherNameDTO
     * @return
     */
    public Teacher mapNameToModel(TeacherNameDTO teacherNameDTO) {
        return new Teacher(
                null,
                teacherNameDTO.getName(),
                List.of()
        );
    }

    /**
     * Преобразует обновляющий дто в модель
     * @param teacherUpdateDTO
     * @return
     */
    public Teacher mapUpdateToModel(TeacherUpdateDTO teacherUpdateDTO) {

        return new Teacher(
                teacherUpdateDTO.getId(),
                teacherUpdateDTO.getName(),
                List.of()
                );
    }
    /**
     * Преобразует модель в исходящий дто
     * @param teacher
     * @return
     */

    public TeacherOutDTO mapModelToOut(Teacher teacher) {
        return new TeacherOutDTO(
                teacher.getId(),
                teacher.getName(),
                courseMapper.mapModelListToNameList(teacher.getCourseList())
        );
    }


    /**
     * Преобразует список моделей в список выходящих дто
     * @param teacherList
     * @return
     */
    public List<TeacherOutDTO> mapModelListToOutList(List<Teacher> teacherList) {
        return teacherList.stream().map(this::mapModelToOut).toList();
    }


    /**
     * Преобразует модель в дто Name
     * @param teacher
     * @return
     */
    public TeacherNameDTO mapModelToName(Teacher teacher) {
        return new TeacherNameDTO(
                teacher.getName()
        );
    }


    /**
     * Преобразует список моделей в список дто Name
     * @param teacherList
     * @return
     */
    public List<TeacherNameDTO> mapModelListToNameList(List<Teacher> teacherList) {
        return  teacherList.stream().map(this::mapModelToName).toList();
    }
}
