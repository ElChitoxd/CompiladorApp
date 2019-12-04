/*:-----------------------------------------------------------------------------
 *:                       INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                     INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                         LENGUAJES Y AUTOMATAS II           
 *: 
 *:        SEMESTRE: ______________            HORA: ______________ HRS
 *:                                   
 *:               
 *:    # Clase con la funcionalidad del Generador de COdigo Intermedio
 *                 
 *:                           
 *: Archivo       : GenCodigoInt.java
 *: Autor         : Fernando Gil  
 *: Fecha         : 03/SEP/2014
 *: Compilador    : Java JDK 7
 *: Descripción   :  
 *:                  
 *:           	     
 *: Ult.Modif.    :
 *:  Fecha      Modificó            Modificacion
 *:=============================================================================
 *:-----------------------------------------------------------------------------
 */


package compilador;

import java.util.Stack;


public class GenCodigoInt {
 
    public static final int NIL = 0;
    private Stack pilaC3D;
    private Compilador cmp;
    private int        consecTemp;
    private int        consecutivoEtiq;  
    private String     preAnalisis;
    private boolean    retroceder;
    private int        p;
    
    
    //--------------------------------------------------------------------------
    // Constructor de la clase, recibe la referencia de la clase principal del 
    // compilador.
    //
	public GenCodigoInt ( Compilador c ) {
        cmp = c;
    }
    // Fin del Constructor
    //--------------------------------------------------------------------------
	
    public void generar () {
        pilaC3D = new Stack();
        pilaC3D = new Stack();
        preAnalisis = cmp.be.preAnalisis.complex;
        consecTemp = 1;
    }
    
    //--------------------------------------------------------------------------

    private void emite ( String c3d ) {
        cmp.erroresListener.mostrarCodInt ( c3d );
    }
    
    //--------------------------------------------------------------------------
    
    private String tempnuevo () {
        return "t" + consecTemp++;
    }
    
    //--------------------------------------------------------------------------
    
    private String etiqnueva () {
        return "etiq" + consecutivoEtiq++;
    }
    
    //--------------------------------------------------------------------------
    
    //***********Producciones************//
    //--------------------------------------------------------------------------
    // * * *   AQUI EMPIEZA  EL CODIGO DE LOS PROCEDURES    * * *
    //Autor: Alonso
    
    private void Clase () {
        /*
        Clase -> public class id {
                                    Declaraciones
                                    Declaraciones_metodos
                                    Metodo_principal
                                 }
        */
        if( preAnalisis.equals ( "public" ) ){
            emparejar ( "public" );
            emparejar ( "class" );
            emparejar ( "id" );
            emparejar ( "{" );
            Declaraciones ();
            Declaraciones_metodos ();
            Metodo_principal ();
            emparejar ( "}" );
        } else {
            error ("Se esperaba public para iniciar la clase");
        }
    }
    
    private void Lista_identificadores () {
        //Lista_identificadores -> id Dimension Lista_identificadores2
        if ( preAnalisis.equals( "id" ) ) {
            emparejar ( "id" );
            Dimension ();
            Lista_identificadores2 ();
        } else {
            error ( "Se esperaba un identificador o variable" );
        }
    }
    
    private void Lista_identificadores2 (){
        //Lista_identificadores2 -> , id Dimension Lista_identificadores2
        if ( preAnalisis.equals( "," ) ) {
            emparejar ( "," );
            emparejar ( "id" );
            Dimension ();
            Lista_identificadores2 ();
        }else {
            //Lista_identificadores2 -> empty
        }
    }
    
    private void Declaraciones () {
        if ( preAnalisis.equals( "public" ) ) {
            // Declaraciones -> public static  Tipo Lista_identificadores 
            //                  ; Declaraciones 
            emparejar ( "public" );
            emparejar ( "static" );
            if( preAnalisis.equals( "void" ) )
            {
            Retroceso(2);
            return;
            }
            Tipo ();
            Lista_identificadores ();
            if ( preAnalisis.equals( "(" ) ){
                Retroceso (4);
                return;
            }
            emparejar ( ";" );
            Declaraciones ();  
        }else {
            //Declaraciones -> empty
        }
    }
    
