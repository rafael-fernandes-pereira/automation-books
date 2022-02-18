package passo0;

import java.math.BigDecimal;

public class BookDTO {

    private Integer id;
    private String name;
    private String author;
    private String category;
    private String publishingCompany;
    private BigDecimal price;

    public BookDTO(){

    }

    public BookDTO(Integer id, String name, String author, String category, String publishingCompany, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.category = category;
        this.publishingCompany = publishingCompany;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPublishingCompany() {
        return publishingCompany;
    }

    public void setPublishingCompany(String publishingCompany) {
        this.publishingCompany = publishingCompany;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
