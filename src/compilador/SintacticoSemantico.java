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

public class SintacticoSemantico {
    
    public static final String VACIO = "vacio";
    public static final String ERROR_TIPO = "error_tipo";
    private Compilador cmp;
    private boolean    analizarSemantica = false;
    private String     preAnalisis;
    private boolean    retroceder;
    //--------------------------------------------------------------------------
    // Constructor de la clase, recibe la referencia de la clase principal del 
    // compilador.
    //

    public SintacticoSemantico(Compilador c) {
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
        Clase ( new Atributos() );

    }

    //--------------------------------------------------------------------------

    private void emparejar(String t) {
        if (cmp.be.preAnalisis.complex.equals(t)) {
            cmp.be.siguiente();
            
            preAnalisis = cmp.be.preAnalisis.complex;            
        } else {
            errorEmparejar( t, cmp.be.preAnalisis.lexema, cmp.be.preAnalisis.numLinea );
        }
    }
    
    //--------------------------------------------------------------------------
    // Metodo para devolver un error al emparejar
    //--------------------------------------------------------------------------
 
    private void errorEmparejar(String _token, String _lexema, int numLinea ) {
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
        msjError += " se encontró " + ( _lexema.equals ( "$" )? "fin de archivo" : _lexema ) + 
                    ". Linea " + numLinea;        // FGil: Se agregó el numero de linea

        cmp.me.error(Compilador.ERR_SINTACTICO, msjError);
    }

    // Fin de ErrorEmparejar
    //--------------------------------------------------------------------------
    // Metodo para mostrar un error sintactico

    private void error(String _descripError) {
        cmp.me.error ( cmp.ERR_SINTACTICO,
                       _descripError + 
                       "Linea: " + cmp.be.preAnalisis.numLinea );
    }

    // Fin de error
    //--------------------------------------------------------------------------
    // * * *   AQUI EMPIEZA  EL CODIGO DE LOS PROCEDURES    * * *
    //Autor: Alonso
    private void Clase (Atributos Clase) {
        Atributos Declaraciones = new Atributos();
        Atributos Declaraciones_metodos = new Atributos();
        Atributos Metodo_principal = new Atributos();
        Linea_BE id = new Linea_BE();
        /*
        Clase -> public class id {1} {
                                    Declaraciones
                                    Declaraciones_metodos
                                    Metodo_principal
                                 } {11}
        */
        if( preAnalisis.equals ( "public" ) ){
            emparejar ( "public" );
            emparejar ( "class" );
            id = cmp.be.preAnalisis;
            emparejar ( "id" );
            if ( analizarSemantica ){
            //Inicio ACCION SEMANTICA 1
            cmp.ts.anadeTipo(id.entrada, "clase");
            //FIN ACCION SEMANTICA 1
            }
            emparejar ( "{" );
            Declaraciones ( Declaraciones );
            Declaraciones_metodos ( Declaraciones_metodos );
            Metodo_principal ( Metodo_principal );
            emparejar ( "}" );
            //INICIO ACCION SEMANTICA 11
            if ( analizarSemantica ){
            if ( cmp.ts.buscaTipo(id.entrada).equals("clase") && 
                 Declaraciones.tipo.equals(VACIO) &&
                 Declaraciones_metodos.tipo.equals(VACIO) &&
                 Metodo_principal.tipo.equals(VACIO)){
                Clase.tipo = VACIO;
            } else {
                Clase.tipo = ERROR_TIPO;
                cmp.me.error(Compilador.ERR_SEMANTICO, 
                        "[Clase] Algun tipo en el programa o no se definio como"
                                + " tipo de clase"+"Linea: " + cmp.be.preAnalisis.numLinea);
            }
            }
            //FIN ACCION SEMANTICA 11
        } else {
            error ("Se esperaba public para iniciar la clase");
        }
    }
    
    private void Lista_identificadores (Atributos Lista_identificadores) {
        Atributos Dimension = new Atributos();
        Atributos Lista_identificadores2 = new Atributos();
        Linea_BE id = new Linea_BE();
        //Lista_identificadores -> id Dimension {9} Lista_identificadores2 {14}
        if ( preAnalisis.equals( "id" ) ) {
            id = cmp.be.preAnalisis;
            emparejar ( "id" );
            Dimension ( Dimension );
            // INICIO DE LA ACCION SEMANTICA 9
            
            if ( analizarSemantica ){
                if ( cmp.ts.buscaTipo(id.entrada).equals("") ){
                if( cmp.ts.buscaTipo(id.entrada).equals("")){
            if ( Dimension.length.equals("") ){
                cmp.ts.anadeTipo(id.entrada, Lista_identificadores.h);
                Lista_identificadores.sin = Integer.toString(id.entrada);
            }else
                cmp.ts.anadeTipo(id.entrada,cmp.ts.array(Dimension.length, Lista_identificadores.h));
            Lista_identificadores2.h = Lista_identificadores.h;
            }
            }
                else {
                Lista_identificadores.tipo = ERROR_TIPO;
                cmp.me.error(Compilador.ERR_SEMANTICO,"[Lista_identificadores]"
                        + "El identificador"+id.getLexema()+
                        " ya ha sido declarado"
                        + " Linea: "+cmp.be.preAnalisis.getNumLinea());
            
            }
            }
            // FIN DE LA ACCION SEMANTICA 9
            Lista_identificadores2 ( Lista_identificadores2 );
            // INICIO DE LA ACCION SEMANTICA 14
            if ( analizarSemantica ){
            if ( Lista_identificadores2.tipo.equals(VACIO) )
                 Lista_identificadores.tipo = VACIO;
            else{
                Lista_identificadores.tipo = ERROR_TIPO;
                cmp.me.error(Compilador.ERR_SEMANTICO,"[Lista_Identificadores]"
                        + "Hubo un error al definir a los identificadores"
                +"Linea: " + cmp.be.preAnalisis.numLinea);
            }
            }
            // FIN DE LA ACCION SEMANTICA 14
        } else {
            error ( "Se esperaba un identificador o variable" );
        }
    }
    
