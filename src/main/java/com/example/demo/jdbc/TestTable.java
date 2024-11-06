package com.example.demo.jdbc;

public class TestTable {
    private String message;
    public TestTable(){
    }
    public TestTable(String message){
        this.message = message;
    }
    @Override
    public String toString() {
        return String.format("[message='%s']", message);
    }
}