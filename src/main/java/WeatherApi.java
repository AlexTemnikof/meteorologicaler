import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherApi {
    private static final String API_KEY = "7ea05983-3ea4-45d2-b477-bb04fb78a2b6";
    private static final double lat = 55.75;
    private static final double lon = 37.62;
    private static final int limit = 5;

    public static void main(String[] args) {

        try {
            final String jsonResponse = getWeatherData(lat, lon);
            System.out.println(jsonResponse);

            final Gson gson = new Gson();
            final WeatherResponse response = gson.fromJson(jsonResponse, WeatherResponse.class);

            final double currentTemp = response.fact.temp;
            System.out.println("Текущая температура: " + currentTemp + " °C");

            double averageTemp = calculateAverageTemperature(response, limit);
            System.out.println("Средняя температура за " + limit + " дней: " + averageTemp + " °C");

        } catch (IOException e) {
            System.out.println("Something went wrong...");
            e.printStackTrace();
        }
    }

    private static String getWeatherData(final double lat, final double lon) throws IOException {
        final String urlString = "https://api.weather.yandex.ru/v2/forecast?lat=" + lat + "&lon=" + lon + "&limit=" + 5;
        final URL url = new URL(urlString);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("X-Yandex-API-Key", API_KEY); // Установите ваш API ключ

        final BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        final StringBuilder response = new StringBuilder();

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    /**
     * Вычисление средней температуры
     */
    private static double calculateAverageTemperature(final WeatherResponse response, final int limit) {
        double sum = 0;
        final int count = Math.min(limit, response.forecasts.length);

        for (int i = 0; i < count; i++) {
            double averageTemp = response.forecasts[i].parts.temp_avg;
            sum += averageTemp;
        }

        return sum / count;
    }

    /**
     * Десереализация json
     */
    class WeatherResponse {
        Fact fact;
        Forecast[] forecasts;

        class Fact {
            double temp;
        }

        class Forecast {
            Parts parts;

            class Parts {
                double temp_avg;
            }
        }
    }
}
