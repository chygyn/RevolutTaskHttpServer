package service.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import service.entity.Account;
import service.entity.AccountDAO;
import service.utils.Util;
import service.utils.UtilStrings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ApiServer {

    public static void main(String[] args) {
        ArrayList<Account> accountList = initialAccounts();

        HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress(8080), 0);
            System.out.println("server started at 8080");
            //get all entries from db
            server.createContext("/get", he -> {
                URI requestedUri = he.getRequestURI();
                Map<String, Object> parameters = new HashMap<String, Object>();
                String query = requestedUri.getRawQuery();
                StringBuilder response = new StringBuilder();
                if (query == null) {
                    for (Account acc : accountList) {
                        response.append(acc.toString());
                    }
                    final byte[] rawResponseBody = response.toString().getBytes(UtilStrings.CHARSET);
                    he.sendResponseHeaders(UtilStrings.STATUS_OK, rawResponseBody.length);
                    OutputStream os = he.getResponseBody();
                    os.write(rawResponseBody);
                    os.close();
                } else {
                    Util.parseQuery(query, parameters);
                    Account acc = AccountDAO.getAccount(parameters.get("id").toString(), accountList);
                    response.append(acc.toString());
                    final byte[] rawResponseBody = response.toString().getBytes(UtilStrings.CHARSET);
                    he.sendResponseHeaders(UtilStrings.STATUS_OK, rawResponseBody.length);
                    OutputStream os = he.getResponseBody();
                    os.write(rawResponseBody);
                    os.close();
                }
                // send response

            });
            //send money from sender to receiver
            server.createContext("/transaction", he -> {
                final Map<String, Object> parameters = new HashMap<String, Object>();
                InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                String query = br.readLine();
                if (query == null) {
                    sendBadRequest(he, UtilStrings.NO_DATA_TRANSACTION);
                } else {
                    Util.parseQuery(query, parameters);
                    if ((parameters.get("receiver") != null | parameters.get("sender") != null | parameters.get("summ") != null) & parameters.get("summ").toString().matches("[0-9]+")) {
                        String receiverId = parameters.get("receiver").toString();
                        String senderId = parameters.get("sender").toString();
                        int summ = Integer.valueOf(parameters.get("summ").toString());
                        Account receiver = new Account();
                        Account sender = new Account();
                        for (Account acc : accountList) {
                            if (acc.getId().equals(receiverId)) {
                                receiver = acc;
                                continue;
                            }
                            if (acc.getId().equals(senderId)) {
                                sender = acc;
                                continue;
                            }
                        }
                        if ((receiver.getId() != null || sender.getId() != null) && (sender.getFund() >= summ)) {
                            sender.setFund(sender.getFund() - summ);
                            receiver.setFund(receiver.getFund() + summ);
                            ArrayList<Account> result = new ArrayList<>();
                            result.add(sender);
                            result.add(receiver);
                            final byte[] rawResponseBody = result.toString().getBytes(UtilStrings.CHARSET);
                            he.sendResponseHeaders(UtilStrings.STATUS_OK, rawResponseBody.length);
                            OutputStream os = he.getResponseBody();
                            os.write(rawResponseBody);
                            os.close();
                        } else {
                            sendBadRequest(he, UtilStrings.NOT_ENOUGH_MONEY);
                        }
                    } else {
                        sendBadRequest(he, UtilStrings.NO_DATA_TRANSACTION);
                    }
                }
            });
            server.createContext("/setAccount", he -> {
                Map<String, Object> parameters = new HashMap<String, Object>();
                InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                String query = br.readLine();
                Util.parseQuery(query, parameters);
                String name = parameters.get("name").toString();
                int fund = 0;
                if (parameters.get("fund").toString().matches("[0-9]+") && parameters.get("fund").toString().length() > 0) {
                    fund = Integer.valueOf(parameters.get("fund").toString());
                    if (!name.equals("")) {
                        Account newAcc = AccountDAO.createNewAcc(name, fund, accountList);
                        System.out.println("New entry was created: " + newAcc.toString());
                        final byte[] rawResponseBody = newAcc.toString().getBytes(UtilStrings.CHARSET);
                        he.sendResponseHeaders(UtilStrings.STATUS_OK, rawResponseBody.length);
                        OutputStream os = he.getResponseBody();
                        os.write(rawResponseBody);
                        os.close();

                    } else {
                        sendBadRequest(he, UtilStrings.CREATE_NONAME);
                    }
                } else {
                    sendBadRequest(he, UtilStrings.CREATE_BAD_DATA);
                }

            });
            server.setExecutor(null);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendBadRequest(HttpExchange he, String message) {
        try {
            he.sendResponseHeaders(UtilStrings.BAD_REQUEST, message.length());
            OutputStream os = he.getResponseBody();
            os.write(message.getBytes());
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        he.close();
    }

    private static ArrayList<Account> initialAccounts() {
        ArrayList<Account> initial = new ArrayList<Account>();
        for (int i = 0; i < 5; i++) {
            Account newAcc = new Account("Account " + i);
            newAcc.toString();
            initial.add(newAcc);
        }
        return initial;
    }
}