    private void Tipo () {
        if ( preAnalisis.equals( "int" )|| preAnalisis.equals( "float" ) || 
                preAnalisis.equals( "String" ) ){
            //Tipo -> Tipo_estandar
            Tipo_estandar ();
        }else {
            error ( "Se esperaba un tipo primitivo" );
        }
    }
    
    private void Tipo_estandar () {
        if ( preAnalisis.equals( "int" ))
            //Tipo_estandar -> int
            emparejar ( "int" );
        else if( preAnalisis.equals( "float" ) )
            //Tipo_estandar -> float
                emparejar ( "float" );
            else if ( preAnalisis.equals( "String" ) )
                //Tipo_estandar -> String
                    emparejar ( "String" );
                else
                    error ( "Se esperaba un tipo primitivo" ) ;
    }
    
    private void Dimension () {
        if ( preAnalisis.equals( "[" ) ){
            //Dimension -> [ num ]  
            emparejar ( "["   );
            emparejar ( "num" );
            emparejar ( "]"   );
        }else{
            //Dimension -> empty
        }
    }
    
    private void Declaraciones_metodos () {
        if ( preAnalisis.equals( "public" ) ) {
            //Declaraciones_metodos -> Declaracion_metodo Declaraciones_metodos
            Declaracion_metodo ();
            if( !retroceder)
                Declaraciones_metodos ();
        }else {
            //Declaraciones_metodos -> empty
        }
    }
    
    private void Declaracion_metodo () {
        if ( preAnalisis.equals( "public" ) ) {
            //Declaracion_metodo -> Encab_metodo Proposicion_compuesta
            Encab_metodo ();
            if( !retroceder)
                Proposicion_compuesta ();
        } else {
            error ( "La declaracion de los metodos empieza deben de ser publicos" );
        }
    }
    
    private void Encab_metodo () {
        retroceder = false;
        if ( preAnalisis.equals( "public" ) ) {
            //Encab_metodo -> public static  Tipo_metodo id ( Lista_parametros )  
            emparejar ( "public" );
            emparejar ( "static" );
            Tipo_metodo ();
            if ( preAnalisis.equals("main") ){
                Retroceso (3);
                retroceder = true;
                return;
            }
            emparejar ( "id" );
                emparejar ( "(" );
                Lista_parametros ();
            emparejar ( ")" );
            
            
        }
        else {
            error ("La declaracion de los metodos deben de ser publicos");
        }
    }
    
    private void Metodo_principal () {
        if ( preAnalisis.equals( "public" ) ) {
            /*Metodo_principal -> public static void  main ( String  args [ ]  
            )   proposición_compuesta
            */
            emparejar ( "public" );
            emparejar ( "static" );
            emparejar ( "void" );
            emparejar ( "main" );
            emparejar ( "(" );
            emparejar ( "String" );
            emparejar ( "args" );
            emparejar ( "[" );
            emparejar ( "]" );
            emparejar ( ")" );
            Proposicion_compuesta ();
        }else {
            error ( "El metodo principal debe ser publico" );
        }
    }
    
    private void Tipo_metodo () {
        if ( preAnalisis.equals( "void" ) )
            //Tipo_metodo -> void
            emparejar ( "void" );
        else if ( preAnalisis.equals( "int" )|| preAnalisis.equals( "float" ) || 
                preAnalisis.equals( "String" ) )
            //Tipo_metodo -> Tipo_estandar
            Tipo_estandar ();
        
    }
    
    private void Lista_parametros () {
        //Lista_parametros -> tipo  id  dimension    lista_parametros2
        if ( preAnalisis.equals( "int" )|| preAnalisis.equals( "float" ) || 
                preAnalisis.equals( "String" ) ){
            Tipo ();
            emparejar ( "id" );
            Dimension ();
            Lista_parametros2 ();
        }else {
            //Lista_parametros -> empty
        }
    }
    
    private void Lista_parametros2 () {
        if ( preAnalisis.equals( "," ) ) {
            //Lista_parametros2 -> ,  tipo  id  dimension   lista_parametros’
            emparejar ( "," );
            Tipo();
            emparejar ( "id" );
            Dimension ();
            Lista_parametros2 ();
        }else{
          //Lista_parametros2 -> empty  
        } 
    }
    
    private void Proposicion_compuesta () {
        if ( preAnalisis.equals( "{" ) ) {
            //Proposicion_compuesta -> { Proposiciones_optativas }
            emparejar ( "{" );
            Proposiciones_optativas ();
            emparejar ( "}" );
        }else{
            error ( "Se esperaba llave que abre {" );
        }
            
    }
    
