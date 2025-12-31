package com.hotel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

/**
 * Gestió de reserves d'un hotel.
 */
public class App {

    // --------- CONSTANTS I VARIABLES GLOBALS ---------

    // Tipus d'habitació
    public static final String TIPUS_ESTANDARD = "Estàndard";
    public static final String TIPUS_SUITE = "Suite";
    public static final String TIPUS_DELUXE = "Deluxe";

    // Serveis addicionals
    public static final String SERVEI_ESMORZAR = "Esmorzar";
    public static final String SERVEI_GIMNAS = "Gimnàs";
    public static final String SERVEI_SPA = "Spa";
    public static final String SERVEI_PISCINA = "Piscina";

    // Capacitat inicial
    public static final int CAPACITAT_ESTANDARD = 30;
    public static final int CAPACITAT_SUITE = 20;
    public static final int CAPACITAT_DELUXE = 10;

    // IVA
    public static final float IVA = 0.21f;

    // Scanner únic
    public static Scanner sc = new Scanner(System.in);

    // HashMaps de consulta
    public static HashMap<String, Float> preusHabitacions = new HashMap<String, Float>();
    public static HashMap<String, Integer> capacitatInicial = new HashMap<String, Integer>();
    public static HashMap<String, Float> preusServeis = new HashMap<String, Float>();

    // HashMaps dinàmics
    public static HashMap<String, Integer> disponibilitatHabitacions = new HashMap<String, Integer>();
    public static HashMap<Integer, ArrayList<String>> reserves = new HashMap<Integer, ArrayList<String>>();

    // Generador de nombres aleatoris per als codis de reserva
    public static Random random = new Random();

    // --------- MÈTODE MAIN ---------

    /**
     * Mètode principal. Mostra el menú en un bucle i gestiona l'opció triada
     * fins que l'usuari decideix eixir.
     */
    public static void main(String[] args) {
        inicialitzarPreus();

        int opcio = 0;
        do {
            mostrarMenu();
            opcio = llegirEnter("Seleccione una opció: ");
            gestionarOpcio(opcio);
        } while (opcio != 6);

        System.out.println("Eixint del sistema... Gràcies per utilitzar el gestor de reserves!");
    }

    // --------- MÈTODES DEMANATS ---------

    /**
     * Configura els preus de les habitacions, serveis addicionals i
     * les capacitats inicials en els HashMaps corresponents.
     */
    public static void inicialitzarPreus() {
        // Preus habitacions
        preusHabitacions.put(TIPUS_ESTANDARD, 50f);
        preusHabitacions.put(TIPUS_SUITE, 100f);
        preusHabitacions.put(TIPUS_DELUXE, 150f);

        // Capacitats inicials
        capacitatInicial.put(TIPUS_ESTANDARD, CAPACITAT_ESTANDARD);
        capacitatInicial.put(TIPUS_SUITE, CAPACITAT_SUITE);
        capacitatInicial.put(TIPUS_DELUXE, CAPACITAT_DELUXE);

        // Disponibilitat inicial (comença igual que la capacitat)
        disponibilitatHabitacions.put(TIPUS_ESTANDARD, CAPACITAT_ESTANDARD);
        disponibilitatHabitacions.put(TIPUS_SUITE, CAPACITAT_SUITE);
        disponibilitatHabitacions.put(TIPUS_DELUXE, CAPACITAT_DELUXE);

        // Preus serveis
        preusServeis.put(SERVEI_ESMORZAR, 10f);
        preusServeis.put(SERVEI_GIMNAS, 15f);
        preusServeis.put(SERVEI_SPA, 20f);
        preusServeis.put(SERVEI_PISCINA, 25f);
    }

    /**
     * Mostra el menú principal amb les opcions disponibles per a l'usuari.
     */
    public static void mostrarMenu() {
        System.out.println("\n===== MENÚ PRINCIPAL =====");
        System.out.println("1. Reservar una habitació");
        System.out.println("2. Alliberar una habitació");
        System.out.println("3. Consultar disponibilitat");
        System.out.println("4. Llistar reserves per tipus");
        System.out.println("5. Obtindre una reserva");
        System.out.println("6. Ixir");
    }

    /**
     * Processa l'opció seleccionada per l'usuari i crida el mètode corresponent.
     */
    public static void gestionarOpcio(int opcio) {
		switch(opcio){
			case 1:
				reservarHabitacio();
				break;
			case 2:
				alliberarHabitacio();
				break;
			case 3:
				consultarDisponibilitat();
				break;
			case 4:
				obtindreReservaPerTipus();
				break;
			case 5:
				obtindreReserva();
				break;
		}
    }

