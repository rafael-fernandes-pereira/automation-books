package passo3;

public enum TypeOfApply {

    CATEGORY ("category", "category"),
    PUBLISHING_COMPANY("publishing_company", "publishingCompany"),
    AUTHOR("author", "author");

    private final String typeOfApply;
    private final String path;

    TypeOfApply(String typeOfApply, String path) {
        this.typeOfApply = typeOfApply;
        this.path = path;
    }

    public String getTypeOfApply() {
        return typeOfApply;
    }

    public String getPath() {
        return path;
    }
}