    private void Proposiciones_optativas () {
        if ( preAnalisis.equals( "id" ) || preAnalisis.equals( "{" ) ||
                preAnalisis.equals( "if" ) || preAnalisis.equals( "while" ) ){
            //Proposiciones_optativas -> Lista_proposiciones
            Lista_proposiciones ();
        }else{
            //Proposciones_optativas -> empty
        }
    }
        
    private void Lista_proposiciones () {
        if ( preAnalisis.equals( "id" ) || preAnalisis.equals( "{" ) ||
                preAnalisis.equals( "if" ) || preAnalisis.equals( "while" ) ){
            //Lista_Proposiciones -> Proposición  ;  Lista_proposiciones
            Proposicion ();
            
            Lista_proposiciones ();
        }
    }
    
    private void Proposicion () {
        if ( preAnalisis.equals( "id" ) ){
            //Proposicion -> id  Proposicion2 ;
            emparejar ( "id" );
            Proposicion2 ();
            emparejar ( ";" );
        }else if( preAnalisis.equals( "{" ) ){
            //Proposicion -> Proposicion_compuesta
            Proposicion_compuesta ();
        }else if ( preAnalisis.equals( "if" ) ){
            //Proposicion -> if ( Expresión ) Proposición else Proposición  
            emparejar ( "if"   );
            emparejar ( "("    );
            Expresion ();
            emparejar ( ")"    );
            Proposicion ();
            emparejar ( "else" );
            Proposicion ();
        } else if(preAnalisis.equals( "while" ) ){
            //Proposicion -> while ( Expresión ) Proposición
            emparejar ( "while" );
            emparejar ( "("     );
            Expresion ();
            emparejar ( ")"     );
            Proposicion ();
        } else {
            error ( "Preposicion no válida" );
        }
}
    private void Proposicion2 () {
        if ( preAnalisis.equals( "[" ) ){
            //Proposicion2 -> [ Expresión ] opasig Expresión 
            emparejar ( "["      );
            Expresion ();
            emparejar ( "]"      );
            emparejar ( "opasig" );
            Expresion ();
        }else if ( preAnalisis.equals( "opasig" ) ){
            //Proposicion2 -> opasig Expresión 
            emparejar ( "opasig" );
            Expresion ();
        }else if ( preAnalisis.equals( "(" ) ){
            //Proposicion2 -> Proposicion_compuesta
            Proposicion_metodo ();
        }else {
            //Proposicion2 -> empty
        }
    }
    
    private void Proposicion_metodo () {
        if ( preAnalisis.equals( "(" ) ){
            //Proposicion_metodo -> ( Lista_expresiones )  
            emparejar ( "(" );
            Lista_expresiones ();
            emparejar ( ")" );
        }else {
            //Proposicion_metodo -> empty
        }
    }
    
    private void Lista_expresiones () {
        if ( preAnalisis.equals( "literal" ) || preAnalisis.equals( "id" ) ||
                preAnalisis.equals( "num" )|| preAnalisis.equals( "num.num" ) ||
                preAnalisis.equals( "(" ) ){
            Expresion ();
            Lista_expresiones2 ();
        } else {
            //Lista expresiones -> empty
            
        }
    }
    
    private void Lista_expresiones2 () {
        if ( preAnalisis.equals( "," ) ){
            //Lista_expresiones2 -> , Expresion Lista_expresiones 
            emparejar ( "," );
            Expresion ();
            Lista_expresiones ();
        }else {
            //Lista_expresiones2 -> empty
        }
    }
    
    private void Expresion () {
        if ( preAnalisis.equals( "literal" ) ){
            //Expresion -> literal
            emparejar ( "literal" );
        } else if ( preAnalisis.equals("id") || preAnalisis.equals("num") ||
                preAnalisis.equals("num.num") || preAnalisis.equals("(")){
            //Expresion -> Expresion_simple Expresion2
            Expresion_simple ();
            Expresion2 ();
        }else {
            error ( "Se esperaba algun tipo primitivo o literal " );
        }
    }
    
    private void Expresion2() {
        if ( preAnalisis.equals( "oprel" ) ) {
            //Expresion2 -> oprel Expresion_simple
            emparejar ( "oprel" );
            Expresion_simple ();
        }else {
            //Expresion2 -> empty
        }
    }
    
