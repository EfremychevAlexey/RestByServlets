package restByServlets.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import restByServlets.exception.NotFoundException;
import restByServlets.modelDTO.CourseNameDTO;
import restByServlets.modelDTO.CourseOutDTO;
import restByServlets.modelDTO.CourseUpdateDTO;
import restByServlets.service.CourseService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

/**
 * Сервлет обрабатывает HTTP запросы /сourse/*
 */
@WebServlet(urlPatterns = {"/course/*"})
public class CourseServlet extends HttpServlet {
    private final transient CourseService courseService = CourseService.getInstance();
    private final ObjectMapper objectMapper;

    public CourseServlet() {
        this.objectMapper = new ObjectMapper();
    }

    private static void setJsonHeader(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }

    private static String getJson(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader postData = req.getReader();
        String line;
        while ((line = postData.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    /**
     * Отрабатывает GET запрос
     * Возвращает записи из бд в формате JSON
     * @param req
     * @param resp
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonHeader(resp);

        String responseAnswer = "";

        try {
            String[] pathPart = req.getPathInfo().split("/");

            if ("all".equals(pathPart[1])) {
                List<CourseOutDTO> courseDto = courseService.findAll();
                resp.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(courseDto);
            }
            else {
                Long courseId = Long.parseLong(pathPart[1]);
                CourseOutDTO courseDto = courseService.findById(courseId);
                resp.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(courseDto);
            }
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = e.getMessage();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Bad request.";
        }
        PrintWriter printWriter = resp.getWriter();
        printWriter.write(responseAnswer);
        printWriter.flush();
    }

    /**
     * Отрабатывает DELETE запрос
     * Возвращает статусы ответов SC_NOT_FOUND, SC_NO_CONTENT, SC_BAD_REQUEST
     * @param req
     * @param resp
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonHeader(resp);
        String responseAnswer = "";

        try {
            String[] pathPart = req.getPathInfo().split("/");
            Long courseId = Long.parseLong(pathPart[1]);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            courseService.delete(courseId);
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = e.getMessage();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Bad request.";
        }
        PrintWriter printWriter = resp.getWriter();
        printWriter.write(responseAnswer);
        printWriter.flush();
    }

    /**
     * Отрабатывает Post запрос
     * Возвращает экземпляр сохраненного в бд экземпляра в формате JSON
     * Возвращает статусы ответов SC_OK, SC_BAD_REQUEST
     * @param req
     * @param resp
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonHeader(resp);
        String json = getJson(req);

        String responseAnswer = null;
        Optional<CourseNameDTO> courseResponse;
        try {
            courseResponse = Optional.ofNullable(objectMapper.readValue(json, CourseNameDTO.class));
            CourseNameDTO course = courseResponse.orElseThrow(IllegalArgumentException::new);
            responseAnswer = objectMapper.writeValueAsString(courseService.save(course));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Incorrect course Object.";
        }
        PrintWriter printWriter = resp.getWriter();
        printWriter.write(responseAnswer);
        printWriter.flush();
    }

    /**
     * Отрабатывает запрос PUT, обновляет записи в бд
     * @param req
     * @param resp
     * @throws IOException
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonHeader(resp);
        String json = getJson(req);

        String responseAnswer = "";
        Optional<CourseUpdateDTO> courseResponse;
        try {
            courseResponse = Optional.ofNullable(objectMapper.readValue(json, CourseUpdateDTO.class));
            CourseUpdateDTO courseUpdateDto = courseResponse.orElseThrow(IllegalArgumentException::new);
            courseService.update(courseUpdateDto);
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = e.getMessage();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Incorrect course Object.";
        }
        PrintWriter printWriter = resp.getWriter();
        printWriter.write(responseAnswer);
        printWriter.flush();
    }
}






















