/*:-----------------------------------------------------------------------------
 *:                       INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                     INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                         LENGUAJES Y AUTOMATAS II           
 *: 
 *:                  SEMESTRE: ___________    HORA: ___________ HRS
 *:                                   
 *:               
 *:         Clase con la funcionalidad del Analizador Sintactico
 *                 
 *:                           
 *: Archivo       : SintacticoSemantico.java
 *: Autor         : Fernando Gil  ( Estructura general de la clase  )
 *:                 Grupo de Lenguajes y Automatas II ( Procedures  )
 *: Fecha         : 03/SEP/2014
 *: Compilador    : Java JDK 7
 *: Descripción   : Esta clase implementa un parser descendente del tipo 
 *:                 Predictivo Recursivo. Se forma por un metodo por cada simbolo
 *:                 No-Terminal de la gramatica mas el metodo emparejar ().
 *:                 El analisis empieza invocando al metodo del simbolo inicial.
 *: Ult.Modif.    :
 *:  Fecha      Modificó            Modificacion
 *:=============================================================================
 *: 22/Feb/2015 FGil                -Se mejoro errorEmparejar () para mostrar el
 *:                                 numero de linea en el codigo fuente donde 
 *:                                 ocurrio el error.
 *: 08/Sep/2015 FGil                -Se dejo lista para iniciar un nuevo analizador
 *:                                 sintactico.
 *: 08/Sep/2017 FGil                - Cambiar el token opasig por opasigna. 
 *: 20/Feb/2018 FGil                - Preparar codigo para usarlo en el sem EJ/2018
 *: 09/Sep/2019 FGil                - Preparar codigo para usarlo en el sem AD/2019
 *:----------------------------------------------------------------------------
 */
package compilador;

import javax.swing.JOptionPane;

public class SintacticoSemanticoGramaticas {

    private Compilador cmp;
    private boolean analizarSemantica = false;
    private String preAnalisis;

