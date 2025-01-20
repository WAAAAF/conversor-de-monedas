import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Principal {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HttpClient client = HttpClient.newHttpClient();

        boolean continuar = true;

        while (continuar) {
            System.out.println("***********************************************");
            System.out.println("* Sea bienvenido/a al Conversor de Moneda =]  *");
            System.out.println("***********************************************");
            System.out.println("1) Dólar => Peso argentino");
            System.out.println("2) Peso argentino => Dólar");
            System.out.println("3) Dólar => Real brasileño");
            System.out.println("4) Real brasileño => Dólar");
            System.out.println("5) Dólar => Peso colombiano");
            System.out.println("6) Peso colombiano => Dólar");
            System.out.println("7) Salir");
            System.out.println("***********************************************");
            System.out.print("Elija una opción válida: ");

            int opcion = Integer.parseInt(scanner.nextLine());

            if (opcion == 7) {
                continuar = false;
                System.out.println("Gracias por usar el conversor de monedas. ¡Adiós!");
                break;
            }

            String monedaBase = "";
            String monedaDestino = "";

            switch (opcion) {
                case 1 -> {
                    monedaBase = "USD";
                    monedaDestino = "ARS";
                }
                case 2 -> {
                    monedaBase = "ARS";
                    monedaDestino = "USD";
                }
                case 3 -> {
                    monedaBase = "USD";
                    monedaDestino = "BRL";
                }
                case 4 -> {
                    monedaBase = "BRL";
                    monedaDestino = "USD";
                }
                case 5 -> {
                    monedaBase = "USD";
                    monedaDestino = "COP";
                }
                case 6 -> {
                    monedaBase = "COP";
                    monedaDestino = "USD";
                }
                default -> {
                    System.out.println("Opción no válida. Intente nuevamente.");
                    continue;
                }
            }

            System.out.print("Ingrese el monto a convertir: ");
            double monto = Double.parseDouble(scanner.nextLine());

            // Construyendo la solicitud
            String apiUrl = "https://v6.exchangerate-api.com/v6/ff906f122b0c6fa4062adf21/latest/" + monedaBase;
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();

            try {
                // Obteniendo la respuesta
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {

                    // Analizando el JSON
                    Gson gson = new Gson();
                    JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
                    JsonObject rates = jsonResponse.getAsJsonObject("conversion_rates");

                    // Filtrando las monedas
                    if (rates.has(monedaDestino)) {
                        double tasaConversion = rates.get(monedaDestino).getAsDouble();

                        // Realizando la conversión
                        double resultado = monto * tasaConversion;

                        // Mostrando el resultado con 2 decimales
                        System.out.printf("%.2f %s equivale a %.2f %s%n", monto, monedaBase, resultado, monedaDestino);
                    } else {
                        System.out.println("La moneda de destino no está disponible.");
                    }
                } else {
                    System.out.println("Error en la solicitud. Código de respuesta: " + response.statusCode());
                }
            } catch (IOException | InterruptedException e) {
                System.out.println("Error al realizar la conversión: " + e.getMessage());
            }
        }

        scanner.close();
    }
}
