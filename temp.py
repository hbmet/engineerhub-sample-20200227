# -*- coding: utf-8 -*-

import csv
import datetime

# 最高・最低気温とその日時を処理するクラス
class Dtemp:
    def __init__(self):
        self.smax = -999.0    # 最高気温、最低気温の保存用
        self.smin = 999.0
        self.max_days = [] # 最高気温の日時
        self.min_days = [] # 最低気温の日時

    # 気温の比較処理
    # 引数は、日時とそのときの気温
    def compValue(self,dstr,tmp):
        if self.max_days.count == 0 or self.smax <= tmp: # 保存した値より大きい場合
            if self.smax < tmp:
                self.max_days.clear()                    # 保存した文字列を削除
            self.smax = tmp
            self.max_days.append(r[0])

        if self.min_days.count == 0 or tmp <= self.smin: # 保存した値より小さい場合
            if tmp < self.smin:
                self.min_days.clear()                    # 保存した文字列を削除
            self.smin = tmp
            self.min_days.append(r[0])

# ひと月の最高・最低気温を処理するクラス
class Mtemp:
    def __init__(self):
        self.smax = -999.0
        self.smin = 999.0
        self.sum = 0.0 # ひと月の気温の合計
        self.ct = 0    # ひと月の気温値の数
        
    # 気温の比較処理
    # 引数は、気温
    def compValue(self,tmp):
        if self.smax < tmp:
            self.smax = tmp
        if tmp < self.smin:
            self.smin = tmp
        self.sum += tmp
        self.ct+=1

dtmp = Dtemp() # 最高・最低気温とその日時を処理するクラスのインスタンス生成

# 12ヶ月分のオブジェクトを作成しておく
map = {}
for i in range(1, 13):
    map[i] = Mtemp()

try:
    # CSVファイルのオープン    
    with open('data.csv') as csvfile:
        for r in csv.reader(csvfile):            # 1行ずつ取得
            if r[2] == "8":                      # 品質情報が8のみ対象にする
                dtmp.compValue(r[0],float(r[1])) # 気温の比較処理
              
                # 日時文字列から、月のみを取り出す
                month = datetime.datetime.strptime(r[0], '%Y/%m/%d %H:%M').month
                map[month].compValue(float(r[1])) # 気温の比較処理
    
    # 結果の表示を行う
    print(f'最高：{dtmp.smax:.1f}℃')
    for ds in dtmp.max_days:
        print(f'{ds}')
    
    print(f'最低：{dtmp.smin:.1f}℃')
    for ds in dtmp.min_days:
        print(f'{ds}')

    for k,v in map.items():
        print(f'{k:2}月 最高：{v.smax:.1f}℃ 最低：{v.smin:.1f}℃ 平均：{(v.sum/v.ct):.1f}℃')
except:
    print('file error')
