using System;
using System.Collections.Generic;
using System.IO;

namespace AmedasSummary
{
    class Program
    {
        // 最高・最低気温とその日時を処理するクラス
        class Dtemp
        {
            public float smax = -999, smin = 999;        // 最高気温、最低気温の保存用
            public readonly List<string> max_days = new List<string>(); // 最高気温の日時
            public readonly List<string> min_days = new List<string>(); // 最低気温の日時

            // 気温の比較処理
            // 引数は、日時とそのときの気温
            public void CompValue(string dstr, float tmp)
            {
                if (max_days.Count == 0 || smax <= tmp) // 保存した値より大きい場合
                {
                    if (smax < tmp) max_days.Clear();   // 保存した文字列を削除
                    smax = tmp;
                    max_days.Add(dstr);
                }
                if (min_days.Count == 0 || tmp <= smin) // 保存した値より小さい場合
                {
                    if (tmp < smin) min_days.Clear();   // 保存した文字列を削除
                    smin = tmp;
                    min_days.Add(dstr);
                }
            }
        }

        // ひと月の最高・最低気温を処理するクラス
        class Mtemp
        {
            public float smax = -999, smin = 999;
            public float sum = 0;   // ひと月の気温の合計
            public int ct = 0;      // ひと月の気温値の数

            // 気温の比較処理
            // 引数は、気温
            public void CompValue(float tmp)
            {
                if (smax < tmp) smax = tmp;
                if (tmp < smin) smin = tmp;
                sum += tmp;
                ct++;
            }
        }

        static void Main()
        {
            // 最高・最低気温とその日時を処理するクラスのインスタンス生成
            var dtmp = new Dtemp();

            // 12ヶ月分のオブジェクトを作成しておく
            var map = new Dictionary<int, Mtemp>();
            for (int i = 1; i <= 12; i++)
            {
                map.Add(i, new Mtemp());
            }
            try
            {
                // CSVファイルのオープン
                using var sr = new StreamReader(@"data.csv");
                while (!sr.EndOfStream)
                {
                    var line = sr.ReadLine();       // 1行ずつ取得
                    var r = line.Split(',');        // カンマで分割する
                    if (r[2] == "8")                // 品質情報が8のみ対象にする
                    {
                        dtmp.CompValue(r[0], float.Parse(r[1]));    // 気温の比較処理
                        var month = DateTime.Parse(r[0]).Month;     // 日時文字列から、月のみを取り出す
                        map[month].CompValue(float.Parse(r[1]));    // 気温の比較処理
                    }
                }

                // 結果の表示を行う
                Console.WriteLine($"最高：{dtmp.smax:F1}℃");
                dtmp.max_days.ForEach(Console.WriteLine);
                Console.WriteLine($"最低：{dtmp.smin:F1}℃");
                dtmp.min_days.ForEach(Console.WriteLine);
                foreach (var m in map)
                {
                    Console.WriteLine($"{m.Key,2}月 最高：{m.Value.smax:F1}℃ 最低：{m.Value.smin:F1}℃ 平均：{(m.Value.sum / m.Value.ct):F1}℃");
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
        }
    }
}
