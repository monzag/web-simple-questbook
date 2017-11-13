package com.monzag.guestbook;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Guestbook implements HttpHandler {

    private HashMap<String, String[]> post;

    public Guestbook() {
        post = new HashMap<>();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        String response = "";
        String method = httpExchange.getRequestMethod();

        if (method.equals("GET")) {
            response = getResponse();


        } if(method.equals("POST")){
            InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String formData = br.readLine();

            Map inputs = parseFormData(formData);

            String[] values = {inputs.get("Name").toString(), inputs.get("Message").toString()};

            post.put(getDate(), values);
            response = getResponse();
        }


        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();

    }

    public String getDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String date = now.format(formatter);

        return date;
    }

    private static Map<String, String> parseFormData(String formData) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        String[] pairs = formData.split("&");
        for(String pair : pairs){
            String[] keyValue = pair.split("=");
            // We have to decode the value because it's urlencoded. see: https://en.wikipedia.org/wiki/POST_(HTTP)#Use_for_submitting_web_forms
            String value = new URLDecoder().decode(keyValue[1], "UTF-8");
            map.put(keyValue[0], value);
        }
        return map;
    }

    public String getPosts() {
        String posts = "";

        for (Map.Entry<String, String[]> entry: post.entrySet()) {
            posts += "<br>" + "<tr>\n<b>" + entry.getValue()[1] + "</b>\n";
            posts += "<br>Name: \n<b>" + entry.getValue()[0] + "</b>\n";
            posts += "<br>Date: \n" + entry.getKey() + "\n</tr>" + "<br>";
        }

        return posts;
    }

    public String getResponse() {
        String response = "<html><body>" +
                "<header><h1>Guestbook</h1></header>" +
                "<table>" +
                getPosts() +
                "</table>" +
                "<form method=\"POST\">\n" +
                "  <br>Message:<br>\n" +
                "  <textarea name=\"Message\" style=\"width:230px;height:120px\"></textarea>\n" +
                "  <br>\n" +
                "  Name:<br>\n" +
                "  <input type=\"text\" name=\"Name\" value=\"\">\n" +
                "  <br><br>\n" +
                "  <input type=\"submit\" value=\"Submit\">\n" +
                "</form> " +
                "</body></html>";

        return response;
    }
}
