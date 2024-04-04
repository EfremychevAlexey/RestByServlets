package restByServlets.service;

import restByServlets.exception.NotFoundException;
import restByServlets.model.Course;
import restByServlets.modelDAO.CourseDAO;
import restByServlets.modelDTO.CourseNameDTO;
import restByServlets.modelDTO.CourseOutDTO;
import restByServlets.modelDTO.CourseUpdateDTO;
import restByServlets.modelMapper.CourseMapper;

import java.util.List;
import java.util.Optional;

/**
 * Совершает операции в таблице courses
 */
public class CourseService {
    private static CourseService instance;
    private static final CourseDAO courseDao = new CourseDAO();
    private static final CourseMapper courseMapper = new CourseMapper();

    /**
     * Проверяем наличие записи в таблице по id
     * Если ее нет выбрасываем исключение
     * @param courseId
     * @throws NotFoundException
     */
    private void checkExistCourse(Long courseId) throws NotFoundException {
        if (!courseDao.existsById(courseId)) {
            throw new NotFoundException("Course not found.");
        }
    }

    /**
     * Преобразуем полученного CourseIncomingDto в модель Course
     * Сохраняем модель Course в бд, получаем модель Course с id
     * Преобразуем модель Course в CourseOutGoingDto и возвращаем её.
     * @param courseNameDto
     * @return
     */

    public CourseOutDTO save(CourseNameDTO courseNameDto) {
        Course course;
        Optional<Course> optionalCourse = courseDao.findByName(courseNameDto.getName());

        if (optionalCourse.isPresent()) {
            return courseMapper.mapModelToOut(optionalCourse.get());
        } else {
            course = courseDao.save(courseMapper.mapNameToModel(courseNameDto));
            return courseMapper.mapModelToOut(course);
        }
    }

    /**
     * Проверяем полученный CourseUpdate на null, значение id на null.
     * Обновляем запись в бд.
     * Если такой записи нет, выбрасываем исключение с соответствующим исключением
     * @param courseUpdateDto
     * @throws NotFoundException
     */
    public void update(CourseUpdateDTO courseUpdateDto) throws NotFoundException {
        if (courseUpdateDto == null || courseUpdateDto.getId() == null) {
            throw  new IllegalArgumentException();
        }
        checkExistCourse(courseUpdateDto.getId());
        courseDao.update(courseMapper.mapUpdateToModel(courseUpdateDto));
    }

    /**
     * В случае если в таблице courses нет записи с таким id, выбрасываем исключение
     * с соответствующим сообщением
     * В ином случае получаем запись, преобрауем в CourseOutDTO и возвращаем
     * @param courseId
     * @return
     * @throws NotFoundException
     */
    public CourseOutDTO findById(Long courseId) throws NotFoundException {
        checkExistCourse(courseId);
        return courseDao.findById(courseId).orElseThrow();
    }

    /**
     * Получаем все записи из таблицы courses
     * возвращаем их в виде CourseOutDTO
     * @return
     */
    public List<CourseOutDTO> findAll() {
        List<CourseOutDTO> allCourses = courseDao.findAll();
        return allCourses;
    }

    /**
     * Удаляем из таблицы courses запись по id
     * Если записи с таким id нет, выбрасываем исключение.
     * @param courseId
     * @throws NotFoundException
     */
    public void delete(Long courseId) throws NotFoundException {
        checkExistCourse(courseId);
        courseDao.deleteById(courseId);
    }
}
