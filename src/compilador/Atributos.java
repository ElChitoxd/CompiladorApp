/*:-----------------------------------------------------------------------------
 *:                       INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                     INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                         LENGUAJES Y AUTOMATAS II           
 *: 
 *:                  SEMESTRE: 7    HORA: 18 HRS
 *:                                   
 *:               
 *:         Clase con la funcionalidad de definir los atributos de las producciones
 *                 
 *:                           
 *: Archivo       : Atributos.java
 *: Autor         : Jose Alonso Martinez Rios  ( Estructura general de la clase  )
 *:                 Grupo de Lenguajes y Automatas II ( Procedures  )
 *: Fecha         : 11/NOV/2019
 *: Compilador    : Java jdk ??
 *: Descripción   : Esta clase implementa los atributos de los simbolos
                    no gramaticales
 *: Ult.Modif.    : 
 *:  Fecha      Modificó            Modificacion
 *:=============================================================================
 *:
 *:
 *:----------------------------------------------------------------------------
 */


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

/**
 *
 * @author Alonso Martinez
 */
public class Atributos {
    String tipo   = "";
    String h      = "";
    String sin    = "";
    String length = "";
    public String  Lugar;
    public String  op;
    public String  verdadera;
    public String  falsa;
    public String  comienzo;
    public String  siguiente;
    
    public Atributos () {
        Lugar     = "";
        op        = "";
        verdadera = "";
        falsa     = "";
        comienzo  = "";
        siguiente = "";
    }
}
