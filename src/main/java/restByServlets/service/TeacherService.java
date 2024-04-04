package restByServlets.service;

import restByServlets.exception.NotFoundException;
import restByServlets.model.CourseToTeacher;
import restByServlets.model.Teacher;
import restByServlets.modelDAO.CourseToTeacherDAO;
import restByServlets.modelDAO.TeacherDAO;
import restByServlets.modelDTO.TeacherNameDTO;
import restByServlets.modelDTO.TeacherOutDTO;
import restByServlets.modelDTO.TeacherUpdateDTO;
import restByServlets.modelMapper.TeacherMapper;

import java.util.List;
import java.util.Optional;

public class TeacherService {
    private static TeacherService instance;
    private static final TeacherDAO teacherDao = new TeacherDAO();
    private static final CourseToTeacherDAO courseToTeacherDAO = new CourseToTeacherDAO();
    private static final TeacherMapper teacherMapper = new TeacherMapper();

    /**
     * Проверяем наличие записи в таблице по id
     * Если ее нет выбрасываем исключение
     *
     * @param teacherId
     * @throws NotFoundException
     */
    private void checkTeacherExist(Long teacherId) throws NotFoundException {
        if (!teacherDao.existsById(teacherId)) {
            throw new NotFoundException("Teacher not found.");
        }
    }

    /**
     * Преобразуем TeacherIncomingDto в Teacher
     * Возвращаем TeacherOutGoingDto с полученным id
     *
     * @param teacherNameDto
     * @return
     */
    public TeacherOutDTO save(TeacherNameDTO teacherNameDto) {
        Teacher teacher = teacherMapper.mapNameToModel(teacherNameDto);
        teacher = teacherDao.save(teacher);
        return teacherMapper.mapModelToOut(teacher);
    }

    /**
     * Проверяем наличие записи по id, если нет выбрасываем исключение
     * Обновляем запись в бд
     *
     * @param teacherUpdateDTO
     * @throws NotFoundException
     */
    public void update(TeacherUpdateDTO teacherUpdateDTO) throws NotFoundException {
        checkTeacherExist(teacherUpdateDTO.getId());

        if (teacherUpdateDTO.getCourse() != null) {
            CourseToTeacher courseToTeacher = new CourseToTeacher(
                    null,
                    teacherUpdateDTO.getCourse().getId(),
                    teacherUpdateDTO.getId()
            );
            courseToTeacherDAO.save(courseToTeacher);
        }
        teacherDao.update(teacherMapper.mapUpdateToModel(teacherUpdateDTO));
    }

    /**
     * Получаем учителя по id, если null выбрасываем исключение
     * Возвращаем TeacherOutGoingDto
     *
     * @param teacherId
     * @return
     * @throws NotFoundException
     */
    public TeacherOutDTO findById(Long teacherId) throws NotFoundException {
        TeacherOutDTO teacher = teacherDao.findById(teacherId).orElseThrow(() ->
                new NotFoundException("Teacher not found."));
        return teacher;
    }

    /**
     * Получаем из бд всех Teacher
     * Возвращаем лист TeacherOutGoingDto
     *
     * @return
     */
    public List<TeacherOutDTO> findAll() {
        return teacherDao.findAll();
    }

    /**
     * Проверяем есть ли запись по id, выбрасываем исключение если нет.
     * Удаляем записб из бд
     *
     * @param teacherId
     * @return
     * @throws NotFoundException
     */
    public boolean delete(Long teacherId) throws NotFoundException {
        checkTeacherExist(teacherId);
        courseToTeacherDAO.deleteByTeacherId(teacherId);
        return teacherDao.deleteById(teacherId);
    }
}
