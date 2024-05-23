package com.universidad;

public class Trabajador {
    private int id;
    public int getTrabajador_id() {
        return id;
    }

    public void setTrabajador_id(int trabajador_id) {
        this.id = trabajador_id;
    }

    private String puesto;

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    private String nombre;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    private int edad;

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }
    public Trabajador(){}

    public Trabajador(int trabajador_id,String nombre, int edad, String puesto) {
        this.id=trabajador_id;
        this.nombre = nombre;
        this.edad = edad;
        this.puesto = puesto;
    }
}
