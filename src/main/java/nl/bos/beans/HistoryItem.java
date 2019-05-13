package nl.bos.beans;

public class HistoryItem {
    private String query;
    private String category;
    private boolean isFavorite;


    public HistoryItem(String query) {
        this.query = query;
        category = "none";
        isFavorite = false;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    @Override
    public String toString() {
        return query;
    }
}
