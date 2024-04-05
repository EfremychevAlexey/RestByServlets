package restByServlets.modelDAO;

import restByServlets.db.ConnectionManager;
import restByServlets.exception.RepositoryException;
import restByServlets.model.CourseToTeacher;
import java.sql.*;

/**
 * Класс описывает взаимодействие CourseTeacher entity с базой даннных
 */
public class CourseToTeacherDAO {

    private static CourseToTeacherDAO instance;
    private static final ConnectionManager connectionManager = ConnectionManager.getInstance();
    static final String SAVE_SQL = "INSERT INTO school.courses_teachers(course_id, teacher_id) VALUES(?, ?)";
    static final String DELETE_BY_COURSE_ID_SQL = "DELETE FROM school.courses_teachers WHERE course_id = ?";
    static final String DELETE_BY_TEACHER_ID_SQL = "DELETE FROM school.courses_teachers WHERE teacher_id = ?";
    static final String EXIST_BY_ID_SQL = "SELECT exists (SELECT 1 FROM school.courses_teachers WHERE id = ? LIMIT 1)";

    private CourseToTeacherDAO() {
    }

    public static synchronized CourseToTeacherDAO getInstance() {
        if (instance == null) {
            instance = new CourseToTeacherDAO();
        }
        return instance;
    }

    /**
     * Сохраняет запись в таблице связей запись на основании экземпляра класса
     * @param courseTeacher
     * @return
     */
    public CourseToTeacher save(CourseToTeacher courseTeacher) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setLong(1, courseTeacher.getCourseId());
            preparedStatement.setLong(2, courseTeacher.getTeacherId());
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                courseTeacher = new CourseToTeacher(
                        resultSet.getLong("id"),
                        courseTeacher.getCourseId(),
                        courseTeacher.getTeacherId()
                );
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return courseTeacher;
    }

    /**
     * Удаляет все записи из таблицы связей по переданному id Курса
     * @param courseId
     * @return
     */
    public boolean deleteByCourseId(Long courseId) {
        boolean deleteResult;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BY_COURSE_ID_SQL);) {

            preparedStatement.setLong(1, courseId);

            deleteResult = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return deleteResult;
    }

    /**
     * Удаляет все записи из таблицы связей по переданному id Учителя
     * @param teacherId
     * @return
     */
    public boolean deleteByTeacherId(Long teacherId) {
        boolean deleteResult;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BY_TEACHER_ID_SQL);) {

            preparedStatement.setLong(1, teacherId);

            deleteResult = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return deleteResult;
    }

    /**
     * Возвращает true если в таблице связей есть запись с передвнным id
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
