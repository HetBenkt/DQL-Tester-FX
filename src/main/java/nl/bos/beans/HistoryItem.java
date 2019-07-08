package nl.bos.beans;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class HistoryItem {
    private String query;
    private String category;
    private BooleanProperty favorite = new SimpleBooleanProperty();


    public HistoryItem(String query) {
        this.query = query;
        category = "none";
        this.favoriteProperty().set(false);
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
        return this.favoriteProperty().get();
    }

    public void setFavorite(boolean isFavorite) {
        this.favoriteProperty().set(isFavorite);
    }
    
    public BooleanProperty favoriteProperty() {
    	return favorite;
    }

    @Override
    public String toString() {
        return getQuery().substring(0, Math.min(getQuery().length(), 200)).replaceAll("\n", " ");
    }
}
