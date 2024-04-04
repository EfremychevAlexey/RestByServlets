package restByServlets.service;

import restByServlets.exception.NotFoundException;
import restByServlets.model.Student;
import restByServlets.modelDAO.StudentDAO;
import restByServlets.modelDTO.StudentNameDTO;
import restByServlets.modelDTO.StudentOutDTO;
import restByServlets.modelDTO.StudentUpdateDTO;
import restByServlets.modelMapper.StudentMapper;

import java.util.List;

/**
 * Совершает операции в таблице students
 */
public class StudentService {
    private static final StudentDAO studentDAO = new StudentDAO();
    private static final StudentMapper studentMapper = new StudentMapper();

    /**
     * Проверяем наличие записи в таблице по id
     * Если ее нет выбрасываем исключение
     * @param studentId
     * @throws NotFoundException
     */
    private void checkExistStudent(Long studentId) throws NotFoundException {
        if (!studentDAO.existsById(studentId)) {
            throw new NotFoundException("Student not found.");
        }
    }

    /**
     * Преобразуем полученного StudentIncomingDto в модель Student
     * Сохраняем модель Student в бд, получаем модель Student с id
     * Преобразуем модель Student в StudentOutGoingDto и возвращаем её.
     * @return
     * @param studentNameDTO
     */
    public StudentOutDTO save(StudentNameDTO studentNameDTO) {
        Student student = studentMapper.mapNameToModel(studentNameDTO);
        student = studentDAO.save(student);
        return studentMapper.mapModelToOut(student);
    }

    /**
     * Проверяем, есть ли в таблице students запись с id = studentUpdateDto.getId.
     * Если есть, преобразуем StudentUpdateDto в модель Student
     * Обновляем запись в бд.
     * Если запись в таблице отсутствует, выбрасываем исключении с соответствующим сообщением
     * @param studentUpdateDto
     */
    public void update(StudentUpdateDTO studentUpdateDto) throws NotFoundException {
        checkExistStudent(studentUpdateDto.getId());
        studentDAO.update(studentMapper.mapUpdateToModel(studentUpdateDto));
    }

    /**
     *
     * Получаем модель Student из таблицы students по id
     * Если записи с таким id в таблице нет, выбрасываем исключение с соответствующим сообщением.
     * Если есть преобразуем Student в StudentOutGoingDto и возвращаем его
     * @param studentId
     * @return
     * @throws NotFoundException
     */
    public StudentOutDTO findById(Long studentId) throws NotFoundException {
        StudentOutDTO student = studentDAO.findById(studentId).orElseThrow(() ->
                new NotFoundException("Student not found."));
        return student;
    }

    /**
     * Получаем все записи из таблицы students,
     * преобразуем в StudentOutGoingDto и возвращаем список
     * @return
     */
    public List<StudentOutDTO> findAll() {
        return studentDAO.findAll();
    }

    /**
     * Проверяем наличие записи в таблице students по id
     * Если записи с таким id в таблице нет, выбрасываем исключение с соответствующим сообщением.
     * Иначе удаляем запись
     * @param studentId
     * @throws NotFoundException
     */
    public void delete(Long studentId) throws NotFoundException {
        checkExistStudent(studentId);
        studentDAO.deleteById(studentId);
    }
}
