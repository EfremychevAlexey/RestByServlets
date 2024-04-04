package restByServlets.modelDAO;

import restByServlets.db.ConnectionManager;
import restByServlets.exception.RepositoryException;
import restByServlets.model.Course;
import restByServlets.model.Student;
import restByServlets.modelDTO.*;

import java.sql.*;
import java.util.*;

public class StudentDAO {
    private static ConnectionManager connectionManager = ConnectionManager.getInstance();
    private static final String SAVE_SQL = "INSERT INTO school.students(student_name) VALUES(?)";
    static final String UPDATE_SQL = "UPDATE school.students SET student_name = ? WHERE id = ?";
    static final String UPDATE_BY_DELETE_COURSE_ID_SQL = "UPDATE school.students SET course_id = NULL WHERE course_id = ?";
    static final String DELETE_SQL = "DELETE FROM school.students WHERE id = ?";
    static final String FIND_BY_ID_SQL = """
            SELECT s.id as student_id, student_name, c.id as course_id, course_name
            FROM school.students AS s
            LEFT JOIN school.courses AS c ON c.id = s.course_id
            WHERE s.id = ?
            """;
    static final String FIND_ALL_SQL = """
            SELECT s.id as student_id, student_name, c.id as course_id, course_name
            FROM school.students AS s
            LEFT JOIN school.courses AS c ON c.id = s.course_id
            """;
    static final String EXIST_BY_ID_SQL = "SELECT exists (SELECT 1 FROM school.students WHERE id = ? LIMIT 1)";

    private static StudentOutDTO createStudent(ResultSet resultSet) throws SQLException {
        StudentOutDTO student = null;

        student = new StudentOutDTO(
                resultSet.getLong("student_id"),
                resultSet.getString("student_name"),
                null);

        String courseName = resultSet.getString("course_name");

        if (courseName != null) {
            student.setCourse(new CourseNameDTO(courseName));
        }

        return student;
    }

    /**
     * Создает новую запись в таблице
     * Возвращает модель с id
     *
     * @param student
     * @return
     * @throws SQLException
     */
    public Student save(Student student) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, student.getName());
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                student = new Student(
                        resultSet.getLong("id"),
                        resultSet.getString("student_name"),
                        null
                );
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return student;
    }

    /**
     * Обновляет имя курсa по id
     *
     * @param student
     */
    public void update(Student student) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {

            preparedStatement.setString(1, student.getName());
            preparedStatement.setLong(2, student.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * Удаляет courseId во всех записях в таблице students по переданному coursId
     * записывает в ячейку null
     * @param courseId
     */
    public void deleteCourseIdByCourseId(Long courseId) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_BY_DELETE_COURSE_ID_SQL)) {

            preparedStatement.setLong(1, courseId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * Удаляет запись из бд таблицы courses по id
     * Удаляет запись из таблицы связей courses_teachers по id удаленного курса
     * Обновляет в таблице students все записи с данным id меняя на null
     *
     * @param id
     * @return
     */
    public boolean deleteById(Long id) {
        boolean deleteResult = true;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL)) {

            preparedStatement.setLong(1, id);
            deleteResult = preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return deleteResult;
    }

    /**
     * Возвращает Optional<Student> из таблицы courses по id
     *
     * @param studentId
     * @return
     */
    public Optional<StudentOutDTO> findById(Long studentId) {
        StudentOutDTO student = null;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setLong(1, studentId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                student = createStudent(resultSet);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(student);
    }

    /**
     * Получает все записи из таблицы courses и соответствующие им записи из связанных таблиц
     * собирает список выходящих dto c заполненными списками студентов и учителей на каждом курсе
     *
     * @return
     */
    public List<StudentOutDTO> findAll() {
        List<StudentOutDTO> studentOutDTOList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(FIND_ALL_SQL);

            while (resultSet.next()) {
                studentOutDTOList.add(createStudent(resultSet));
            }

        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return studentOutDTOList;
    }

    /**
     * Возвращает true если в таблице courses есть запись с переданным id
     * и наоборот
     *
     * @param id
     * @return
     */
    public boolean existsById(Long id) {
        boolean isExists = false;
        try (Connection connection = connectionManager.getConnection();
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
