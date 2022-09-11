package main.java.org.example;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class OrderLedger {

    public static void main(String[] args) throws IOException {


        HashMap<Integer, Integer> bid = new HashMap<>();
        HashMap<Integer, Integer> ask = new HashMap<>();

        Long date1 = new Date().getTime();

        ArrayList<String> strArr = new ArrayList<>();;


        try (CSVReader reader = new CSVReader(new FileReader(System.getProperty("user.dir") + "/src/main/java/org/example/input.txt"))) {
            String[] lineInArray;
            while ((lineInArray = reader.readNext()) != null) {

                if (lineInArray[0].contentEquals("u")) {

                    if (lineInArray[3].contentEquals("bid")) {
                        bid.put(Integer.parseInt(lineInArray[1]), Integer.parseInt(lineInArray[2]));

                        //System.out.println("Установлены акции по цене bid " + lineInArray[1] + ", в количестве " + lineInArray[2]);

                    } else if (lineInArray[3].contentEquals("ask")) {
                        ask.put(Integer.parseInt(lineInArray[1]), Integer.parseInt(lineInArray[2]));

                        //System.out.println("Установлены акции по цене ask " + lineInArray[1] + ", в количестве " + lineInArray[2]);
                    }

                } else if (lineInArray[0].contentEquals("o")) {

                    if (lineInArray[1].contentEquals("buy")) {

                        //how many want to buy from ask
                        int shares_to_buy = Integer.parseInt(lineInArray[2]);

                        //minimum price of ask
                        Optional<Map.Entry<Integer, Integer>> min_value_ask = ask.entrySet().stream().min(Comparator.comparing(Map.Entry::getKey));

                        //max selling price for bid
                        int min_price_ask = min_value_ask.get().getKey();

                        //number of shares at minimum price of ask
                        int shares_at_stock = ask.get(min_price_ask);

                        int shares_after_buy = shares_at_stock - shares_to_buy;

                        //update ask shares after buy
                        ask.put(min_price_ask, shares_after_buy);

                        //System.out.println("Проданы акции по цене " + min_price_ask + ", в количестве " + shares_to_buy);

                    } else if (lineInArray[1].contentEquals("sell")) {

                        //how many want to buy from ask
                        int shares_to_sell = Integer.parseInt(lineInArray[2]);

                        //maximum price of bid
                        Optional<Map.Entry<Integer, Integer>> max_value_bid = bid.entrySet().stream().max(Comparator.comparing(Map.Entry::getKey));

                        //max selling price for bid
                        int max_price_bid = max_value_bid.get().getKey();

                        //number of shares at minimum price of ask
                        int shares_at_stock = bid.get(max_price_bid);

                        int shares_after_sale = shares_at_stock - shares_to_sell;

                        //update ask shares after sale
                        bid.put(max_price_bid, shares_after_sale);

                        //System.out.println("Проданы акции по цене " + max_price_bid + ", в количестве " + shares_to_sell);

                    }

                } else if (lineInArray[0].contentEquals("q")) {
                    //getting query request in input file

                    if ((lineInArray.length == 2) && (lineInArray[1].contentEquals("best_bid"))) {

                        //getting minimum bid
                        Optional<Map.Entry<Integer, Integer>> max_value_bid = bid.entrySet().stream().max(Comparator.comparing(Map.Entry::getKey));

                        //System.out.println("Имеется " + max_value_bid.get().getValue() + " акций по цене " + max_value_bid.get().getKey());

                        strArr.add(max_value_bid.get().getKey() + "," + max_value_bid.get().getValue());


                    } else if ((lineInArray.length == 2) && (lineInArray[1].contentEquals("best_ask"))) {
                        //getting minimum ask
                        Optional<Map.Entry<Integer, Integer>> min_value_ask = ask.entrySet().stream().min(Comparator.comparing(Map.Entry::getKey));

                        //System.out.println("Имеется " + min_value_ask.get().getValue() + " акций по цене " + min_value_ask.get().getKey());

                        strArr.add(min_value_ask.get().getKey() + "," + min_value_ask.get().getValue());

                    } else if ((lineInArray.length == 3) && (lineInArray[1].contentEquals("size"))) {
                        //query on the size of ask, bid or spread

                        if (bid.containsKey(Integer.parseInt(lineInArray[2]))) {
                            //System.out.println("Имеется " + bid.get(Integer.parseInt(lineInArray[2])) + " штук акций по bid цене " + lineInArray[2]);
                            strArr.add(bid.get(Integer.parseInt(lineInArray[2])).toString());
                        } else if (ask.containsKey(Integer.parseInt(lineInArray[2]))) {
                            //System.out.println("Имеется " + ask.get(Integer.parseInt(lineInArray[2])) + " штук акций по ask цене " + lineInArray[2]);
                            strArr.add(ask.get(Integer.parseInt(lineInArray[2])).toString());
                        } else {
                            //System.out.println("Имеется 0 количество акций по spread цене " + lineInArray[2]);
                            strArr.add("0");
                        }
                    }
                }
            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }

        //System.out.println(strArr);

        try (FileWriter writer = new FileWriter(System.getProperty("user.dir") + "/src/main/java/org/example/output.txt", false)) {
            for (String strLine : strArr) {

                // запись всей строки
                writer.write(strLine);
                // запись по символам
                writer.append('\n');
            }
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        Long date2 = new Date().getTime();

        System.out.println(date2 - date1);

    }
}