solution-finderドキュメント
===========================================

概要
-------------------------------------------

solution-finderとは、テトリスで「ある地形からパーフェクトできる確率・接着手順」など、条件に従った解を探索するためのツールです。

solution-finderは、探索ツールとして次の特徴を持っています。

* 任意のフィールド・ミノ組み合わせを指定した探索が可能
* 探索時の回転法則はSRSに準拠
* マルチスレッドによる探索に対応
* 実行時にオプションを与えることで「ホールドあり・なし」など細かい設定が可能
* 入力として `連続テト譜エディタ Ver 1.15a <http://fumen.zui.jp>`_ のデータに対応


主な機能
-------------------------------------------

* percent: ある地形からパーフェクトできる確率を計算する
   - 7種のミノ(TIJLSZO) の様々な組み合わせでの探索が可能
   - 先頭3ミノごとのパーフェクト成功確率もツリー形式で表示
   - ホールドあり・なしの切り替えに対応
   - パーフェクトができないツモ順を表示
   - ソフトドロップありの場合のみ対応

* path: ある地形からパーフェクトまでの操作手順をすべて列挙する
    - 指定したミノの組み合わせから、パーフェクトまでの全パターンを列挙してファイルに出力
    - 2種類の結果を列挙して出力
    - 出力フォーマットは、テト譜リンクとCSVに対応
    - ホールドあり・なしの切り替えに対応

* util: solution-finderの機能を補助するユーティリティ
   - fig: テト譜をもとに画像を生成


ダウンロード
-------------------------------------------

ダウンロードは 以下のリンク からお願いします。

https://github.com/knewjade/solution-finder/releases/

※ プログラムの実行には、Java8が実行できる環境が必要です
※ メモリの関係から 64bit版 を推奨します


補助GUI
-------------------------------------------

solution-finderを操作しやすくするGUIを `@kitsune_fuchi (twitter) <https://twitter.com/kitsune_fuchi>`_ さんに作成していただきました。

ダウンロードは 以下のツイートに記載されているリンク先 からお願いします。

* path, percent

    - https://twitter.com/kitsune_fuchi/status/868826325770878976

* util fig

    - https://twitter.com/kitsune_fuchi/status/871189534746202113



.. toctree::
   :maxdepth: 2
   :caption: 目次

   quick_start
   field
   patterns
   command
   percent/main
   path/main
   util/main
   caution


.. * :ref:`genindex`
.. * :ref:`modindex`
.. :ref:`search`
