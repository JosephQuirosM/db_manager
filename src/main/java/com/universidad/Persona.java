package com.universidad;

class Persona
{
    private int id;
    private String nombre;
    private int edad;
 
    public int getId() {
         return id;
     }
 
    public void setId(int id) {
         this.id = id;
     }
 
    public String getNombre() {
       return this.nombre;
    }
 
    public void setNombre(String var1) {
       this.nombre = var1;
    }
 
    public int getEdad() {
       return this.edad;
    }
 
    public void setEdad(int var1) {
       this.edad = var1;
    }
 
    public Persona(int var1,String var2, int var3) {
       this.id = var1;
       this.nombre = var2;
       this.edad = var3;
    }
 }