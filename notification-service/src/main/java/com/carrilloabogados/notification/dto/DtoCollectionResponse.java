package com.carrilloabogados.notification.dto;

import java.util.List;

public class DtoCollectionResponse<T> {

    private List<T> collection;
    private long totalElements;
    private int page;
    private int size;

    public DtoCollectionResponse() {
    }

    public DtoCollectionResponse(List<T> collection) {
        this.collection = collection;
        this.totalElements = collection.size();
        this.page = 0;
        this.size = collection.size();
    }

    public DtoCollectionResponse(List<T> collection, long totalElements, int page, int size) {
        this.collection = collection;
        this.totalElements = totalElements;
        this.page = page;
        this.size = size;
    }

    public List<T> getCollection() {
        return collection;
    }

    public void setCollection(List<T> collection) {
        this.collection = collection;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