    /**
     * Gestiona tot el procés de reserva: selecció del tipus d'habitació,
     * serveis addicionals, càlcul del preu total i generació del codi de reserva.
     */
    public static void reservarHabitacio() {
        System.out.println("\n===== RESERVAR HABITACIÓ =====");
        
		// Elegir tipus d'habitació
		String tipus = seleccionarTipusHabitacioDisponible();
		if (tipus == null) {
            System.out.println("No hi ha disponibilitat per a aquest tipus.");
            return;
        }

		// Funció per a seleccionar els serveis
		ArrayList<String> serveis = seleccionarServeis();

		// Funció per a calcular el preu total
		float preuTotal = calcularPreuTotal(tipus, serveis);

		// Funció per a generar el codi de reserva
		int codi = generarCodiReserva();

		// Es crea una variable amb la informació de la reserva
		ArrayList<String> informacioReserva = new ArrayList<>();
		informacioReserva.add(tipus);
		informacioReserva.add(String.valueOf(preuTotal));

		for(String servei : serveis){
			informacioReserva.add(servei);
		}
        
		// S'afegeix la reserva y es descompta el nombre d'habitacions lliures
		reserves.put(codi, informacioReserva);
		disponibilitatHabitacions.put(tipus, disponibilitatHabitacions.get(tipus)-1);

		System.out.println("\nReserva creada amb èxit!");
        System.out.println("Codi de reserva: " + codi);

    }

    /**
     * Pregunta a l'usuari un tipus d'habitació en format numèric i
     * retorna el nom del tipus.
     */
    public static String seleccionarTipusHabitacio() {
        int op = llegirEnter("\nSeleccione tipus d'habitació: ");

        if (op == 1) return TIPUS_ESTANDARD;
        if (op == 2) return TIPUS_SUITE;
    	if (op == 3) return TIPUS_DELUXE;
        return null;
    }

    /**
     * Mostra la disponibilitat i el preu de cada tipus d'habitació,
     * demana a l'usuari un tipus i només el retorna si encara hi ha
     * habitacions disponibles. En cas contrari, retorna null.
     */
    public static String seleccionarTipusHabitacioDisponible() {
        System.out.println("\nTipus d'habitació disponibles:");

		System.out.print("1. ");
        mostrarInfoTipus(TIPUS_ESTANDARD);
		System.out.print("2. ");
        mostrarInfoTipus(TIPUS_SUITE);
		System.out.print("3. ");
        mostrarInfoTipus(TIPUS_DELUXE);

        String tipus = seleccionarTipusHabitacio();

        if (tipus != null && disponibilitatHabitacions.get(tipus) > 0){
			return tipus;	
		}

        return null;
    }

    /**
     * Permet triar serveis addicionals (entre 0 i 4, sense repetir) i
     * els retorna en un ArrayList de String.
     */
    public static ArrayList<String> seleccionarServeis() {
        ArrayList<String> serveis = new ArrayList<>();
		boolean continuar = true;

		while(continuar){
			System.out.println("\n0. Finalitzar");
            System.out.println("1. Esmorzar");
            System.out.println("2. Gimnàs");
            System.out.println("3. Spa");
            System.out.println("4. Piscina");

			int opcio = llegirEnter("Selecciona un servei: ");
			String servei = null;

			switch(opcio){
				case 0: break;
				case 1: servei = SERVEI_ESMORZAR; break;
				case 2: servei = SERVEI_GIMNAS; break;
				case 3: servei = SERVEI_SPA; break;
				case 4: servei = SERVEI_PISCINA; break;
			}

			// Si no s'ha seleccionat cap opció entre l'1 i el 4, s'ix del bucle
			if(servei == null){
				continuar = false;
				continue;
			}

			// Si el servei seleccionat no ha sigut afegit abans
			if(!serveis.contains(servei)){
				serveis.add(servei);
				System.out.println("\nEl servei " + servei + " ha sigut afegit");
			} else {
				// Si el servei seleccionat ha sigut afegit aband
				System.out.println("\nEl servei " + servei + " ya ha sigut seleccionat");
			}

			// Pregunta si es vol afegir un altre servei
			System.out.println("\nVols afegir un altre servei? (s/n): ");
			// Si el text de l'usuari es "s" o "S", continuar valdrá "true".
            continuar = sc.next().equalsIgnoreCase("s");
		}

        return serveis;
    }

    /**
     * Calcula i retorna el cost total de la reserva, incloent l'habitació,
     * els serveis seleccionats i l'IVA.
     */
    public static float calcularPreuTotal(String tipusHabitacio, ArrayList<String> serveisSeleccionats) {
        float preuHabitacio = preusHabitacions.get(tipusHabitacio);
		float totalServeis = 0f;

		for(String servei : serveisSeleccionats){
			totalServeis += preusServeis.get(servei);
		}

		float preuAmbIVA = (preuHabitacio + totalServeis) * (1+IVA);

        return preuAmbIVA;
    }