    //--------------------------------------------------------------------------
    // Constructor de la clase, recibe la referencia de la clase principal del 
    // compilador.
    //
    public SintacticoSemanticoGramaticas(Compilador c) {
        cmp = c;
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    // Metodo que inicia la ejecucion del analisis sintactico predictivo.
    // analizarSemantica : true = realiza el analisis semantico a la par del sintactico
    //                     false= realiza solo el analisis sintactico sin comprobacion semantica
    public void analizar(boolean analizarSemantica) {
        this.analizarSemantica = analizarSemantica;
        preAnalisis = cmp.be.preAnalisis.complex;

        // * * *   INVOCAR AQUI EL PROCEDURE DEL SIMBOLO INICIAL   * * *
        Clase();

    }

    //--------------------------------------------------------------------------
    private void emparejar(String t) {
        if (cmp.be.preAnalisis.complex.equals(t)) {
            cmp.be.siguiente();

            preAnalisis = cmp.be.preAnalisis.complex;
        } else {
            errorEmparejar(t, cmp.be.preAnalisis.lexema, cmp.be.preAnalisis.numLinea);
        }
    }

    //--------------------------------------------------------------------------
    // Metodo para devolver un error al emparejar
    //--------------------------------------------------------------------------
    private void errorEmparejar(String _token, String _lexema, int numLinea) {
        String msjError = "";

        if (_token.equals("id")) {
            msjError += "Se esperaba un identificador";
        } else if (_token.equals("num")) {
            msjError += "Se esperaba una constante entera";
        } else if (_token.equals("num.num")) {
            msjError += "Se esperaba una constante real";
        } else if (_token.equals("literal")) {
            msjError += "Se esperaba una literal";
        } else if (_token.equals("oparit")) {
            msjError += "Se esperaba un operador aritmetico";
        } else if (_token.equals("oprel")) {
            msjError += "Se esperaba un operador relacional";
        } else if (_token.equals("opasigna")) {
            msjError += "Se esperaba operador de asignacion";
        } else {
            msjError += "Se esperaba " + _token;
        }
        msjError += " se encontró " + (_lexema.equals("$") ? "fin de archivo" : _lexema)
                + ". Linea " + numLinea;        // FGil: Se agregó el numero de linea

        cmp.me.error(Compilador.ERR_SINTACTICO, msjError);
    }

    // Fin de ErrorEmparejar
    //--------------------------------------------------------------------------
    // Metodo para mostrar un error sintactico
    private void error(String _descripError) {
        cmp.me.error(cmp.ERR_SINTACTICO,
                _descripError
                + "Linea: " + cmp.be.preAnalisis.numLinea);
    }

    // Fin de error
    //--------------------------------------------------------------------------
    // * * *   AQUI EMPIEZA  EL CODIGO DE LOS PROCEDURES    * * *
    //Autor: Alonso
    private void Clase() {
        /*
        Clase -> public class id {
                                    Declaraciones
                                    Declaraciones_metodos
                                    Metodo_principal
                                 }
         */
        if (preAnalisis.equals("public")) {
            emparejar("public");
            emparejar("class");
            emparejar("id");
            emparejar("{");
            Declaracion();
            //Declaraciones_metodos();
            //Metodo_principal();
            emparejar("}");
        } else {
            error("Se esperaba public para iniciar la clase");
        }
    }

    private void Declaracion () {
        if ( preAnalisis.equals( "public" ) ){
            emparejar ( "public" );
            emparejar ( "static" );
            Tipos ();
            Declaracion2 ();
        }
    }
    
    private void Declaracion2 (){
        if ( preAnalisis.equals( "public" ) ){
            Declaracion ();
        }else{
            //Declaracion2 -> empty
        }
    }
    
    private void Lista_identificadores() {
        //Lista_identificadores -> Dimension listaidentificadores2
        if (preAnalisis.equals("[")) {
            Dimension();
            Lista_identificadores2();
        } else if ( preAnalisis.equals( "," ) ){
            Lista_identificadores2 ();
        }
    }

    private void Lista_identificadores2() {
        //Lista_identificadores2 -> , id dimension listaidentificadores2
        if (preAnalisis.equals(",")) {
            emparejar(",");
            emparejar("id");
            Dimension();
            Lista_identificadores2();
        } else {
            //Lista_identificadores2 -> empty
        }
    }

    private void Declaraciones() {
        if (preAnalisis.equals("[") || preAnalisis.equals( "," ) || preAnalisis.equals( ";" )) {
            // Declaraciones -> ; declaraciones 
            Lista_identificadores();
            emparejar(";");
            Declaraciones();
        } else {
            //Declaraciones -> empty
        }
    }

    public void Retroceder() {
        //Retroceso();
    }

    private void Tipo () {
        Tipo_estandar();
    }
    
    private void Tipos() {
        if (preAnalisis.equals("int") || preAnalisis.equals("float")
                || preAnalisis.equals("String")) {
            //Tipo -> Tipo_estandar
            Tipo_metodo();
        } else if ( preAnalisis.equals( "void" ) ){
            emparejar ( "void" );
            Ides();
        }
    }
    
    private void Ides () {
        if ( preAnalisis.equals( "id" ) ){
            Ides2 ();
        }else if ( preAnalisis.equals( "main" )){
            emparejar ( "main" );
            Metodo_principal ();
        }
    }
    
    private void Ides2 () {
        if ( preAnalisis.equals ( "id" ) ){
            emparejar ( "id" );
            Ides3 ();
        }else {
            error ( "" );
        }
    }
    
    private void Ides3 () {
        if ( preAnalisis.equals ( "(" ) ){
            emparejar ( "(" );
            Declaraciones_metodos ();
        }else if ( preAnalisis.equals( "[" ) || preAnalisis.equals( "," )  || preAnalisis.equals(";") ){
            Declaraciones ();
        }else{
            error ("");
        } 
            
    }
    

    private void Tipo_estandar() {
        if (preAnalisis.equals("int")) //Tipo_estandar -> int
        {
            emparejar("int");
        } else if (preAnalisis.equals("float")) //Tipo_estandar -> float
        {
            emparejar("float");
        } else if (preAnalisis.equals("String")) //Tipo_estandar -> String
        {
            emparejar("String");
        } else {
            error("");
        }
    }

    private void Dimension() {
        if (preAnalisis.equals("[")) {
            //Dimension -> [ num ]  
            emparejar("[");
            emparejar("num");
            emparejar("]");
        } else {
            //Dimension -> empty
        }
    }

    private void Declaraciones_metodos() {
        if (preAnalisis.equals( "int" )  || preAnalisis.equals( "float" )  ||
                preAnalisis.equals( "String" ) || preAnalisis.equals( ")" ) ){
            //Declaraciones_metodos -> Declaracion_metodo Declaraciones_metodos
            Declaracion_metodo();
            Declaraciones_metodos();
        } else {
            //Declaraciones_metodos -> empty
        }
    }

    private void Declaracion_metodo() {
        if (preAnalisis.equals( "int" )  || preAnalisis.equals( "float" )  ||
                preAnalisis.equals( "String" ) || preAnalisis.equals( ")" ) ){
            //Declaracion_metodo -> Encab_metodo Proposicion_compuesta
            Encab_metodo();
            Proposicion_compuesta();
        } else {
            error("Error");
        }
        
    }

    private void Encab_metodo() {
        if (preAnalisis.equals( "int" )  || preAnalisis.equals( "float" )  ||
                preAnalisis.equals( "String" ) ){
            //Encab_metodo -> public static  tipo_metodo id ( lista_parametros )  
                Lista_parametros();
                emparejar(")");
            } else if (preAnalisis.equals( ")" )){
                emparejar ( ")" ); 
             }else {
                error ( "" );
            }
        
    }

    private void Metodo_principal() {
        if (preAnalisis.equals("(")) {
            /*Metodo_principal -> public static void  main ( String  args [ ]  
            )   proposición_compuesta
             */
            emparejar("(");
            emparejar("String");
            emparejar("args");
            emparejar("[");
            emparejar("]");
            emparejar(")");
            Proposicion_compuesta();
        } else {
            error("");
        }
    }

    private void Tipo_metodo() {
        if (preAnalisis.equals("void")) //Tipo_metodo -> void
        {
            emparejar("void");
        } else if (preAnalisis.equals("int") || preAnalisis.equals("float")
                || preAnalisis.equals("String")) //Tipo_metodo -> Tipo_estandar
        {
            Tipo_estandar();
            Ides();
        }

    }

    private void Lista_parametros() {
        //Lista_parametros -> tipo  id  dimension    lista_parametros2
        if (preAnalisis.equals("int") || preAnalisis.equals("float")
                || preAnalisis.equals("String")) {
            Tipo();
            emparejar("id");
            Dimension();
            Lista_parametros2();
        } else {
            //Lista_parametros -> empty
        }
    }

    private void Lista_parametros2() {
        if (preAnalisis.equals(",")) {
            //Lista_parametros2 -> ,  tipo  id  dimension   lista_parametros’
            emparejar(",");
            Tipo ();
            emparejar ( "id" );
            Dimension();
            Lista_parametros2();
        } else {
            //Lista_parametros2 -> empty  
        }
    }

    private void Proposicion_compuesta() {
        if (preAnalisis.equals("{")) {
            //Proposicion_compuesta -> { Proposiciones_optativas }
            emparejar("{");
            Proposiciones_optativas();
            emparejar("}");
        } else {
            error("");
        }

    }

    private void Proposiciones_optativas() {
        if (preAnalisis.equals("id") || preAnalisis.equals("{")
                || preAnalisis.equals("if") || preAnalisis.equals("while")) {
            //Proposiciones_optativas -> Lista_proposiciones
            Lista_proposiciones();
        } else {
            //Proposciones_optativas -> empty
        }
    }

    private void Lista_proposiciones() {
        if (preAnalisis.equals("id") || preAnalisis.equals("{")
                || preAnalisis.equals("if") || preAnalisis.equals("while")) {
            //Lista_Proposiciones -> Proposición  ;  Lista_proposiciones
            Proposicion();
            emparejar(";");
            Lista_proposiciones();
        }
    }

    private void Proposicion() {
        if (preAnalisis.equals("id")) {
            //Proposicion -> id  Proposicion2 
            emparejar("id");
            Proposicion2();
        } else if (preAnalisis.equals("{")) {
            //Proposicion -> Proposicion_compuesta
            Proposicion_compuesta();
        } else if (preAnalisis.equals("if")) {
            //Proposicion -> if ( Expresión ) Proposición else Proposición  
            emparejar("if");
            emparejar("(");
            Expresion();
            emparejar(")");
            Proposicion();
            emparejar("else");
            Proposicion();
        } else if (preAnalisis.equals("while")) {
            //Proposicion -> while ( Expresión ) Proposición
            emparejar("while");
            emparejar("(");
            Expresion();
            emparejar(")");
            Proposicion();
        } else {
            error("");
        }
    }

    private void Proposicion2() {
        if (preAnalisis.equals("[")) {
            //Proposicion2 -> [ Expresión ] opasig Expresión 
            emparejar("[");
            Expresion();
            emparejar("]");
            emparejar("opasig");
            Expresion();
        } else if (preAnalisis.equals("opasig")) {
            //Proposicion2 -> opasig Expresión 
            emparejar("opasig");
            Expresion();
        } else if (preAnalisis.equals("(")) {
            //Proposicion2 -> Proposicion_compuesta
            Proposicion_metodo();
        } else {
            //Proposicion2 -> empty
        }
    }

    private void Proposicion_metodo() {
        if (preAnalisis.equals("(")) {
            //Proposicion_metodo -> ( Lista_expresiones )  
            emparejar("(");
            Lista_expresiones();
            emparejar(")");
        } else {
            //Proposicion_metodo -> empty
        }
    }

    private void Lista_expresiones() {
        if (preAnalisis.equals("literal") || preAnalisis.equals("id")
                || preAnalisis.equals("num") || preAnalisis.equals("num.num")
                || preAnalisis.equals("(")) {
            Expresion();
            Lista_expresiones2();
        } else {
            error("");
        }
    }

    private void Lista_expresiones2() {
        if (preAnalisis.equals(",")) {
            //Lista_expresiones2 -> ,  Lista_expresiones 
            emparejar(",");
            Lista_expresiones();
        } else {
            //Lista_expresiones2 -> empty
        }
    }

    private void Expresion() {
        if (preAnalisis.equals("literal")) {
            //Expresion -> literal
            emparejar("literal");
        } else if (preAnalisis.equals("id") || preAnalisis.equals("num")
                || preAnalisis.equals("num.num") || preAnalisis.equals("(")) {
            //Expresion -> Expresion_simple Expresion2
            Expresion_simple();
            Expresion2();
        } else {
            error("");
        }
    }

    private void Expresion2() {
        if (preAnalisis.equals("oprel")) {
            //Expresion2 -> oprel Expresion_simple
            emparejar("oprel");
            Expresion_simple();
        } else {
            //Expresion2 -> empty
        }
    }

    private void Expresion_simple() {
        if (preAnalisis.equals("id") || preAnalisis.equals("num")
                || preAnalisis.equals("num.num") || preAnalisis.equals("(")) {
            //Expresion_simple -> Termino Expresion_simple2
            Termino();
            Expresion_simple2();
        } else {
            error("");
        }
    }

    private void Expresion_simple2() {
        if (preAnalisis.equals("opsuma")) {
            //Expresion_simple2 -> opsuma Termino Expresion2
            emparejar("opsuma");
            Termino();
            Expresion_simple2();
        } else {
            //Expresion_simple2 -> empty
        }
    }

    private void Termino() {
        if (preAnalisis.equals("id") || preAnalisis.equals("num")
                || preAnalisis.equals("num.num") || preAnalisis.equals("(")) {
            //Termino -> Factor Termino2
            Factor();
            Termino2();
        } else {
            error("");
        }
    }

    private void Termino2() {
        if (preAnalisis.equals("opmult")) {
            //Termino2 -> opmult Factor Termino2
            emparejar("opmult");
            Factor();
            Termino2();
        } else {
            //Termino2 -> empty
        }
    }

    private void Factor() {
        if (preAnalisis.equals("id")) {
            //Factor -> id
            emparejar("id");
            Factor2();
        } else if (preAnalisis.equals("num")) {
            //Factor -> num
            emparejar("num");
        } else if (preAnalisis.equals("num.num")) {
            //Factor -> num.num
            emparejar("num.num");
        } else if (preAnalisis.equals("(")) {
            //Factor -> ( Expresion )
            emparejar("(");
            Expresion();
            emparejar(")");
        } else {
            error("");
        }
    }

    private void Factor2() {
        if (preAnalisis.equals("(")) {
            //Factor2 -> ( Lista_expresiones )
            emparejar("(");
            Lista_expresiones();
            emparejar(")");
        } else {
            //Factor2 -> empty
        }
    }

    private void Retroceso(int x) {
        for (int i = 0; i < x; i++) {
            cmp.be.anterior();
        }
        preAnalisis = cmp.be.preAnalisis.complex;
    }

}
//------------------------------------------------------------------------------
//::
