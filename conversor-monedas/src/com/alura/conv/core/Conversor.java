package com.alura.conv.core;

import com.alura.conv.net.ApiFx;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.*;

/**
 * Menú y lógica del conversor.
 * Muestra códigos + nombres de monedas y mensajes claros:
 * "25 (Dólares) USD equivalen a 100.000,00 (Pesos colombianos) COP"
 */
public class Conversor {

    private final ApiFx api = new ApiFx();
    private final Scanner sc = new Scanner(System.in);

    // Orden de monedas permitidas en el reto
    private final List<String> codes = Arrays.asList("USD","COP","ARS","BRL","CLP","BOB");

    // Info de cada moneda: nombre en singular/plural y símbolo
    private static class CInfo {
        final String singular;
        final String plural;
        final String symbol;
        CInfo(String singular, String plural, String symbol) {
            this.singular = singular; this.plural = plural; this.symbol = symbol;
        }
    }

    private final Map<String, CInfo> info = Map.of(
            "USD", new CInfo("Dólar",            "Dólares",            "$"),
            "COP", new CInfo("Peso colombiano",  "Pesos colombianos",  "$"),
            "ARS", new CInfo("Peso argentino",   "Pesos argentinos",   "$"),
            "BRL", new CInfo("Real brasileño",   "Reales brasileños",  "R$"),
            "CLP", new CInfo("Peso chileno",     "Pesos chilenos",     "$"),
            "BOB", new CInfo("Boliviano",        "Bolivianos",         "Bs")
    );

    // Formateador con estilo colombiano (punto para miles, coma para decimales)
    private final NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es", "CO"));

    public Conversor() {
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
    }

    public void menu() {
        System.out.println("========================================");
        System.out.println("   Bienvenido/a al Conversor de Moneda  ");
        System.out.println("========================================");

        int op;
        do {
            System.out.println("\nElige una opción:");
            System.out.println(" 1) USD (" + sgl("USD") + ") -> COP (" + sgl("COP") + ")");
            System.out.println(" 2) COP (" + sgl("COP") + ") -> USD (" + sgl("USD") + ")");
            System.out.println(" 3) USD (" + sgl("USD") + ") -> ARS (" + sgl("ARS") + ")");
            System.out.println(" 4) ARS (" + sgl("ARS") + ") -> USD (" + sgl("USD") + ")");
            System.out.println(" 5) USD (" + sgl("USD") + ") -> BRL (" + sgl("BRL") + ")");
            System.out.println(" 6) CLP (" + sgl("CLP") + ") -> USD (" + sgl("USD") + ")");
            System.out.println(" 7) Otra combinación (elige tus códigos)");
            System.out.println(" 0) Salir");
            System.out.print("Opción: ");

            op = leerInt();

            switch (op) {
                case 1 -> convertir("USD", "COP");
                case 2 -> convertir("COP", "USD");
                case 3 -> convertir("USD", "ARS");
                case 4 -> convertir("ARS", "USD");
                case 5 -> convertir("USD", "BRL");
                case 6 -> convertir("CLP", "USD");
                case 7 -> otra();
                case 0 -> System.out.println("¡Hasta pronto!");
                default -> System.out.println("Opción inválida.");
            }
        } while (op != 0);
    }

    private void otra() {
        System.out.println("\nCódigos disponibles:");
        System.out.println(listadoCodigosConNombre());
        System.out.print("Código base (ej: USD): ");
        String base = sc.next().trim().toUpperCase();
        System.out.print("Código destino (ej: COP): ");
        String tgt = sc.next().trim().toUpperCase();

        if (!codes.contains(base) || !codes.contains(tgt)) {
            System.out.println("Código no permitido para este reto. Usa los listados mostrados.");
            return;
        }
        convertir(base, tgt);
    }

    private void convertir(String base, String tgt) {
        try {
            // Mensaje de entrada con código + nombre: "Ingresa el monto en USD (Dólares): "
            System.out.printf("Ingresa el monto en %s (%s): ", base, plr(base));
            double monto = leerDouble();
            if (monto < 0) {
                System.out.println("El monto no puede ser negativo.");
                return;
            }

            double tasa = api.obtenerTasaPar(base, tgt);
            BigDecimal convertido = BigDecimal.valueOf(monto)
                    .multiply(BigDecimal.valueOf(tasa))
                    .setScale(2, RoundingMode.HALF_UP);

            String montoStr = nf.format(monto);
            String convStr  = nf.format(convertido);

            System.out.printf("Tasa %s (%s) -> %s (%s): %.6f%n",
                    base, sgl(base), tgt, sgl(tgt), tasa);

            // Mensaje final solicitado:
            // "25 (Dólares) USD equivalen a 100.000,00 (Pesos colombianos) COP"
            System.out.printf("%s (%s) %s equivalen a %s (%s) %s%n",
                    montoStr, plr(base), base,
                    convStr, plr(tgt), tgt);

        } catch (Exception e) {
            System.out.println("Error realizando la conversión: " + e.getMessage());
        }
    }

    // Helpers de lectura segura
    private int leerInt() {
        while (!sc.hasNextInt()) {
            System.out.print("Ingresa un número: ");
            sc.next();
        }
        return sc.nextInt();
    }
    private double leerDouble() {
        while (!sc.hasNextDouble()) {
            System.out.print("Ingresa un valor numérico: ");
            sc.next();
        }
        return sc.nextDouble();
    }

    // Helpers de nombres/símbolos
    private String sgl(String code) { // singular
        CInfo c = info.get(code);
        return c != null ? c.singular : code;
    }
    private String plr(String code) { // plural
        CInfo c = info.get(code);
        return c != null ? c.plural : code;
    }
    private String sym(String code) {
        CInfo c = info.get(code);
        return c != null ? c.symbol : "";
    }
    private String listadoCodigosConNombre() {
        StringBuilder sb = new StringBuilder();
        for (String c : codes) {
            sb.append(c)
                    .append(" (").append(sgl(c)).append(", símbolo ").append(sym(c)).append(")")
                    .append(c.equals(codes.get(codes.size()-1)) ? "" : ", ");
        }
        return sb.toString();
    }
}
