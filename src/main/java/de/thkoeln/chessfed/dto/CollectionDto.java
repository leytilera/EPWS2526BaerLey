package de.thkoeln.chessfed.dto;

public class CollectionDto extends ActivityPubDto {
    private int totalItems;
    private ActivityPubDto[] items;

    public CollectionDto() {
        setType("OrderedCollection");
        withContext();
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public ActivityPubDto[] getItems() {
        return items;
    }

    public void setItems(ActivityPubDto[] items) {
        this.items = items;
    }

}
