/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.sbt.jschool.session2;

import java.io.PrintStream;
import java.text.*;
import java.util.*;

/**
 */
public class OutputFormatter {
    private PrintStream out;

    public OutputFormatter(PrintStream out) {
       this.out = out;
    }

    public void output(String[] names, Object[][] data) {

        if(names==null) return;

        StringBuilder sb = new StringBuilder();
        ArrayList<ArrayList<String>> listColumns =null;

        if(data.length!=0) {

            listColumns  = new ArrayList<>();

            for (int i = 0; i < names.length; i++) {

                ArrayList list = new ArrayList();

                for (int j = 0; j < data.length; j++) {
                    list.add(data[j][i]);
                }

                ArrayList<String> listResult = new ArrayList<>();

                Object a = data[0][i];

                if (a instanceof String) {
                    listResult = createColumnString(list, names[i].length());
                } else if (a instanceof Double) {
                    listResult = createColumnMoney(list, names[i].length());
                } else if (a instanceof Number) {
                    listResult = createColumnNumbers(list, names[i].length());
                } else if (a instanceof Date) {
                    listResult = createColumnDate(list);
                }

                listColumns.add(listResult);
            }
        }

        String line = "";

        StringBuilder sbLine = new StringBuilder();

        if(listColumns==null) {
            for (int i = 0; i < names.length; i++) {
                sbLine.append("+" + createLine(names[0].length()));
            }

        } else {
           for (int i = 0; i < listColumns.size(); i++) {
                sbLine.append("+" + createLine(listColumns.get(i).get(0).length()));
            }
        }

        sbLine.append("+\n");
        line = sbLine.toString();

        sb.append(line+"|");

        if(listColumns!=null) {
            for (int i = 0; i < data[0].length; i++) {
                String title = "";
                if (data[0][i] == null || names[i].isEmpty()) {
                    title = "-";
                } else {
                    title = names[i];
                }
                int lengthPol = listColumns.get(i).get(0).length();
                int indexInc = (lengthPol - title.length()) / 2;
                for (int j = 0; j < indexInc; j++) {
                    sb.append(" ");
                }
                sb.append(title);
                for (int j = indexInc + title.length(); j < lengthPol; j++) {
                    sb.append(" ");
                }
                sb.append("|");
            }
            sb.append("\n");
        }

        if(listColumns==null){

            for (int i = 0; i < names.length; i++) {
                sb.append(names[i]+"|");
            }
            sb.append("\n");
        }


        if(listColumns!=null)
        for (int i = 0; i < data.length; i++) {
            sb.append(line);
            for (int j = 0; j < listColumns.size(); j++) {
                sb.append("|"+listColumns.get(j).get(i));
            }
            sb.append("|\n");
        }

        sb.append(line);

        out.print(sb.toString());


        //TODO: implement me.
    }

    public ArrayList<String> createColumnMoney(ArrayList<Object> list, int minLength){
        ArrayList<String> listMoney = new ArrayList<>();
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setGroupingSeparator(' ');
        decimalFormatSymbols.setDecimalSeparator(',');
        String pattern = "###,##0.00";
        DecimalFormat formatter = new DecimalFormat(pattern);
        formatter.setDecimalFormatSymbols(decimalFormatSymbols);
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i)==null||list.get(i).toString().isEmpty()){
                listMoney.add("-");
                continue;
            }
            listMoney.add(formatter.format((Double)list.get(i)));
        }

        int maxLength = searchMaxLength(listMoney,minLength);
        createStringFormat(listMoney,maxLength);

        return listMoney;
    }

    public ArrayList<String> createColumnString(ArrayList<Object> list, int minLength){
        ArrayList<String> listStrings = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i)==null||list.get(i).toString().isEmpty()){
                listStrings.add("-");
                continue;
            }
            listStrings.add(list.get(i).toString());
        }
        int maxLength = searchMaxLength(listStrings,minLength);

        StringBuilder sb;

        for (int i = 0; i < listStrings.size(); i++) {
            sb = new StringBuilder(listStrings.get(i));
            for (int j = listStrings.get(i).length(); j < maxLength; j++) {
                sb.append(" ");
            }
            listStrings.set(i,sb.toString());
        }

        return listStrings;
    }

    public ArrayList<String> createColumnDate(ArrayList<Object> list) {
        ArrayList<String> listDates = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i)==null||list.get(i).toString().isEmpty()){
                listDates.add("         -");
                continue;
            }
                Date date = (Date) list.get(i);
                listDates.add(dateFormat.format(date));

        }

        return listDates;
    }

    public ArrayList<String> createColumnNumbers(ArrayList<Object> list, int minLength) {
        ArrayList<String> listNumbers = new ArrayList<>();
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setGroupingSeparator(' ');
        String pattern = "###,##0";
        DecimalFormat formatter = new DecimalFormat(pattern);
        formatter.setDecimalFormatSymbols(decimalFormatSymbols);
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i)==null||list.get(i).toString().isEmpty()){
                listNumbers.add("-");
                continue;
            }
            listNumbers.add(formatter.format((Integer) list.get(i)));
        }

        int maxLength = searchMaxLength(listNumbers,minLength);
        createStringFormat(listNumbers,maxLength);

        return listNumbers;
    }

    public int searchMaxLength(ArrayList<String> list, int minLength){
        int maxLength = minLength;
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).length()>maxLength) {
                maxLength=list.get(i).length();
            }
        }
        return maxLength;
    }

    public void createStringFormat(ArrayList<String> list,int maxLength){
        StringBuilder sb;
        for (int i = 0; i < list.size(); i++) {
            sb = new StringBuilder(list.get(i));
            sb.reverse();
            for (int j = list.get(i).length(); j < maxLength; j++) {
                sb.append(" ");
            }
            list.set(i,sb.reverse().toString());
        }
    }

    public String createLine(int length){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append("-");
        }

        return sb.toString();
    }




}