    private void Lista_identificadores2 (Atributos Lista_identificadores2){
        Atributos Dimension = new Atributos();
        Atributos Lista_identificadores21 = new Atributos();
        Linea_BE id = new Linea_BE();
        //Lista_identificadores2 -> , id Dimension {10} Lista_identificadores2 {15}
        if ( preAnalisis.equals( "," ) ) {
            emparejar ( "," );
            id = cmp.be.preAnalisis;
            emparejar ( "id" );
            Dimension (Dimension);
            // INICIO DE LA ACCION SEMANTICA 10
            if ( analizarSemantica ){
                if ( cmp.ts.buscaTipo(id.entrada).equals("") ){
            if ( Dimension.length.equals("") )
                cmp.ts.anadeTipo(id.entrada, Lista_identificadores2.h);
            else
                cmp.ts.anadeTipo(id.entrada,cmp.ts.array(Dimension.length, Lista_identificadores2.h));
            Lista_identificadores21.h = Lista_identificadores2.h;
                }else{
                    Lista_identificadores2.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO,"[Lista_identificado"
                            + "res2] El identificador "+id.getLexema()
                            +" Ya ha sido declarado Linea: "
                                    + id.getNumLinea() );
                }
            }
            // FIN DE LA ACCION SEMANTICA 10
            Lista_identificadores2 (Lista_identificadores21);
            // INICIO DE LA ACCION SEMANTICA 15
            if ( analizarSemantica ){
            if ( Lista_identificadores21.tipo.equals( VACIO ))
                    Lista_identificadores2.tipo = VACIO; 
            else{
                Lista_identificadores2.tipo = ERROR_TIPO;
                cmp.me.error(Compilador.ERR_SEMANTICO,"[Lista_identificadores2]"
                        + "Hubo un error al definir a los identificadores"+
                        "Linea: " + cmp.be.preAnalisis.numLinea);
            }
            }
            // FIN DE LA ACCION SEMANTICA 15
            
        }else {
            //Lista_identificadores2 -> empty {16}
            // INICIO DE LA ACCION SEMANTICA 16
            if(analizarSemantica)
                Lista_identificadores2.tipo = VACIO;
            // FIN DE LA ACCION SEMANTICA 16
        }
    }
    
    private void Declaraciones (Atributos Declaraciones) {
        Atributos Tipo = new Atributos();
        Atributos Lista_identificadores = new Atributos();
        Atributos Declaraciones1 = new Atributos();
        if ( preAnalisis.equals( "public" ) ) {
            // Declaraciones -> public static  Tipo {6} Lista_identificadores 
            //                  ; Declaraciones {12}
            emparejar ( "public" );
            emparejar ( "static" );
            if( preAnalisis.equals( "void" ) )
            {
            Retroceso(2);
            Declaraciones.tipo = VACIO;
            return;
            }
            Tipo ( Tipo );
            if ( analizarSemantica ) {
                // INICIO DE LA ACCION SEMANTICA 6
                Lista_identificadores.h = Tipo.tipo;
                // FIN DE LA ACCION SEMANTICA 6
            }
            Lista_identificadores (Lista_identificadores);
            if ( preAnalisis.equals( "(" ) ){
                Retroceso (4);
                if ( analizarSemantica )
                    cmp.ts.eliminaTipo(Integer.parseInt(Lista_identificadores.sin));
                Declaraciones.tipo = VACIO;
                return;
            }
            emparejar ( ";" );
            Declaraciones (Declaraciones1);
            if( analizarSemantica ){
                // INICIO DE LA ACCION SEMANTICA 12
                if ( analizarSemantica ){
                    if ( Lista_identificadores.tipo.equals(VACIO) &&
                         Declaraciones1.tipo.equals(VACIO) )
                         Declaraciones.tipo = VACIO;
                    else{
                        Declaraciones.tipo = ERROR_TIPO;
                        cmp.me.error(Compilador.ERR_SEMANTICO,"[Declaraciones]"
                                + "Error al declarar los identificadores"+
                                "Linea: " + cmp.be.preAnalisis.numLinea);
                    }
                }
                    
                // FIN DE LA ACCION SEMANTICA 12
            }
        }else {
            //Declaraciones -> empty {13}
            if ( analizarSemantica ){
                //INICIO DE LA ACCION SEMANTICA 13
                Declaraciones.tipo = VACIO;
                // FIN DE LA ACCION SEMANTICA 13
            }
        }
    }
    
    private void Tipo (Atributos Tipo) {
        Atributos Tipo_estandar = new Atributos();
        if ( preAnalisis.equals( "int" )|| preAnalisis.equals( "float" ) || 
                preAnalisis.equals( "String" ) ){
            //Tipo -> Tipo_estandar {2}
            Tipo_estandar (Tipo_estandar);
            if ( analizarSemantica ){
                // INICIO DE LA ACCION SEMANTICA 2
                Tipo.tipo = Tipo_estandar.tipo;
                // FIN DE LA ACCION SEMANTICA 2
            }
            
        }else {
            error ( "Se esperaba un tipo primitivo" );
        }
    }
    
    private void Tipo_estandar (Atributos Tipo_estandar) {
        if ( preAnalisis.equals( "int" )){
            //Tipo_estandar -> int {3}
            emparejar ( "int" );
        if( analizarSemantica ){
            // INICIO ACCION SEMANTICA 3
            Tipo_estandar.tipo = "int";
            // FIN DE LA ACCION SEMANTICA 3
        }
        }
        else if( preAnalisis.equals( "float" ) ){
            //Tipo_estandar -> float {4}
                emparejar ( "float" );
        if( analizarSemantica ){
            // INICIO ACCION SEMANTICA 4
            Tipo_estandar.tipo = "float";
            // FIN DE LA ACCION SEMANTICA 4
        }
        }
            else if ( preAnalisis.equals( "String" ) ){
                //Tipo_estandar -> String {5}
                    emparejar ( "String" );
                    if( analizarSemantica ){
            // INICIO ACCION SEMANTICA 5
            Tipo_estandar.tipo = "String";
            // FIN DE LA ACCION SEMANTICA 5
        }
            }
                else
                    error ( "Se esperaba un tipo primitivo" ) ;
    }
    
    private void Dimension (Atributos Dimension) {
        Linea_BE num = new Linea_BE();
        if ( preAnalisis.equals( "[" ) ){
            //Dimension -> [ num ]  {7}
            emparejar ( "["   );
            num = cmp.be.preAnalisis;
            emparejar ( "num" );
            emparejar ( "]"   );
            if ( analizarSemantica ){
                //INICIO ACCION SEMANTICA 7
                Dimension.length = num.lexema;
                // FIN DE LA ACCION SEMANTICA 7
            }
        }else{
            //Dimension -> empty {8}
            if ( analizarSemantica ){
                //INICIO ACCION SEMANTICA 8
                Dimension.length = "";
                // FIN DE LA ACCION SEMANTICA 8
            }
        }
    }
    
    private void Declaraciones_metodos (Atributos Declaraciones_metodos) {
        Atributos Declaracion_metodo = new Atributos();
        Atributos Declaraciones_metodos1 = new Atributos();
        if ( preAnalisis.equals( "public" ) ) {
            //Declaraciones_metodos -> Declaracion_metodo Declaraciones_metodos1 {38}
            Declaracion_metodo (Declaracion_metodo);
            if( !retroceder){
                Declaraciones_metodos (Declaraciones_metodos1);
                if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 38
                    if(Declaracion_metodo.tipo.equals(VACIO) &&
                       Declaraciones_metodos1.tipo.equals(VACIO) )
                            Declaraciones_metodos.tipo = VACIO;
                    else{
                            Declaraciones_metodos.tipo = ERROR_TIPO;
                            cmp.me.error(Compilador.ERR_SEMANTICO,"[Declaracion"
                                    + "es Metodos] Error al declarar los meto"
                                    + "dos"+"Linea: " + 
                                    cmp.be.preAnalisis.numLinea);
                    }
                    //FIN ACCION SEMANTICA 38
                }
            }else{
                Declaraciones_metodos.tipo = VACIO;
            }
        }else {
            //Declaraciones_metodos -> empty {59}
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 59
                    Declaraciones_metodos.tipo = VACIO;
                    //FIN ACCION SEMANTICA 59
                }
        }
    }
    
    private void Declaracion_metodo (Atributos Declaracion_metodo) {
        Atributos Encab_metodo = new Atributos();
        Atributos Proposicion_compuesta = new Atributos();
        if ( preAnalisis.equals( "public" ) ) {
            //Declaracion_metodo -> Encab_metodo Proposicion_compuesta {39}
            Encab_metodo (Encab_metodo);
            if( !retroceder){
                Proposicion_compuesta (Proposicion_compuesta);
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 39
                    if ( Encab_metodo.tipo.equals(VACIO) && 
                         Proposicion_compuesta.tipo.equals(VACIO) )
                        Declaracion_metodo.tipo = VACIO;
                    else{
                        Declaracion_metodo.tipo = ERROR_TIPO;
                        cmp.me.error(Compilador.ERR_SEMANTICO,"[Declaracion "
                                + "Metodo] Error al declarar el metodo"+
                                "Linea: " + cmp.be.preAnalisis.numLinea);
                    }
                    //FIN ACCION SEMANTICA 39
                }
            }else{
                Declaracion_metodo.tipo = VACIO;
            }
        } else {
            error ( "La declaracion de los metodos empieza deben de ser publicos" );
        }
    }
    
    private void Encab_metodo (Atributos Encab_metodo) {
        Atributos Tipo_metodo = new Atributos();
        Atributos Lista_parametros= new Atributos();
        Linea_BE id = new Linea_BE();
        retroceder = false;
        if ( preAnalisis.equals( "public" ) ) {
            //Encab_metodo -> public static  Tipo_metodo id {23} ( Lista_parametros )  {40}
            emparejar ( "public" );
            emparejar ( "static" );
            Tipo_metodo (Tipo_metodo);
            if ( preAnalisis.equals("main") ){
                Retroceso (3);
                retroceder = true;
                Encab_metodo.tipo = VACIO;
                return;
            }
            id = cmp.be.preAnalisis;
            emparejar ( "id" );
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 23
                    String tipo = cmp.ts.buscaTipo(id.entrada);
                    if( cmp.ts.buscaTipo(id.entrada).equals("") ){
                    cmp.ts.anadeTipo(id.entrada, Tipo_metodo.tipo);
                    }else{
                        Encab_metodo.tipo = ERROR_TIPO;
                        cmp.me.error(Compilador.ERR_SEMANTICO,"[Encab_metodo]"
                                + " El identificador "+id.getLexema()
                                +" Ya ha sido declarado Linea: "+
                                id.getNumLinea());
                    }
                    //FIN ACCION SEMANTICA 23
                }
                emparejar ( "(" );
                Lista_parametros (Lista_parametros);
            emparejar ( ")" );
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 40
                    if(Lista_parametros.tipo.equals(VACIO))
                        Encab_metodo.tipo = VACIO;
                    else
                        Encab_metodo.tipo = VACIO;
                    //FIN ACCION SEMANTICA 40
                }
            
            
        }
        else {
            error ("La declaracion de los metodos deben de ser publicos");
        }
    }
    
    private void Metodo_principal (Atributos Metodo_principal) {
        Atributos Proposicion_compuesta = new Atributos();
        if ( preAnalisis.equals( "public" ) ) {
            /*Metodo_principal -> public static void  main ( String  args [ ]  
            )   proposición_compuesta {71}
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
            Proposicion_compuesta (Proposicion_compuesta);
            
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 71
                    Metodo_principal.tipo = Proposicion_compuesta.tipo;
                    //FIN ACCION SEMANTICA 71
                }
        }else {
            error ( "El metodo principal debe ser publico" );
        }
    }
    
    private void Tipo_metodo (Atributos Tipo_metodo) {
        
        Atributos Tipo_estandar = new Atributos();
        
        if ( preAnalisis.equals( "void" ) ){
            //Tipo_metodo -> void {21}
            emparejar ( "void" );
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 21
                    Tipo_metodo.tipo = "void";
                    //FIN ACCION SEMANTICA 21
                }
        }
        else if ( preAnalisis.equals( "int" )|| preAnalisis.equals( "float" ) || 
                preAnalisis.equals( "String" ) ){
            //Tipo_metodo -> Tipo_estandar {22}
            Tipo_estandar (Tipo_estandar);
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 22
                    Tipo_metodo.tipo = Tipo_estandar.tipo;
                    //FIN ACCION SEMANTICA 22
                }
        }
        
    }
    
    private void Lista_parametros (Atributos Lista_parametros) {
        Atributos Tipo = new Atributos();
        Atributos Dimension = new Atributos();
        Atributos Lista_parametros2 = new Atributos();
        Linea_BE id = new Linea_BE();
        //Lista_parametros -> tipo  id  dimension  {41}  lista_parametros2 {42}
        if ( preAnalisis.equals( "int" )|| preAnalisis.equals( "float" ) || 
                preAnalisis.equals( "String" ) ){
            Tipo (Tipo);
            id = cmp.be.preAnalisis;
            emparejar ( "id" );
            Dimension (Dimension);
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 41
                    if(cmp.ts.buscaTipo(id.entrada).equals("")){
                    if ( Dimension.length.equals(""))
                        cmp.ts.anadeTipo(id.entrada, Tipo.tipo);
                    else
                        cmp.ts.anadeTipo(id.entrada,cmp.ts.array(Dimension.length, Tipo.tipo) );
                    }else{
                        Lista_parametros.tipo = ERROR_TIPO;
                        cmp.me.error(Compilador.ERR_SEMANTICO,"[Lista_parametro"
                                + "s] El identificador "+id.getLexema()
                                +" Ya ha sido declarado Linea: "
                                        +id.getNumLinea() );
                    }
                    //FIN ACCION SEMANTICA 41
                }
            Lista_parametros2 (Lista_parametros2);
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 42
                    if ( Lista_parametros2.tipo.equals(VACIO))
                        Lista_parametros.tipo = VACIO;
                    else{
                        Lista_parametros.tipo = ERROR_TIPO;
                        cmp.me.error(Compilador.ERR_SEMANTICO,"[Lista_Parametros"
                                + "] Error al definir la lista de los para"
                                + "metros"+"Linea: " + 
                                cmp.be.preAnalisis.numLinea);
                    }
                    //FIN ACCION SEMANTICA 42
                }
        }else {
            //Lista_parametros -> empty {43}
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 38
                    Lista_parametros.tipo = VACIO;
                    //FIN ACCION SEMANTICA 38
                }
        }
    }
    
    private void Lista_parametros2 (Atributos Lista_parametros2) {
        Atributos Tipo = new Atributos();
        Atributos Dimension = new Atributos();
        Atributos Lista_parametros21 = new Atributos();
        Linea_BE id = new Linea_BE();
        if ( preAnalisis.equals( "," ) ) {
            //Lista_parametros2 -> ,  tipo  id  dimension {44}  lista_parametros’ {45}
            emparejar ( "," );
            Tipo(Tipo);
            id = cmp.be.preAnalisis;
            emparejar ( "id" );
            Dimension (Dimension);
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 44
                    if ( cmp.ts.buscaTipo(id.entrada).equals("") ){
                    if ( Dimension.length.equals("") )
                         cmp.ts.anadeTipo(id.entrada, Tipo.tipo);
                    else
                        cmp.ts.anadeTipo(id.entrada, cmp.ts.array(Dimension.length, Tipo.tipo));
                    }else{
                        Lista_parametros2.tipo = ERROR_TIPO;
                        cmp.me.error(Compilador.ERR_SEMANTICO,"[Lista_parametro"
                                + "s2] EL identificador "+id.getLexema()
                                +" Ya ha sido declarado "+id.getEntrada());
                    }
                    //FIN ACCION SEMANTICA 44
                }
            Lista_parametros2 (Lista_parametros21);
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 45
                    if ( Lista_parametros21.tipo.equals(VACIO) )
                        Lista_parametros2.tipo = VACIO;
                    else{
                        Lista_parametros2.tipo = ERROR_TIPO;
                        cmp.me.error(Compilador.ERR_SEMANTICO,"[Lista_parametro"
                                + "s2] Error al definir los parametros"
                        +"Linea: " + cmp.be.preAnalisis.numLinea);
                    }
                    //FIN ACCION SEMANTICA 45
                }
        }else{
          //Lista_parametros2 -> empty {46}
          if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 46
                    Lista_parametros2.tipo = VACIO;
                    //FIN ACCION SEMANTICA 46
                }
        } 
    }
    
    private void Proposicion_compuesta ( Atributos Proposicion_compuesta) {
        Atributos Proposiciones_optativas = new Atributos();
        if ( preAnalisis.equals( "{" ) ) {
            //Proposicion_compuesta -> { Proposiciones_optativas } {47}
            emparejar ( "{" );
            Proposiciones_optativas ( Proposiciones_optativas );
            emparejar ( "}" );
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 47
                    Proposicion_compuesta.tipo = Proposiciones_optativas.tipo;
                    //FIN ACCION SEMANTICA 47   
                }
        }else{
            error ( "Se esperaba llave que abre {" );
        }
            
    }
    
    private void Proposiciones_optativas ( Atributos Proposiciones_optativas ) {
        Atributos Lista_proposiciones = new Atributos();
        if ( preAnalisis.equals( "id" ) || preAnalisis.equals( "{" ) ||
                preAnalisis.equals( "if" ) || preAnalisis.equals( "while" ) ){
            //Proposiciones_optativas -> Lista_proposiciones {48}
            Lista_proposiciones (Lista_proposiciones);
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 48
                    Proposiciones_optativas.tipo = Lista_proposiciones.tipo;
                    //FIN ACCION SEMANTICA 48
                }
        }else{
            //Proposciones_optativas -> empty {49}
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 49
                    Proposiciones_optativas.tipo = VACIO;
                    //FIN ACCION SEMANTICA 49
                }
        }
    }
        
    private void Lista_proposiciones ( Atributos Lista_proposiciones ) {
        Atributos Proposicion = new Atributos();
        Atributos Lista_proposiciones1 = new Atributos();
        if ( preAnalisis.equals( "id" ) || preAnalisis.equals( "{" ) ||
                preAnalisis.equals( "if" ) || preAnalisis.equals( "while" ) ){
            //Lista_Proposiciones -> Proposición  ;  Lista_proposiciones {57}
            Proposicion (Proposicion);
            Lista_proposiciones ( Lista_proposiciones1 );
            
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 57
                    if( Proposicion.tipo.equals(VACIO) &&
                        Lista_proposiciones1.tipo.equals(VACIO) )
                        Lista_proposiciones.tipo = VACIO;
                    else{
                        Lista_proposiciones.tipo = ERROR_TIPO;
                        cmp.me.error(Compilador.ERR_SEMANTICO,"[Lista_proposic"
                                + "iones] Error en las proposiciones"
                        +"Linea: " + cmp.be.preAnalisis.numLinea);
                    }
                    //FIN ACCION SEMANTICA 57
                }
            
        }else{
            //Lista_proposiciones -> empty {58}
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 58
                    Lista_proposiciones.tipo = VACIO;
                    //FIN ACCION SEMANTICA 58
                }
        }
    }
    
    private void Proposicion ( Atributos Proposicion ) {
        Atributos Proposicion2 = new Atributos();
        Atributos Proposicion_compuesta = new Atributos();
        Atributos Expresion = new Atributos();
        Atributos Proposicion11 = new Atributos();
        Atributos Proposicion12 = new Atributos();
        Linea_BE id = new Linea_BE();
        
        if ( preAnalisis.equals( "id" ) ){
            //Proposicion -> id {17} Proposicion2 {18} ;
            id = cmp.be.preAnalisis;
            emparejar ( "id" );
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 17
                    if ( !cmp.ts.buscaTipo(id.entrada).equals(""))
                        Proposicion2.h = cmp.ts.buscaTipo(id.entrada);
                    //FIN ACCION SEMANTICA 17
                }
            Proposicion2 (Proposicion2);
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 18
                    Proposicion.tipo = Proposicion2.tipo;
                    //FIN ACCION SEMANTICA 18
                }
            emparejar ( ";" );
        }else if( preAnalisis.equals( "{" ) ){
            //Proposicion -> Proposicion_compuesta {50}
            Proposicion_compuesta (Proposicion_compuesta);
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 50
                    Proposicion.tipo = Proposicion_compuesta.tipo;
                    //FIN ACCION SEMANTICA 50
                }
        }else if ( preAnalisis.equals( "if" ) ){
            //Proposicion -> if ( Expresión ) Proposición11 else Proposición12 {51}  
            emparejar ( "if"   );
            emparejar ( "("    );
            Expresion ( Expresion );
            emparejar ( ")"    );
            Proposicion ( Proposicion11 );
            emparejar ( "else" );
            Proposicion ( Proposicion12 );
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 51
                    if ( Expresion.sin.equals("BOOL") &&
                         Proposicion11.tipo.equals(VACIO)&&
                         Proposicion12.tipo.equals(VACIO))
                        Proposicion.tipo = VACIO;
                    else{
                        Proposicion.tipo = ERROR_TIPO;
                        cmp.me.error(Compilador.ERR_SEMANTICO,"[Proposicion]"
                                + "Error al hacer la proposicion \"if\" "
                        +"Linea: " + cmp.be.preAnalisis.numLinea);
                    }
                               
                    //FIN ACCION SEMANTICA 51
                }
        } else if(preAnalisis.equals( "while" ) ){
            //Proposicion -> while ( Expresión ) Proposición11 {52}
            emparejar ( "while" );
            emparejar ( "("     );
            Expresion ( Expresion );
            emparejar ( ")"     );
            Proposicion ( Proposicion11);
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 52
                  if( Expresion.sin.equals("BOOL") &&
                      Proposicion11.tipo.equals(VACIO) )
                      Proposicion.tipo = VACIO;
                  else{
                      Proposicion.tipo = ERROR_TIPO;
                      cmp.me.error(Compilador.ERR_SEMANTICO,"[Proposicion]"
                              + "Error al hacer la proposicion\"while\""
                      +"Linea: " + cmp.be.preAnalisis.numLinea);
                  }
                            
                    //FIN ACCION SEMANTICA 52
                }
        } else {
            error ( "Preposicion no válida" );
        }
}
    private void Proposicion2 ( Atributos Proposicion2 ) {
        Atributos Expresion = new Atributos();
        Atributos Expresion1 = new Atributos();
        Atributos Proposicion_metodo = new Atributos();
        if ( preAnalisis.equals( "[" ) ){
            //Proposicion2 -> [ Expresión ] opasig Expresión {20}
            emparejar ( "["      );
            Expresion ( Expresion );
            emparejar ( "]"      );
            emparejar ( "opasig" );
            Expresion ( Expresion1 );
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 20
                    String t[] = new String[2]; 
                    if( Expresion.sin.equals( "int" ) && Proposicion2.h.contains("array") && cmp.ts.buscaTipoArray(Proposicion2.h, t) &&
                        (Expresion1.sin.equals(t[0]) || 
                         Expresion.sin.equals("int") && t[0].equals("float") )    )
                        Proposicion2.tipo = VACIO;
                    else{
                        Proposicion2.tipo=ERROR_TIPO;
                        cmp.me.error(Compilador.ERR_SEMANTICO,"[Proposicion2]"
                                + "Error al asignar en un arreglo"
                        +"Linea: " + cmp.be.preAnalisis.numLinea);
                    }
                    //FIN ACCION SEMANTICA 20
                }
        }else if ( preAnalisis.equals( "opasig" ) ){
            //Proposicion2 -> opasig Expresión {19}
            emparejar ( "opasig" );
            Expresion ( Expresion );
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 19
                    if ( Proposicion2.h.contains("array") ){
                        String t[] = new String[2];
                        String t2[] = new String[2];
                        if( cmp.ts.buscaTipoArray(Proposicion2.h, t) && Expresion.sin.contains("array") && cmp.ts.buscaTipoArray(Expresion.sin, t2)
                            && ( t[0].equals(t2[0]) || (t[0].equals("float") && t2[0].equals("int") ))) 
                                Proposicion2.tipo = VACIO;
                        
                        else{
                            Proposicion2.tipo=ERROR_TIPO;
                            cmp.me.error(Compilador.ERR_SEMANTICO,"[Proposicion2]"
                                + "Error al asignar en un arreglo son de diferente tipo"
                                    + "(int <- float o float <- String"
                                    + "int <- String o array <- no array sin direccionar"
                                +"Linea: " + cmp.be.preAnalisis.numLinea);
                                }
                    }
                    else if(Proposicion2.h.equals(Expresion.sin) && Expresion.tipo.equals(VACIO)){
                    Proposicion2.tipo = VACIO;
                    }else if (Proposicion2.h.equals("float") && Expresion.sin.equals("int")
                       && Expresion.tipo.equals(VACIO))
                        Proposicion2.tipo = VACIO;
                    else if(Proposicion2.h.equals("int") && Expresion.sin.equals("float")){
                        Proposicion2.tipo = ERROR_TIPO;
                        cmp.me.error(Compilador.ERR_SEMANTICO, "No se puede asignar un tipo float a un identificador int "+"Linea: "+cmp.be.preAnalisis.numLinea);
                    }else {
                        cmp.me.error(Compilador.ERR_SEMANTICO, "No se puede asignar un tipo String a un identificador int o float "+"Linea: "+cmp.be.preAnalisis.numLinea);
                    }
                    //FIN ACCION SEMANTICA 19
                }
        }else if ( preAnalisis.equals( "(" ) ){
            //Proposicion2 -> Proposicion_metodo {53}
            Proposicion_metodo ( Proposicion_metodo );
            if ( analizarSemantica ){
            //INICIO ACCION SEMANTICA 53
            Proposicion2.tipo = Proposicion_metodo.tipo;
            //FIN ACCION SEMANTICA 53
            }
    
            
        }else {
            //Proposicion2 -> empty {54}
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 54
                    Proposicion2.tipo = VACIO;
                    //FIN ACCION SEMANTICA 54
                }
    
        }
    }
    
    private void Proposicion_metodo ( Atributos Proposicion_metodo ) {
        Atributos Lista_expresiones = new Atributos();
        if ( preAnalisis.equals( "(" ) ){
            //Proposicion_metodo -> ( Lista_expresiones ) {55}
            emparejar ( "(" );
            Lista_expresiones (Lista_expresiones);
            emparejar ( ")" );
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 55
                    Proposicion_metodo.tipo = Lista_expresiones.tipo;
                    //FIN ACCION SEMANTICA 55
                }
    
        }else {
            //Proposicion_metodo -> empty {56}
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 56
                    Proposicion_metodo.tipo = VACIO;
                    //FIN ACCION SEMANTICA 56
                }
    
        }
    }
    
    private void Lista_expresiones ( Atributos Lista_expresiones ) {
        Atributos Expresion = new Atributos();
        Atributos Lista_expresiones2 = new Atributos();
        if ( preAnalisis.equals( "literal" ) || preAnalisis.equals( "id" ) ||
                preAnalisis.equals( "num" )|| preAnalisis.equals( "num.num" ) ||
                preAnalisis.equals( "(" ) ){
            //Lista_expresiones -> Expresion Lista_expresiones2 {70}
            Expresion (Expresion);
            Lista_expresiones2 (Lista_expresiones2);
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 70
                    if ( Expresion.tipo.equals(VACIO) &&
                         Lista_expresiones2.tipo.equals(VACIO))
                        Lista_expresiones.tipo = VACIO;
                    else{
                        Lista_expresiones.tipo = ERROR_TIPO;
                        cmp.me.error(Compilador.ERR_SEMANTICO,"[Lista_expresion"
                                + "es] Error al definir las expresiones"+
                                "Linea: " + cmp.be.preAnalisis.numLinea);
                    }
                    //FIN ACCION SEMANTICA 70
                }
    
        } else {
            //Lista expresiones -> empty {34}
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 34
                    Lista_expresiones.tipo = VACIO;
                    //FIN ACCION SEMANTICA 34
                }
    
            
        }
    }
    
    private void Lista_expresiones2 ( Atributos Lista_expresiones2 ) {
        Atributos Lista_expresiones21 = new Atributos();
        Atributos Expresion = new Atributos();
        if ( preAnalisis.equals( "," ) ){
            //Lista_expresiones2 -> , Expresion Lista_expresiones {69}
            emparejar ( "," );
            Expresion ( Expresion );
            Lista_expresiones2 ( Lista_expresiones21 );
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 69
                    if ( Expresion.tipo.equals(VACIO) && 
                         Lista_expresiones21.tipo.equals(VACIO))
                        Lista_expresiones2.tipo = VACIO;
                    else{
                        Lista_expresiones2.tipo = ERROR_TIPO;
                        cmp.me.error(Compilador.ERR_SEMANTICO,"[Lista_expresion"
                                + "es2] Error al definir las expresiones"
                        +"Linea: " + cmp.be.preAnalisis.numLinea);
                    }
                    //FIN ACCION SEMANTICA 69
                }
    
        }else {
            //Lista_expresiones2 -> empty {35}
            Lista_expresiones2.tipo = VACIO;
        }
    }
    
    private void Expresion ( Atributos Expresion) {
        Atributos Expresion_simple = new Atributos();
        Atributos Expresion2 = new Atributos();
        
        /*
        if ( preAnalisis.equals( "literal" ) ){
            //Expresion -> literal  {36}
            emparejar ( "literal" );
            Expresion_simple
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 36
                    Expresion.tipo = "String";
                    //FIN ACCION SEMANTICA 36
                }
    
        } else*/ if ( preAnalisis.equals("id") || preAnalisis.equals("num") ||
                preAnalisis.equals("num.num") || preAnalisis.equals("(") || preAnalisis.equals("literal")){
            //Expresion -> Expresion_simple Expresion2 {68}
            Expresion_simple (Expresion_simple);
            Expresion2 (Expresion2);
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 68
                    if ( Expresion_simple.tipo.equals(VACIO) && 
                         Expresion2.tipo.equals(VACIO))
                        Expresion.tipo = VACIO;
                    else{
                        Expresion.tipo = ERROR_TIPO;
                        cmp.me.error(Compilador.ERR_SEMANTICO,"[Expresion]"
                                + "Se definió erroneamente la(s) expresion(es)"
                        +"Linea: " + cmp.be.preAnalisis.numLinea);
                    }
                    if ( !Expresion_simple.sin.equals("String") &&
                          !Expresion2.sin.equals("String") ){ 
                        if(!Expresion_simple.sin.equals("BOOL") && !Expresion2.sin.equals("BOOL")){
                            if( Expresion_simple.sin.contains("array") ){
                                Expresion.sin = Expresion_simple.sin;
                            } else if(Expresion_simple.sin.equals("int") ){
                                Expresion.sin = "int";
                            }else 
                                Expresion.sin = "float";
                        }else
                            Expresion.sin = "BOOL";
                    }else
                        Expresion.sin = "String";
                    //FIN ACCION SEMANTICA 68
                }
    
        }else {
            error ( "Se esperaba algun tipo primitivo o literal " );
        }
    }
    
    private void Expresion2( Atributos Expresion2 ) {
        Atributos Expresion_simple = new Atributos();
        if ( preAnalisis.equals( "oprel" ) ) {
            //Expresion2 -> oprel Expresion_simple {67}
            emparejar ( "oprel" );
            Expresion_simple ( Expresion_simple );
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 67
                    if ( !Expresion_simple.sin.equals("String") ){
                        Expresion2.tipo = VACIO;
                        Expresion2.sin = "BOOL";
                    }
                    //FIN ACCION SEMANTICA 67
                }
    
        }else {
            //Expresion2 -> empty {60}
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 60
                    Expresion2.tipo = VACIO;
                    //FIN ACCION SEMANTICA 60
                }
    
        }
    }
    
    private void Expresion_simple ( Atributos Expresion_simple ) {
        Atributos Termino = new Atributos ();
        Atributos Expresion_simple2 = new Atributos();
        if ( preAnalisis.equals("id") || preAnalisis.equals("num") ||
                preAnalisis.equals("num.num") || preAnalisis.equals("(") || preAnalisis.equals("literal")){
            //Expresion_simple -> Termino {63} Expresion_simple2 {64}
            Termino ( Termino );
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 63
                    Expresion_simple2.h = Termino.sin;
                    //FIN ACCION SEMANTICA 63
                }
            Expresion_simple2 ( Expresion_simple2 );
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 64
                    if ( Expresion_simple2.tipo.equals(VACIO) &&
                         Termino.tipo.equals(VACIO) )
                            Expresion_simple.tipo = VACIO;
                    else
                        Expresion_simple.tipo = VACIO;
                    if ( !Expresion_simple2.sin.equals(""))
                        Expresion_simple.sin = Expresion_simple2.sin;
                    else
                        Expresion_simple.sin = Termino.sin;
                    //FIN ACCION SEMANTICA 64
                }
    
        } else 
            error ( "Se esperaba un término" );
    }
    
    private void Expresion_simple2 ( Atributos Expresion_simple2 ) {
        Atributos Termino = new Atributos();
        Atributos Expresion_simple21 = new Atributos();
        if ( preAnalisis.equals( "opsuma" ) ) {
            //Expresion_simple2 -> opsuma Termino {65} Expresion2 {66}
            emparejar ( "opsuma" );
            Termino ( Termino );
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 65
                    Expresion_simple21.h = Termino.sin;
                    //FIN ACCION SEMANTICA 65
                }
    
            Expresion_simple2 ( Expresion_simple21 );
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 66
                    if ( !Expresion_simple2.h.equals("void") && !Termino.sin.equals("void") )
                    {
                        if ( Expresion_simple2.h.equals("String") && Termino.sin.equals("String") ){
                            Expresion_simple2.tipo = VACIO;
                            Expresion_simple2.sin = "String";
                        }
                        else if ( !Expresion_simple2.h.equals("String") && !Termino.sin.equals("String") )
                                    Expresion_simple2.tipo = VACIO; 
                        else{
                                        Expresion_simple2.tipo = ERROR_TIPO;
                                        cmp.me.error(Compilador.ERR_SEMANTICO,
                                                "[Expresion_simple] Error al "
                                                 +"Hacer la suma:"
                                                 + "No puede haber suma entre"
                                                 +" dos elementos que no"
                                                 + "Sean String ambos \n"
                                        +"Linea: " + cmp.be.preAnalisis.numLinea);
                        }
                            
                    }else{
                        Expresion_simple2.tipo = ERROR_TIPO;
                        cmp.me.error(Compilador.ERR_SEMANTICO,
                                                "[Expresion_simple] Error al "
                                                 +"Hacer la suma:"
                                                 + "No puede haber suma entre"
                                                 + "Elementos void\n o "+
                                  "Linea: " + cmp.be.preAnalisis.numLinea
                        );
                    }
                    
                    
                    
                     if ( !Expresion_simple2.h.equals("void") && !Termino.sin.equals("void"))
                    {
                        if ( Expresion_simple2.h.equals("String") && Termino.sin.equals("String") ){
                            Expresion_simple2.sin = "String";
                        }else if ( !Expresion_simple2.h.equals("String") && !Termino.sin.equals("String") ){
                                  if( Expresion_simple2.h.equals("int") && Termino.sin.equals("int") ){
                                      Expresion_simple2.sin = "int";
                                  }else{
                                      Expresion_simple2.sin = "float";
                                  }  
                        }else{
                            Expresion_simple2.sin = "";
                        } 
                    }else
                        Expresion_simple2.sin = "";
                    //FIN ACCION SEMANTICA 66
                }
    
        } else {
            //Expresion_simple2 -> empty {61}
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 61
                    Expresion_simple2.tipo = VACIO;
                    Expresion_simple2.sin = Expresion_simple2.h;
                    //FIN ACCION SEMANTICA 61
                }
    
        }
    }
    
    private void Termino( Atributos Termino ){
        Atributos Factor = new Atributos();
        Atributos Termino2 = new Atributos();
        if ( preAnalisis.equals("id") || preAnalisis.equals("num") ||
                preAnalisis.equals("num.num") || preAnalisis.equals("(") || preAnalisis.equals("literal")) {
                //Termino -> Factor {29} Termino2 {30}
                Factor ( Factor );
                 if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 29
                    Termino2.h = Factor.sin;
                    //FIN ACCION SEMANTICA 29
                }                           
                Termino2 ( Termino2 ); 
                if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 30
                    Termino.tipo = Termino2.tipo;
                    Termino.sin  = Factor.sin;

                    //FIN ACCION SEMANTICA 30
                } 
        } else {
          error ( "Se esperaba un termino" );  
        }
    }

    private void Termino2 ( Atributos Termino2 ){
        Atributos Termino21 = new Atributos();
        Atributos Factor = new Atributos();
        if ( preAnalisis.equals( "opmult" ) ) {
            //Termino2 -> opmult Factor {31} Termino2 {32}
            emparejar ( "opmult" );
            Factor( Factor );
              if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 31
                    Termino21.h = Factor.sin;
                    //FIN ACCION SEMANTICA 31
                } 
            Termino2( Termino21 );
              if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 32
                    if ( !Termino2.h.equals("void") &&  !Termino2.h.equals("String")
                    && !Factor.sin.equals("void") && !Factor.sin.equals("String")
                    && Termino21.tipo.equals(VACIO)){
                       Termino2.tipo = VACIO;
                    }else{
                       Termino2.tipo = ERROR_TIPO;
                       cmp.me.error(Compilador.ERR_SEMANTICO," [Termino2] No"
                               + "Puede haber multiplicacion con elementos"
                               + "String o void" 
                       +"Linea: " + cmp.be.preAnalisis.numLinea);
                    }
                    //FIN ACCION SEMANTICA 32
                } 
        } else {
            //Termino2 -> empty {62}
              if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 62
                    Termino2.tipo = VACIO;
                    //FIN ACCION SEMANTICA 62
                } 
        }
    }
    
    /*
    if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 62
                    Termino2.tipo = VACIO;
                    //FIN ACCION SEMANTICA 62
                }
    */
    
    private void Factor( Atributos Factor ) {
        Atributos Factor2 = new Atributos();
        Atributos Expresion = new Atributos();
        Linea_BE num = new Linea_BE();
        Linea_BE numnum = new Linea_BE();
        Linea_BE id = new Linea_BE();
        Linea_BE literal = new Linea_BE();
        if ( preAnalisis.equals( "id" ) ){
            //Factor -> id Factor2 {28}
            id = cmp.be.preAnalisis;
            emparejar ( "id" );
            Factor2 ( Factor2 );
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 28
                    if( !cmp.ts.buscaTipo(id.entrada).equals("") )
                        if( Factor2.tipo.equals(VACIO) ){
                            Factor.tipo = VACIO;
                            Factor.sin = cmp.ts.buscaTipo(id.entrada);
                        }
                        else{
                            Factor.tipo = ERROR_TIPO;
                            cmp.me.error(Compilador.ERR_SEMANTICO,"[Factor] "
                                    + "Error en la expresion del metodo"+
                                    "Linea: " + cmp.be.preAnalisis.numLinea);
                        }
                    else
                        cmp.me.error(Compilador.ERR_SEMANTICO,"[Factor]"
                                + "Error el ID: "+id.lexema+" "
                                        + "no ah sido declarado");
                        // ERROR EL ID NO HA SIDO DECLARADO. //
                    //FIN ACCION SEMANTICA 28
                }
            
        } else if ( preAnalisis.equals( "num" ) ){
            //Factor -> num {27}
            emparejar ( "num" );
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 27
                    Factor.sin = "int";
                    Factor.tipo = VACIO;
                    //FIN ACCION SEMANTICA 27
                }
        } else if ( preAnalisis.equals( "num.num" ) ){
            //Factor → num.num {26}
            emparejar ( "num.num" );
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 26
                    Factor.tipo = VACIO;
                    Factor.sin = "float";
                    //FIN ACCION SEMANTICA 26
                }
        } else if ( preAnalisis.equals( "(" ) ){
            //Factor -> ( Expresion ) {25}
            emparejar ( "(" );
            Expresion ( Expresion );
            emparejar ( ")" );
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 25
                    Factor.tipo = VACIO;
                    Factor.sin = cmp.ts.buscaTipo(id.entrada);
                    //FIN ACCION SEMANTICA 25
                }
        } else if (preAnalisis.equals("literal") ) {
            //Factor → literal {24}
            emparejar ( "literal" );
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 24
                    Factor.tipo = VACIO;
                    Factor.sin = "String";
                    //FIN ACCION SEMANTICA 24
                }
        }else {
            error ( "Se esperaba un termino" );
        }
    }
    
    private void Factor2 ( Atributos Factor2 ){
        Atributos Lista_expresiones = new Atributos();
        if ( preAnalisis.equals( "(" ) ){
            //Factor2 -> ( Lista_expresiones ) {37}
            emparejar ( "(" );
            Lista_expresiones ( Lista_expresiones );
            emparejar ( ")" );
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 37
                    if ( Lista_expresiones.tipo.equals(VACIO) )
                        Factor2.tipo = VACIO;
                    else{
                        Factor2.tipo = ERROR_TIPO;
                        cmp.me.error(Compilador.ERR_SEMANTICO,"[Factor2] Error"
                                + "En las expresiones"
                        +"Linea: " + cmp.be.preAnalisis.numLinea);
                    }
                    //FIN ACCION SEMANTICA 37
                }
        }else {
            //Factor2 -> empty {33}
            if ( analizarSemantica ){
                    //INICIO ACCION SEMANTICA 62
                    Factor2.tipo = VACIO;
                    //FIN ACCION SEMANTICA 62
                }
        }
    }

    private void Retroceso(int x) {
        for(int i=0;i<x;i++){
            cmp.be.anterior();
        }
        preAnalisis = cmp.be.preAnalisis.complex;
    }
    
}
//------------------------------------------------------------------------------
//::