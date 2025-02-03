package com.myapp.jdbc;

import util.H2Util;

public class Main {
    public static void main(String[] args) {
        // Generate tables
        System.out.println("Creating tables...");
        H2Util.generateTables();
        System.out.println("Tables created successfully.");
        
        // Optionally, drop tables after some operations
        // H2Util.dropAllTables();
        // System.out.println("Tables dropped successfully.");
    }
}