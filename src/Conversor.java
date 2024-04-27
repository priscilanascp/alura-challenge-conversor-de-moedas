import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Currency;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Conversor {
    public static void main(String[] args) {
        Conversor conversor = new Conversor();
        conversor.iniciar();
    }

    public void iniciar() {
        String apiUrl = "https://v6.exchangerate-api.com/v6/62ef6a1456f1d3a219bbadeb/latest/USD";

        System.out.println("Seja bem vindo ao Conversor de Moedas");
        System.out.println("1) Dólar =>> Peso Argentino");
        System.out.println("2) Peso argentino =>> Dólar");
        System.out.println("3) Dólar =>> Real Brasileiro");
        System.out.println("4) Real Brasileiro =>> Dólar");
        System.out.println("5) Dólar =>> Peso Colombiano");
        System.out.println("6) Peso Colombiano =>> Dólar");
        System.out.println("7) Sair");
        System.out.println("***************************");

        try {
            HttpClient cliente = HttpClient.newHttpClient();
            Gson gson = new Gson();
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("Escolha uma opção válida:");
                String opcao = scanner.nextLine();

                if (opcao.equals("7")) {
                    System.out.println("Até mais!");
                    break;
                }

                Currency moedaOrigem;
                Currency moedaConvertida;
                switch (opcao) {
                    case "1":
                        moedaOrigem = Currency.getInstance("USD");
                        moedaConvertida = Currency.getInstance("ARS");
                        break;
                    case "2":
                        moedaOrigem = Currency.getInstance("ARS");
                        moedaConvertida = Currency.getInstance("USD");
                        break;
                    case "3":
                        moedaOrigem = Currency.getInstance("USD");
                        moedaConvertida = Currency.getInstance("BRL");
                        break;
                    case "4":
                        moedaOrigem = Currency.getInstance("BRL");
                        moedaConvertida = Currency.getInstance("USD");
                        break;
                    case "5":
                        moedaOrigem = Currency.getInstance("USD");
                        moedaConvertida = Currency.getInstance("COP");
                        break;
                    case "6":
                        moedaOrigem = Currency.getInstance("COP");
                        moedaConvertida = Currency.getInstance("USD");
                        break;
                    default:
                        System.out.println("Opção inválida.");
                        continue;
                }

                System.out.println("Digite o valor que deseja converter:");
                double valor = Double.parseDouble(scanner.nextLine());

                double valorConvertido = converterMoeda(apiUrl, cliente, gson, moedaOrigem, moedaConvertida, valor);

                System.out.println(valor + " " + moedaOrigem.getCurrencyCode() + " é equivalente a " + valorConvertido + " "
                        + moedaConvertida.getCurrencyCode());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static double converterMoeda(String apiUrl, HttpClient cliente, Gson gson, Currency moedaOrigem,
                                         Currency moedaConvertida, double valor) throws IOException, InterruptedException {
        HttpRequest requisicao = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET()
                .build();

        HttpResponse<String> resposta = cliente.send(requisicao, HttpResponse.BodyHandlers.ofString());

        JsonObject jsonResponse = gson.fromJson(resposta.body(), JsonObject.class);
        JsonObject taxas = jsonResponse.getAsJsonObject("conversion_rates");

        double taxaOrigem = taxas.get(moedaOrigem.getCurrencyCode()).getAsDouble();
        double taxaConvertida = taxas.get(moedaConvertida.getCurrencyCode()).getAsDouble();

        // Convertendo o valor da moeda de origem para USD (dólar americano)
        double valorEmUSD = valor / taxaOrigem;

        // Convertendo o valor de USD para a moeda convertida
        double valorConvertido = valorEmUSD * taxaConvertida;

        return valorConvertido;
    }
}

