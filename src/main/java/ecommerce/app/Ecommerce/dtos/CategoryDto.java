package ecommerce.app.Ecommerce.dtos;

public class CategoryDto {
    private Long id;
   private String name ;
    private String descreption;

    public CategoryDto(String name, String descreption , Long id) {
        this.name = name;
        this.descreption = descreption;
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescreption() {
        return descreption;
    }

    public void setDescreption(String descreption) {
        this.descreption = descreption;
    }
}

