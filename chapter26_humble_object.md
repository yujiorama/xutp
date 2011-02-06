Chapter26. Humble Object
========================

- How can we make code testable when it is too closely coupled to its environment?
- **We extract the logic into a separate, easay-to-test component that is decoupled from its environment.**

- - -

- なんらかのフレームワークに密結合されたソフトウェアをテストしなければならない、という状況
- 本章の例で扱っているもの
    - visual components (ウィジェットやダイアログ)
    - transactional component plug-ins
- これらのオブジェクトのテストは大変
    - オブジェクトを生成するための SUT とのやりとりが重い (be expensive)
    - 不可能だったりする
    - 非同期で動作する場合もある
        - 本章の例で扱っているもの
        - active objects (スレッド、プロセス、Web サーバ)
        - user interfaces
    - 非同期処理による不確実性をテストで扱うために必要なこと
        - interprocess coordination
        - 遅延させる機能
- 一般的にはこういったコードのテストは諦められてしまうが悪い結果になる
    - *Untested Code* になる
    - *Untested Requirements* ができる
    - *Production Bugs* が発生する
- *Humble Object*
    - インスタンスの生成がハードなオブジェクトが持つロジックを、効率的なやりかたでテストに持ち込む方法です
