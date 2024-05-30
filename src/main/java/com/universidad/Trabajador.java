package com.universidad;

public class Trabajador{
    private int id;
    private String nombre;
    private int edad;
    private String puesto;
    
    public Trabajador(int trabajador_id,String nombre, int edad, String puesto)
    {
        this.id=trabajador_id;
        this.nombre = nombre;
        this.edad = edad;
        this.puesto = puesto;
    }

    public Trabajador() {
    }

    public int getId() {
        return id;
    }

    public void setId(int trabajador_id) {
        this.id = trabajador_id;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    } 

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

}
