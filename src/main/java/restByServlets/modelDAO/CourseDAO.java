package restByServlets.modelDAO;

import restByServlets.db.ConnectionManager;
import restByServlets.exception.RepositoryException;
import restByServlets.model.Course;
import restByServlets.modelDTO.*;

import java.sql.*;
import java.util.*;

public class CourseDAO {
    private static ConnectionManager connectionManager = ConnectionManager.getInstance();
    private static final String SAVE_SQL = "INSERT INTO school.courses(course_name) VALUES(?)";
    static final String UPDATE_SQL = "UPDATE school.courses SET course_name = ? WHERE id = ?";
    static final String DELETE_SQL = "DELETE FROM school.courses WHERE id = ?";
    static final String FIND_BY_COURSE_NAME_SQL = "SELECT id, course_name FROM school.courses WHERE course_name = ?";
    static final String FIND_BY_ID_SQL = """
            SELECT c.id as course_id, course_name, s.id AS student_id, student_name, t.id AS teacher_id, teacher_name 
            FROM school.students AS s 
            RIGHT JOIN school.courses AS c ON c.id = s.course_id 
            LEFT JOIN school.courses_teachers AS ct ON ct.course_id = c.id 
            LEFT JOIN school.teachers AS t ON t.id = ct.teacher_id
            WHERE c.id = ?
            """;
    static final String FIND_ALL_SQL = """
            SELECT c.id as course_id, course_name, s.id AS student_id, student_name, t.id AS teacher_id, teacher_name 
            FROM school.students AS s 
            RIGHT JOIN school.courses AS c ON c.id = s.course_id 
            LEFT JOIN school.courses_teachers AS ct ON ct.course_id = c.id 
            LEFT JOIN school.teachers AS t ON t.id = ct.teacher_id
            """;
    static final String EXIST_BY_ID_SQL = "SELECT exists (SELECT 1 FROM school.courses WHERE id = ? LIMIT 1)";

    private static Course createCourse(ResultSet resultSet) throws SQLException {
        Course course;
        course = new Course(
                resultSet.getLong("id"),
                resultSet.getString("course_name"),
                null,
                null);
        return course;
    }

    /**
     * Создает новую запись в таблице coures
     * Возвращает модель с id
     * @param course
     * @return
     * @throws SQLException
     */
    public Course save(Course course) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, course.getName());
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                course = createCourse(resultSet);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return course;
    }

    /**
     * Обновляет имя курсa по id
     *
     * @param course
     */
    public void update(Course course) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {

            preparedStatement.setString(1, course.getName());
            preparedStatement.setLong(2, course.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * Удаляет запись из бд таблицы courses по id
     * Удаляет запись из таблицы связей courses_teachers по id удаленного курса
     * Обновляет в таблице students все записи с данным id меняя на null
     * @param id
     * @return
     */
    public boolean deleteById(Long id) {
        boolean deleteResult = true;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setLong(1, id);

            StudentDAO studentDAO = new StudentDAO();
            studentDAO.deleteCourseIdByCourseId(id);

            CourseToTeacherDAO courseToTeacherDAO = new CourseToTeacherDAO();
            courseToTeacherDAO.deleteByCourseId(id);

            deleteResult = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return deleteResult;
    }

    /**
     * Возвращает запись из таблицы courses по имени Курса
     * @param name
     * @return
     */
    public Optional<Course> findByName(String name) {
        Course course = null;
        try(Connection connection = connectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_COURSE_NAME_SQL)) {

            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                course = createCourse(resultSet);
            }

        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return  Optional.ofNullable(course);
    }

    /**
     * Возвращает Optional<Course> из таблицы courses по id
     * @param courseId
     * @return
     */
    public Optional<CourseOutDTO> findById(Long courseId) {
        CourseOutDTO course = null;
        Map<Long, StudentNameDTO> studentMap = new HashMap<>();
        Map<Long, TeacherNameDTO> teacherMap = new HashMap<>();

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setLong(1, courseId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (course == null) {
                    course = new CourseOutDTO(
                            courseId,
                            resultSet.getString("course_name"),
                            List.of(),
                            List.of()
                    );
                }

                Long studentId = resultSet.getLong("student_id");
                if (studentId != 0) {
                    String studentName = resultSet.getString("student_name");
                    StudentNameDTO student = new StudentNameDTO(studentName);
                    studentMap.put(studentId, student);
                }

                Long teacherId = resultSet.getLong("teacher_id");
                if (teacherId != 0) {
                    String teacherName = resultSet.getString("teacher_name");
                    TeacherNameDTO teacher = new TeacherNameDTO(teacherName);
                    teacherMap.put(teacherId, teacher);
                }
            }

            if (course != null) {
                course.setStudentList(studentMap.values().stream().toList());
                course.setTeacherList(teacherMap.values().stream().toList());
            }

        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return Optional.ofNullable(course);
    }

    /**
     * Получает все записи из таблицы courses и соответствующие им записи из связанных таблиц
     * собирает список выходящих dto c заполненными списками студентов и учителей на каждом курсе
     * @return
     */
    public List<CourseOutDTO> findAll() {
        Map<Long, CourseOutDTO> courseMap = new HashMap<>();
        Map<Long, Map<Long, StudentNameDTO>> studentMap = new HashMap<>();
        Map<Long, Map<Long, TeacherNameDTO>> teacherMap = new HashMap<>();

        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(FIND_ALL_SQL);
            while (resultSet.next()) {
                Long courseId = resultSet.getLong("course_id");
                String courseName = resultSet.getString("course_name");
                courseMap.put(courseId, new CourseOutDTO(courseId, courseName, List.of(), List.of()));

                Long studentId = resultSet.getLong("student_id");
                if (studentId != 0) {
                    String studentName = resultSet.getString("student_name");
                    StudentNameDTO student = new StudentNameDTO(studentName);
                    if(studentMap.containsKey(courseId)) {
                        studentMap.get(courseId).put(studentId, student);
                    } else {
                        Map<Long, StudentNameDTO> studentNameDTOMap = new HashMap<>();
                        studentNameDTOMap.put(studentId, student);
                        studentMap.put(courseId, studentNameDTOMap);
                    }
                }

                Long teacherId = resultSet.getLong("teacher_id");
                if (teacherId != 0) {
                    String teacherName = resultSet.getString("teacher_name");
                    TeacherNameDTO teacher = new TeacherNameDTO(teacherName);
                    if (teacherMap.containsKey(courseId)) {
                        teacherMap.get(courseId).put(teacherId, teacher);
                    } else {
                        Map<Long, TeacherNameDTO> teacherNameDTOMap = new HashMap<>();
                        teacherNameDTOMap.put(teacherId, teacher);
                        teacherMap.put(courseId, teacherNameDTOMap);
                    }
                }
            }

            for (CourseOutDTO course : courseMap.values()) {
                Map<Long, StudentNameDTO> studentNameDTOMap = studentMap.get(course.getId());
                if (studentNameDTOMap != null) {
                    course.setStudentList(studentNameDTOMap.values().stream().toList());
                }

                Map<Long, TeacherNameDTO> teacherNameDTOMap = teacherMap.get(course.getId());
                if (teacherNameDTOMap != null) {
                    course.setTeacherList(teacherNameDTOMap.values().stream().toList());
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return courseMap.values().stream().toList();
    }

    /**
     * Возвращает true если в таблице courses есть запись с переданным id
     * и наоборот
     * @param id
     * @return
     */
    public boolean existsById(Long id) {
        boolean isExists = false;
        try(Connection connection = connectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(EXIST_BY_ID_SQL)) {
            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                isExists = resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return isExists;
    }
}