    /**
     * Genera i retorna un codi de reserva únic de tres xifres
     * (entre 100 i 999) que no estiga repetit.
     */
    public static int generarCodiReserva() {
        int codi;
        do {
            codi = random.nextInt(900) + 100;
        } while (reserves.containsKey(codi));
    	return codi;
    }

    /**
     * Permet alliberar una habitació utilitzant el codi de reserva
     * i actualitza la disponibilitat.
     */
    public static void alliberarHabitacio() {
        System.out.println("\n===== ALLIBERAR HABITACIÓ =====");
        
		int codi = llegirEnter("Introdueix el codi de reserva: ");
        
		if (!reserves.containsKey(codi)) {
            System.out.println("No s'ha trobat cap reserva.");
            return;
        }

		// Es mostren les dades de la reserva
		System.out.println("\n===== INFORMACIÓ DE LA RESERVA =====");
		mostrarDadesReserva(codi);

		System.out.println("Vols alliberar aquesta habitació? (s/n): ");
		boolean eleccio = sc.next().equalsIgnoreCase("s");

		// Si l'eleccio es "false" es cancela la operació
		if(eleccio == false) return;
        
		String tipus = reserves.get(codi).get(0);
        
		// S'elimina la reserva 
		reserves.remove(codi);
        disponibilitatHabitacions.put(tipus, disponibilitatHabitacions.get(tipus) + 1);
        
		System.out.println("Habitació alliberada correctament.");
    }

    /**
     * Mostra la disponibilitat actual de les habitacions (lliures i ocupades).
     */
    public static void consultarDisponibilitat() {
        // TODO: Mostrar lliures i ocupades
    }

    /**
     * Funció recursiva. Mostra les dades de totes les reserves
     * associades a un tipus d'habitació.
     */
    public static void llistarReservesPerTipus(int[] codis, String tipus) {
         // TODO: Implementar recursivitat
    }

    /**
     * Permet consultar els detalls d'una reserva introduint el codi.
     */
    public static void obtindreReserva() {
        System.out.println("\n===== CONSULTAR RESERVA =====");
        // TODO: Mostrar dades d'una reserva concreta
 
    }

    /**
     * Mostra totes les reserves existents per a un tipus d'habitació
     * específic.
     */
    public static void obtindreReservaPerTipus() {
        System.out.println("\n===== CONSULTAR RESERVES PER TIPUS =====");
        // TODO: Llistar reserves per tipus
    }

    /**
     * Consulta i mostra en detall la informació d'una reserva.
     */
    public static void mostrarDadesReserva(int codi) {
        ArrayList<String> informacio = reserves.get(codi);
        System.out.println("Codi: " + codi);
        System.out.println("- Tipus: " + informacio.get(0));
        System.out.println("- Cost total: " + informacio.get(1) + "€");
        System.out.println("- Serveis:");
		// Si no hi han serveis
        if (informacio.size() == 2){
			System.out.println("  (cap)");
			return;
		}
        for (int i = 2; i < informacio.size(); i++){
			System.out.println("  * " + informacio.get(i));
		}
    }

    // --------- MÈTODES AUXILIARS (PER MILLORAR LEGIBILITAT) ---------

    /**
     * Llig un enter per teclat mostrant un missatge i gestiona possibles
     * errors d'entrada.
     */
    static int llegirEnter(String missatge) {
        int valor = 0;
        boolean correcte = false;
        while (!correcte) {
                System.out.print(missatge);
                valor = sc.nextInt();
                correcte = true;
        }
        return valor;
    }

    /**
     * Mostra per pantalla informació d'un tipus d'habitació: preu i
     * habitacions disponibles.
     */
    static void mostrarInfoTipus(String tipus) {
        int disponibles = disponibilitatHabitacions.get(tipus);
        int capacitat = capacitatInicial.get(tipus);
        float preu = preusHabitacions.get(tipus);
        System.out.println("- " + tipus + " (" + disponibles + " disponibles de " + capacitat + ") - " + preu + "€");
    }

    /**
     * Mostra la disponibilitat (lliures i ocupades) d'un tipus d'habitació.
     */
    static void mostrarDisponibilitatTipus(String tipus) {
        int lliures = disponibilitatHabitacions.get(tipus);
        int capacitat = capacitatInicial.get(tipus);
        int ocupades = capacitat - lliures;

        String etiqueta = tipus;
        if (etiqueta.length() < 8) {
            etiqueta = etiqueta + "\t"; // per a quadrar la taula
        }

        System.out.println(etiqueta + "\t" + lliures + "\t" + ocupades);
    }
}
