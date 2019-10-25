package com.sgallego.aia.cercaWiki;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



/**
 * 
 * Misión 1 -- EJERCICIO OBTENER INFO DE WIKI
 * @author Sergi Gallego Mainar
 * 27/10/2019
 * 
 * A partir de unos nombres almacanados en un txt:
 * Cargar registros
 * Montar url con cada nombre cargado y buscaar info en la web de wiki
 * Añadir cáculos
 * Descargar información
 * JUNIT
 * 
 */
public class CercaWiki 
{
	//Variables globales 
	static int numErrores = 0;
	static int cambioActual = 0;
    
    public static void main( String[] args )
    {
        System.out.println( "Mision 1 - Cerca Wiki" );
        
        //Variables
        ArrayList nombresList = null;
        ArrayList resultadoList = new ArrayList();
        String urlFicheroIn = "C:\\Users\\sergi.gallego\\Documents\\Personal\\cercaWiki\\first10000.txt";
        String urlFicheroOut = "C:\\Users\\sergi.gallego\\Documents\\Personal\\cercaWiki\\salida_first10000.csv";
        String urlWiki = "https://en.wikipedia.org/wiki/";
        String anyModificacion = "2019"; 
                
        //Proceso
        //Carga nombres desde fichero
        nombresList = cargaNombres( urlFicheroIn );
        
        //Busca info en wiki de cada nombre, descartando cabecera empieza por 1
        for (int i=1; i<20; i++) {//nombresList.size(); i++) {
        	resultadoList.add(buscaInfoWiki(urlWiki, (String)nombresList.get(i), anyModificacion ));
        }
        System.out.println("Num resultados: " + resultadoList.size());
        //Vuelca resultados
        guardaResultado( urlFicheroOut, resultadoList, nombresList.size() );
         
    }
    
    
    /*
	 * Función encargada de descargar el resultado en un fichero CSV
	 * @param urlFicheroOut url del fuchero salida
	 * @param resultadoList ArrayList con los resultados almacenados
	 * @param size número total de nombres buscados
	 */
    public static void guardaResultado(String urlFicheroOut, ArrayList resultadoList, int size) {
    	ArrayList nombresList = new ArrayList();
		FileWriter flwriter = null;
		try {
			
			//Fichero salida
			flwriter = new FileWriter(urlFicheroOut);
			//Buffer datos
			BufferedWriter bfwriter = new BufferedWriter(flwriter);
			//Cabecera
			bfwriter.write( "URL,NOMBRE,TITULO,AÑO MODIFICAION\n" );
			for (int i=0; i<resultadoList.size();i++) {
				//Registro i
				bfwriter.write( resultadoList.get(i) + "\n" );
				System.out.println("\nREGISTREEEEEE"+ i );
			}
			//Resumen
			bfwriter.write( "\nFIN PROCESO\n" );
			System.out.println( "FIN PROCESO" );
			
			bfwriter.write( "RESUMEN\n" );
			bfwriter.write( "Registros tratados: " + size +"\n" );
	        bfwriter.write( "Paginas modificadas este año: " + cambioActual +"\n" );
	        bfwriter.write( "Registros con error al leer info en página web: " + numErrores  +"\n" );
	        
	        bfwriter.close();
			System.out.println("Archivo creado y cerrado");
	        
		} catch (IOException e) {
			System.out.println("Error al en fichero salida" + e);
		}
		finally {
			if (flwriter != null) {
				try {
					flwriter.close();
				} catch (IOException e) {
					System.out.println("Error al cerrar fichero salida" + e);
				}
			}
		}
		
		
		
	}
	
	/*
	 * Función encargada de buscar la información en la página de wiki
	 * 
	 * @param resultado ArrayList para guardar en memoria el resultado buscado
	 * @param url urlWiki
	 * @param url nombre a buscar en la wiki
	 * @param año a comparar para cada página para saber si es el de la modificación
	 * @return ArrayList con los resultados de la búqueda
	 */
    public static String buscaInfoWiki( String wikiConsulta, String nombre, String anyModificacion ) {
		String resultado = "";
		Document page = null;
		try {
			//Conexión con la página
			Connection con = Jsoup.connect(wikiConsulta+nombre).userAgent("Mozilla").timeout(100000);
		    Connection.Response resp = con.execute();
			if (resp.statusCode() == 200) {
				page = con.get();
	        }
			
			//1.Búsqueda del título de la página
			String titulo = page.getElementsByClass("firstHeading").text();
			
			//2.Búsqueda de la última modificación de la página
			
			//Selecciono listados
			Elements listLi = page.select("li");
			//Texto de los listados
			String strLi = listLi.text();
			//Selecciono el inicio del texto de la ultima modificación
			int posicioIni = strLi.lastIndexOf("This page was last edited on");
			//Selecciono la frase con el texto
			String strModificacion = strLi.substring(posicioIni+28, posicioIni+58);
			//Posición de la coma final del año
			int posicioComa = strModificacion.lastIndexOf(",");
			//System.out.println( strLi );
			//System.out.println( strModificacion +"\n" );
			String anyo = strModificacion.substring(posicioComa-4, posicioComa);
			
			//Se monta el resultado de la búsqueda
			resultado = wikiConsulta + nombre + "," + nombre + "," + titulo + "," + anyo;
			System.out.println( wikiConsulta + nombre + "," + nombre + "," + titulo + "," + anyo );
			if (anyo.equals(anyModificacion))
				cambioActual ++;
			
		    } catch (IOException  ex) {
		    	System.out.println("Excepción al obtener el HTML de la página" + ex.getMessage());
		    	numErrores ++;
		    }
			catch (IndexOutOfBoundsException er) {
				System.out.println("Excepción al buscar la fecha modificación para " +nombre+" Error: "+ er.getMessage());
				numErrores ++;
			}
		return resultado;
	}
	/*
	 * Función encargada de cargar los nombres contenidos en el fichero txt
	 * 
	 * @param nombres ArrayList para guardar en memoria los nombres a buscar
	 * @param urlFicheroIn URL fichero entrada con los nombres
	 * @return ArrayList con los nombres del fichero cargado
	 */
    public static ArrayList cargaNombres( String urlFicheroIn ) {
		ArrayList nombres = new ArrayList();
		FileReader fr = null;
	    try {
	      fr = new FileReader(urlFicheroIn);
	      BufferedReader br = new BufferedReader(fr);
	 
	      String linea;
	      while((linea = br.readLine()) != null) {
	    	  nombres.add(linea);
	    	  System.out.println("Nombre: " +linea);
	      }
	      System.out.println("Numero de nombres cargados " +nombres.size());
	 
	      fr.close();
	      
	    }
	    catch(Exception e) {
	      System.out.println("Excepcion leyendo fichero "+ urlFicheroIn + ": " + e);
	    }
	    finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					System.out.println("Error al cerrar fichero lectura" + e);
				}
			}
		}
		return nombres;
		
	}
}
