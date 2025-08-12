package com.alura.conv.net;

import com.alura.conv.model.LatestResp;
import com.alura.conv.model.PairResp;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

public class ApiFx {

    // Puedes moverla a variable de entorno si lo prefieres
    private static final String API_KEY = "f64579d41c3f8c49343f55d";
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/";

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final Gson gson = new Gson();

    // /pair/{base}/{target}
    public double obtenerTasaPar(String base, String target) throws IOException, InterruptedException {
        String url = BASE_URL + API_KEY + "/pair/" + base + "/" + target;
        String body = get(url);
        PairResp resp = gson.fromJson(body, PairResp.class);
        validarResultado(resp.getResult(), body);
        return resp.getConversion_rate();
    }

    // /latest/{base} (opcional)
    public Map<String, Double> obtenerTablaLatest(String base) throws IOException, InterruptedException {
        String url = BASE_URL + API_KEY + "/latest/" + base;
        String body = get(url);
        LatestResp resp = gson.fromJson(body, LatestResp.class);
        validarResultado(resp.getResult(), body);
        return resp.getConversion_rates();
    }

    private String get(String url) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(20))
                .GET()
                .build();

        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200) {
            throw new IOException("HTTP " + res.statusCode() + " al consultar API");
        }
        return res.body();
    }

    private void validarResultado(String result, String body) {
        if (result == null || !result.equalsIgnoreCase("success")) {
            throw new IllegalStateException("Respuesta inválida de API: " + body);
        }
    }
}
