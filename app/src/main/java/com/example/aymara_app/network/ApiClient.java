package com.example.aymara_app.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.security.cert.X509Certificate;

public class ApiClient {
    private static final String BASE_URL = "https://aymara.pythonanywhere.com/";  // URL de nuestra API
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Configurar el interceptor
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Crear TrustManager que confíe en todos los certificados
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    }
            };

            try {
                // Crear un contexto SSL que use el TrustManager anterior
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

                // Crear el cliente OkHttp
                OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
                httpClient.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0]);
                httpClient.hostnameVerifier((hostname, session) -> true); // Ignorar la verificación del nombre del host
                httpClient.addInterceptor(logging); // Añadir el interceptor de logging

                // Construir Retrofit con el cliente OkHttp
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(httpClient.build()) // Añadir el cliente con logging y configuración SSL
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            } catch (Exception e) {
                e.printStackTrace(); // Manejo de excepciones
            }
        }
        return retrofit;
    }
}