    private void Expresion_simple () {
        if ( preAnalisis.equals("id") || preAnalisis.equals("num") ||
                preAnalisis.equals("num.num") || preAnalisis.equals("(")){
            //Expresion_simple -> Termino Expresion_simple2
            Termino ();
            Expresion_simple2 ();
        } else 
            error ( "Se esperaba un término" );
    }
    
    private void Expresion_simple2 () {
        if ( preAnalisis.equals( "opsuma" ) ) {
            //Expresion_simple2 -> opsuma Termino Expresion2
            emparejar ( "opsuma" );
            Termino ();
            Expresion_simple2 ();
        } else {
            //Expresion_simple2 -> empty
        }
    }
    
    private void Termino(){
        if ( preAnalisis.equals("id") || preAnalisis.equals("num") ||
                preAnalisis.equals("num.num") || preAnalisis.equals("(")) {
                //Termino -> Factor Termino2
                Factor ();
                Termino2 (); 
        } else {
          error ( "Se esperaba un termino" );  
        }
    }
    
    private void Termino2 (){
        if ( preAnalisis.equals( "opmult" ) ) {
            //Termino2 -> opmult Factor Termino2
            emparejar ( "opmult" );
            Factor();
            Termino2();
        } else {
            //Termino2 -> empty
        }
    }
    
    private void Factor() {
        if ( preAnalisis.equals( "id" ) ){
            //Factor -> id
            emparejar ( "id" );
            Factor2 ();
        } else if ( preAnalisis.equals( "num" ) ){
            //Factor -> num
            emparejar ( "num" );
        } else if ( preAnalisis.equals( "num.num" ) ){
            //Factor -> num.num
            emparejar ( "num.num" );
        } else if ( preAnalisis.equals( "(" ) ){
            //Factor -> ( Expresion )
            emparejar ( "(" );
            Expresion ();
            emparejar ( ")" );
        } else {
            error ( "Se esperaba un termino" );
        }
    }
    
    private void Factor2 (){
        if ( preAnalisis.equals( "(" ) ){
            //Factor2 -> ( Lista_expresiones )
            emparejar ( "(" );
            Lista_expresiones ();
            emparejar ( ")" );
        }else {
            //Factor2 -> empty
        }
    }

    private void Retroceso(int x) {
        for(int i=0;i<x;i++){
            cmp.be.anterior();
        }
        preAnalisis = cmp.be.preAnalisis.complex;
    }
    
    //***********Fin Producciones*******//
    
    //************EMPAREJAR**************//
    private void emparejar ( String t ) {
	if (cmp.be.preAnalisis.complex.equals ( t ) )
		cmp.be.siguiente ();
	else
		errorEmparejar ( "Se esperaba " + t + " se encontró " +
                                 cmp.be.preAnalisis.lexema );
    }	
	
    //--------------------------------------------------------------------------
    // Metodo para devolver un error al emparejar
    //--------------------------------------------------------------------------

    private void errorEmparejar ( String _token ) {
        String msjError = "ERROR SINTACTICO: ";
              
        if ( _token.equals ( "id" ) )
            msjError += "Se esperaba un identificador" ;
        else if ( _token.equals ( "num" ) )
            msjError += "Se esperaba una constante entera" ;
        else if ( _token.equals ( "num.num" ) )
            msjError += "Se esperaba una constante real";
        else if ( _token.equals ( "literal" ) )
            msjError += "Se esperaba una literal";
        else if ( _token.equals ( "oparit" ) )
            msjError += "Se esperaba un Operador Aritmetico";
        else if ( _token.equals ( "oprel" ) )
            msjError += "Se esperaba un Operador Relacional";
        else 
            msjError += "Se esperaba " + _token;
                
        cmp.me.error ( Compilador.ERR_SINTACTICO, msjError );    
    }            

    // Fin de ErrorEmparejar
    //--------------------------------------------------------------------------
	
    //--------------------------------------------------------------------------
    // Metodo para mostrar un error sintactico
 
    private void error ( String _token ) {
        cmp.me.error ( cmp.ERR_SINTACTICO,
         "ERROR SINTACTICO: en la produccion del simbolo  " + _token );
    }
 
    // Fin de error
    //--------------------------------------------------------------------------

}
