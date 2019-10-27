package com.sgallego.aia.cercaWiki;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
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
    
    public static void main( String[] args ) throws Exception
    {
        System.out.println( "Mision 1 - Cerca Wiki" );
        
        //Variables
        ArrayList nombresList = null;
        ArrayList resultadoList = new ArrayList();
        String urlWiki = "https://en.wikipedia.org/wiki/";
        String anyModificacion = "2019";
        //Fichero entrada
        String urlFicheroIn = args[0];
        if (urlFicheroIn != null && !urlFicheroIn.equals(""))
			System.out.println( "Fichero entrada: " + urlFicheroIn );
		else
			throw new FileNotFoundException("ERROR: se tiene que especificar fichero entrada");
        //Fichero salida
        String urlFicheroOut = args[1];
		if (urlFicheroOut != null && !urlFicheroOut.equals(""))
			System.out.println( "Fichero salida: " + urlFicheroIn );
		else
			throw new FileNotFoundException("ERROR: se tiene que especificar fichero salida");
		
              
                
        //PROCESO
        //Carga nombres desde fichero
        nombresList = cargaNombres( urlFicheroIn );
        
        //Busca info en wiki de cada nombre, descartando cabecera empiezo por 1
        for (int i=1; i<nombresList.size(); i++) {//for (int i=1; i<20; i++)
        	resultadoList.add( buscaInfoWiki(urlWiki, (String)nombresList.get(i), anyModificacion ));
        }
        
        //Vuelca resultado en urlFicheroOut
        guardaResultado( urlFicheroOut, resultadoList, nombresList.size() );
         
    }
    
    
    /*
	 * Función encargada de descargar el resultado en un fichero CSV
	 * @param urlFicheroOut url del fuchero salida
	 * @param resultadoList ArrayList con los resultados almacenados
	 * @param size número total de nombres buscados
	 */
    public static void guardaResultado(String urlFicheroOut, ArrayList resultadoList, int size) throws IOException {
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
			throw new IOException("ERROR: se tiene que especificar fichero entrada" + e);
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
    public static ArrayList buscaInfoWiki( String wikiConsulta, String nombre, String anyModificacion ) {
		ArrayList resultado = new ArrayList();
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
			String anyo = strModificacion.substring(posicioComa-4, posicioComa);
			
			System.out.println( "Nombre: " +nombre+ " ----Titiulo: " +titulo+ " ----Año modif: " +anyo );
			//Se monta el resultado de la búsqueda
			resultado.add( wikiConsulta + nombre + "," + nombre + "," + titulo + "," + anyo );
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
	 * @param nombres ArrayList para guardar en memoria los nombres a buscar
	 * @param urlFicheroIn URL fichero entrada con los nombres
	 * @return ArrayList con los nombres del fichero cargado
	 */
    public static ArrayList cargaNombres( String urlFicheroIn ) throws IOException {
		ArrayList nombres = new ArrayList();
		FileReader fr = null;
	    try {
	      fr = new FileReader(urlFicheroIn);
	      BufferedReader br = new BufferedReader(fr);
	 
	      String linea;
	      while((linea = br.readLine()) != null) {
	    	  nombres.add(linea);
	    	  //System.out.println("Nombre: " +linea);
	      }
	      System.out.println("Numero de nombres cargados " +nombres.size());
	 
	      fr.close(); 
	      
	    }   
	    
	    catch(IOException e) {
		      throw new IOException(e.toString());
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
