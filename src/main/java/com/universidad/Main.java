package com.universidad;

public class Main
{
    public static void main(String[] args)
    {
        final String urlOracle = "jdbc:oracle:thin:@localhost:1521:XE";
        final String userOracle= "RIFA";
        final String passwordOracle = "1234";

        Mapping mapeadorOracle = new Mapping(urlOracle, userOracle, passwordOracle);
        //Trabajador chambeador1 = new Trabajador(118, "Josepe", 21, "Awwk");
        Trabajador chambeador2 = new Trabajador(604, "Reyner", 22, "Wiko");
        //Trabajador chambeador3 = new Trabajador(608, "Esdras", 19, "Sleeper");
        
        mapeadorOracle.updateObjectFromTable(chambeador2);
    }
}