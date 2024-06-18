package com.universidad;
import java.util.List;
        
public class Main
{
    public static void main(String[] args)
    {
        //"mongodb://localhost:27017", ""

        OracleMapping oracle = new OracleMapping("jdbc:mysql://localhost:3306/db", "root", "1234");
        Trabajador trabajador1 = new Trabajador(109, "Juanito", 39, "Profe");
        List<Trabajador> array = oracle.selectAll(Trabajador.class);

        for(int i = 0; i < array.size(); i++){
            oracle.deleteObjectFromTable(array.get(i));
        }

    }
}