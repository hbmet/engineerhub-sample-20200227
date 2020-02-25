package enjapan.com.wings.amedas;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {

        // 最高・最低気温とその日時を処理するクラス
        class Dtemp{
            public float smax = -999, smin = 999;   // 最高気温、最低気温の保存用
            public List<String> max_days = new ArrayList<>();   // 最高気温の日時
            public List<String> min_days = new ArrayList<>();   // 最低気温の日時

            // 気温の比較処理
            // 引数は、日時とそのときの気温
            public void compValue(String dstr, float tmp) {
                if (max_days.isEmpty() || smax <= tmp) {    // 保存した値より大きい場合
                    if (smax < tmp) max_days.clear();       // 保存した文字列を削除
                    smax = tmp;
                    max_days.add(dstr);
                }
                if (min_days.isEmpty() || tmp <= smin) {    // 保存した値より小さい場合
                    if (tmp < smin) min_days.clear();       // 保存した文字列を削除
                    smin = tmp;
                    min_days.add(dstr);
                }
            }
        }

        // ひと月の最高・最低気温を処理するクラス
        class Mtemp{
            public float smax = -999, smin = 999;
            public float sum = 0;   // ひと月の気温の合計
            public int ct = 0;      // ひと月の気温値の数

            // 気温の比較処理
            // 引数は、気温
            public void compValue(float tmp) {
                if (smax < tmp) smax = tmp;
                if (tmp < smin) smin = tmp;
                sum += tmp;
                ct++;
            }
        }

        // 12ヶ月分のオブジェクトを作成しておく
        var map = new HashMap<Integer,Mtemp>();
        for(int i=1; i<=12; i++) {
            map.put(i,new Mtemp());
        }

        // 最高・最低気温とその日時を処理するクラスのインスタンス生成
        var dtmp = new Dtemp();

        var file = Paths.get("data.csv");// CSVファイルの指定
        try (var br = Files.newBufferedReader(file);) {         // ファイルのオープン
            String line = null;
            while ((line = br.readLine()) != null) {                // 1行ずつ取得
                var r = line.split(",");                            // カンマで分割する
                if (r[2].compareTo("8") == 0) {                     // 品質情報が8のみ対象にする
                    dtmp.compValue(r[0], Float.valueOf(r[1]));      // 気温の比較処理

                    // 日時文字列から、月のみを取り出す
                    var month = LocalDate.parse(r[0],DateTimeFormatter.ofPattern("yyyy/M/d H:mm")).getMonthValue();
                    map.get(month).compValue(Float.valueOf(r[1]));  // 気温の比較処理
                }
            }

            // 結果の表示を行う
            System.out.printf("最高：%.1f℃\n", dtmp.smax);
            dtmp.max_days.forEach(System.out::println);
            System.out.printf("最低：%.1f℃\n", dtmp.smin);
            dtmp.min_days.forEach(System.out::println);

            for (var m : map.entrySet() ) {
                System.out.printf("%2d月 最高：%.1f℃ 最低：%.1f℃ 平均：%.1f℃\n",
                        m.getKey(), m.getValue().smax,m.getValue().smin,m.getValue().sum/m.getValue().ct);
            }

        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
