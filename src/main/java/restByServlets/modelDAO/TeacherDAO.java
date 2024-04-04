package restByServlets.modelDAO;

import restByServlets.db.ConnectionManager;
import restByServlets.exception.RepositoryException;
import restByServlets.model.Course;
import restByServlets.model.CourseToTeacher;
import restByServlets.model.Teacher;
import restByServlets.modelDTO.*;

import java.sql.*;
import java.util.*;

public class TeacherDAO {
    private static ConnectionManager connectionManager = ConnectionManager.getInstance();
    private static CourseToTeacherDAO courseToTeacherDAO = new CourseToTeacherDAO();
    private static final String SAVE_SQL = "INSERT INTO school.teacher(teacher_name) VALUES(?)";
    static final String UPDATE_SQL = "UPDATE school.teacher SET teacher_name = ? WHERE id = ?";
    static final String DELETE_SQL = "DELETE FROM school.teacher WHERE id = ?";
    static final String FIND_BY_ID_SQL = """
            SELECT t.id AS teacher_id, teacher_name, c.id as course_id, course_name
            FROM school.courses AS c
            LEFT JOIN school.courses_teachers AS ct ON ct.course_id = c.id
            LEFT JOIN school.teachers AS t ON t.id = ct.teacher_id
            WHERE t.id = ?
            """;
    static final String FIND_ALL_SQL = """
            SELECT t.id AS teacher_id, teacher_name, c.id as course_id, course_name
            FROM school.courses AS c
            JOIN school.courses_teachers AS ct ON ct.course_id = c.id
            RIGHT JOIN school.teachers AS t ON t.id = ct.teacher_id
            ORDER BY t.id
            """;
    static final String EXIST_BY_ID_SQL = "SELECT exists (SELECT 1 FROM school.teacher WHERE id = ? LIMIT 1)";

//    private static TeacherOutDTO createTeacher(ResultSet resultSet) throws SQLException {
//        TeacherOutDTO teacher;
//        course = new Course(
//                resultSet.getLong("id"),
//                resultSet.getString("course_name"),
//                null,
//                null);
//        return course;
//    }

    /**
     * Создает новую запись в таблице
     * Возвращает модель с id
     * @param teacher
     * @return
     * @throws SQLException
     */
    public Teacher save(Teacher teacher) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, teacher.getName());
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                teacher = new Teacher(
                        resultSet.getLong("teacher_id"),
                        resultSet.getString("teacher_name"),
                        List.of()
                );
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return teacher;
    }

    /**
     * Обновляет имя учителя по id
     *
     * @param teacher
     */
    public void update(Teacher teacher) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {

            preparedStatement.setString(1, teacher.getName());
            preparedStatement.setLong(2, teacher.getId());

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
        boolean deleteResult;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL);) {

            courseToTeacherDAO.deleteByTeacherId(id);

            preparedStatement.setLong(1, id);
            deleteResult = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return deleteResult;
    }

    /**
     * Возвращает Optional<Teacher> из таблицы courses по id
     * @param teacherId
     * @return
     */
    public Optional<TeacherOutDTO> findById(Long teacherId) {
        TeacherOutDTO teacher = null;
        List<CourseNameDTO> courseList = new ArrayList<>();

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setLong(1, teacherId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                if (teacher == null) {
                    teacher = new TeacherOutDTO(
                            teacherId,
                            resultSet.getString("teacher_name"),
                            List.of()
                    );
                }

                Long courseId = resultSet.getLong("course_id");
                if (courseId != 0) {
                    String courseName = resultSet.getString("course_name");
                    CourseNameDTO course = new CourseNameDTO(courseName);
                    courseList.add(course);
                }
            }

            if (teacher != null) teacher.setCourseList(courseList);

        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return Optional.ofNullable(teacher);
    }

    /**
     * Получает все записи из таблицы courses и соответствующие им записи из связанных таблиц
     * собирает список выходящих dto c заполненными списками студентов и учителей на каждом курсе
     * @return
     */
    public List<TeacherOutDTO> findAll() {
        Map<Long, TeacherOutDTO> teacherMap = new HashMap<>();
        Map<Long, Map<Long, CourseNameDTO>> courseMap = new HashMap<>();

        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(FIND_ALL_SQL);
            while (resultSet.next()) {
                Long teacherId = resultSet.getLong("teacher_id");
                String teacherName = resultSet.getString("teacher_name");
                teacherMap.put(teacherId, new TeacherOutDTO(teacherId, teacherName, List.of()));

                Long courseId = resultSet.getLong("course_id");
                if (courseId != 0) {
                    String courseName = resultSet.getString("course_name");
                    CourseNameDTO course = new CourseNameDTO(courseName);

                    if (courseMap.containsKey(teacherId)) {
                        courseMap.get(teacherId).put(teacherId, course);
                    } else {
                        Map<Long, CourseNameDTO> courseNameDTOMap = new HashMap<>();
                        courseNameDTOMap.put(teacherId, course);
                        courseMap.put(teacherId, courseNameDTOMap);
                    }
                }
            }

            for (TeacherOutDTO teacher : teacherMap.values()) {
                Map<Long, CourseNameDTO> courseNameDTOMap = courseMap.get(teacher.getId());
                if (courseNameDTOMap != null) {
                    teacher.setCourseList(courseNameDTOMap.values().stream().toList());
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return teacherMap.values().stream().toList();
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
