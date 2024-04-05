package restByServlets.modelDTO;

import java.util.List;

public class TeacherUpdateDTO {
    private Long id;
    private String name;

    public TeacherUpdateDTO() {
    }

    public TeacherUpdateDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
