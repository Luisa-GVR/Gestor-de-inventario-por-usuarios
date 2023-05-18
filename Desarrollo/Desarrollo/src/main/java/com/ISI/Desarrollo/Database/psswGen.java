package com.ISI.Desarrollo.Database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;



public class psswGen {


    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String prueba = "1234";
        String pruebacoded = encoder.encode(prueba);

        System.out.println(pruebacoded);
    }

}
