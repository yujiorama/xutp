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

[Humble Object] How it Works
----------------------------

- 「テストしづらいコンポーネント」のロジックを、「テストしやすいコンポーネント」に移動します
    - **synchronous tests** なのでテストしやすいんです
    - テストしやすいコンポーネント
        - テストしづらいコンポーネントのロジックを公開するためのメソッドを持つサービスインターフェースを実装してます
        - 唯一の違いは同期的なメソッド呼び出しができることです
- *Humble Object* は、少しのコードからなる薄いアダプタです
- フレームワークによる *Humble Object* の呼び出しは、「テストしやすいコンポーネント」に委譲されます
- *Humble Object* には、「テストしやすいコンポーネント」が必要とするコンテキストの情報を渡す責務があります
- *Humble Object* のコードはとても小さいけど、テストを実行するための setup がとても大変なので、書くのが退屈なんてことにはなりません

[Humble Object] When to Use It
------------------------------

- フレームワークに依存してる or 非同期な呼び出ししかできない。なのになんだか複雑なロジックを持ってるコンポーネントがある
    - *Humble Object* を取り入れましょう
- テストが大変になってしまうのにはいろんな理由があります
    - 依存関係を切るためのやりかたもいろんな方法になります
- 本章の variations はよくあるパターンです
    - 自前の variation を考案しても驚くことではないです

### Variation: Humble Dialog

- GUI フレームワークは、ページやコントロールを表示するためにオブジェクトを要求してきます
- GUI フレームワークが必要とするオブジェクトの提供するロジック
    - ユーザの操作をシステムに伝えます
    - システムの応答をユーザの認識できる振る舞いとして伝えます
    - ユーザインターフェースの裏でアプリケーションを呼び出します
    - 自分や他の visual object の状態を変えます
- Visual object の効率的なテストはとっても大変
    - プレゼンテーションフレームワークとの結合が密すぎる
    - テストは、環境をシミュレートして、visual object が必要とする情報や設備 (属性?機能?) を提供しないといけない
- Visual object の効率的なテストは本当に大変
    - フレームワークが管理してる専用のスレッドから使用してることがある (# SWT とかそうですね)
    - **asynchronous test** を使わざるを得ない
        - とてもチャレンジング
        - *Slow Test* になってしまう
        - *Nondeterminisitic Test* になってしまう
- *Humble Object* によって解決できる!
