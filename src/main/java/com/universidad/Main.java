package com.universidad;

public class Main
{
    public static void main(String[] args)
    {
        final String urlOracle = "jdbc:oracle:thin:@localhost:1521:XE";
        final String userOracle= "RIFA";
        final String passwordOracle = "1234";
        Mapping mapeadorOracle = new Mapping(urlOracle,userOracle, passwordOracle);
        Trabajador zPene = new Trabajador(118, "Josepe", 21, "Awwk");
        mapeadorOracle.convertObjectToTable(zPene);
        
    }
}